package com.test.trestproject.controller;

import com.test.trestproject.dto.*;
import com.test.trestproject.service.PaymentOrderService;
import com.test.trestproject.service.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentOrderController.class)
class PaymentOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentOrderService paymentOrderService;

    private PaymentOrderResponse response;
    private PaymentOrderStatusResponse statusResponse;

    @BeforeEach
    void setUp() {
        response = PaymentOrderResponse.builder()
                .paymentOrderId("PO-0001")
                .externalReference("EXT-001")
                .debtorAccount(new AccountReference("EC12DEBTOR"))
                .creditorAccount(new AccountReference("EC98CREDITOR"))
                .instructedAmount(new InstructedAmount(new BigDecimal("150.75"), "USD"))
                .remittanceInformation("Test Payment")
                .requestedExecutionDate("2025-10-31")
                .status("ACCEPTED")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

        statusResponse = PaymentOrderStatusResponse.builder()
                .paymentOrderId("PO-0001")
                .status("SETTLED")
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    @Test
    void testInitiatePaymentOrder_Success() throws Exception {
        // Arrange
        when(paymentOrderService.initiatePaymentOrder(any(PaymentOrderRequest.class)))
                .thenReturn(response);

        String requestJson = """
                {
                    "externalReference": "EXT-001",
                    "debtorAccount": { "iban": "EC12DEBTOR" },
                    "creditorAccount": { "iban": "EC98CREDITOR" },
                    "instructedAmount": { "amount": 150.75, "currency": "USD" },
                    "remittanceInformation": "Test Payment",
                    "requestedExecutionDate": "2025-10-31"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/payment-initiation/payment-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentOrderId").value("PO-0001"))
                .andExpect(jsonPath("$.externalReference").value("EXT-001"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void testRetrievePaymentOrder_Success() throws Exception {
        // Arrange
        when(paymentOrderService.retrievePaymentOrder("PO-0001"))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/payment-initiation/payment-orders/PO-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentOrderId").value("PO-0001"))
                .andExpect(jsonPath("$.externalReference").value("EXT-001"));
    }

    @Test
    void testRetrievePaymentOrder_NotFound() throws Exception {
        // Arrange
        when(paymentOrderService.retrievePaymentOrder("PO-9999"))
                .thenThrow(new ResourceNotFoundException("Payment order not found: PO-9999"));

        // Act & Assert
        mockMvc.perform(get("/payment-initiation/payment-orders/PO-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment order not found: PO-9999"));
    }

    @Test
    void testRetrievePaymentOrderStatus_Success() throws Exception {
        // Arrange
        when(paymentOrderService.retrievePaymentOrderStatus("PO-0001"))
                .thenReturn(statusResponse);

        // Act & Assert
        mockMvc.perform(get("/payment-initiation/payment-orders/PO-0001/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentOrderId").value("PO-0001"))
                .andExpect(jsonPath("$.status").value("SETTLED"));
    }

    @Test
    void testInitiatePaymentOrder_InvalidRequest() throws Exception {
        // Arrange - Missing required fields
        String invalidRequestJson = """
                {
                    "externalReference": "EXT-001"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/payment-initiation/payment-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }
}
