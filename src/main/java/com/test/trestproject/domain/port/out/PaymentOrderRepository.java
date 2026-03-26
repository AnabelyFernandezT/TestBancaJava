package com.test.trestproject.domain.port.out;

import com.test.trestproject.domain.model.PaymentOrder;

import java.util.Optional;

/**
 * Output Port - Repository interface
 * Part of hexagonal architecture - defines persistence contract
 */
public interface PaymentOrderRepository {
    
    /**
     * Save a payment order
     * @param paymentOrder The payment order to save
     * @return The saved payment order
     */
    PaymentOrder save(PaymentOrder paymentOrder);
    
    /**
     * Find a payment order by ID
     * @param paymentOrderId The payment order identifier
     * @return Optional containing the payment order if found
     */
    Optional<PaymentOrder> findById(String paymentOrderId);
    
    /**
     * Update the status of a payment order
     * @param paymentOrderId The payment order identifier
     * @param paymentOrder The updated payment order
     */
    void update(String paymentOrderId, PaymentOrder paymentOrder);
}
