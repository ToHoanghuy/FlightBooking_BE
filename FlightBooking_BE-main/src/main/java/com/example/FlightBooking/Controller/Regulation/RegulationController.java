package com.example.FlightBooking.Controller.Regulation;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirlineDTO;
import com.example.FlightBooking.DTOs.Request.RegulationDTO;
import com.example.FlightBooking.Models.Regulation;
import com.example.FlightBooking.Services.RegulationService.RegulationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/regulations")
@Tag(name = "Regulations Controller ", description = "Regulation for tickets price")
public class RegulationController {

    @Autowired
    private RegulationService regulationService;

    @GetMapping("/all/{airlineId}")
    public ResponseEntity<List<RegulationDTO>> getAllPricingByAirline(@PathVariable Long airlineId) {
        List<RegulationDTO> pricing = regulationService.getAllPricingByAirline(airlineId);
        return ResponseEntity.ok(pricing);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegulationDTO> getPricingById(@PathVariable Long id) {
        RegulationDTO pricing = regulationService.getPricingById(id);
        return pricing != null ? ResponseEntity.ok(pricing) : ResponseEntity.notFound().build();
    }
    @GetMapping("/byAirline/{airlineId}")
    public ResponseEntity<RegulationDTO> getRegulationByAirlineId(@PathVariable Long airlineId) {
        RegulationDTO regulation = regulationService.getRegulationByAirlineId(airlineId);
        return regulation != null ? ResponseEntity.ok(regulation) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<RegulationDTO> savePricing(@RequestBody Regulation pricing) {
        RegulationDTO savedPricing = regulationService.savePricing(pricing);
        return ResponseEntity.ok(savedPricing);
    }

    @PutMapping("/updatePrice/{id}")
    public ResponseEntity<RegulationDTO> updatePricing(@PathVariable Long id,
                                                       @RequestParam(required = false) Double firstClassPrice,
                                                       @RequestParam(required = false) Double businessPrice,
                                                       @RequestParam(required = false) Double economyPrice) {
        RegulationDTO updatedPricing = regulationService.updatePricing(id, firstClassPrice, businessPrice, economyPrice);
        return updatedPricing != null ? ResponseEntity.ok(updatedPricing) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricing(@PathVariable Long id) {
        regulationService.deletePricing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllWithRegulations")
    public ResponseEntity<List<AirlineDTO>> getAllAirlinesWithRegulations() {
        List<AirlineDTO> airlines = regulationService.getAllAirlinesWithRegulations();
        return ResponseEntity.ok(airlines);
    }

}
