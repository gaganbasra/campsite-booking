package com.campsite.booking.repository;

import com.campsite.booking.utils.model.Booking;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BookingRepositoryIT {

    private LocalDate todayDate;

    @Autowired
    private BookingRepository bookingRepository;

    @Before
    public void init() {
        todayDate = LocalDate.now();
    }

    @Test
    public void saveBooking_getBooking_validateSave() {
        Booking bookingRequest = createBookingRequest();
        Booking savedBooking = bookingRepository.save(bookingRequest);
        Booking booking = bookingRepository.getOne(savedBooking.getReferenceId());
        assertThat(booking).isEqualTo(savedBooking);

        bookingRepository.deleteById(savedBooking.getReferenceId());
        assertThat(bookingRepository.findAll()).isEmpty();
    }

    @Test
    public void saveMultipleBookings_validateAll() {
        Booking bookingRequest1 = createBookingRequest();
        Booking booking1 = bookingRepository.save(bookingRequest1);

        Booking bookingRequest2 = createBookingRequest();
        Booking booking2 = bookingRepository.save(bookingRequest2);

        List<Booking> bookings = bookingRepository.findAll();

        assertThat(bookings).size().isEqualTo(2);
        assertThat(bookings).containsExactlyInAnyOrder(booking1, booking2);

        bookingRepository.deleteAll();
        assertThat(bookingRepository.findAll()).isEmpty();
    }

    private Booking createBookingRequest() {
        Booking booking = new Booking();
        booking.setCampsiteId(1);
        booking.setArrivalDate(todayDate.plusDays(1));
        booking.setDepartureDate(todayDate.plusDays(2));
        booking.setFullName("test name");
        booking.setEmail("a@a.com");
        return booking;
    }
}
