package com.test.trestproject.adapter.in.rest;

import com.test.trestproject.adapter.in.rest.mapper.PaymentOrderRestMapper;
import com.test.trestproject.domain.model.PaymentOrder;
import com.test.trestproject.domain.port.in.InitiatePaymentOrderCommand;
import com.test.trestproject.domain.port.in.InitiatePaymentOrderUseCase;
import com.test.trestproject.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.test.trestproject.domain.port.in.RetrievePaymentOrderUseCase;
import com.test.trestproject.dto.PaymentOrderRequest;
import com.test.trestproject.dto.PaymentOrderResponse;
import com.test.trestproject.dto.PaymentOrderStatusResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adapter - REST Controller
 * Input Adapter that calls Use Cases through Input Ports
 */
@RestController
@RequestMapping("/payment-initiation/payment-orders")
public class PaymentOrderRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderRestController.class);
    
    private final InitiatePaymentOrderUseCase initiatePaymentOrderUseCase;
    private final RetrievePaymentOrderUseCase retrievePaymentOrderUseCase;
    private final RetrievePaymentOrderStatusUseCase retrievePaymentOrderStatusUseCase;
    private final PaymentOrderRestMapper mapper;

    public PaymentOrderRestController(
            InitiatePaymentOrderUseCase initiatePaymentOrderUseCase,
            RetrievePaymentOrderUseCase retrievePaymentOrderUseCase,
            RetrievePaymentOrderStatusUseCase retrievePaymentOrderStatusUseCase,
            PaymentOrderRestMapper mapper) {
        this.initiatePaymentOrderUseCase = initiatePaymentOrderUseCase;
        this.retrievePaymentOrderUseCase = retrievePaymentOrderUseCase;
        this.retrievePaymentOrderStatusUseCase = retrievePaymentOrderStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * POST /payment-initiation/payment-orders
     * Initiate a new payment order (BIAN: Initiate)
     */
    @PostMapping
    public ResponseEntity<PaymentOrderResponse> initiatePaymentOrder(
            @Valid @RequestBody PaymentOrderRequest request) {
        
        logger.info("POST /payment-initiation/payment-orders - Initiating payment order");
        
        // Map REST DTO to Command
        InitiatePaymentOrderCommand command = mapper.toCommand(request);
        
        // Execute use case
        PaymentOrder paymentOrder = initiatePaymentOrderUseCase.initiatePaymentOrder(command);
        
        // Map domain model to REST DTO
        PaymentOrderResponse response = mapper.toResponse(paymentOrder);
        
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
        
        // Execute use case
        PaymentOrder paymentOrder = retrievePaymentOrderUseCase.retrievePaymentOrder(paymentOrderId);
        
        // Map to REST DTO
        PaymentOrderResponse response = mapper.toResponse(paymentOrder);
        
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
        
        // Execute use case
        PaymentOrder paymentOrder = retrievePaymentOrderStatusUseCase.retrievePaymentOrderStatus(paymentOrderId);
        
        // Map to REST DTO
        PaymentOrderStatusResponse response = mapper.toStatusResponse(paymentOrder);
        
        return ResponseEntity.ok(response);
    }
}
