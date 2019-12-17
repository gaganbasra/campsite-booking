package com.campsite.booking.service;

import com.campsite.booking.utils.model.BookingRequest;
import com.campsite.booking.utils.exception.ProviderException;
import com.campsite.booking.utils.model.AvailabilityRequest;
import com.campsite.booking.utils.model.Booking;
import com.campsite.booking.utils.model.BookingResponse;
import com.campsite.booking.repository.BookingRepository;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    @Setter(AccessLevel.PACKAGE)
    private BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Integer referenceId) {
        log.info("Getting booking with reference number: {}", referenceId);
        return Try.of(() -> bookingRepository.getOne(referenceId))
                .filter(Objects::nonNull)
                .map(this::createBookingResponse)
                .getOrElseThrow(e -> ProviderException.builder(String.format("Unable to find booking with reference number: %s", referenceId))
                        .build()
                );
    }

    @Override
    @Transactional
    public BookingResponse save(BookingRequest bookingRequest) {
        AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        availabilityRequest.setArrivalDate(bookingRequest.getArrivalDate());
        availabilityRequest.setDepartureDate(bookingRequest.getDepartureDate());
        List<LocalDate> availableDates = getAvailability(availabilityRequest);
        List<LocalDate> intendedDates = getDates(bookingRequest.getArrivalDate(), bookingRequest.getDepartureDate());

        if (availableDates.containsAll(intendedDates)) {
            Booking booking = createBooking(bookingRequest);
            return createBookingResponse(bookingRepository.save(booking));
        }
        throw ProviderException.builder("Intended booking dates are not available, please check dates availability").build();
    }

    @Override
    @Transactional
    public void deleteBookingById(Integer referenceId) {
        try {
            bookingRepository.deleteById(referenceId);
            log.info("Deleted booking with reference number: {}", referenceId);
        } catch (RuntimeException ex) {
            throw ProviderException.builder(String.format("Unable to find booking with reference number: %s", referenceId)).build();
        }
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(BookingRequest bookingRequest, Integer referenceId) {
        return Try.of(() -> bookingRepository.getOne(referenceId))
                .filter(Objects::nonNull)
                .map(booking -> {
                    createBooking(bookingRequest, booking);
                    return createBookingResponse(bookingRepository.save(booking));
                })
                .getOrElseThrow(e -> ProviderException.builder(String.format("Unable to find booking with reference number: %s", referenceId))
                        .build()
                );
    }

    @Override
    @Transactional(readOnly = true)
    @Lock(LockModeType.PESSIMISTIC_READ)
    public List<LocalDate> getAvailability(AvailabilityRequest availabilityRequest) {
        List<Booking> bookings = bookingRepository.findAll();
        List<LocalDate> bookedDays = Optional.ofNullable(bookings)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(c -> Stream.iterate(c.getArrivalDate(), date -> date.plusDays(1))
                        .limit(ChronoUnit.DAYS.between(c.getArrivalDate(), c.getDepartureDate()))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<LocalDate> totalDays = getDates(availabilityRequest.getArrivalDate(), availabilityRequest.getDepartureDate());

        totalDays.removeAll(bookedDays);
        return totalDays;
    }

    private BookingResponse createBookingResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setReferenceNumber(booking.getReferenceId());
        bookingResponse.setCampsiteNumber(booking.getCampsiteId());
        bookingResponse.setArrivalDate(booking.getArrivalDate());
        bookingResponse.setDepartureDate(booking.getDepartureDate());
        bookingResponse.setFullName(booking.getFullName());
        bookingResponse.setEmail(booking.getEmail());
        return bookingResponse;
    }

    private Booking createBooking(BookingRequest bookingRequest) {
        Booking booking = new Booking();
        return createBooking(bookingRequest, booking);
    }

    private Booking createBooking(BookingRequest bookingRequest, Booking booking) {
        booking.setCampsiteId(bookingRequest.getCampsiteId());
        booking.setArrivalDate(bookingRequest.getArrivalDate());
        booking.setDepartureDate(bookingRequest.getDepartureDate());
        booking.setFullName(bookingRequest.getFullName());
        booking.setEmail(bookingRequest.getEmail());
        return booking;
    }

    private List<LocalDate> getDates(LocalDate arrivalDate, LocalDate departureDate) {
        return Stream.iterate(arrivalDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(arrivalDate, departureDate) + 1)
                .collect(Collectors.toList());
    }
}
