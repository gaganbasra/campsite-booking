package com.campsite.booking.utils.validation;

import com.campsite.booking.utils.model.AvailabilityRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

public class AvailabilityValidator implements ConstraintValidator<ValidAvailability, AvailabilityRequest> {
    @Override
    public boolean isValid(AvailabilityRequest availabilityRequest, ConstraintValidatorContext context) {
        if (Objects.nonNull(availabilityRequest.getArrivalDate())) {
            LocalDate todayDate = LocalDate.now();
            return availabilityRequest.getDepartureDate().isAfter(availabilityRequest.getArrivalDate())
                    && availabilityRequest.getDepartureDate().isBefore(todayDate.plusDays(todayDate.getMonth().maxLength()).plusDays(1));
        }
        return true;
    }
}
