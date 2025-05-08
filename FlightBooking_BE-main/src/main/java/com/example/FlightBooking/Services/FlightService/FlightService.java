package com.example.FlightBooking.Services.FlightService;

import com.example.FlightBooking.Components.FactoryMethod.*;
import com.example.FlightBooking.Components.Strategy.FlightSearchContext;
import com.example.FlightBooking.Components.Strategy.OneWayFlightSearchStrategy;
import com.example.FlightBooking.Components.Strategy.RoundTripFlightSearchStrategy;
import com.example.FlightBooking.Components.TemplateMethod.FlightCancelEmailSender;
import com.example.FlightBooking.Components.TemplateMethod.FlightDelayEmailSender;
import com.example.FlightBooking.Components.TemplateMethod.FlightScheduleEmailSender;
import com.example.FlightBooking.DTOs.Request.Flight.FlightDTORequest;
import com.example.FlightBooking.DTOs.Response.Flight.FlightDTOResponse;
import com.example.FlightBooking.DTOs.Request.RegulationDTO;
import com.example.FlightBooking.Enum.FlightStatus;
import com.example.FlightBooking.Models.*;
import com.example.FlightBooking.Repositories.*;
import com.example.FlightBooking.Services.PaymentService.PaymentService;
import com.example.FlightBooking.Services.Planes.PlaneService;
import com.example.FlightBooking.Services.RegulationService.RegulationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightService {
    private static final Logger logger = LoggerFactory.getLogger(PlaneService.class);
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private FlightDelayEmailSender flightDelayEmailSender;
    @Autowired
    private FlightCancelEmailSender flightCancelEmailSender;
    @Autowired
    private FlightScheduleEmailSender flightScheduleEmailSender; // Component mới cho việc gửi email
    @Autowired
    private RegulationService regulationService;
    @Autowired
    private AirlinesRepository airlinesRepository;
    @Autowired
    private PlaneRepository planeRepository;

    @Transactional
    public Flights createFlight(FlightDTORequest flightDTORequest) throws JsonProcessingException {
        // Validate flight data
        validateFlightData(flightDTORequest);
        Flights flight = new Flights();
        Map<String, Map<String, String>> seatStatuses = new HashMap<>();
        // Using factory to manage seats in a flight
        SeatCreator firstClassSeatCreator = new FirstClassSeatCreator();
        SeatCreator businessClassSeatCreator = new BusinessClassSeatCreator();
        SeatCreator economyClassSeatCreator = new EconomyClassSeatCreator();

        seatStatuses.putAll(firstClassSeatCreator.generateSeats(flight));
        seatStatuses.putAll(businessClassSeatCreator.generateSeats(flight));
        seatStatuses.putAll(economyClassSeatCreator.generateSeats(flight));

        String seatStatusesJson = objectMapper.writeValueAsString(seatStatuses);

        flight.setFlightStatus(flightDTORequest.getFlightStatus());
        flight.setDepartureDate(flightDTORequest.getDepartureDate());
        flight.setArrivalDate(flightDTORequest.getArrivalDate());
        flight.setDuration(flightDTORequest.getDuration());
        flight.setDepartureAirportId(flightDTORequest.getDepartureAirportId());
        flight.setArrivalAirportId(flightDTORequest.getArrivalAirportId());
        flight.setPlaneId(flightDTORequest.getPlaneId());

        // Get ticket prices from Regulation
        Planes planes = planeRepository.findById(flightDTORequest.getPlaneId()).orElseThrow(() -> new IllegalArgumentException("Invalid plane ID"));
        Airlines airlines = airlinesRepository.findByPlanes(planes).orElseThrow(() -> new IllegalArgumentException("Invalid plane"));
        RegulationDTO regulation = regulationService.getRegulationByPlaneId(airlines.getId());
        flight.setEconomyPrice(regulation.getEconomyPrice());
        flight.setBusinessPrice(regulation.getBusinessPrice());
        flight.setFirstClassPrice(regulation.getFirstClassPrice());

        flight.setSeatStatuses(seatStatusesJson);

        // Check for minimum 1-hour gap between departure and arrival
        if (flight.getArrivalDate().getTime() - flight.getDepartureDate().getTime() < 3600000) {
            throw new IllegalArgumentException("Departure and arrival time must be at least 1 hour apart.");
        }

        // Check for 10-hour maintenance gap between flights
        validateFlightIntervals(flightDTORequest);

        return flightRepository.save(flight);
    }

    @org.springframework.transaction.annotation.Transactional
    public List<Flights> searchFlights(String type, Long departureAirportId, Long arrivalAirportId, Timestamp departureDate, Timestamp returnDate) {
        FlightSearchContext context = new FlightSearchContext();

        if (type.equalsIgnoreCase("ONE_WAY")) {
            context.setStrategy(new OneWayFlightSearchStrategy(flightRepository));
        } else if (type.equalsIgnoreCase("ROUND_TRIP")) {
            context.setStrategy(new RoundTripFlightSearchStrategy(flightRepository));
        }
        return context.searchFlights(departureAirportId, arrivalAirportId, departureDate, returnDate);
    }
    public double calculateTotalPrice(Long flightId, int numberOfTickets, String ticketType, boolean isRoundTrip) {
        Flights flight = flightRepository.findById(flightId).orElseThrow(() -> new IllegalArgumentException("Invalid flight ID"));
        double ticketPrice;
        switch (ticketType) {
            case "ECONOMY":
                ticketPrice = flight.getEconomyPrice();
                break;
            case "BUSINESS":
                ticketPrice = flight.getBusinessPrice();
                break;
            case "FIRST_CLASS":
                ticketPrice = flight.getFirstClassPrice();
                break;
            default:
                throw new IllegalArgumentException("Invalid ticket type: " + ticketType);
        }

        int multiplier = isRoundTrip ? 2 : 1;
        return numberOfTickets * ticketPrice * multiplier;
    }
//    public void uploadFlightData(MultipartFile file, Long planeId) throws IOException {
//        List<Flights> flights = parseExcelFile(file, planeId);
//        flightRepository.saveAll(flights);
//    }

//    private List<FlightDTORequest> parseExcelFile(MultipartFile file, Long planeId) throws IOException {
//        List<Flights> flightsList = new ArrayList<>();
//        Workbook workbook = new XSSFWorkbook(file.getInputStream());
//        Sheet sheet = workbook.getSheetAt(0);
//        Iterator<Row> rows = sheet.iterator();
//        rows.next(); // Skip header row
//
//        while (rows.hasNext()) {
//            Row currentRow = rows.next();
//            Flights flight = new Flights();
//            flight.setFlightStatus(currentRow.getCell(0).getStringCellValue());
//            flight.setDepartureDate(new Timestamp(currentRow.getCell(1).getDateCellValue().getTime()));
//            flight.setArrivalDate(new Timestamp(currentRow.getCell(2).getDateCellValue().getTime()));
//            flight.setDuration((long) currentRow.getCell(3).getNumericCellValue());
//            flight.setDepartureAirportId((long) currentRow.getCell(4).getNumericCellValue());
//            flight.setArrivalAirportId((long) currentRow.getCell(5).getNumericCellValue());
//            flight.setEconomyPrice(currentRow.getCell(6).getNumericCellValue());
//            flight.setBusinessPrice(currentRow.getCell(7).getNumericCellValue());
//            flight.setFirstClassPrice(currentRow.getCell(8).getNumericCellValue());
//            Map<String, Map<String, String>> seatStatuses = new HashMap<>();
//            seatStatuses.putAll(new FirstClassSeatFactory().createSeats(flight));
//            seatStatuses.putAll(new BusinessClassSeatFactory().createSeats(flight));
//            seatStatuses.putAll(new EconomyClassSeatFactory().createSeats(flight));
//            String seatStatusesJson = objectMapper.writeValueAsString(seatStatuses);
//            flight.setSeatStatuses(seatStatusesJson);
//            flight.setPlaneId(planeId);
//            // Ràng buộc dữ liệu
//            validateFlightData(flight);
//            flightsList.add(flight);
//        }
//        workbook.close();
//        return flightsList;
//    }
    private void validateFlightData(FlightDTORequest flightDTORequest) {
        List<Flights> conflictingFlights = flightRepository.findConflictingFlights(
                flightDTORequest.getPlaneId(),
                flightDTORequest.getDepartureDate(),
                flightDTORequest.getArrivalDate()
        );
        if (!conflictingFlights.isEmpty()) {
            throw new IllegalArgumentException("Flight time conflicts with existing flight(s): " +
                    conflictingFlights.stream().map(Flights::getId).collect(Collectors.toList()));
        }
    }

    private void validateFlightIntervals(FlightDTORequest flightDTORequest) {
        List<Flights> existingFlights = flightRepository.findAllByPlaneId(flightDTORequest.getPlaneId());
        for (Flights existingFlight : existingFlights) {
            if (isIntervalConflict(existingFlight, flightDTORequest)) {
                throw new IllegalArgumentException("Flight must be scheduled at least 10 hours apart from other flights for maintenance.");
            }
        }
    }
    private boolean isIntervalConflict(Flights existingFlight, FlightDTORequest newFlight) {
        long newFlightDeparture = newFlight.getDepartureDate().getTime();
        long newFlightArrival = newFlight.getArrivalDate().getTime();
        long existingFlightDeparture = existingFlight.getDepartureDate().getTime();
        long existingFlightArrival = existingFlight.getArrivalDate().getTime();
        long tenHoursInMillis = 10 * 60 * 60 * 1000;

        return (Math.abs(existingFlightDeparture - newFlightDeparture) < tenHoursInMillis ||
                Math.abs(existingFlightArrival - newFlightArrival) < tenHoursInMillis);
    }
    public Map<String, Map<String, String>> getSeatStatuses(Long flightId) throws Exception {
        Flights flight = flightRepository.findById(flightId).orElseThrow(() -> new RuntimeException("Plane not found"));
        String seatStatusesJson = flight.getSeatStatuses();
        Map<String, Map<String, String>> seatStatuses = objectMapper.readValue(seatStatusesJson, Map.class);
        Map<String, Map<String, String>> sortedSeatStatuses = new TreeMap<>(seatStatuses);
        return sortedSeatStatuses;
    }
    @Transactional
    public Flights delayFlight(Long flightId, String reason, Timestamp newDepartureTime, Timestamp newArrivalTime) throws MessagingException {
        Flights flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        flight.setFlightStatus(FlightStatus.DELAYED.name());
        flight.setDepartureDate(newDepartureTime);
        flight.setArrivalDate(newArrivalTime);
        flightRepository.save(flight);

        List<Booking> bookings = bookingRepository.findAllByFlightId(flightId);
        for (Booking booking : bookings) {
            flightDelayEmailSender.sendEmail(booking.getBookerEmail(), reason);
        }

        return flight;
    }

    @Transactional
    public Flights cancelFlight(Long flightId, String reason) throws MessagingException {
        Flights flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        flight.setFlightStatus(FlightStatus.CANCELED.name());
        flightRepository.save(flight);

        List<Booking> bookings = bookingRepository.findAllByFlightId(flightId);
        for (Booking booking : bookings) {
            flightCancelEmailSender.sendEmail(booking.getBookerEmail(), reason);
        }

        return flight;
    }
    @Transactional
    public Flights scheduleFlight(Long flightId, String reason, Timestamp newDepartureTime, Timestamp newArrivalTime) throws MessagingException {
        Flights flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        flight.setFlightStatus(FlightStatus.SCHEDULED.name());
        flight.setDepartureDate(newDepartureTime);
        flight.setArrivalDate(newArrivalTime);
        flightRepository.save(flight);
        List<Booking> bookings = bookingRepository.findAllByFlightId(flightId);
        for (Booking booking : bookings) {
            flightScheduleEmailSender.sendEmail(booking.getBookerEmail(), reason);
        }
        return flight;
    }
    @org.springframework.transaction.annotation.Transactional
    public List<Flights> filterFlightsByTimeFrame(String type, Long departureAirportId, Long arrivalAirportId, Timestamp departureDate, Timestamp returnDate, LocalTime startTime, LocalTime endTime) {
        List<Flights> flights = searchFlights(type, departureAirportId, arrivalAirportId, departureDate, returnDate);
        return flights.stream()
                .filter(flight -> {
                    LocalTime departureTime = flight.getDepartureDate().toLocalDateTime().toLocalTime();
                    return !departureTime.isBefore(startTime) && !departureTime.isAfter(endTime);
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public List<Flights> filterFlightsByTimeFrameAndPrice(String type, Long departureAirportId, Long arrivalAirportId, Timestamp departureDate, Timestamp returnDate, LocalTime startTime, LocalTime endTime, String classType, String order) {
        List<Flights> flights = searchFlights(type, departureAirportId, arrivalAirportId, departureDate, returnDate);
        List<Flights> filteredFlightsByTime = flights.stream()
                .filter(flight -> {
                    LocalTime departureTime = flight.getDepartureDate().toLocalDateTime().toLocalTime();
                    return !departureTime.isBefore(startTime) && !departureTime.isAfter(endTime);
                })
                .collect(Collectors.toList());

        switch (classType.toLowerCase()) {
            case "economy":
                if (order.equalsIgnoreCase("asc")) {
                    filteredFlightsByTime.sort((f1, f2) -> Double.compare(f1.getEconomyPrice(), f2.getEconomyPrice()));
                } else {
                    filteredFlightsByTime.sort((f1, f2) -> Double.compare(f2.getEconomyPrice(), f1.getEconomyPrice()));
                }
                break;
            case "business":
                if (order.equalsIgnoreCase("asc")) {
                    filteredFlightsByTime.sort((f1, f2) -> Double.compare(f1.getBusinessPrice(), f2.getBusinessPrice()));
                } else {
                    filteredFlightsByTime.sort((f1, f2) -> Double.compare(f2.getBusinessPrice(), f1.getBusinessPrice()));
                }
                break;
            case "firstclass":
                if (order.equalsIgnoreCase("asc")) {
                    filteredFlightsByTime.sort((f1, f2) -> Double.compare(f1.getFirstClassPrice(), f2.getFirstClassPrice()));
                } else {
                    filteredFlightsByTime.sort((f1, f2) -> Double.compare(f2.getFirstClassPrice(), f1.getFirstClassPrice()));
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid class type");
        }

        return filteredFlightsByTime;
    }
 }
