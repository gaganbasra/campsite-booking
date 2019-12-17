package com.campsite.booking.utils.model;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", nullable = false)
    private int referenceId;
    @Column(name = "campsite_id", nullable = false)
    private int campsiteId;
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "email", nullable = false)
    private String email;
}
