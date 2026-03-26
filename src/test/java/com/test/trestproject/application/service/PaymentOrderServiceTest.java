package com.test.trestproject.application.service;

import com.test.trestproject.domain.exception.PaymentOrderNotFoundException;
import com.test.trestproject.domain.model.PaymentOrder;
import com.test.trestproject.domain.model.PaymentStatus;
import com.test.trestproject.domain.port.in.InitiatePaymentOrderCommand;
import com.test.trestproject.domain.port.out.LegacyPaymentService;
import com.test.trestproject.domain.port.out.PaymentOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOrderServiceTest {

    @Mock
    private LegacyPaymentService legacyPaymentService;

    @Mock
    private PaymentOrderRepository paymentOrderRepository;

    @InjectMocks
    private PaymentOrderService paymentOrderService;

    private InitiatePaymentOrderCommand command;
    private LegacyPaymentService.LegacyPaymentResponse legacyResponse;
    private PaymentOrder paymentOrder;

    @BeforeEach
    void setUp() {
        command = InitiatePaymentOrderCommand.builder()
                .externalReference("EXT-TEST-001")
                .debtorIban("EC12DEBTOR")
                .creditorIban("EC98CREDITOR")
                .amount(new BigDecimal("150.75"))
                .currency("USD")
                .remittanceInformation("Test Payment")
                .requestedExecutionDate("2025-10-31")
                .build();

        legacyResponse = new LegacyPaymentService.LegacyPaymentResponse("PO-0001", "ACCEPTED");

        paymentOrder = PaymentOrder.builder()
                .paymentOrderId("PO-0001")
                .externalReference("EXT-TEST-001")
                .debtorIban("EC12DEBTOR")
                .creditorIban("EC98CREDITOR")
                .amount(new BigDecimal("150.75"))
                .currency("USD")
                .remittanceInformation("Test Payment")
                .requestedExecutionDate("2025-10-31")
                .status(PaymentStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    @Test
    void testInitiatePaymentOrder_Success() {
        // Arrange
        when(legacyPaymentService.submitPaymentOrder(any(LegacyPaymentService.LegacyPaymentRequest.class)))
                .thenReturn(legacyResponse);
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenReturn(paymentOrder);

        // Act
        PaymentOrder result = paymentOrderService.initiatePaymentOrder(command);

        // Assert
        assertNotNull(result);
        assertEquals("PO-0001", result.getPaymentOrderId());
        assertEquals("EXT-TEST-001", result.getExternalReference());
        assertEquals(PaymentStatus.ACCEPTED, result.getStatus());

        verify(legacyPaymentService, times(1))
                .submitPaymentOrder(any(LegacyPaymentService.LegacyPaymentRequest.class));
        verify(paymentOrderRepository, times(1)).save(any(PaymentOrder.class));
    }

    @Test
    void testRetrievePaymentOrder_Success() {
        // Arrange
        when(paymentOrderRepository.findById("PO-0001")).thenReturn(Optional.of(paymentOrder));

        // Act
        PaymentOrder result = paymentOrderService.retrievePaymentOrder("PO-0001");

        // Assert
        assertNotNull(result);
        assertEquals("PO-0001", result.getPaymentOrderId());

        verify(paymentOrderRepository, times(1)).findById("PO-0001");
    }

    @Test
    void testRetrievePaymentOrder_NotFound() {
        // Arrange
        when(paymentOrderRepository.findById("PO-9999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentOrderNotFoundException.class, () -> {
            paymentOrderService.retrievePaymentOrder("PO-9999");
        });

        verify(paymentOrderRepository, times(1)).findById("PO-9999");
    }

    @Test
    void testRetrievePaymentOrderStatus_Success() {
        // Arrange
        when(paymentOrderRepository.findById("PO-0001")).thenReturn(Optional.of(paymentOrder));
        
        LegacyPaymentService.LegacyStatusResponse statusResponse = 
            new LegacyPaymentService.LegacyStatusResponse("PO-0001", "SETTLED", LocalDateTime.now());
        
        when(legacyPaymentService.getPaymentOrderStatus("PO-0001")).thenReturn(statusResponse);
        doNothing().when(paymentOrderRepository).update(anyString(), any(PaymentOrder.class));

        // Act
        PaymentOrder result = paymentOrderService.retrievePaymentOrderStatus("PO-0001");

        // Assert
        assertNotNull(result);
        assertEquals("PO-0001", result.getPaymentOrderId());
        assertEquals(PaymentStatus.SETTLED, result.getStatus());

        verify(legacyPaymentService, times(1)).getPaymentOrderStatus("PO-0001");
        verify(paymentOrderRepository, times(1)).update(anyString(), any(PaymentOrder.class));
    }
}
