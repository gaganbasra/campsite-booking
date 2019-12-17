package com.campsite.booking.utils.model;

import com.campsite.booking.utils.validation.ValidDate;
import com.campsite.booking.utils.validation.ValidDepartureDate;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@JsonRootName("bookingRequest")
@ValidDepartureDate(message = "{departureDate.range}")
public class BookingRequest {

    private static final int CAMPSITE_ID = 1; //as there is only one at the moment

    @NotNull(message = "{arrivalDate.notNull}")
    @ValidDate(message = "{arrivalDate.range}")
    private LocalDate arrivalDate;
    @NotNull(message = "{departureDate.notNull}")
    private LocalDate departureDate;
    @NotBlank(message = "{fullName}")
    private String fullName;
    @NotBlank(message = "{email.notNull}")
    @Email(message = "{email}")
    private String email;
    @NotNull
    @Min(1)
    private int campsiteId = CAMPSITE_ID;
}
