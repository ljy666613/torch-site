package com.example.torchwebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(value = "com.example")
@EnableScheduling
public class TorchWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(TorchWebsiteApplication.class, args);
    }

}
