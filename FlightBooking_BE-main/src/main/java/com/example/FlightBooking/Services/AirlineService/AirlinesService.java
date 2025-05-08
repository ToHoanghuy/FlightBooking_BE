package com.example.FlightBooking.Services.AirlineService;
import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirlineDTO;
import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirlineRequest;
import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.PlaneDTO;
import com.example.FlightBooking.Models.Airlines;
import com.example.FlightBooking.Models.Planes;
import com.example.FlightBooking.Models.Regulation;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.AirlinesRepository;

import com.example.FlightBooking.Repositories.PlaneRepository;
import com.example.FlightBooking.Repositories.RegulationRepository;
import com.example.FlightBooking.Services.CloudinaryService.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AirlinesService {
    private static final Logger logger = LoggerFactory.getLogger(AirlinesService.class);
    
    @Autowired
    private AirlinesRepository airlineRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private RegulationRepository regulationRepository;

    @Autowired
    private PlaneRepository planeRepository;
    @Transactional
    public List<AirlineDTO> getAllAirlines() {
        List<Airlines> airlines = airlineRepository.findAll();
        return airlines.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    @Transactional
    public AirlineDTO getAirlinesById(Long id) {
        Airlines airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline not found with id " + id));
        return convertToDTO(airline);
    }
    @Transactional
    public AirlineDTO addAirlines(AirlineDTO airline) {
        Airlines airlines = new Airlines();
        airlines.setAirlineName(airline.getAirlineName());
        airlines.setLogoUrl(airline.getLogoUrl());
        airlines.setPromoForAirline(airline.getPromoForAirline());
        airlines.setPlanes(new ArrayList<>());
        Airlines savedAirline = airlineRepository.save(airlines);
        return convertToDTO(savedAirline);
    }
    @Transactional
    public AirlineDTO updateAirlines(Long id, List<MultipartFile> files) throws IOException {
        List<String> promo = cloudinaryService.uploadAirlinePromo(files);
        Airlines airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline not found with id " + id));
        airline.setPromoForAirline(promo);
        Airlines updatedAirline = airlineRepository.save(airline);
        return convertToDTO(updatedAirline);
    }
    @Transactional
    public void deleteAirlines(Long id) {
        // Kiểm tra airline có tồn tại không
        Airlines airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline not found with id " + id));
        
        // Tìm và xóa regulation liên quan trước
        Regulation regulation = regulationRepository.findByAirline(airline);
        if (regulation != null) {
            logger.info("Deleting regulation with id: {} for airline: {}", regulation.getId(), id);
            regulationRepository.delete(regulation);
        }
        
        // Sau đó mới xóa airline
        logger.info("Deleting airline with id: {}", id);
        airlineRepository.deleteById(id);
    }
    @Transactional
    public AirlineDTO createNewAirline(String airlineName, MultipartFile file, List<MultipartFile> files,
                                       double firstClassPrice, double businessPrice, double economyPrice) throws IOException {
        String logoUrl = cloudinaryService.uploadAirlineLogo(file);
        List<String> promoUrls = cloudinaryService.uploadAirlinePromo(files);
        Airlines airline = new Airlines();
        airline.setAirlineName(airlineName);
        airline.setLogoUrl(logoUrl);
        airline.setPromoForAirline(promoUrls);

        Airlines savedAirline = airlineRepository.save(airline);

        Regulation regulation = new Regulation();
        regulation.setAirline(savedAirline);
        regulation.setFirstClassPrice(firstClassPrice);
        regulation.setBusinessPrice(businessPrice);
        regulation.setEconomyPrice(economyPrice);

        regulationRepository.save(regulation);

        return convertToDTO(savedAirline);
    }
    @Transactional
    public AirlineDTO getAirlineByPlaneId(Long planeId) {
        Planes plane = planeRepository.findById(planeId)
                .orElseThrow(() -> new RuntimeException("Plane not found with id " + planeId));
        return convertToDTO(plane.getAirline());
    }
    @Transactional
    private AirlineDTO convertToDTO(Airlines airline) {
        AirlineDTO dto = new AirlineDTO();
        dto.setId(airline.getId());
        dto.setAirlineName(airline.getAirlineName());
        dto.setLogoUrl(airline.getLogoUrl());
        dto.setPromoForAirline(airline.getPromoForAirline());
        List<PlaneDTO> planeDTOs = airline.getPlanes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dto.setPlanes(planeDTOs);
        return dto;
    }
    @Transactional
    private PlaneDTO convertToDTO(Planes plane) {
        PlaneDTO dto = new PlaneDTO();
        dto.setId(plane.getId());
        dto.setFlightNumber(plane.getFlightNumber());
        return dto;
    }
}
