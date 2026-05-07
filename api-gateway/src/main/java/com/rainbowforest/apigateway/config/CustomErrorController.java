package com.rainbowforest.apigateway.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {
    
    // Phương thức này thay thế cái bị thiếu mà Zuul đang gọi sai
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    @ResponseBody
    public String handleError() {
        return "Error occurred";
    }
}
