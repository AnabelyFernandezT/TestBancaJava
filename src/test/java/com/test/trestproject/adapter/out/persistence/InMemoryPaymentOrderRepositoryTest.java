package com.test.trestproject.adapter.out.persistence;

import com.test.trestproject.domain.model.PaymentOrder;
import com.test.trestproject.domain.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPaymentOrderRepositoryTest {

    private InMemoryPaymentOrderRepository repository;
    private PaymentOrder paymentOrder;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaymentOrderRepository();
        
        paymentOrder = PaymentOrder.builder()
                .paymentOrderId("PO-0001")
                .externalReference("EXT-001")
                .debtorIban("EC12DEBTOR")
                .creditorIban("EC98CREDITOR")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .remittanceInformation("Test")
                .requestedExecutionDate("2025-10-31")
                .status(PaymentStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    @Test
    void testSave_Success() {
        // Act
        PaymentOrder saved = repository.save(paymentOrder);

        // Assert
        assertNotNull(saved);
        assertEquals("PO-0001", saved.getPaymentOrderId());
    }

    @Test
    void testFindById_Found() {
        // Arrange
        repository.save(paymentOrder);

        // Act
        Optional<PaymentOrder> found = repository.findById("PO-0001");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("PO-0001", found.get().getPaymentOrderId());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<PaymentOrder> found = repository.findById("PO-9999");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        repository.save(paymentOrder);
        paymentOrder.updateStatus(PaymentStatus.SETTLED);

        // Act
        repository.update("PO-0001", paymentOrder);

        // Assert
        Optional<PaymentOrder> updated = repository.findById("PO-0001");
        assertTrue(updated.isPresent());
        assertEquals(PaymentStatus.SETTLED, updated.get().getStatus());
    }
}
