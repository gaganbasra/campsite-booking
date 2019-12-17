package com.campsite.booking.utils.validation;

import com.campsite.booking.utils.model.BookingRequest;
import org.springframework.util.ObjectUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class DepartureDateValidator implements ConstraintValidator<ValidDepartureDate, BookingRequest> {
    @Override
    public boolean isValid(BookingRequest booking, ConstraintValidatorContext context) {
        if (Objects.nonNull(booking.getArrivalDate())) {
            return (!ObjectUtils.isEmpty(booking.getDepartureDate())) && booking.getDepartureDate().isAfter(booking.getArrivalDate()) && booking.getDepartureDate().isBefore(booking.getArrivalDate().plusDays(4));
        }
        return true;
    }
}
