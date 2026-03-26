package com.test.trestproject.application.service;

import com.test.trestproject.domain.exception.PaymentOrderNotFoundException;
import com.test.trestproject.domain.model.PaymentOrder;
import com.test.trestproject.domain.model.PaymentStatus;
import com.test.trestproject.domain.port.in.InitiatePaymentOrderCommand;
import com.test.trestproject.domain.port.in.InitiatePaymentOrderUseCase;
import com.test.trestproject.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.test.trestproject.domain.port.in.RetrievePaymentOrderUseCase;
import com.test.trestproject.domain.port.out.LegacyPaymentService;
import com.test.trestproject.domain.port.out.PaymentOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Application Service - Implements Use Cases
 * Orchestrates domain logic and adapters
 */
@Service
public class PaymentOrderService implements 
        InitiatePaymentOrderUseCase,
        RetrievePaymentOrderUseCase,
        RetrievePaymentOrderStatusUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderService.class);
    
    private final LegacyPaymentService legacyPaymentService;
    private final PaymentOrderRepository paymentOrderRepository;

    public PaymentOrderService(LegacyPaymentService legacyPaymentService, 
                              PaymentOrderRepository paymentOrderRepository) {
        this.legacyPaymentService = legacyPaymentService;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    public PaymentOrder initiatePaymentOrder(InitiatePaymentOrderCommand command) {
        logger.info("Initiating payment order with externalReference: {}", command.getExternalReference());
        
        // Create request for legacy system
        LegacyPaymentService.LegacyPaymentRequest legacyRequest = 
            new LegacyPaymentService.LegacyPaymentRequest(
                command.getExternalReference(),
                command.getDebtorIban(),
                command.getCreditorIban(),
                command.getAmount(),
                command.getCurrency(),
                command.getRemittanceInformation(),
                command.getRequestedExecutionDate()
            );
        
        // Call legacy service
        LegacyPaymentService.LegacyPaymentResponse legacyResponse = 
            legacyPaymentService.submitPaymentOrder(legacyRequest);
        
        // Create domain entity
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder paymentOrder = PaymentOrder.builder()
                .paymentOrderId(legacyResponse.paymentOrderId())
                .externalReference(command.getExternalReference())
                .debtorIban(command.getDebtorIban())
                .creditorIban(command.getCreditorIban())
                .amount(command.getAmount())
                .currency(command.getCurrency())
                .remittanceInformation(command.getRemittanceInformation())
                .requestedExecutionDate(command.getRequestedExecutionDate())
                .status(PaymentStatus.fromString(legacyResponse.status()))
                .createdAt(now)
                .lastUpdate(now)
                .build();
        
        // Save to repository
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder retrievePaymentOrder(String paymentOrderId) {
        logger.info("Retrieving payment order: {}", paymentOrderId);
        
        return paymentOrderRepository.findById(paymentOrderId)
                .orElseThrow(() -> new PaymentOrderNotFoundException(
                    "Payment order not found: " + paymentOrderId));
    }

    @Override
    public PaymentOrder retrievePaymentOrderStatus(String paymentOrderId) {
        logger.info("Retrieving payment order status: {}", paymentOrderId);
        
        // Get current order from repository
        PaymentOrder paymentOrder = paymentOrderRepository.findById(paymentOrderId)
                .orElseThrow(() -> new PaymentOrderNotFoundException(
                    "Payment order not found: " + paymentOrderId));
        
        // Get latest status from legacy system
        LegacyPaymentService.LegacyStatusResponse statusResponse = 
            legacyPaymentService.getPaymentOrderStatus(paymentOrderId);
        
        // Update domain entity
        paymentOrder.updateStatus(PaymentStatus.fromString(statusResponse.status()));
        paymentOrder.setLastUpdate(statusResponse.lastUpdate());
        
        // Update repository
        paymentOrderRepository.update(paymentOrderId, paymentOrder);
        
        return paymentOrder;
    }
}
