package com.test.trestproject.adapter.out.soap;

import com.test.trestproject.domain.port.out.LegacyPaymentService;
import com.test.trestproject.soap.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adapter - SOAP Client implementation
 * Implements Output Port for legacy payment service
 */
@Component
public class SoapLegacyPaymentServiceAdapter implements LegacyPaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(SoapLegacyPaymentServiceAdapter.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    private final WebServiceTemplate webServiceTemplate;
    
    @Value("${soap.legacy.url:http://localhost:8081/legacy/payments}")
    private String soapServiceUrl;

    public SoapLegacyPaymentServiceAdapter(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public LegacyPaymentResponse submitPaymentOrder(LegacyPaymentRequest request) {
        logger.info("Calling SOAP service to submit payment order for externalId: {}", request.externalId());
        
        try {
            // Map to SOAP model
            SubmitPaymentOrderRequest soapRequest = new SubmitPaymentOrderRequest();
            soapRequest.setExternalId(request.externalId());
            soapRequest.setDebtorIban(request.debtorIban());
            soapRequest.setCreditorIban(request.creditorIban());
            soapRequest.setAmount(request.amount());
            soapRequest.setCurrency(request.currency());
            soapRequest.setRemittanceInfo(request.remittanceInfo());
            soapRequest.setRequestedExecutionDate(request.requestedExecutionDate());
            
            // Call SOAP service
            SubmitPaymentOrderResponse soapResponse = (SubmitPaymentOrderResponse) webServiceTemplate
                    .marshalSendAndReceive(soapServiceUrl, soapRequest);
            
            logger.info("SOAP service returned paymentOrderId: {}, status: {}", 
                    soapResponse.getPaymentOrderId(), soapResponse.getStatus());
            
            // Map to domain response
            return new LegacyPaymentResponse(
                soapResponse.getPaymentOrderId(),
                soapResponse.getStatus()
            );
        } catch (Exception e) {
            logger.error("Error calling SOAP service for submit payment order", e);
            throw new RuntimeException("Failed to submit payment order to legacy system", e);
        }
    }

    @Override
    public LegacyStatusResponse getPaymentOrderStatus(String paymentOrderId) {
        logger.info("Calling SOAP service to get status for paymentOrderId: {}", paymentOrderId);
        
        try {
            // Create SOAP request
            GetPaymentOrderStatusRequest soapRequest = new GetPaymentOrderStatusRequest(paymentOrderId);
            
            // Call SOAP service
            GetPaymentOrderStatusResponse soapResponse = (GetPaymentOrderStatusResponse) webServiceTemplate
                    .marshalSendAndReceive(soapServiceUrl, soapRequest);
            
            logger.info("SOAP service returned status: {} for paymentOrderId: {}", 
                    soapResponse.getStatus(), paymentOrderId);
            
            // Map to domain response
            LocalDateTime lastUpdate = LocalDateTime.parse(soapResponse.getLastUpdate(), ISO_FORMATTER);
            
            return new LegacyStatusResponse(
                soapResponse.getPaymentOrderId(),
                soapResponse.getStatus(),
                lastUpdate
            );
        } catch (Exception e) {
            logger.error("Error calling SOAP service for payment order status", e);
            throw new RuntimeException("Failed to get payment order status from legacy system", e);
        }
    }
}
