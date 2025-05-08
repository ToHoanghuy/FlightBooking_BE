package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Enum.Roles;
import com.example.FlightBooking.Models.Flights;
import com.example.FlightBooking.Models.Users;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource
@Hidden
public interface FlightRepository extends JpaRepository<Flights, Long> {

    Optional<Flights> findById (Long id);
    @Query("SELECT f FROM Flights f WHERE f.departureAirportId = :departureAirportId AND f.arrivalAirportId = :arrivalAirportId AND CAST(f.departureDate AS date) = CAST(:departureDate AS date)")
    List<Flights> searchFlightOneWay(
            @Param("departureAirportId") Long departureAirportId,
            @Param("arrivalAirportId") Long arrivalAirportId,
            @Param("departureDate") Timestamp departureDate
    );
    @Query("SELECT f FROM Flights f WHERE f.departureAirportId = :departureAirportId AND f.arrivalAirportId = :arrivalAirportId AND CAST(f.departureDate AS date) BETWEEN CAST(:departureStartDate AS date) AND CAST(:departureEndDate AS date)")
    List<Flights> searchFlightRoundTrip(
            @Param("departureAirportId") Long departureAirportId,
            @Param("arrivalAirportId") Long arrivalAirportId,
            @Param("departureStartDate") Timestamp departureStartDate,
            @Param("departureEndDate") Timestamp departureEndDate
    );

    @Query("SELECT f FROM Flights f WHERE f.planeId = :planeId")
    List<Flights> findAllByPlaneId(@Param("planeId") Long planeId);

    @Query("SELECT f FROM Flights f WHERE f.planeId = :planeId AND " +
            "(:departureDate BETWEEN f.departureDate AND f.arrivalDate OR " +
            ":arrivalDate BETWEEN f.departureDate AND f.arrivalDate OR " +
            "(f.departureDate BETWEEN :departureDate AND :arrivalDate) OR " +
            "(f.arrivalDate BETWEEN :departureDate AND :arrivalDate))")
    List<Flights> findConflictingFlights(@Param("planeId") Long planeId,
                                         @Param("departureDate") Timestamp departureDate,
                                         @Param("arrivalDate") Timestamp arrivalDate);

}

