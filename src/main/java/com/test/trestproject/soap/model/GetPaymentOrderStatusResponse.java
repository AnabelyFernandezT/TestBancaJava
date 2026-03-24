package com.test.trestproject.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GetPaymentOrderStatusResponse", namespace = "http://legacy.bank/payments")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPaymentOrderStatusResponse {
    @XmlElement(required = true)
    private String paymentOrderId;
    
    @XmlElement(required = true)
    private String status;
    
    @XmlElement(required = true)
    private String lastUpdate;
}
