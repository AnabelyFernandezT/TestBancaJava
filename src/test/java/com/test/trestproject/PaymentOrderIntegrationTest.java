package com.test.trestproject;

import com.test.trestproject.dto.AccountReference;
import com.test.trestproject.dto.InstructedAmount;
import com.test.trestproject.dto.PaymentOrderRequest;
import com.test.trestproject.dto.PaymentOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
        properties = {"soap.mock.enabled=true"})
class PaymentOrderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateAndRetrievePaymentOrder() {
        // Create payment order request
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setExternalReference("EXT-TEST-001");
        request.setDebtorAccount(new AccountReference("EC12DEBTOR"));
        request.setCreditorAccount(new AccountReference("EC98CREDITOR"));
        request.setInstructedAmount(new InstructedAmount(new BigDecimal("150.75"), "USD"));
        request.setRemittanceInformation("Test Payment");
        request.setRequestedExecutionDate("2025-10-31");

        // POST to create payment order
        ResponseEntity<PaymentOrderResponse> createResponse = restTemplate.postForEntity(
                "/payment-initiation/payment-orders",
                request,
                PaymentOrderResponse.class
        );

        // Verify creation
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getPaymentOrderId());
        assertEquals("EXT-TEST-001", createResponse.getBody().getExternalReference());
        assertEquals("ACCEPTED", createResponse.getBody().getStatus());

        String paymentOrderId = createResponse.getBody().getPaymentOrderId();

        // GET to retrieve payment order
        ResponseEntity<PaymentOrderResponse> getResponse = restTemplate.getForEntity(
                "/payment-initiation/payment-orders/" + paymentOrderId,
                PaymentOrderResponse.class
        );

        // Verify retrieval
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(paymentOrderId, getResponse.getBody().getPaymentOrderId());
        assertEquals("EXT-TEST-001", getResponse.getBody().getExternalReference());
    }
}
