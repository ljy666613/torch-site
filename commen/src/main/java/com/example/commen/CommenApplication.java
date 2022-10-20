package com.example.commen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.example")
public class CommenApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommenApplication.class, args);
    }

}
