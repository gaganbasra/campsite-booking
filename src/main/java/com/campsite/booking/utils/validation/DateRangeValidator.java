package com.campsite.booking.utils.validation;

import org.springframework.util.ObjectUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate todayDate = LocalDate.now();
        return (!ObjectUtils.isEmpty(value)) && (value.isAfter(todayDate) && value.isBefore(todayDate.plusDays(todayDate.getMonth().maxLength()).plusDays(1)));
    }
}
