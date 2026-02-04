package com.graphy.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // This handles ONLY image loading. No CORS here (SecurityConfig handles CORS).
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("./uploads/screenshots").toFile().getAbsolutePath();
        
        registry.addResourceHandler("/screenshots/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}