package com.example.FlightBooking.Services.RegulationService;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirlineDTO;
import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.PlaneDTO;
import com.example.FlightBooking.DTOs.Request.RegulationDTO;
import com.example.FlightBooking.Models.Airlines;
import com.example.FlightBooking.Models.Planes;
import com.example.FlightBooking.Models.Regulation;
import com.example.FlightBooking.Repositories.AirlinesRepository;
import com.example.FlightBooking.Repositories.PlaneRepository;
import com.example.FlightBooking.Repositories.RegulationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegulationService {

    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AirlinesRepository airlineRepository;
    @Autowired
    private PlaneRepository planeRepository;

    @Transactional(readOnly = true)
    public List<AirlineDTO> getAllAirlinesWithRegulations() {
        List<Airlines> airlines = airlineRepository.findAll();
        return airlines.stream().map(this::convertToAirlineDTO).collect(Collectors.toList());
    }

    @Transactional
    public RegulationDTO addPricing(Long airlineId, double firstClassPrice, double businessPrice, double economyPrice) {
        Airlines airline = airlineRepository.findById(airlineId).orElseThrow(() -> new RuntimeException("Airline not found"));
        Regulation regulation = new Regulation();
        regulation.setAirline(airline);
        regulation.setFirstClassPrice(firstClassPrice);
        regulation.setBusinessPrice(businessPrice);
        regulation.setEconomyPrice(economyPrice);
        Regulation savedRegulation = regulationRepository.save(regulation);
        return convertToDTO(savedRegulation);
    }
    @Transactional(readOnly = true)
    public RegulationDTO getRegulationByPlaneId(Long planeId) {
        Planes plane = planeRepository.findById(planeId).orElseThrow(() -> new IllegalArgumentException("Invalid plane ID"));
        Airlines airline = airlineRepository.findByPlanes(plane).orElseThrow(() -> new IllegalArgumentException("Invalid airline"));
        Regulation regulation = regulationRepository.findByAirline(airline);
        return convertToDTO(regulation);
    }
    @Transactional
    public RegulationDTO getRegulationByAirlineId(Long airlineId) {
        Airlines airline = airlineRepository.findById(airlineId).orElseThrow(() -> new IllegalArgumentException("Invalid airline ID"));
        Regulation regulation = regulationRepository.findByAirline(airline);
        return convertToDTO(regulation);
    }
    @Transactional
    public List<RegulationDTO> getAllPricingByAirline(Long airlineId) {
        List<Regulation> regulations = regulationRepository.findByAirlineId(airlineId);
        return regulations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public RegulationDTO getPricingById(Long id) {
        Regulation regulation = regulationRepository.findById(id).orElseThrow(() -> new RuntimeException("Regulation not found"));
        return convertToDTO(regulation);
    }

    public RegulationDTO savePricing(Regulation pricing) {
        Regulation savedRegulation = regulationRepository.save(pricing);
        return convertToDTO(savedRegulation);
    }

    public void deletePricing(Long id) {
        regulationRepository.deleteById(id);
    }

    public RegulationDTO updatePricing(Long id, Double firstClassPrice, Double businessPrice, Double economyPrice) {
        Regulation regulation = regulationRepository.findById(id).orElseThrow(() -> new RuntimeException("Regulation not found"));
        if (firstClassPrice != null) {
            regulation.setFirstClassPrice(firstClassPrice);
        }
        if (businessPrice != null) {
            regulation.setBusinessPrice(businessPrice);
        }
        if (economyPrice != null) {
            regulation.setEconomyPrice(economyPrice);
        }
        Regulation updatedRegulation = regulationRepository.save(regulation);
        return convertToDTO(updatedRegulation);
    }

    private RegulationDTO convertToDTO(Regulation regulation) {
        RegulationDTO dto = new RegulationDTO();
        dto.setId(regulation.getId());
        dto.setFirstClassPrice(regulation.getFirstClassPrice());
        dto.setBusinessPrice(regulation.getBusinessPrice());
        dto.setEconomyPrice(regulation.getEconomyPrice());
        dto.setAirline(convertToAirlineDTO(regulation.getAirline()));
        return dto;
    }

    private AirlineDTO convertToAirlineDTO(Airlines airline) {
        AirlineDTO dto = new AirlineDTO();
        dto.setId(airline.getId());
        dto.setAirlineName(airline.getAirlineName());
        dto.setLogoUrl(airline.getLogoUrl());
        dto.setPromoForAirline(airline.getPromoForAirline());
        dto.setPlanes(airline.getPlanes().stream().map(this::convertToPlaneDTO).collect(Collectors.toList()));
        return dto;
    }

    private PlaneDTO convertToPlaneDTO(Planes plane) {
        PlaneDTO dto = new PlaneDTO();
        dto.setId(plane.getId());
        dto.setFlightNumber(plane.getFlightNumber());
        // other fields
        return dto;
    }
}
