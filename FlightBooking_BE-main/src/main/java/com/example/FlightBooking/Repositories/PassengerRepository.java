package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.Booking;
import com.example.FlightBooking.Models.Passengers;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface PassengerRepository extends JpaRepository<Passengers, Long> {
    List<Passengers> findByBooking(Booking booking);
}
