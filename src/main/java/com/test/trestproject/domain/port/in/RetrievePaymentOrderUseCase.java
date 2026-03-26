package com.test.trestproject.domain.port.in;

import com.test.trestproject.domain.model.PaymentOrder;

/**
 * Input Port - Use Case for retrieving payment orders
 */
public interface RetrievePaymentOrderUseCase {
    
    /**
     * Retrieve a payment order by ID
     * @param paymentOrderId The payment order identifier
     * @return The payment order
     */
    PaymentOrder retrievePaymentOrder(String paymentOrderId);
}
