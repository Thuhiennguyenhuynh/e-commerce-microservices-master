package com.rainbowforest.virtualassistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatService {
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    public String consultProduct(String userMessage, String context) {
        try {
            String systemPrompt = buildSystemPrompt(context);
            String response = callOpenAI(systemPrompt, userMessage);
            return response;
        } catch (Exception e) {
            return "Xin lỗi, tôi đang gặp vấn đề. Vui lòng thử lại sau.";
        }
    }
    
    private String buildSystemPrompt(String context) {
        return "Bạn là trợ lý ảo bán hàng giúp khách hàng tìm sản phẩm phù hợp. " +
               "Hãy lắng nghe nhu cầu của khách hàng và đề xuất sản phẩm tốt nhất. " +
               "Sử dụng tiếng Việt và luôn thân thiện. Ngữ cảnh: " + context;
    }
    
    private String callOpenAI(String systemPrompt, String userMessage) {
        // This is a simple implementation. In production, use proper OpenAI client
        // For now, return a friendly response
        return generateFriendlyResponse(userMessage);
    }
    
    private String generateFriendlyResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("gì") || lowerMessage.contains("tìm")) {
            return "Tôi rất sẵn lòng giúp bạn tìm sản phẩm phù hợp! Bạn đang tìm loại sản phẩm nào? " +
                   "Tôi có thể giúp bạn tìm điều gì đó về điện tử, quần áo, sách, hoặc thực phẩm.";
        } else if (lowerMessage.contains("giá")) {
            return "Chúng tôi có nhiều sản phẩm với các mức giá khác nhau. " +
                   "Hãy cho tôi biết ngân sách của bạn, tôi sẽ giúp bạn tìm sản phẩm phù hợp!";
        } else if (lowerMessage.contains("hãng") || lowerMessage.contains("nhãn")) {
            return "Chúng tôi cung cấp nhiều thương hiệu nổi tiếng. Bạn thích brand nào?";
        } else {
            return "Cảm ơn bạn đã đặt câu hỏi. Tôi có thể giúp bạn tìm sản phẩm, so sánh giá, " +
                   "hoặc trả lời các câu hỏi về sản phẩm. Bạn cần gì?";
        }
    }
    
    public List<String> getProductSuggestions(String category, String budget) {
        // Return suggested products based on category and budget
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Sản phẩm 1 - " + category + " - " + budget + "đ");
        suggestions.add("Sản phẩm 2 - " + category + " - " + budget + "đ");
        suggestions.add("Sản phẩm 3 - " + category + " - " + budget + "đ");
        return suggestions;
    }
}
