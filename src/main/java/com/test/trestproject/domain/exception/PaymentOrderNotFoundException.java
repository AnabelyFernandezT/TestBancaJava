package com.test.trestproject.domain.exception;

/**
 * Domain Exception - Payment Order Not Found
 */
public class PaymentOrderNotFoundException extends RuntimeException {
    public PaymentOrderNotFoundException(String message) {
        super(message);
    }
}
