package com.campsite.booking.service;

import com.campsite.booking.utils.exception.ProviderException;
import com.campsite.booking.utils.model.AvailabilityRequest;
import com.campsite.booking.utils.model.Booking;
import com.campsite.booking.utils.model.BookingRequest;
import com.campsite.booking.utils.model.BookingResponse;
import com.campsite.booking.repository.BookingRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Before
    public void setUp() {
        bookingService = new BookingServiceImpl();
        bookingRepository = Mockito.mock(BookingRepository.class);
        bookingService.setBookingRepository(bookingRepository);
    }

    @Test(expected = ProviderException.class)
    public void getBooking() {
        when(bookingRepository.getOne(anyInt())).thenReturn(null);
        bookingService.getBookingById(1);
        verify(bookingRepository, never()).getOne(anyInt());
    }

    @Test
    public void getBookingById() {
        when(bookingRepository.getOne(anyInt())).thenReturn(createBooking());
        BookingResponse response = bookingService.getBookingById(1);
        assertThat(response).isNotNull();
        assertThat(response.getReferenceNumber()).isEqualTo(1);
        assertThat(response.getCampsiteNumber()).isEqualTo(1);
        verify(bookingRepository, atLeastOnce()).getOne(anyInt());
    }

    @Test
    public void saveBooking() {
        when(bookingRepository.save(any())).thenReturn(createBooking());
        BookingResponse bookingResponse = bookingService.save(createBookingRequest());
        assertThat(bookingResponse).isNotNull();
        verify(bookingRepository, atLeastOnce()).save(any());
    }

    @Test(expected = ProviderException.class)
    public void saveBooking_datesNotAvailable() {
        when(bookingRepository.save(any())).thenReturn(createBooking());
        Booking booking = new Booking();
        booking.setArrivalDate(LocalDate.now().plusDays(2));
        booking.setDepartureDate(LocalDate.now().plusDays(3));
        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(booking));
        bookingService.save(createBookingRequest());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void deleteBooking() {
        bookingService.deleteBookingById(1);
        verify(bookingRepository, atLeastOnce()).deleteById(anyInt());
    }

    @Test
    public void updateBooking() {
        when(bookingRepository.getOne(anyInt())).thenReturn(createBooking());
        when(bookingRepository.save(any())).thenReturn(createBooking());
        bookingService.updateBooking(createBookingRequest(), 1);
        verify(bookingRepository, atLeastOnce()).getOne(anyInt());
        verify(bookingRepository, atLeastOnce()).save(any());
    }

    @Test(expected = ProviderException.class)
    public void updateBooking_bookingNotExist() {
        when(bookingRepository.getOne(anyInt())).thenReturn(null);
        bookingService.updateBooking(createBookingRequest(), 1);
        verify(bookingRepository, atLeastOnce()).getOne(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void getAvailability() {
        Booking booking = new Booking();
        booking.setArrivalDate(LocalDate.now().plusDays(2));
        booking.setDepartureDate(LocalDate.now().plusDays(6));
        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(booking));
        List<LocalDate> localDates = bookingService.getAvailability(new AvailabilityRequest());
        assertThat(localDates).isNotEmpty();
        assertThat(localDates).size().isEqualTo(27);
        assertThat(localDates).doesNotContain(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), LocalDate.now().plusDays(5));
        verify(bookingRepository, atLeastOnce()).findAll();
    }

    private BookingRequest createBookingRequest() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setArrivalDate(LocalDate.now().plusDays(1));
        bookingRequest.setDepartureDate(LocalDate.now().plusDays(3));
        bookingRequest.setFullName("test name");
        bookingRequest.setEmail("a@a.com");
        return bookingRequest;
    }

    private Booking createBooking() {
        Booking booking = new Booking();
        booking.setCampsiteId(1);
        booking.setReferenceId(1);
        booking.setArrivalDate(LocalDate.now());
        booking.setDepartureDate(LocalDate.now().plusDays(1));
        booking.setFullName("name");
        booking.setEmail("email@email.com");
        return booking;
    }
}
