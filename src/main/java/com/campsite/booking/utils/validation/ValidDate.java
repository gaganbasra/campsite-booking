package com.campsite.booking.utils.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {DateRangeValidator.class})
public @interface ValidDate {
    String message() default "{invalid date range}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
