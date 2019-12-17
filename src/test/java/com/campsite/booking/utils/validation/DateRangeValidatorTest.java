package com.campsite.booking.utils.validation;

import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

public class DateRangeValidatorTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator;

    @Before
    public void setup() {
        validator = factory.getValidator();
    }

    @Test
    public void isValid() {
        Testee testee = new Testee(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<Testee>> violations = validator.validate(testee);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void isNotValid_dateIsToday() {
        Testee testee = new Testee(LocalDate.now());
        Set<ConstraintViolation<Testee>> violations = validator.validate(testee);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void isNotValid_dateIsAfter1Month() {
        Testee testee = new Testee(LocalDate.now().plusDays(32));
        Set<ConstraintViolation<Testee>> violations = validator.validate(testee);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void isNotValid_dateIsNull() {
        Testee testee = new Testee(null);
        Set<ConstraintViolation<Testee>> violations = validator.validate(testee);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Getter
    private static class Testee {
        @ValidDate
        private final LocalDate localDate;

        Testee(LocalDate localDate) {
            this.localDate = localDate;
        }
    }
}
