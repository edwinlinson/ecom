package com.example.demo.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ImageFieldValidator.class,SetImageValidator.class })
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "Inavlid image file.Supported extensions are .jpg. jpeg and .png";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
