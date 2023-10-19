package com.example.demo.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoWhiteSpaceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoWhitespace {
    String message() default "Field cannot be only whitespace";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default{};
}
