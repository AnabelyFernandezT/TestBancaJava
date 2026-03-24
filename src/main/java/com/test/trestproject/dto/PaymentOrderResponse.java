package com.test.trestproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private String paymentOrderId;
    private String externalReference;
    private AccountReference debtorAccount;
    private AccountReference creditorAccount;
    private InstructedAmount instructedAmount;
    private String remittanceInformation;
    private String requestedExecutionDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate;
}
