package com.example.demo.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class SetImageValidator implements ConstraintValidator<ValidImage, Set<String>> {
    @Override
    public boolean isValid(Set<String> imageNames, ConstraintValidatorContext constraintValidatorContext) {
        return imageNames.stream().allMatch(imageName ->
                imageName.toLowerCase().endsWith(".jpg") ||
                        imageName.toLowerCase().endsWith(".png") ||
                        imageName.toLowerCase().endsWith(".jpeg"));
    }

}
