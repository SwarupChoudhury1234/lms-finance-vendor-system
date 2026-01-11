package com.graphy.lms.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class PaymentModeValidator implements ConstraintValidator<ValidPaymentMode, String> {
    
    private static final Set<String> VALID_MODES = Set.of(
        "CASH", "CHEQUE", "DD", "ONLINE", "CARD", "BANK_TRANSFER", "UPI"
    );
    
    @Override
    public void initialize(ValidPaymentMode constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Payment mode is required")
                   .addConstraintViolation();
            return false;
        }
        
        if (!VALID_MODES.contains(value.toUpperCase())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Invalid payment mode. Valid modes are: %s", 
                    String.join(", ", VALID_MODES)))
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}