package com.test.trestproject.repository;

import com.test.trestproject.domain.PaymentOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PaymentOrderRepositoryTest {

    private PaymentOrderRepository repository;
    private PaymentOrder paymentOrder;

    @BeforeEach
    void setUp() {
        repository = new PaymentOrderRepository();
        
        paymentOrder = PaymentOrder.builder()
                .paymentOrderId("PO-0001")
                .externalReference("EXT-001")
                .debtorIban("EC12DEBTOR")
                .creditorIban("EC98CREDITOR")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .remittanceInformation("Test")
                .requestedExecutionDate("2025-10-31")
                .status("ACCEPTED")
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
        assertEquals("EXT-001", found.get().getExternalReference());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<PaymentOrder> found = repository.findById("PO-9999");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateStatus_Success() {
        // Arrange
        repository.save(paymentOrder);
        LocalDateTime newUpdate = LocalDateTime.now().plusHours(1);

        // Act
        repository.updateStatus("PO-0001", "SETTLED", newUpdate);

        // Assert
        Optional<PaymentOrder> updated = repository.findById("PO-0001");
        assertTrue(updated.isPresent());
        assertEquals("SETTLED", updated.get().getStatus());
        assertEquals(newUpdate, updated.get().getLastUpdate());
    }

    @Test
    void testUpdateStatus_NotFound() {
        // Act - should not throw exception
        repository.updateStatus("PO-9999", "SETTLED", LocalDateTime.now());

        // Assert - no exception thrown
        Optional<PaymentOrder> found = repository.findById("PO-9999");
        assertFalse(found.isPresent());
    }
}
