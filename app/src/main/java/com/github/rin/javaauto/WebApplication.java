package com.github.rin.javaauto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApplication {

    public static void start(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
