package com.test.trestproject.service;

import com.test.trestproject.domain.PaymentOrder;
import com.test.trestproject.dto.*;
import com.test.trestproject.repository.PaymentOrderRepository;
import com.test.trestproject.soap.client.PaymentOrderSoapClient;
import com.test.trestproject.soap.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentOrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderService.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    private final PaymentOrderSoapClient soapClient;
    private final PaymentOrderRepository repository;

    public PaymentOrderService(PaymentOrderSoapClient soapClient, PaymentOrderRepository repository) {
        this.soapClient = soapClient;
        this.repository = repository;
    }

    public PaymentOrderResponse initiatePaymentOrder(PaymentOrderRequest request) {
        logger.info("Initiating payment order with externalReference: {}", request.getExternalReference());
        
        // Map REST request to SOAP request
        SubmitPaymentOrderRequest soapRequest = new SubmitPaymentOrderRequest();
        soapRequest.setExternalId(request.getExternalReference());
        soapRequest.setDebtorIban(request.getDebtorAccount().getIban());
        soapRequest.setCreditorIban(request.getCreditorAccount().getIban());
        soapRequest.setAmount(request.getInstructedAmount().getAmount());
        soapRequest.setCurrency(request.getInstructedAmount().getCurrency());
        soapRequest.setRemittanceInfo(request.getRemittanceInformation());
        soapRequest.setRequestedExecutionDate(request.getRequestedExecutionDate());
        
        // Call legacy SOAP service
        SubmitPaymentOrderResponse soapResponse = soapClient.submitPaymentOrder(soapRequest);
        
        // Create domain entity
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder paymentOrder = PaymentOrder.builder()
                .paymentOrderId(soapResponse.getPaymentOrderId())
                .externalReference(request.getExternalReference())
                .debtorIban(request.getDebtorAccount().getIban())
                .creditorIban(request.getCreditorAccount().getIban())
                .amount(request.getInstructedAmount().getAmount())
                .currency(request.getInstructedAmount().getCurrency())
                .remittanceInformation(request.getRemittanceInformation())
                .requestedExecutionDate(request.getRequestedExecutionDate())
                .status(soapResponse.getStatus())
                .createdAt(now)
                .lastUpdate(now)
                .build();
        
        // Save to repository
        repository.save(paymentOrder);
        
        // Map to response DTO
        return mapToResponse(paymentOrder);
    }

    public PaymentOrderResponse retrievePaymentOrder(String paymentOrderId) {
        logger.info("Retrieving payment order: {}", paymentOrderId);
        
        PaymentOrder paymentOrder = repository.findById(paymentOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found: " + paymentOrderId));
        
        return mapToResponse(paymentOrder);
    }

    public PaymentOrderStatusResponse retrievePaymentOrderStatus(String paymentOrderId) {
        logger.info("Retrieving payment order status: {}", paymentOrderId);
        
        // Call SOAP service to get latest status
        GetPaymentOrderStatusResponse soapResponse = soapClient.getPaymentOrderStatus(paymentOrderId);
        
        // Parse lastUpdate
        LocalDateTime lastUpdate = LocalDateTime.parse(soapResponse.getLastUpdate(), ISO_FORMATTER);
        
        // Update repository
        repository.updateStatus(paymentOrderId, soapResponse.getStatus(), lastUpdate);
        
        // Return status response
        return PaymentOrderStatusResponse.builder()
                .paymentOrderId(soapResponse.getPaymentOrderId())
                .status(soapResponse.getStatus())
                .lastUpdate(lastUpdate)
                .build();
    }

    private PaymentOrderResponse mapToResponse(PaymentOrder paymentOrder) {
        return PaymentOrderResponse.builder()
                .paymentOrderId(paymentOrder.getPaymentOrderId())
                .externalReference(paymentOrder.getExternalReference())
                .debtorAccount(new AccountReference(paymentOrder.getDebtorIban()))
                .creditorAccount(new AccountReference(paymentOrder.getCreditorIban()))
                .instructedAmount(new InstructedAmount(paymentOrder.getAmount(), paymentOrder.getCurrency()))
                .remittanceInformation(paymentOrder.getRemittanceInformation())
                .requestedExecutionDate(paymentOrder.getRequestedExecutionDate())
                .status(paymentOrder.getStatus())
                .createdAt(paymentOrder.getCreatedAt())
                .lastUpdate(paymentOrder.getLastUpdate())
                .build();
    }
}
