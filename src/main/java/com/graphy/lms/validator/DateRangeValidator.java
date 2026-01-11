package com.graphy.lms.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {
    
    private String startField;
    private String endField;
    
    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            // Using reflection to get field values
            java.lang.reflect.Field startField = value.getClass().getDeclaredField(this.startField);
            java.lang.reflect.Field endField = value.getClass().getDeclaredField(this.endField);
            
            startField.setAccessible(true);
            endField.setAccessible(true);
            
            LocalDate startDate = (LocalDate) startField.get(value);
            LocalDate endDate = (LocalDate) endField.get(value);
            
            if (startDate == null || endDate == null) {
                return true; // Let @NotNull handle null values
            }
            
            if (startDate.isAfter(endDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Start date must be before or equal to end date")
                       .addPropertyNode(this.startField)
                       .addConstraintViolation();
                return false;
            }
            
            // Check if date range is not too large (max 1 year)
            if (startDate.plusYears(1).isBefore(endDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Date range cannot exceed 1 year")
                       .addPropertyNode(this.endField)
                       .addConstraintViolation();
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
}