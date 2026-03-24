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
public class PaymentOrderStatusResponse {
    private String paymentOrderId;
    private String status;
    private LocalDateTime lastUpdate;
}
