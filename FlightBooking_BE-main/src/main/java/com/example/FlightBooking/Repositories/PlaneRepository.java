package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.Planes;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Hidden
public interface PlaneRepository extends JpaRepository<Planes, Long> {
    boolean existsByFlightNumber(String flightNumber);
    List<Planes> findByAirlineId(Long airlineId);
}
