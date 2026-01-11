package com.graphy.lms.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class AmountValidator implements ConstraintValidator<ValidAmount, BigDecimal> {
    
    @Override
    public void initialize(ValidAmount constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        
        // Amount must be positive
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Amount must be greater than zero")
                   .addConstraintViolation();
            return false;
        }
        
        // Amount must have max 2 decimal places
        if (value.scale() > 2) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Amount cannot have more than 2 decimal places")
                   .addConstraintViolation();
            return false;
        }
        
        // Amount must not exceed max value (10 crore)
        if (value.compareTo(new BigDecimal("100000000")) > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Amount cannot exceed ₹10,00,00,000")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}