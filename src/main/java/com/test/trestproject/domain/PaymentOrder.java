package com.test.trestproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {
    private String paymentOrderId;
    private String externalReference;
    private String debtorIban;
    private String creditorIban;
    private BigDecimal amount;
    private String currency;
    private String remittanceInformation;
    private String requestedExecutionDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate;
}
