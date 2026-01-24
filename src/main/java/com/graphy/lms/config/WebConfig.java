package com.graphy.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the "uploads" folder to the URL "/screenshots/**"
        String uploadPath = Paths.get("./uploads/screenshots").toFile().getAbsolutePath();
        registry.addResourceHandler("/screenshots/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}