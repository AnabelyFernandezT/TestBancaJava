package com.test.trestproject.service;

import com.test.trestproject.domain.PaymentOrder;
import com.test.trestproject.dto.*;
import com.test.trestproject.repository.PaymentOrderRepository;
import com.test.trestproject.soap.client.PaymentOrderSoapClient;
import com.test.trestproject.soap.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOrderServiceTest {

    @Mock
    private PaymentOrderSoapClient soapClient;

    @Mock
    private PaymentOrderRepository repository;

    @InjectMocks
    private PaymentOrderService paymentOrderService;

    private PaymentOrderRequest request;
    private SubmitPaymentOrderResponse soapResponse;
    private PaymentOrder paymentOrder;

    @BeforeEach
    void setUp() {
        request = new PaymentOrderRequest();
        request.setExternalReference("EXT-TEST-001");
        request.setDebtorAccount(new AccountReference("EC12DEBTOR"));
        request.setCreditorAccount(new AccountReference("EC98CREDITOR"));
        request.setInstructedAmount(new InstructedAmount(new BigDecimal("150.75"), "USD"));
        request.setRemittanceInformation("Test Payment");
        request.setRequestedExecutionDate("2025-10-31");

        soapResponse = new SubmitPaymentOrderResponse();
        soapResponse.setPaymentOrderId("PO-0001");
        soapResponse.setStatus("ACCEPTED");

        paymentOrder = PaymentOrder.builder()
                .paymentOrderId("PO-0001")
                .externalReference("EXT-TEST-001")
                .debtorIban("EC12DEBTOR")
                .creditorIban("EC98CREDITOR")
                .amount(new BigDecimal("150.75"))
                .currency("USD")
                .remittanceInformation("Test Payment")
                .requestedExecutionDate("2025-10-31")
                .status("ACCEPTED")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    @Test
    void testInitiatePaymentOrder_Success() {
        // Arrange
        when(soapClient.submitPaymentOrder(any(SubmitPaymentOrderRequest.class)))
                .thenReturn(soapResponse);
        when(repository.save(any(PaymentOrder.class))).thenReturn(paymentOrder);

        // Act
        PaymentOrderResponse response = paymentOrderService.initiatePaymentOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals("PO-0001", response.getPaymentOrderId());
        assertEquals("EXT-TEST-001", response.getExternalReference());
        assertEquals("ACCEPTED", response.getStatus());
        assertEquals("EC12DEBTOR", response.getDebtorAccount().getIban());
        assertEquals("EC98CREDITOR", response.getCreditorAccount().getIban());
        assertEquals(new BigDecimal("150.75"), response.getInstructedAmount().getAmount());
        assertEquals("USD", response.getInstructedAmount().getCurrency());

        verify(soapClient, times(1)).submitPaymentOrder(any(SubmitPaymentOrderRequest.class));
        verify(repository, times(1)).save(any(PaymentOrder.class));
    }

    @Test
    void testRetrievePaymentOrder_Success() {
        // Arrange
        when(repository.findById("PO-0001")).thenReturn(Optional.of(paymentOrder));

        // Act
        PaymentOrderResponse response = paymentOrderService.retrievePaymentOrder("PO-0001");

        // Assert
        assertNotNull(response);
        assertEquals("PO-0001", response.getPaymentOrderId());
        assertEquals("EXT-TEST-001", response.getExternalReference());

        verify(repository, times(1)).findById("PO-0001");
    }

    @Test
    void testRetrievePaymentOrder_NotFound() {
        // Arrange
        when(repository.findById("PO-9999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentOrderService.retrievePaymentOrder("PO-9999");
        });

        verify(repository, times(1)).findById("PO-9999");
    }

    @Test
    void testRetrievePaymentOrderStatus_Success() {
        // Arrange
        GetPaymentOrderStatusResponse statusResponse = new GetPaymentOrderStatusResponse();
        statusResponse.setPaymentOrderId("PO-0001");
        statusResponse.setStatus("SETTLED");
        statusResponse.setLastUpdate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        when(soapClient.getPaymentOrderStatus("PO-0001")).thenReturn(statusResponse);
        doNothing().when(repository).updateStatus(anyString(), anyString(), any(LocalDateTime.class));

        // Act
        PaymentOrderStatusResponse response = paymentOrderService.retrievePaymentOrderStatus("PO-0001");

        // Assert
        assertNotNull(response);
        assertEquals("PO-0001", response.getPaymentOrderId());
        assertEquals("SETTLED", response.getStatus());
        assertNotNull(response.getLastUpdate());

        verify(soapClient, times(1)).getPaymentOrderStatus("PO-0001");
        verify(repository, times(1)).updateStatus(anyString(), anyString(), any(LocalDateTime.class));
    }

    @Test
    void testInitiatePaymentOrder_SoapClientException() {
        // Arrange
        when(soapClient.submitPaymentOrder(any(SubmitPaymentOrderRequest.class)))
                .thenThrow(new RuntimeException("SOAP service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            paymentOrderService.initiatePaymentOrder(request);
        });

        verify(soapClient, times(1)).submitPaymentOrder(any(SubmitPaymentOrderRequest.class));
        verify(repository, never()).save(any(PaymentOrder.class));
    }
}
