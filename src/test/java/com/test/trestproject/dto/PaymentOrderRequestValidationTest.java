package com.test.trestproject.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentOrderRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidPaymentOrderRequest() {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setExternalReference("EXT-001");
        request.setDebtorAccount(new AccountReference("EC12DEBTOR"));
        request.setCreditorAccount(new AccountReference("EC98CREDITOR"));
        request.setInstructedAmount(new InstructedAmount(new BigDecimal("100.00"), "USD"));
        request.setRemittanceInformation("Test Payment");
        request.setRequestedExecutionDate("2025-10-31");

        // Act
        Set<ConstraintViolation<PaymentOrderRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @Test
    void testNullExternalReference() {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setExternalReference(null);
        request.setDebtorAccount(new AccountReference("EC12DEBTOR"));
        request.setCreditorAccount(new AccountReference("EC98CREDITOR"));
        request.setInstructedAmount(new InstructedAmount(new BigDecimal("100.00"), "USD"));
        request.setRequestedExecutionDate("2025-10-31");

        // Act
        Set<ConstraintViolation<PaymentOrderRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Null external reference should cause violation");
    }

    @Test
    void testNullDebtorAccount() {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setExternalReference("EXT-001");
        request.setDebtorAccount(null);
        request.setCreditorAccount(new AccountReference("EC98CREDITOR"));
        request.setInstructedAmount(new InstructedAmount(new BigDecimal("100.00"), "USD"));
        request.setRequestedExecutionDate("2025-10-31");

        // Act
        Set<ConstraintViolation<PaymentOrderRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        AccountReference debtorAccount = new AccountReference("EC12DEBTOR");
        AccountReference creditorAccount = new AccountReference("EC98CREDITOR");
        InstructedAmount amount = new InstructedAmount(new BigDecimal("100.00"), "USD");

        // Act
        request.setExternalReference("EXT-001");
        request.setDebtorAccount(debtorAccount);
        request.setCreditorAccount(creditorAccount);
        request.setInstructedAmount(amount);
        request.setRemittanceInformation("Test");
        request.setRequestedExecutionDate("2025-10-31");

        // Assert
        assertEquals("EXT-001", request.getExternalReference());
        assertEquals(debtorAccount, request.getDebtorAccount());
        assertEquals(creditorAccount, request.getCreditorAccount());
        assertEquals(amount, request.getInstructedAmount());
        assertEquals("Test", request.getRemittanceInformation());
        assertEquals("2025-10-31", request.getRequestedExecutionDate());
    }
}
