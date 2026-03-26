package com.test.trestproject.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain Entity - Payment Order
 * Pure domain model without infrastructure dependencies
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {
    private String paymentOrderId;
    private String externalReference;
    private String debtorIban;
    private String creditorIban;
    private BigDecimal amount;
    private String currency;
    private String remittanceInformation;
    private String requestedExecutionDate;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate;

    /**
     * Domain method to update status
     */
    public void updateStatus(PaymentStatus newStatus) {
        this.status = newStatus;
        this.lastUpdate = LocalDateTime.now();
    }

    /**
     * Domain method to check if payment can be processed
     */
    public boolean canBeProcessed() {
        return status == PaymentStatus.ACCEPTED || status == PaymentStatus.PENDING;
    }
}
