package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.Booking;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByFlightId(Long flightId);
    List<Booking> findAllByUserId(Long userId);
}
