package com.test.trestproject.domain.port.in;

import com.test.trestproject.domain.model.PaymentOrder;

/**
 * Input Port - Use Case for retrieving payment order status
 */
public interface RetrievePaymentOrderStatusUseCase {
    
    /**
     * Retrieve the current status of a payment order
     * @param paymentOrderId The payment order identifier
     * @return The payment order with updated status
     */
    PaymentOrder retrievePaymentOrderStatus(String paymentOrderId);
}
