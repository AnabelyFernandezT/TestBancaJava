package com.test.trestproject.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GetPaymentOrderStatusRequest", namespace = "http://legacy.bank/payments")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPaymentOrderStatusRequest {
    @XmlElement(required = true)
    private String paymentOrderId;
}
