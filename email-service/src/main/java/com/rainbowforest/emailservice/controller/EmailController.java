package com.rainbowforest.emailservice.controller;

import com.rainbowforest.emailservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/payment-confirmation")
    public ResponseEntity<String> sendPaymentConfirmation(@RequestBody Map<String, String> request) {
        try {
            emailService.sendPaymentConfirmation(
                request.get("email"),
                request.get("customerName"),
                request.get("orderId"),
                request.get("amount")
            );
            return ResponseEntity.ok("Payment confirmation email sent");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
    
    @PostMapping("/password-reset")
    public ResponseEntity<String> sendPasswordReset(@RequestBody Map<String, String> request) {
        try {
            emailService.sendPasswordResetEmail(
                request.get("email"),
                request.get("resetLink")
            );
            return ResponseEntity.ok("Password reset email sent");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
    
    @PostMapping("/order-status")
    public ResponseEntity<String> sendOrderStatus(@RequestBody Map<String, String> request) {
        try {
            emailService.sendOrderStatusUpdate(
                request.get("email"),
                request.get("orderId"),
                request.get("status")
            );
            return ResponseEntity.ok("Order status email sent");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
    
    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcome(@RequestBody Map<String, String> request) {
        try {
            emailService.sendWelcomeEmail(
                request.get("email"),
                request.get("customerName")
            );
            return ResponseEntity.ok("Welcome email sent");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
}
