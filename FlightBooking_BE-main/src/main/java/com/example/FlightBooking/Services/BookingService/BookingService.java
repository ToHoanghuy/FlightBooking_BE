package com.example.FlightBooking.Services.BookingService;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirlineRequest;
import com.example.FlightBooking.DTOs.Request.Booking.BookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.Passenger.PassengerDTO;
import com.example.FlightBooking.DTOs.Response.Ticket.TicketDetailResponse;
import com.example.FlightBooking.DTOs.Response.Ticket.TicketResponse;
import com.example.FlightBooking.Enum.SeatStatus;
import com.example.FlightBooking.Mapper.PassengerMapper;
import com.example.FlightBooking.Models.*;
import com.example.FlightBooking.Repositories.*;
import com.example.FlightBooking.Services.AuthJWT.JwtService;
import com.example.FlightBooking.Services.PaymentService.PaymentService;
import com.example.FlightBooking.Services.Planes.PlaneService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(PlaneService.class);
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PassengerRepository passengerRepository;


    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaneRepository planeRepository;
    @Autowired
    private AirlinesRepository airlinesRepository;
    @Autowired
    private AirportsRepository airportsRepository;

    private final PaymentService paymentService;
    private final FlightRepository flightRepository;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Autowired
    public BookingService(FlightRepository flightRepository, ObjectMapper objectMapper, @Lazy PaymentService paymentService) {
        this.flightRepository = flightRepository;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }
    //Giu cho truoc khi dat ve
    @Transactional
    public boolean holdSeats(Long flightId, Set<String> seatNumbers) throws Exception {
        logger.info("Attempting to hold seats for flightId: {}", flightId);
        Flights flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        String seatStatusesJson = flight.getSeatStatuses();
        Map<String, Map<String, String>> seatStatuses = objectMapper.readValue(seatStatusesJson, Map.class);

        for (String seatNumber : seatNumbers) {
            logger.debug("Checking seat: {}", seatNumber);
            if (!seatStatuses.containsKey(seatNumber)) {
                logger.warn("Seat {} not found", seatNumber);
                return false;
            }
            String status = seatStatuses.get(seatNumber).get("status");
            if (!SeatStatus.AVAILABLE.name().equals(status)) {
                logger.warn("Seat {} is not available, current status: {}", seatNumber, status);
                return false;
            }
        }

        for (String seatNumber : seatNumbers) {
            logger.info("Holding seat: {}", seatNumber);
            seatStatuses.get(seatNumber).put("status", SeatStatus.ON_HOLD.name());
        }

        seatStatusesJson = objectMapper.writeValueAsString(seatStatuses);
        flight.setSeatStatuses(seatStatusesJson);
        flightRepository.save(flight);

        scheduler.schedule(() -> {
            try {
                releaseSeats(flightId, seatNumbers);
            } catch (Exception e) {
                logger.error("Error releasing seats: ", e);
            }
        }, 5, TimeUnit.MINUTES);

        return true;
    }

    @Transactional
    public void releaseSeats(Long flightId, Set<String> seatNumbers) throws Exception {
        logger.info("Releasing seats for flightId: {}", flightId);
        Flights flight = flightRepository.findById(flightId).orElseThrow(() -> new RuntimeException("Flight not found"));
        String seatStatusesJson = flight.getSeatStatuses();
        Map<String, Map<String, String>> seatStatuses = objectMapper.readValue(seatStatusesJson, Map.class);

        for (String seatNumber : seatNumbers) {
            logger.debug("Releasing seat: {}", seatNumber);
            if (SeatStatus.ON_HOLD.name().equals(seatStatuses.get(seatNumber).get("status"))) {
                seatStatuses.get(seatNumber).put("status", SeatStatus.AVAILABLE.name());
                seatStatuses.get(seatNumber).remove("userId");
            }
        }

        seatStatusesJson = objectMapper.writeValueAsString(seatStatuses);
        flight.setSeatStatuses(seatStatusesJson);
        flightRepository.save(flight);
        logger.info("Seats released successfully for flightId: {}", flightId);
    }

    @Transactional
    public boolean bookSeats(Long flightId, Set<String> seatNumbers) throws Exception {
        Flights flight = flightRepository.findById(flightId).orElseThrow(() -> new RuntimeException("Flight not found"));
        String seatStatusesJson = flight.getSeatStatuses();
        Map<String, Map<String, String>> seatStatuses = objectMapper.readValue(seatStatusesJson, Map.class);

        for (String seatNumber : seatNumbers) {
            String status = seatStatuses.get(seatNumber).get("status");
            if (!status.equals("ON_HOLD")) {
                return false;
            }
        }
        for (String seatNumber : seatNumbers) {
            seatStatuses.get(seatNumber).put("status", "BOOKED");
        }

        flight.setSeatStatuses(objectMapper.writeValueAsString(seatStatuses));
        flightRepository.save(flight);
        return true;
    }

    public double calculateTotalPriceAfter(Long flightId, Set<String> seatNumbers) throws Exception {
        Flights flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        double totalPrice = 0;
        String seatStatusesJson = flight.getSeatStatuses();
        JsonNode seatStatusesNode = objectMapper.readTree(seatStatusesJson);
        for (String seatNumber : seatNumbers) {
            JsonNode seatNode = seatStatusesNode.get(seatNumber);
            if (seatNode == null) {
                throw new RuntimeException("Seat not found: " + seatNumber);
            }
            String seatClass = seatNode.get("class").asText();
            switch (seatClass) {
                case "ECONOMY":
                    totalPrice += flight.getEconomyPrice();
                    break;
                case "BUSINESS":
                    totalPrice += flight.getBusinessPrice();
                    break;
                case "FIRST_CLASS":
                    totalPrice += flight.getFirstClassPrice();
                    break;
                default:
                    throw new RuntimeException("Invalid seat class: " + seatClass);
            }
        }

        return totalPrice;
    }
    public double getSeatPrice(Long flightId, String seatNumber) throws Exception {
        Flights flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        double totalPrice = 0;
        String seatStatusesJson = flight.getSeatStatuses();
        JsonNode seatStatusesNode = objectMapper.readTree(seatStatusesJson);
        JsonNode seatNode = seatStatusesNode.get(seatNumber);
        if (seatNode == null) {
            throw new RuntimeException("Seat not found: " + seatNumber);
        }
        String seatClass = seatNode.get("class").asText();
        switch (seatClass) {
            case "ECONOMY":
                totalPrice += flight.getEconomyPrice();
                break;
            case "BUSINESS":
                totalPrice += flight.getBusinessPrice();
                break;
            case "FIRST_CLASS":
                totalPrice += flight.getFirstClassPrice();
                break;
            default:
                throw new RuntimeException("Invalid seat class: " + seatClass);
        }
        return totalPrice;
    }

    public List<TicketResponse> getAllTicketByUserId(Long userId)
    {
        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        if (bookings.isEmpty()) {
            throw new RuntimeException("No bookings found with user-id: " + userId);
        }
        List<TicketResponse> ticketResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            List<Passengers> passengersList = passengerRepository.findByBooking(booking);
            Flights flights = flightRepository.findById(booking.getFlightId()).orElseThrow(() -> new RuntimeException("Flight not found with id: " + booking.getFlightId()));
            Planes planes = planeRepository.findById(flights.getPlaneId()).orElseThrow(() -> new RuntimeException("Plane not found with id: " + flights.getPlaneId()));
            Airlines airlines = airlinesRepository.findByPlanes(planes).orElseThrow(() -> new RuntimeException("Airlin not found with id: " + planes));
            Airports departureAirport = airportsRepository.findById(flights.getDepartureAirportId()).orElseThrow(() -> new RuntimeException("Airport not found with id: " + flights.getDepartureAirportId()));
            Airports arrivalAirport = airportsRepository.findById(flights.getArrivalAirportId()).orElseThrow(() -> new RuntimeException("Airport not found with id: " + flights.getArrivalAirportId()));
            List<String> seatNumbers = new ArrayList<>();
            for (Passengers passengers : passengersList) {
                seatNumbers.add(passengers.getSeatNumber());
            }

            TicketResponse ticketResponse = new TicketResponse();
            ticketResponse.setBookingId(booking.getId());
            ticketResponse.setAirlineLogo(airlines.getLogoUrl());
            ticketResponse.setDepartAirport(departureAirport.getAirportName());
            ticketResponse.setArrivalAirport(arrivalAirport.getAirportName());
            ticketResponse.setDepartDate(flights.getDepartureDate());
            ticketResponse.setArrivalDate(flights.getArrivalDate());
            ticketResponse.setIataCodeDepart(departureAirport.getIataCode());
            ticketResponse.setIataCodeArrival(arrivalAirport.getIataCode());
            ticketResponse.setSeatNumber(seatNumbers);
            ticketResponses.add(ticketResponse);
        }
        return ticketResponses;
    }
    @Transactional
    public TicketDetailResponse getTicketDetailByBookingId(Long bookingId)
    {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        Flights flights = flightRepository.findById(booking.getFlightId()).orElseThrow(() -> new RuntimeException("Flight not found with id: " + booking.getFlightId()));
        Planes planes = planeRepository.findById(flights.getPlaneId()).orElseThrow(() -> new RuntimeException("Plane not found with id: " + flights.getPlaneId()));
        Airlines airlines = airlinesRepository.findByPlanes(planes).orElseThrow(() -> new RuntimeException("Airlin not found with id: " + planes));
        Airports departureAirport = airportsRepository.findById(flights.getDepartureAirportId()).orElseThrow(() -> new RuntimeException("Airport not found with id: " + flights.getDepartureAirportId()));
        Airports arrivalAirport = airportsRepository.findById(flights.getArrivalAirportId()).orElseThrow(() -> new RuntimeException("Airport not found with id: " + flights.getArrivalAirportId()));
        TicketDetailResponse ticketDetailResponse = new TicketDetailResponse();
        ticketDetailResponse.setFlightId(flights.getId());
        ticketDetailResponse.setBookerFullName(booking.getBookerFullName());
        ticketDetailResponse.setBookerEmail(booking.getBookerEmail());
        ticketDetailResponse.setBookerPhoneNumber(booking.getBookerPhoneNumber());
        ticketDetailResponse.setPlaneNumber(planes.getFlightNumber());
        ticketDetailResponse.setDepartureAirportName(departureAirport.getAirportName());
        ticketDetailResponse.setIataDepartureCode(departureAirport.getIataCode());
        ticketDetailResponse.setDepartureDate(flights.getDepartureDate());
        ticketDetailResponse.setArrivalDate(flights.getArrivalDate());
        ticketDetailResponse.setIataArrivalCode(arrivalAirport.getIataCode());
        ticketDetailResponse.setArrivalAirportName(arrivalAirport.getAirportName());
        ticketDetailResponse.setAirlineLogoUrl(airlines.getLogoUrl());
        ticketDetailResponse.setAirlineName(airlines.getAirlineName());
        List<PassengerDTO> passengerDTOList = booking.getPassengers().stream()
                .map(PassengerMapper::toDTO)
                .collect(Collectors.toList());
        ticketDetailResponse.setPassengerDTOList(passengerDTOList);
        return ticketDetailResponse;
    }
}
