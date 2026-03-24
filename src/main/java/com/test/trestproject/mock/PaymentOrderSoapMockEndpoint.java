package com.test.trestproject.mock;

import com.test.trestproject.soap.model.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock SOAP endpoint for testing purposes.
 * Enable by setting: soap.mock.enabled=true in application.properties
 */
@Component
@Endpoint
@ConditionalOnProperty(name = "soap.mock.enabled", havingValue = "true", matchIfMissing = false)
public class PaymentOrderSoapMockEndpoint {

    private static final String NAMESPACE_URI = "http://legacy.bank/payments";
    private static final AtomicInteger counter = new AtomicInteger(1);

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SubmitPaymentOrderRequest")
    @ResponsePayload
    public SubmitPaymentOrderResponse submitPaymentOrder(@RequestPayload SubmitPaymentOrderRequest request) {
        
        String paymentOrderId = "PO-" + String.format("%04d", counter.getAndIncrement());
        
        SubmitPaymentOrderResponse response = new SubmitPaymentOrderResponse();
        response.setPaymentOrderId(paymentOrderId);
        response.setStatus("ACCEPTED");
        
        System.out.println("Mock SOAP: Created payment order " + paymentOrderId + 
                " for externalId: " + request.getExternalId());
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPaymentOrderStatusRequest")
    @ResponsePayload
    public GetPaymentOrderStatusResponse getPaymentOrderStatus(@RequestPayload GetPaymentOrderStatusRequest request) {
        
        GetPaymentOrderStatusResponse response = new GetPaymentOrderStatusResponse();
        response.setPaymentOrderId(request.getPaymentOrderId());
        response.setStatus("SETTLED");
        response.setLastUpdate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        System.out.println("Mock SOAP: Returned status for payment order " + request.getPaymentOrderId());
        
        return response;
    }
}
