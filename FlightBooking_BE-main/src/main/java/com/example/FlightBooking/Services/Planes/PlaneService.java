package com.example.FlightBooking.Services.Planes;

import com.example.FlightBooking.Components.FactoryMethod.BusinessClassSeatFactory;
import com.example.FlightBooking.Components.FactoryMethod.EconomyClassSeatFactory;
import com.example.FlightBooking.Components.FactoryMethod.FirstClassSeatFactory;
import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.PlaneDTO;
import com.example.FlightBooking.DTOs.Request.Booking.BookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.Booking.SelectSeatDTO;
import com.example.FlightBooking.Enum.SeatStatus;
import com.example.FlightBooking.Models.*;
import com.example.FlightBooking.Repositories.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaneService {

    @Autowired
    private PlaneRepository planeRepository;

    @Autowired
    private AirlinesRepository airlineRepository;
    @Transactional
    public PlaneDTO createPlaneWithSeats(Long airlineId) throws Exception {
        Airlines airline = getAirlineById(airlineId);
        String flightNumber = generateUniqueFlightNumber(airline);
        Planes plane = new Planes();
        plane.setFlightNumber(flightNumber);
        plane.setAirline(airline);
        airline.addPlane(plane);
        Planes savedPlane = planeRepository.save(plane);
        return convertToDTO(savedPlane );
    }
    @Transactional
    private String generateUniqueFlightNumber(Airlines airline) {
        String flightNumber;
        Random random = new Random();
        do {
            flightNumber = generateFlightNumber(airline, random);
        } while (planeRepository.existsByFlightNumber(flightNumber));
        return flightNumber;
    }
    @Transactional
    private String generateFlightNumber(Airlines airline, Random random) {
        int number = 100 + random.nextInt(900);
        switch (airline.getAirlineName()) {
            case "Vietnam Airlines":
                return "VN-" + number;
            case "VietJet Air":
                return "VJ-" + number;
            case "Bamboo Airways":
                return "BAV-" + number;
            case "Jetstar Airlines":
                return "JST-" + number;
            default:
                return "UNDEFINED";
        }
    }
    @Transactional
    public Airlines getAirlineById(Long airlineId) {
        return airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline not found with id " + airlineId));
    }
    @Transactional
    public PlaneDTO getDetailPlane(Long planeId) {
        Planes plane = planeRepository.findById(planeId)
                .orElseThrow(() -> new RuntimeException("Plane not found with id " + planeId));
        return convertToDTO(plane);
    }
    @Transactional
    public List<PlaneDTO> getAllPlanesByAirlineId(Long airlineId) {
        List<Planes> planes = planeRepository.findByAirlineId(airlineId);
        return planes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    @Transactional
    private PlaneDTO convertToDTO(Planes plane) {
        PlaneDTO dto = new PlaneDTO();
        dto.setId(plane.getId());
        dto.setFlightNumber(plane.getFlightNumber());
        return dto;
    }
    @Transactional
    public void deletePlane(Long planeId) {
        Planes plane = planeRepository.findById(planeId)
                .orElseThrow(() -> new RuntimeException("Plane not found with id " + planeId));
        
        // Xóa tham chiếu từ airline đến plane
        Airlines airline = plane.getAirline();
        if (airline != null) {
            airline.removePlane(plane);
        }
        
        // Xóa plane từ database
        planeRepository.deleteById(planeId);
    }
}
