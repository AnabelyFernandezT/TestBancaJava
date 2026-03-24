package com.test.trestproject.soap.client;

import com.test.trestproject.soap.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class PaymentOrderSoapClient {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderSoapClient.class);
    
    private final WebServiceTemplate webServiceTemplate;
    
    @Value("${soap.legacy.url:http://localhost:8081/legacy/payments}")
    private String soapServiceUrl;

    public PaymentOrderSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public SubmitPaymentOrderResponse submitPaymentOrder(SubmitPaymentOrderRequest request) {
        logger.info("Calling SOAP service to submit payment order for externalId: {}", request.getExternalId());
        
        try {
            SubmitPaymentOrderResponse response = (SubmitPaymentOrderResponse) webServiceTemplate
                    .marshalSendAndReceive(soapServiceUrl, request);
            
            logger.info("SOAP service returned paymentOrderId: {}, status: {}", 
                    response.getPaymentOrderId(), response.getStatus());
            
            return response;
        } catch (Exception e) {
            logger.error("Error calling SOAP service for submit payment order", e);
            throw new RuntimeException("Failed to submit payment order to legacy system", e);
        }
    }

    public GetPaymentOrderStatusResponse getPaymentOrderStatus(String paymentOrderId) {
        logger.info("Calling SOAP service to get status for paymentOrderId: {}", paymentOrderId);
        
        try {
            GetPaymentOrderStatusRequest request = new GetPaymentOrderStatusRequest(paymentOrderId);
            GetPaymentOrderStatusResponse response = (GetPaymentOrderStatusResponse) webServiceTemplate
                    .marshalSendAndReceive(soapServiceUrl, request);
            
            logger.info("SOAP service returned status: {} for paymentOrderId: {}", 
                    response.getStatus(), paymentOrderId);
            
            return response;
        } catch (Exception e) {
            logger.error("Error calling SOAP service for payment order status", e);
            throw new RuntimeException("Failed to get payment order status from legacy system", e);
        }
    }
}
