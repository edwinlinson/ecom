package com.example.demo.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;

public class ImageFieldValidator implements ConstraintValidator<ValidImage,String> {
    @Override
    public void initialize(ValidImage constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && (value.toLowerCase().endsWith(".jpg")||
                value.toLowerCase().endsWith(".png")||
                value.toLowerCase().endsWith("jpeg"));
    }
}
