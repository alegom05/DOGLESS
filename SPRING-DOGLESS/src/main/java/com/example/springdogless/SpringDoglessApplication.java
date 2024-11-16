package com.example.springdogless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringDoglessApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDoglessApplication.class, args);
    }

}
