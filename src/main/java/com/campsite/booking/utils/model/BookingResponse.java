package com.campsite.booking.utils.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingResponse {
    private int referenceNumber;
    private int campsiteNumber;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String fullName;
    private String email;
}

