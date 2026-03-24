package com.test.trestproject.repository;

import com.test.trestproject.domain.PaymentOrder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentOrderRepository {
    private final Map<String, PaymentOrder> storage = new ConcurrentHashMap<>();

    public PaymentOrder save(PaymentOrder paymentOrder) {
        storage.put(paymentOrder.getPaymentOrderId(), paymentOrder);
        return paymentOrder;
    }

    public Optional<PaymentOrder> findById(String paymentOrderId) {
        return Optional.ofNullable(storage.get(paymentOrderId));
    }

    public void updateStatus(String paymentOrderId, String status, java.time.LocalDateTime lastUpdate) {
        PaymentOrder order = storage.get(paymentOrderId);
        if (order != null) {
            order.setStatus(status);
            order.setLastUpdate(lastUpdate);
        }
    }
}
