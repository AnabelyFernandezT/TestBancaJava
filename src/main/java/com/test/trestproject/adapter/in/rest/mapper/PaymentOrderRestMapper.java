package com.test.trestproject.adapter.in.rest.mapper;

import com.test.trestproject.domain.model.PaymentOrder;
import com.test.trestproject.domain.port.in.InitiatePaymentOrderCommand;
import com.test.trestproject.dto.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for REST Adapter
 * Maps between REST DTOs and Domain objects/Commands
 */
@Component
public class PaymentOrderRestMapper {

    public InitiatePaymentOrderCommand toCommand(PaymentOrderRequest request) {
        return InitiatePaymentOrderCommand.builder()
                .externalReference(request.getExternalReference())
                .debtorIban(request.getDebtorAccount().getIban())
                .creditorIban(request.getCreditorAccount().getIban())
                .amount(request.getInstructedAmount().getAmount())
                .currency(request.getInstructedAmount().getCurrency())
                .remittanceInformation(request.getRemittanceInformation())
                .requestedExecutionDate(request.getRequestedExecutionDate())
                .build();
    }

    public PaymentOrderResponse toResponse(PaymentOrder paymentOrder) {
        return PaymentOrderResponse.builder()
                .paymentOrderId(paymentOrder.getPaymentOrderId())
                .externalReference(paymentOrder.getExternalReference())
                .debtorAccount(new AccountReference(paymentOrder.getDebtorIban()))
                .creditorAccount(new AccountReference(paymentOrder.getCreditorIban()))
                .instructedAmount(new InstructedAmount(paymentOrder.getAmount(), paymentOrder.getCurrency()))
                .remittanceInformation(paymentOrder.getRemittanceInformation())
                .requestedExecutionDate(paymentOrder.getRequestedExecutionDate())
                .status(paymentOrder.getStatus().name())
                .createdAt(paymentOrder.getCreatedAt())
                .lastUpdate(paymentOrder.getLastUpdate())
                .build();
    }

    public PaymentOrderStatusResponse toStatusResponse(PaymentOrder paymentOrder) {
        return PaymentOrderStatusResponse.builder()
                .paymentOrderId(paymentOrder.getPaymentOrderId())
                .status(paymentOrder.getStatus().name())
                .lastUpdate(paymentOrder.getLastUpdate())
                .build();
    }
}
