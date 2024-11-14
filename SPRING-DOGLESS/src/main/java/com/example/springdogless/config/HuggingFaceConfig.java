package com.example.springdogless.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "huggingface")
public class HuggingFaceConfig {
    private String apiKey;
    private String model = "mistralai/Mistral-7B-Instruct-v0.2"; // Modelo gratuito de alto rendimiento
}