package com.graphy.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LmsFinanceVendorSystemApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LmsFinanceVendorSystemApplication.class, args);
        System.out.println("\n=========================================");
        System.out.println("Finanace Management System Started Successfully!");
    }
}