package ru.nicholas.smarttracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Разрешаем запросы к API
            .allowedOrigins("*")  // Или конкретный домен фронтенда, например "http://localhost"
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")  // Разрешаем фронтенду читать заголовок
            .allowCredentials(false);  // Для простоты (если нет кук)
    }
}