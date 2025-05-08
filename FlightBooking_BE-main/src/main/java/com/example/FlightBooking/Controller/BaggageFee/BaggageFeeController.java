package com.example.FlightBooking.Controller.BaggageFee;

import com.example.FlightBooking.Models.Decorator.BaggageFees;
import com.example.FlightBooking.Services.DecoratorService.BaggageFeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Baggage Fee Controller ", description = "Service for some flight")
@RequestMapping("/baggage-fee")
public class BaggageFeeController {
    @Autowired
    private BaggageFeeService baggageFeeService;

    @GetMapping("/get")
    public ResponseEntity<BaggageFees> getById(@RequestParam Long id)
    {
        return ResponseEntity.ok().body(baggageFeeService.getBaggageFeeById(id));
    }
//    @PostMapping("/create-new-baggage-fee")
//    public ResponseEntity<BaggageFees> createNewBaggageFee(@RequestParam BaggageFees baggageFees)
//    {
//
//    }
}
