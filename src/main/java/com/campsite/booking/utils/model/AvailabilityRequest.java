package com.campsite.booking.utils.model;

import com.campsite.booking.utils.validation.ValidAvailability;
import com.campsite.booking.utils.validation.ValidDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@ValidAvailability(message = "{availability.date}")
public class AvailabilityRequest {
    private static final LocalDate LOCAL_DATE = LocalDate.now();

    @ValidDate(message = "{arrivalDate.range}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate = LOCAL_DATE.plusDays(1);
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate = LOCAL_DATE.plusDays(LOCAL_DATE.getMonth().maxLength());
}
