package com.example.FlightBooking.Controller.Booking;

import com.example.FlightBooking.DTOs.Request.Booking.BookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.Booking.SelectSeatDTO;
import com.example.FlightBooking.DTOs.Response.Ticket.TicketResponse;
import com.example.FlightBooking.DTOs.Response.Ticket.TicketDetailResponse;
import com.example.FlightBooking.Models.Booking;
import com.example.FlightBooking.Models.Flights;
import com.example.FlightBooking.Repositories.FlightRepository;
import com.example.FlightBooking.Services.BookingService.BookingService;
import com.example.FlightBooking.Services.FlightService.FlightService;
import com.example.FlightBooking.Services.LoggingService.BookingLogService;
import com.example.FlightBooking.Services.Planes.PlaneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/booking")
@CrossOrigin
@Tag(name = "Booking Ticket", description = "APIs for choose position sit down at once flight")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private BookingLogService bookingLogService;
    //Cai nay la cai de tinh so tien khi chon ve ne
    @PostMapping("/calculate-total-price-after-booking")
    public ResponseEntity<Double> calculateTotalPrice(@RequestParam Long flightId, @RequestBody Set<String> seatNumbers, HttpServletRequest request) {
        try {
            // Log price calculation attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("flightId", flightId);
            requestInfo.put("seatCount", seatNumbers.size());
            requestInfo.put("seatNumbers", seatNumbers.toString());
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            bookingLogService.logBookingEvent(
                "/booking/calculate-total-price-after-booking", 
                "unknown", // No user ID available
                requestInfo, 
                "PRICE_CALCULATION_ATTEMPT", 
                "Calculating total price for seats"
            );
            
            double totalPrice = bookingService.calculateTotalPriceAfter(flightId, seatNumbers);
            
            // Log successful price calculation
            Map<String, Object> responseInfo = new HashMap<>(requestInfo);
            responseInfo.put("totalPrice", totalPrice);
            
            bookingLogService.logBookingEvent(
                "/booking/calculate-total-price-after-booking", 
                "unknown", 
                responseInfo, 
                "PRICE_CALCULATION_SUCCESS", 
                "Total price calculated: " + totalPrice
            );
            
            return ResponseEntity.ok(totalPrice);
        } catch (Exception e) {
            // Log exception
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("flightId", flightId);
            errorInfo.put("seatCount", seatNumbers.size());
            errorInfo.put("seatNumbers", seatNumbers.toString());
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("error", e.getMessage());
            
            bookingLogService.logBookingFailure(
                "/booking/calculate-total-price-after-booking", 
                "unknown", 
                errorInfo, 
                e.getMessage()
            );
            
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/hold-seat-before-booking")
    public ResponseEntity<?> holdSeat(@RequestBody Set<String> seatNumbers, @RequestParam Long flightId, HttpServletRequest request)
    {
        try {
            // Log hold seat attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("flightId", flightId);
            requestInfo.put("seatCount", seatNumbers.size());
            requestInfo.put("seatNumbers", seatNumbers.toString());
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            bookingLogService.logBookingEvent(
                "/booking/hold-seat-before-booking", 
                "unknown", // No user ID available in this endpoint
                requestInfo, 
                "HOLD_SEAT_ATTEMPT", 
                "Attempt to hold seats"
            );
            
            boolean booking = bookingService.holdSeats(flightId, seatNumbers);
            if(booking)
            {
                // Log successful hold
                bookingLogService.logBookingEvent(
                    "/booking/hold-seat-before-booking", 
                    "unknown", 
                    requestInfo, 
                    "HOLD_SEAT_SUCCESS", 
                    "Selected seat completed"
                );
                return ResponseEntity.ok().body("Selected seat completed");
            }
            else
            {
                // Log failed hold
                bookingLogService.logBookingFailure(
                    "/booking/hold-seat-before-booking", 
                    "unknown", 
                    requestInfo, 
                    "Selected seat error - Unavailable seats"
                );
                return ResponseEntity.ok().body("Selected seat error");
            }
        } catch (Exception e) {
            // Log exception
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("flightId", flightId);
            errorInfo.put("seatCount", seatNumbers.size());
            errorInfo.put("seatNumbers", seatNumbers.toString());
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("error", e.getMessage());
            
            bookingLogService.logBookingFailure(
                "/booking/hold-seat-before-booking", 
                "unknown", 
                errorInfo, 
                e.getMessage()
            );
            
            return ResponseEntity.status(500).build();
        }
    }
    @PostMapping("/release-seat-before-booking")
    public ResponseEntity<?> releaseSeat(@RequestBody Set<String> seatNumbers, @RequestParam Long flightId, HttpServletRequest request)
    {
        try {
            // Log release seat attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("flightId", flightId);
            requestInfo.put("seatCount", seatNumbers.size());
            requestInfo.put("seatNumbers", seatNumbers.toString());
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            bookingLogService.logBookingEvent(
                "/booking/release-seat-before-booking", 
                "unknown", // No user ID available in this endpoint
                requestInfo, 
                "RELEASE_SEAT_ATTEMPT", 
                "Attempt to release seats"
            );
            
            bookingService.releaseSeats(flightId, seatNumbers);
            
            // Log successful release
            bookingLogService.logBookingEvent(
                "/booking/release-seat-before-booking", 
                "unknown", 
                requestInfo, 
                "RELEASE_SEAT_SUCCESS", 
                "Release seat completed"
            );
            
            return ResponseEntity.ok().body("Release seat completed");
        } catch (Exception e) {
            // Log exception
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("flightId", flightId);
            errorInfo.put("seatCount", seatNumbers.size());
            errorInfo.put("seatNumbers", seatNumbers.toString());
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("error", e.getMessage());
            
            bookingLogService.logBookingFailure(
                "/booking/release-seat-before-booking", 
                "unknown", 
                errorInfo, 
                e.getMessage()
            );
            
            return ResponseEntity.status(500).build();
        }
    }
    @PostMapping("/book-seat-before-booking")
    public ResponseEntity<?> bookSeat(@RequestBody Set<String> seatNumbers, @RequestParam Long flightId, HttpServletRequest request)
    {
        try {
            // Log booking attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("flightId", flightId);
            requestInfo.put("seatCount", seatNumbers.size());
            requestInfo.put("seatNumbers", seatNumbers.toString());
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            bookingLogService.logBookingEvent(
                "/booking/book-seat-before-booking", 
                "unknown", // No user ID available in this endpoint
                requestInfo, 
                "BOOK_SEAT_ATTEMPT", 
                "Attempt to book seats"
            );
            
            boolean booking = bookingService.bookSeats(flightId, seatNumbers);
            if(booking) {
                // Log successful booking
                bookingLogService.logBookingEvent(
                    "/booking/book-seat-before-booking", 
                    "unknown", 
                    requestInfo, 
                    "BOOK_SEAT_SUCCESS", 
                    "Book seat completed"
                );
                return ResponseEntity.ok().body("Book seat completed");
            }
            else
            {
                // Log failed booking
                bookingLogService.logBookingFailure(
                    "/booking/book-seat-before-booking", 
                    "unknown", 
                    requestInfo, 
                    "Book seat error - Unavailable seats"
                );
                return ResponseEntity.ok().body("Book seat error");
            }
        } catch (Exception e) {
            // Log exception
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("flightId", flightId);
            errorInfo.put("seatCount", seatNumbers.size());
            errorInfo.put("seatNumbers", seatNumbers.toString());
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("error", e.getMessage());
            
            bookingLogService.logBookingFailure(
                "/booking/book-seat-before-booking", 
                "unknown", 
                errorInfo, 
                e.getMessage()
            );
            
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/get-ticket-by-user-id")
    public List<TicketResponse> getTicket(@RequestParam Long userId, HttpServletRequest request)
    {
        try {
            // Log ticket retrieval attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("userId", userId);
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            bookingLogService.logBookingEvent(
                "/booking/get-ticket-by-user-id", 
                userId.toString(),
                requestInfo, 
                "TICKET_RETRIEVAL_ATTEMPT", 
                "Getting tickets for user"
            );
            
            List<TicketResponse> tickets = bookingService.getAllTicketByUserId(userId);
            
            // Log successful ticket retrieval
            Map<String, Object> responseInfo = new HashMap<>(requestInfo);
            responseInfo.put("ticketCount", tickets.size());
            
            bookingLogService.logBookingEvent(
                "/booking/get-ticket-by-user-id", 
                userId.toString(), 
                responseInfo, 
                "TICKET_RETRIEVAL_SUCCESS", 
                "Retrieved " + tickets.size() + " tickets"
            );
            
            return tickets;
        }
        catch(Exception e)
        {
            // Log exception
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("userId", userId);
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("error", e.getMessage());
            
            bookingLogService.logBookingFailure(
                "/booking/get-ticket-by-user-id", 
                userId.toString(), 
                errorInfo, 
                e.getMessage()
            );
            
            return new ArrayList<>();
        }
    }
    @GetMapping("/get-seat-price")
    public ResponseEntity<?> getSeatPrice (@RequestParam Long flightId, @RequestParam String seatNumber, HttpServletRequest request)
    {
        try {
            // Log seat price query attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("flightId", flightId);
            requestInfo.put("seatNumber", seatNumber);
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            bookingLogService.logBookingEvent(
                "/booking/get-seat-price", 
                "unknown", // No user ID available
                requestInfo, 
                "SEAT_PRICE_QUERY_ATTEMPT", 
                "Querying price for seat " + seatNumber + " on flight " + flightId
            );
            
            Optional<Flights> flightOptional = flightRepository.findById(flightId);
            if (flightOptional.isPresent()) {
                Flights flight = flightOptional.get();
                double seatPrice = bookingService.getSeatPrice(flightId, seatNumber);
                
                // Log successful price query
                Map<String, Object> responseInfo = new HashMap<>(requestInfo);
                responseInfo.put("seatPrice", seatPrice);
                
                bookingLogService.logBookingEvent(
                    "/booking/get-seat-price", 
                    "unknown", 
                    responseInfo, 
                    "SEAT_PRICE_QUERY_SUCCESS", 
                    "Price for seat " + seatNumber + " on flight " + flightId + " is " + seatPrice
                );
                
                return ResponseEntity.ok(seatPrice);
            } else {
                // Log flight not found
                bookingLogService.logBookingFailure(
                    "/booking/get-seat-price", 
                    "unknown", 
                    requestInfo, 
                    "Flight not found with ID: " + flightId
                );
                
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            // Log exception
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("flightId", flightId);
            errorInfo.put("seatNumber", seatNumber);
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("error", e.getMessage());
            
            bookingLogService.logBookingFailure(
                "/booking/get-seat-price", 
                "unknown", 
                errorInfo, 
                e.getMessage()
            );
            
            return ResponseEntity.status(500).body(null);
        }
    }
        @GetMapping("/get-ticket-detail-by-booking-id")
    public TicketDetailResponse getTicketDetail(@RequestParam Long bookingId, HttpServletRequest request)
    {
        try {
            // Log ticket detail retrieval attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("bookingId", bookingId);
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            bookingLogService.logBookingEvent(
                "/booking/get-ticket-detail-by-booking-id", 
                "unknown", // No user ID available
                requestInfo, 
                "TICKET_DETAIL_RETRIEVAL_ATTEMPT", 
                "Getting ticket details for booking ID: " + bookingId
            );
            
            TicketDetailResponse ticketDetail = bookingService.getTicketDetailByBookingId(bookingId);
            
            // Log successful ticket detail retrieval
            bookingLogService.logBookingEvent(
                "/booking/get-ticket-detail-by-booking-id", 
                "unknown", 
                requestInfo, 
                "TICKET_DETAIL_RETRIEVAL_SUCCESS", 
                "Retrieved ticket details for booking ID: " + bookingId
            );
            
            return ticketDetail;
        } catch (Exception e) {
            // Log exception
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("bookingId", bookingId);
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("error", e.getMessage());
            
            bookingLogService.logBookingFailure(
                "/booking/get-ticket-detail-by-booking-id", 
                "unknown", 
                errorInfo, 
                e.getMessage()
            );
            
            throw e; // Re-throw to preserve original error handling
        }
    }

}
