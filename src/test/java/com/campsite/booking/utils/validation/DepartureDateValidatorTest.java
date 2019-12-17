package com.campsite.booking.utils.validation;

import com.campsite.booking.utils.model.BookingRequest;
import org.junit.Before;
import org.junit.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

public class DepartureDateValidatorTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator;

    @Before
    public void setup() {
        validator = factory.getValidator();
    }

    @Test
    public void isValid() {
        BookingRequest bookingRequest = createBookingRequest();
        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(bookingRequest);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void isNotValid_arrivalDateIsMoreThan3Days() {
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setDepartureDate(LocalDate.now().plusDays(6));
        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(bookingRequest);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void isNotValid_isEmpty() {
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setDepartureDate(null);
        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(bookingRequest);
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    public void isNotValid_departureDateIsInPast() {
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setDepartureDate(LocalDate.now().minusDays(3));
        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(bookingRequest);
        assertThat(violations.size()).isEqualTo(1);
    }

    private BookingRequest createBookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setArrivalDate(LocalDate.now().plusDays(1));
        request.setDepartureDate(LocalDate.now().plusDays(2));
        request.setFullName("test name");
        request.setEmail("a@a.com");
        return request;
    }
}
