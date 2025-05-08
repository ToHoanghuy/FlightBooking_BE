package com.example.FlightBooking.Services.AirportService;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirportRequest;
import com.example.FlightBooking.Models.Airports;
import com.example.FlightBooking.Repositories.AirportsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class AirportsService {
    @Autowired
    private AirportsRepository airportsRepository;

    public List<Airports> getAllAirports() {
        return airportsRepository.findAll();
    }

    public Optional<Airports> getAirportById(Long id) {
        return airportsRepository.findById(id);
    }

    public Airports createNewAirport (AirportRequest airportRequest)
    {
       Airports airports = new Airports();
       airports.setAirportName(airportRequest.getAirportName());
       airports.setCity(airportRequest.getCity());
       airports.setIataCode(airportRequest.getIataCode());
       return airportsRepository.save(airports);
    }

    public Airports updateAirport(Long id, Airports airportDetails) {
        Optional<Airports> optionalAirport = airportsRepository.findById(id);
        if (optionalAirport.isPresent()) {
            Airports airport = optionalAirport.get();
            airport.setAirportName(airportDetails.getAirportName());
            airport.setCity(airportDetails.getCity());
            airport.setIataCode(airportDetails.getIataCode());
            return airportsRepository.save(airport);
        } else {
            throw new RuntimeException("Airport not found");
        }
    }

    public boolean deleteAirport(Long id) {
        if (airportsRepository.existsById(id)) {
            airportsRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
