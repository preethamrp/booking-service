package com.maersk.bookingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidSizeValidator implements ConstraintValidator<ValidSize, Integer> {

    @Override
    public void initialize(ValidSize constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == 20 || value == 40;
    }
}
