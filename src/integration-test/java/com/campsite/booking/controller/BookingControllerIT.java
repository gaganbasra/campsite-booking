package com.campsite.booking.controller;

import com.campsite.booking.utils.BookingConstants;
import com.campsite.booking.utils.model.BookingRequest;
import com.campsite.booking.utils.model.BookingResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingControllerIT {
    private static final LocalDate TODAY_DATE = LocalDate.now();

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAvailabilities_defaultOneMonth_getValidResponse() {
        URI uri = UriComponentsBuilder.fromPath(String.format("%s/%s", BookingConstants.ENDPOINT_PATH, BookingConstants.AVAILABILITY_ENDPOINT))
                .build()
                .encode()
                .toUri();

        ResponseEntity<LocalDate[]> response = restTemplate.getForEntity(uri, LocalDate[].class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(31);
    }

    @Test
    public void getAvailabilities_withDates_getValidResponse() {
        URI uri = UriComponentsBuilder.fromPath(String.format("%s/%s", BookingConstants.ENDPOINT_PATH, BookingConstants.AVAILABILITY_ENDPOINT))
                .queryParam("arrivalDate", TODAY_DATE.plusDays(3))
                .queryParam("departureDate", TODAY_DATE.plusDays(5))
                .build()
                .encode()
                .toUri();

        ResponseEntity<LocalDate[]> response = restTemplate.getForEntity(uri, LocalDate[].class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getBody()[0]).isEqualTo(TODAY_DATE.plusDays(3));
        assertThat(response.getBody()[1]).isEqualTo(TODAY_DATE.plusDays(4));
        assertThat(response.getBody()[2]).isEqualTo(TODAY_DATE.plusDays(5));
    }

    @Test
    public void getBooking_nonExiting_getValidResponseMessage() {
        URI uri = UriComponentsBuilder.fromPath(String.format("%s/%s", BookingConstants.ENDPOINT_PATH, 78945613))
                .build()
                .encode()
                .toUri();

        ResponseEntity<String[]> response = restTemplate.getForEntity(uri, String[].class);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(1);
        assertThat(response.getBody()[0]).isEqualTo("Unable to find booking with reference number: 78945613");
    }

    @Test
    public void saveBooking_allCRUDOperations_getValidResponses() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        ResponseEntity<BookingResponse> response = restTemplate.postForEntity(uri, bookingRequest, BookingResponse.class);
        BookingResponse bookingResponse = response.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(bookingResponse).isNotNull();
        assertThat(bookingResponse.getCampsiteNumber()).isEqualTo(1);
        assertThat(bookingResponse.getReferenceNumber()).isNotNull();
        assertThat(bookingResponse.getArrivalDate()).isEqualTo(bookingRequest.getArrivalDate());
        assertThat(bookingResponse.getDepartureDate()).isEqualTo(bookingRequest.getDepartureDate());
        assertThat(bookingResponse.getFullName()).isEqualTo(bookingRequest.getFullName());
        assertThat(bookingResponse.getEmail()).isEqualTo(bookingRequest.getEmail());

        //get for same booking reference number
        response = getBookingResponseEntity(bookingResponse.getReferenceNumber());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCampsiteNumber()).isEqualTo(1);
        assertThat(response.getBody().getReferenceNumber()).isEqualTo(bookingResponse.getReferenceNumber());
        assertThat(response.getBody().getArrivalDate()).isEqualTo(bookingRequest.getArrivalDate());
        assertThat(response.getBody().getDepartureDate()).isEqualTo(bookingRequest.getDepartureDate());
        assertThat(response.getBody().getFullName()).isEqualTo(bookingRequest.getFullName());
        assertThat(response.getBody().getEmail()).isEqualTo(bookingRequest.getEmail());

        //update for same booking reference number
        uri = UriComponentsBuilder.fromPath(String.format("%s/%s", BookingConstants.ENDPOINT_PATH, bookingResponse.getReferenceNumber()))
                .build()
                .encode()
                .toUri();
        bookingRequest.setDepartureDate(TODAY_DATE.plusDays(2));
        restTemplate.put(uri, bookingRequest);
        response = getBookingResponseEntity(bookingResponse.getReferenceNumber());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        bookingResponse = response.getBody();
        assertThat(bookingResponse).isNotNull();
        assertThat(bookingResponse.getDepartureDate()).isEqualTo(TODAY_DATE.plusDays(2));

        //delete for same booking reference number
        deleteBooking(bookingResponse.getReferenceNumber());

        //get to assure the exception errors
        uri = UriComponentsBuilder.fromPath(String.format("%s/%s", BookingConstants.ENDPOINT_PATH, bookingResponse.getReferenceNumber()))
                .build()
                .encode()
                .toUri();
        ResponseEntity<String[]> responseError = restTemplate.getForEntity(uri, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(1);
        assertThat(responseError.getBody()[0]).isEqualTo("Unable to find booking with reference number: 1");
    }

    @Test
    public void saveBooking_nullArrivalDate_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setArrivalDate(null);
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(2);
        assertThat(responseError.getBody()).containsExactlyInAnyOrder("Invalid arrivalDate: must not be null or empty",
                "Invalid arrivalDate: must be minimum 1 day ahead of arrival and must be only 1 month in advance");
    }

    @Test
    public void saveBooking_todayArrivalDate_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setArrivalDate(TODAY_DATE);
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(1);
        assertThat(responseError.getBody()[0]).isEqualTo("Invalid arrivalDate: must be minimum 1 day ahead of arrival and must be only 1 month in advance");
    }

    @Test
    public void saveBooking_monthAfterArrivalDate_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setArrivalDate(TODAY_DATE.plusDays(32));
        bookingRequest.setDepartureDate(TODAY_DATE.plusDays(33));
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(1);
        assertThat(responseError.getBody()[0]).isEqualTo("Invalid arrivalDate: must be minimum 1 day ahead of arrival and must be only 1 month in advance");
    }

    @Test
    public void saveBooking_nullDepartureDate_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setDepartureDate(null);
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(2);
        assertThat(responseError.getBody()).containsExactlyInAnyOrder("Invalid departureDate: must not be null or empty",
                "Invalid departureDate: departure date must not be before arrival date and campsite must be reserved for max 3 days");
    }

    @Test
    public void saveBooking_pastDepartureDate_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setDepartureDate(TODAY_DATE);
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(1);
        assertThat(responseError.getBody()[0]).isEqualTo("Invalid departureDate: departure date must not be before arrival date and campsite must be reserved for max 3 days");
    }

    @Test
    public void saveBooking_moreThanThreeDaysDepartureDate_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setDepartureDate(TODAY_DATE.plusDays(5));
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(1);
        assertThat(responseError.getBody()[0]).isEqualTo("Invalid departureDate: departure date must not be before arrival date and campsite must be reserved for max 3 days");
    }

    @Test
    public void saveBooking_nullFullName_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setFullName(null);
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(1);
        assertThat(responseError.getBody()[0]).isEqualTo("Invalid fullName: must not be null or blank");
    }

    @Test
    public void saveBooking_nullEmail_getValidErrorMessages() {
        URI uri = UriComponentsBuilder.fromPath(BookingConstants.ENDPOINT_PATH)
                .build()
                .encode()
                .toUri();
        BookingRequest bookingRequest = createBookingRequest();
        bookingRequest.setEmail(null);
        ResponseEntity<String[]> responseError = restTemplate.postForEntity(uri, bookingRequest, String[].class);
        assertThat(responseError).isNotNull();
        assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseError.getBody()).isNotNull();
        assertThat(responseError.getBody().length).isEqualTo(1);
        assertThat(responseError.getBody()[0]).isEqualTo("Invalid email: must not be null or blank");
    }

    private ResponseEntity<BookingResponse> getBookingResponseEntity(int referenceNumber) {
        URI uri = UriComponentsBuilder.fromPath(String.format("%s/%s", BookingConstants.ENDPOINT_PATH, referenceNumber))
                .build()
                .encode()
                .toUri();
        return restTemplate.getForEntity(uri, BookingResponse.class);
    }

    private void deleteBooking(int referenceNumber) {
        URI uri = UriComponentsBuilder.fromPath(String.format("%s/%s", BookingConstants.ENDPOINT_PATH, referenceNumber))
                .build()
                .encode()
                .toUri();
        restTemplate.delete(uri);
    }

    private BookingRequest createBookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setArrivalDate(TODAY_DATE.plusDays(1));
        request.setDepartureDate(TODAY_DATE.plusDays(2));
        request.setFullName("test name");
        request.setEmail("a@a.com");
        return request;
    }

}
