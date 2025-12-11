package com.ouedkniss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.context.annotation.PropertySource;

;

@SpringBootApplication(scanBasePackages = "com.ouedkniss")
@PropertySource("classpath:application.properties")
public class OuedknissApplication {
    public static void main(String[] args) {
        SpringApplication.run(OuedknissApplication.class, args);
    }

}



