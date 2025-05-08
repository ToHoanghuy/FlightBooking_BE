package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.Airlines;

import com.example.FlightBooking.Models.Planes;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Hidden
public interface AirlinesRepository extends JpaRepository<Airlines, Long> {
    Optional<Airlines> findByAirlineName(String airlineName);
    Optional<Airlines> findByPlanes(Planes planes);
}
