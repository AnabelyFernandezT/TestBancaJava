package com.test.trestproject.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "SubmitPaymentOrderRequest", namespace = "http://legacy.bank/payments")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubmitPaymentOrderRequest {
    @XmlElement(required = true)
    private String externalId;
    
    @XmlElement(required = true)
    private String debtorIban;
    
    @XmlElement(required = true)
    private String creditorIban;
    
    @XmlElement(required = true)
    private BigDecimal amount;
    
    @XmlElement(required = true)
    private String currency;
    
    @XmlElement
    private String remittanceInfo;
    
    @XmlElement(required = true)
    private String requestedExecutionDate;
}
