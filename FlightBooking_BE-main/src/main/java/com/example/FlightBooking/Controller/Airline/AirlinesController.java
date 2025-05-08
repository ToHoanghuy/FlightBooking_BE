package com.example.FlightBooking.Controller.Airline;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirlineDTO;
import com.example.FlightBooking.DTOs.Response.Airline.AirlineResponse;
import com.example.FlightBooking.Models.Airlines;
import com.example.FlightBooking.Repositories.AirlinesRepository;
import com.example.FlightBooking.Services.AirlineService.AirlinesService;

import com.example.FlightBooking.Services.Planes.PlaneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Tag(name ="CRUD FOR AIRLINE", description = "apis for changing AIRLINE info")
@RequestMapping("/airlines")
public class AirlinesController {
    @Autowired
    private AirlinesService airlinesService;
    @Autowired
    private AirlinesRepository airlinesRepository;

    @GetMapping
    @Transactional
    public List<AirlineResponse> getAllAirlines() {
        List<AirlineDTO> airlinesList = airlinesService.getAllAirlines();
        return airlinesList.stream().map(this::convertToAirlineResponse).collect(Collectors.toList());
    }
    private AirlineResponse convertToAirlineResponse(AirlineDTO airlines) {
        AirlineResponse response = new AirlineResponse();
        response.setId(airlines.getId());
        response.setAirlineName(airlines.getAirlineName());
        response.setLogoUrl(airlines.getLogoUrl());
        response.setPicture(airlines.getPromoForAirline());
        return response;
    }

    @GetMapping("/{id}")
    @Transactional
    public AirlineResponse getAirlinesById(@RequestParam Long id) {
        AirlineDTO airlines = airlinesService.getAirlinesById(id);
        return convertToAirlineResponse(airlines);
    }

    @PostMapping
    public AirlineDTO addAirlines(@RequestBody AirlineDTO airlines) {
        return airlinesService.addAirlines(airlines);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAirlines(@PathVariable Long id) {
        airlinesService.deleteAirlines(id);
        return ResponseEntity.noContent().build();
    }

    @Autowired
    private PlaneService planeService;

    @PostMapping(value = "/upload-new-airline", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AirlineDTO> createNewAirline(
            @RequestParam String airlineName,
            @RequestPart MultipartFile logoFile,
            @RequestPart List<MultipartFile> promoFiles,
            @RequestParam double firstClassPrice,
            @RequestParam double businessPrice,
            @RequestParam double economyPrice) throws IOException {
        AirlineDTO newAirlines = airlinesService.createNewAirline(airlineName, logoFile, promoFiles, firstClassPrice, businessPrice, economyPrice);
        return ResponseEntity.ok(newAirlines);
    }
    @GetMapping("/get-airline-by-planeId")
    @Transactional
    public ResponseEntity<AirlineResponse> getAirlineByPlaneId(@RequestParam Long planeId)
    {
        return ResponseEntity.ok(convertToAirlineResponse(airlinesService.getAirlineByPlaneId(planeId)));
    }
}
