package com.rainbowforest.paymentservice.repository;

import com.rainbowforest.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByStatus(String status);
    
    List<Payment> findByUserIdAndStatus(Long userId, String status);
    
}
