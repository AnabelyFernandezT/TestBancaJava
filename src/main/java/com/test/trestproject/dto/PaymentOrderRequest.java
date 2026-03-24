package com.test.trestproject.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderRequest {
    @NotNull
    private String externalReference;
    
    @NotNull
    @Valid
    private AccountReference debtorAccount;
    
    @NotNull
    @Valid
    private AccountReference creditorAccount;
    
    @NotNull
    @Valid
    private InstructedAmount instructedAmount;
    
    private String remittanceInformation;
    
    @NotNull
    private String requestedExecutionDate;
}
