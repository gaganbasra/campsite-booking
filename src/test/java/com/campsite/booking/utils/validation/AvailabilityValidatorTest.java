package com.campsite.booking.utils.validation;

import com.campsite.booking.utils.model.AvailabilityRequest;
import org.junit.Before;
import org.junit.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

public class AvailabilityValidatorTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator;

    @Before
    public void setup() {
        validator = factory.getValidator();
    }

    @Test
    public void isValid() {
        AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        Set<ConstraintViolation<AvailabilityRequest>> violations = validator.validate(availabilityRequest);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void isNotValid_arrivalDateIsInPast() {
        AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        availabilityRequest.setArrivalDate(LocalDate.now().minusDays(1));
        Set<ConstraintViolation<AvailabilityRequest>> violations = validator.validate(availabilityRequest);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void isNotValid_departureDateIsBeforeArrivalDate() {
        AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        availabilityRequest.setArrivalDate(LocalDate.now().plusDays(2));
        availabilityRequest.setDepartureDate(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<AvailabilityRequest>> violations = validator.validate(availabilityRequest);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void isNotValid_departureDateIsMoreThan1MonthInAdvance() {
        AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        availabilityRequest.setArrivalDate(LocalDate.now().plusDays(1));
        availabilityRequest.setDepartureDate(LocalDate.now().plusDays(32));
        Set<ConstraintViolation<AvailabilityRequest>> violations = validator.validate(availabilityRequest);
        assertThat(violations.size()).isEqualTo(1);
    }
}
