package com.test.trestproject.domain.port.out;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Output Port - Legacy Payment Service interface
 * Defines contract for communicating with legacy SOAP system
 */
public interface LegacyPaymentService {
    
    /**
     * Submit a payment order to legacy system
     * @param request The payment order submission request
     * @return The response from legacy system
     */
    LegacyPaymentResponse submitPaymentOrder(LegacyPaymentRequest request);
    
    /**
     * Get payment order status from legacy system
     * @param paymentOrderId The payment order identifier
     * @return The status response
     */
    LegacyStatusResponse getPaymentOrderStatus(String paymentOrderId);
    
    /**
     * Request DTO for legacy payment service
     */
    record LegacyPaymentRequest(
        String externalId,
        String debtorIban,
        String creditorIban,
        BigDecimal amount,
        String currency,
        String remittanceInfo,
        String requestedExecutionDate
    ) {}
    
    /**
     * Response DTO from legacy payment service
     */
    record LegacyPaymentResponse(
        String paymentOrderId,
        String status
    ) {}
    
    /**
     * Status response DTO from legacy service
     */
    record LegacyStatusResponse(
        String paymentOrderId,
        String status,
        LocalDateTime lastUpdate
    ) {}
}
