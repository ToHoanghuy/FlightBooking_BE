package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.PopularPlace;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Hidden
@Repository
public interface PopularPlaceRepository extends JpaRepository<PopularPlace, Long> {
    Optional<PopularPlace> findByFlightId(Long flightId);
}
