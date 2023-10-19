package com.example.demo.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoWhiteSpaceValidator implements ConstraintValidator<NoWhitespace,CharSequence> {
    @Override
    public void initialize(NoWhitespace constraintAnnotation) {
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return value != null && !value.toString().trim().isEmpty();
    }
}
