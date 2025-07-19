package com.github.sidd6p.store.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom validator class that implements ConstraintValidator interface to validate strings are in lowercase.
 * ConstraintValidator<A, T> where:
 * - A (LowerCase): The annotation type this validator is for
 * - T (String): The type of the object that will be validated
 */
public class LowerCaseValidator implements ConstraintValidator<LowerCase, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        return value != null && !value.isEmpty() && value.equals(value.toLowerCase());
    }
}
