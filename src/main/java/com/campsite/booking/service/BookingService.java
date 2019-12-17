package com.campsite.booking.service;

import com.campsite.booking.utils.model.BookingRequest;
import com.campsite.booking.utils.model.AvailabilityRequest;
import com.campsite.booking.utils.model.BookingResponse;
import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponse getBookingById(Integer id);

    BookingResponse save(BookingRequest bookingRequest);

    List<LocalDate> getAvailability(AvailabilityRequest availabilityRequest);

    void deleteBookingById(Integer referenceId);

    BookingResponse updateBooking(BookingRequest bookingRequest, Integer referenceId);
}
