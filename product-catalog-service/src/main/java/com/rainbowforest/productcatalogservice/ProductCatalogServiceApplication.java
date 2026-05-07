// package com.rainbowforest.productcatalogservice;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @SpringBootApplication
// @EnableEurekaClient
// @EnableJpaRepositories
// public class ProductCatalogServiceApplication {
//     public static void main(String[] args) {
//         SpringApplication.run(ProductCatalogServiceApplication.class, args);
//     }
// }


package com.rainbowforest.productcatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.CommandLineRunner; // <--- CẦN THÊM
import org.springframework.context.ApplicationContext; // <--- CẦN THÊM
import org.springframework.beans.factory.annotation.Autowired; // <--- CẦN THÊM

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories
public class ProductCatalogServiceApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        boolean hasController = context.containsBean("adminCategoryController");
        System.out.println("### IS CONTROLLER LOADED: " + hasController);
    }
}
