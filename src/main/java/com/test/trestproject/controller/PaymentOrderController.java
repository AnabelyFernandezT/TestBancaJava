package com.test.trestproject.controller;

import com.test.trestproject.dto.PaymentOrderRequest;
import com.test.trestproject.dto.PaymentOrderResponse;
import com.test.trestproject.dto.PaymentOrderStatusResponse;
import com.test.trestproject.service.PaymentOrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-initiation/payment-orders")
public class PaymentOrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderController.class);
    
    private final PaymentOrderService paymentOrderService;

    public PaymentOrderController(PaymentOrderService paymentOrderService) {
        this.paymentOrderService = paymentOrderService;
    }

    /**
     * POST /payment-initiation/payment-orders
     * Initiate a new payment order (BIAN: Initiate)
     */
    @PostMapping
    public ResponseEntity<PaymentOrderResponse> initiatePaymentOrder(
            @Valid @RequestBody PaymentOrderRequest request) {
        
        logger.info("POST /payment-initiation/payment-orders - Initiating payment order");
        
        PaymentOrderResponse response = paymentOrderService.initiatePaymentOrder(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /payment-initiation/payment-orders/{paymentOrderId}
     * Retrieve a payment order (BIAN: Retrieve)
     */
    @GetMapping("/{paymentOrderId}")
    public ResponseEntity<PaymentOrderResponse> retrievePaymentOrder(
            @PathVariable String paymentOrderId) {
        
        logger.info("GET /payment-initiation/payment-orders/{} - Retrieving payment order", paymentOrderId);
        
        PaymentOrderResponse response = paymentOrderService.retrievePaymentOrder(paymentOrderId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /payment-initiation/payment-orders/{paymentOrderId}/status
     * Retrieve payment order status (BIAN: Retrieve BQ)
     */
    @GetMapping("/{paymentOrderId}/status")
    public ResponseEntity<PaymentOrderStatusResponse> retrievePaymentOrderStatus(
            @PathVariable String paymentOrderId) {
        
        logger.info("GET /payment-initiation/payment-orders/{}/status - Retrieving status", paymentOrderId);
        
        PaymentOrderStatusResponse response = paymentOrderService.retrievePaymentOrderStatus(paymentOrderId);
        
        return ResponseEntity.ok(response);
    }
}
