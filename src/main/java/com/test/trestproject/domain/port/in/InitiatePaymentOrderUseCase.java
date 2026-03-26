package com.test.trestproject.domain.port.in;

import com.test.trestproject.domain.model.PaymentOrder;

/**
 * Input Port - Use Case for initiating payment orders
 * Part of hexagonal architecture - defines what the application can do
 */
public interface InitiatePaymentOrderUseCase {
    
    /**
     * Initiate a new payment order
     * @param command The payment order initiation command
     * @return The created payment order
     */
    PaymentOrder initiatePaymentOrder(InitiatePaymentOrderCommand command);
}
