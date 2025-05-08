package com.example.FlightBooking.Controller.Airline;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.PlaneDTO;
import com.example.FlightBooking.DTOs.Response.Airline.AllPlanesResponse;
import com.example.FlightBooking.DTOs.Response.Airline.PlaneResponse;
import com.example.FlightBooking.Models.Airlines;
import com.example.FlightBooking.Models.Planes;
import com.example.FlightBooking.Repositories.PlaneRepository;
import com.example.FlightBooking.Services.Planes.PlaneService;
import com.stripe.model.Plan;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/airlines")
@Tag(name ="CRUD FOR AIRLINE", description = "apis for changing AIRLINE info")
public class PlaneController {
    @Autowired
    private PlaneService planeService;
    @Autowired
    private PlaneRepository planeRepository;

    @GetMapping("/get-all-plane")
    public List<AllPlanesResponse> getAll()
    {
        List<Planes>  planes =  planeRepository.findAll();
        List<AllPlanesResponse> allPlanesResponseList = new ArrayList<>();
        for(Planes plane : planes)
        {
            AllPlanesResponse allPlanesResponse = new AllPlanesResponse();
            allPlanesResponse.setId(plane.getId());
            allPlanesResponse.setAirlineId(plane.getAirline().getId());
            allPlanesResponse.setFlightNumber(plane.getFlightNumber());
            allPlanesResponseList.add(allPlanesResponse);
        }
        return allPlanesResponseList;
    }
    @PostMapping("/create-new-plane")
    public ResponseEntity<?> createNewPlane(@RequestParam Long airlineId) {
        try {
            PlaneDTO plane = planeService.createPlaneWithSeats(airlineId);
            return ResponseEntity.ok(plane);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating plane: " + e.getMessage());
        }
    }

    @GetMapping("/get-plane-detail-by-planeId")
    public ResponseEntity<?> getDetailPlane(@RequestParam Long planeId)
    {
        try {
            PlaneDTO planes = planeService.getDetailPlane(planeId);
            return ResponseEntity.ok(planes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting Plane detail: " + e.getMessage());
        }
    }
    @GetMapping("/get-plane-number")
    public ResponseEntity<?> getPlaneNumber(@RequestParam Long planeId)
    {
        try {
            PlaneDTO planes = planeService.getDetailPlane(planeId);
            PlaneResponse planeResponse = new PlaneResponse();
            planeResponse.setFlightNumber(planes.getFlightNumber());
            planeResponse.setId(planes.getId());
            return ResponseEntity.ok(planeResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting Plane detail: " + e.getMessage());
        }
    }
    @GetMapping("/get-all-plane-by-airline-id")
    public ResponseEntity<?> getAllPlane(@RequestParam Long airlineId) {
        try {
            List<PlaneDTO> planes = planeService.getAllPlanesByAirlineId(airlineId);
            List<PlaneResponse> planeResponses = planes.stream().map(this::convertToPlaneResponse).collect(Collectors.toList());
            return ResponseEntity.ok(planeResponses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting Plane detail: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete-plane/{id}")
    public ResponseEntity<?> deletePlane(@PathVariable Long id) {
        try {
            planeService.deletePlane(id);
            return ResponseEntity.ok().body(Map.of("message", "Plane deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error deleting plane: " + e.getMessage()));
        }
    }
    private PlaneResponse convertToPlaneResponse(PlaneDTO planes) {
        PlaneResponse response = new PlaneResponse();
        response.setId(planes.getId());
        response.setFlightNumber(planes.getFlightNumber());
        return response;
    }
}
