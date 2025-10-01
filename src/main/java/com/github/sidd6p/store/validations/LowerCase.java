package com.github.sidd6p.store.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to ensure that a String value is in lowercase.
 *
 * @Target - Specifies where the annotation can be applied:
 * - METHOD: Can be used on methods
 * - FIELD: Can be used on class fields
 * - PARAMETER: Can be used on method parameters
 * - ANNOTATION_TYPE: Can be used on other annotations
 * @Retention(RetentionPolicy.RUNTIME) - Indicates this annotation will be available
 * at runtime through reflection
 * @Constraint - Specifies the validator class (LowerCaseValidator) that will perform
 * the actual validation logic
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LowerCaseValidator.class)
public @interface LowerCase {
    /**
     * Default error message if validation fails
     */
    String message() default "Value must be in lower case";

    /**
     * Allows for validation grouping - useful when you want to apply
     * different validation rules in different contexts
     */
    Class<?>[] groups() default {};

    /**
     * Can carry payload objects with validation metadata
     */
    Class<? extends Payload>[] payload() default {};
}
