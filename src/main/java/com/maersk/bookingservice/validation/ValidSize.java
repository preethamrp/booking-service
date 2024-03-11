package com.maersk.bookingservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ValidSizeValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSize {

    String message() default "Invalid container size. Valid values are 20 or 40";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
