package com.rainbowforest.virtualassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class VirtualAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualAssistantApplication.class, args);
    }

}
