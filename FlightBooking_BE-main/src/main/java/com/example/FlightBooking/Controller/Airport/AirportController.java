package com.example.FlightBooking.Controller.Airport;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirportRequest;
import com.example.FlightBooking.Models.Airports;
import com.example.FlightBooking.Services.AirportService.AirportsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/airports")
@Tag(name = "Airport CRUD", description = "APIs for create, read, update, delete airport")
public class AirportController {
    @Autowired
    private AirportsService airportsService;

    @GetMapping
    public List<Airports> getAllAirports() {
        return airportsService.getAllAirports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airports> getAirportById(@PathVariable Long id) {
        Optional<Airports> airport = airportsService.getAirportById(id);
        return airport.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping ("/create-new-airport")
    public  ResponseEntity<Airports> addAirport(@RequestBody AirportRequest airportRequest) {
        Airports airports = airportsService.createNewAirport(airportRequest);
        return new ResponseEntity<>(airports, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Airports> updateAirport(@PathVariable Long id, @RequestBody Airports airportDetails) {
        try {
            Airports updatedAirport = airportsService.updateAirport(id, airportDetails);
            return ResponseEntity.ok(updatedAirport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAirport(@PathVariable Long id) {
        boolean isDeleted = airportsService.deleteAirport(id);
        if (isDeleted) {
            return ResponseEntity.ok("Delete successful");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
