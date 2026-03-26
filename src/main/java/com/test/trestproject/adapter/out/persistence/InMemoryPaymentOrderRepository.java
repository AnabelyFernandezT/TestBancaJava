package com.test.trestproject.adapter.out.persistence;

import com.test.trestproject.domain.model.PaymentOrder;
import com.test.trestproject.domain.port.out.PaymentOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter - Persistence implementation using in-memory storage
 * Implements Output Port for repository
 */
@Repository
public class InMemoryPaymentOrderRepository implements PaymentOrderRepository {
    
    private final Map<String, PaymentOrder> storage = new ConcurrentHashMap<>();

    @Override
    public PaymentOrder save(PaymentOrder paymentOrder) {
        storage.put(paymentOrder.getPaymentOrderId(), paymentOrder);
        return paymentOrder;
    }

    @Override
    public Optional<PaymentOrder> findById(String paymentOrderId) {
        return Optional.ofNullable(storage.get(paymentOrderId));
    }

    @Override
    public void update(String paymentOrderId, PaymentOrder paymentOrder) {
        if (storage.containsKey(paymentOrderId)) {
            storage.put(paymentOrderId, paymentOrder);
        }
    }
}
