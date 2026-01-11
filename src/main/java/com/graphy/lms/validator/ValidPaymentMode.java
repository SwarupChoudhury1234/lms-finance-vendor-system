package com.graphy.lms.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PaymentModeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPaymentMode {
    String message() default "Invalid payment mode";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}