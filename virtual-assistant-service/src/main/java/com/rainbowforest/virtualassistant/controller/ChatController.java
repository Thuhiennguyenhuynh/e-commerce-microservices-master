package com.rainbowforest.virtualassistant.controller;

import com.rainbowforest.virtualassistant.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String context = request.getOrDefault("context", "Tìm sản phẩm mua sắm");
        
        String response = chatService.consultProduct(userMessage, context);
        
        return ResponseEntity.ok(Map.of(
            "message", response,
            "timestamp", System.currentTimeMillis() + ""
        ));
    }
    
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSuggestions(
            @RequestParam String category,
            @RequestParam String budget) {
        return ResponseEntity.ok(Map.of(
            "category", category,
            "budget", budget,
            "suggestions", chatService.getProductSuggestions(category, budget)
        ));
    }
}
