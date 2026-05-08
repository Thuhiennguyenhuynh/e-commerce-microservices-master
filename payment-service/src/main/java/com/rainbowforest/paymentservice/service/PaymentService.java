package com.rainbowforest.paymentservice.service;

import com.rainbowforest.paymentservice.entity.Payment;
import com.rainbowforest.paymentservice.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Value("${stripe.api.key}")
    private String stripeApiKey;
    
    public PaymentService(@Value("${stripe.api.key}") String stripeApiKey) {
        Stripe.apiKey = stripeApiKey;
    }
    
    public Payment createPayment(Payment payment) {
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setStatus("PENDING");
        return paymentRepository.save(payment);
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
    
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    public Payment processStripePayment(Long paymentId, String stripeToken) throws StripeException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Convert cents
        long amountInCents = payment.getAmount().multiply(new BigDecimal(100)).longValue();
        
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amountInCents);
        chargeParams.put("currency", payment.getCurrency().toLowerCase());
        chargeParams.put("source", stripeToken);
        chargeParams.put("description", "Payment for Order #" + payment.getOrderId());
        
        Charge charge = Charge.create(chargeParams);
        
        payment.setTransactionId(charge.getId());
        payment.setStatus(charge.getPaid() ? "COMPLETED" : "FAILED");
        payment.setGatewayResponse(charge.toJson().toString());
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    public Payment refundPayment(Long paymentId) throws StripeException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (!payment.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        Charge charge = Charge.retrieve(payment.getTransactionId());
        charge.refund();
        
        payment.setStatus("REFUNDED");
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }
}
