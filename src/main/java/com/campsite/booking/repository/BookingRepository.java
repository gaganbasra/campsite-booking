package com.campsite.booking.repository;

import com.campsite.booking.utils.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, Integer> {

}
