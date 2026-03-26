package com.test.trestproject.domain.model;

/**
 * Domain Value Object - Payment Status
 */
public enum PaymentStatus {
    ACCEPTED,
    PENDING,
    SETTLED,
    REJECTED,
    CANCELLED;

    public static PaymentStatus fromString(String status) {
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
