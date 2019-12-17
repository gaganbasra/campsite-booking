package com.campsite.booking.controller;

import com.campsite.booking.utils.BookingConstants;
import com.campsite.booking.utils.model.BookingRequest;
import com.campsite.booking.utils.model.AvailabilityRequest;
import com.campsite.booking.utils.model.BookingResponse;
import com.campsite.booking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = BookingConstants.ENDPOINT_PATH)
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/{referenceId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Integer referenceId) {
        BookingResponse bookingResponse = bookingService.getBookingById(referenceId);
        return new ResponseEntity<>(bookingResponse, HttpStatus.OK);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingRequest bookingRequest) {
        BookingResponse bookingResponse = bookingService.save(bookingRequest);
        return new ResponseEntity<>(bookingResponse, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{referenceId}")
    public ResponseEntity cancelBooking(@PathVariable Integer referenceId) {
        bookingService.deleteBookingById(referenceId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/{referenceId}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Integer referenceId, @RequestBody @Valid BookingRequest bookingRequest) {
        BookingResponse bookingResponse = bookingService.updateBooking(bookingRequest, referenceId);
        return new ResponseEntity<>(bookingResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping(BookingConstants.AVAILABILITY_ENDPOINT)
    public ResponseEntity<List<LocalDate>> getAvailability(@Valid AvailabilityRequest availabilityRequest) {
        List<LocalDate> availabilities = bookingService.getAvailability(availabilityRequest);
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }
}
