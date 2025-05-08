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
import com.example.FlightBooking.Services.Planes.PlaneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    //Cai nay la cai de tinh so tien khi chon ve ne
    @PostMapping("/calculate-total-price-after-booking")
    public ResponseEntity<Double> calculateTotalPrice(@RequestParam Long flightId, @RequestBody Set<String> seatNumbers) {
        try {
            double totalPrice = bookingService.calculateTotalPriceAfter(flightId, seatNumbers);
            return ResponseEntity.ok(totalPrice);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/hold-seat-before-booking")
    public ResponseEntity<?> holdSeat(@RequestBody Set<String>  seatNumbers, @RequestParam Long flightId)
    {
        try {
            boolean booking = bookingService.holdSeats(flightId, seatNumbers);
            if(booking)
            {
                return ResponseEntity.ok().body("Selected seat completed");
            }
            else
            {
                return ResponseEntity.ok().body("Selected seat error");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @PostMapping("/release-seat-before-booking")
    public ResponseEntity<?> releaseSeat(@RequestBody Set<String> seatNumbers, @RequestParam Long flightId)
    {
        try {
            bookingService.releaseSeats(flightId, seatNumbers);
            return ResponseEntity.ok().body("Release seat completed");
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @PostMapping("/book-seat-before-booking")
    public ResponseEntity<?> bookSeat(@RequestBody Set<String> seatNumbers, @RequestParam Long flightId)
    {
        try {
            boolean booking = bookingService.bookSeats(flightId, seatNumbers);
            if(booking) {
                return ResponseEntity.ok().body("Book seat completed");
            }
            else
            {
                return ResponseEntity.ok().body("Book seat error");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/get-ticket-by-user-id")
    public List<TicketResponse> getTicket(@RequestParam Long userId)
    {
        try {
            return bookingService.getAllTicketByUserId(userId);
        }
        catch(Exception e)
        {
            return new ArrayList<>();
        }
    }
    @GetMapping("/get-seat-price")
    public ResponseEntity<?> getSeatPrice (@RequestParam Long flightId, @RequestParam String seatNumber)
    {
        try {
            Optional<Flights> flightOptional = flightRepository.findById(flightId);
            if (flightOptional.isPresent()) {
                Flights flight = flightOptional.get();
                double seatPrice = bookingService.getSeatPrice(flightId, seatNumber);
                return ResponseEntity.ok(seatPrice);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
        @GetMapping("/get-ticket-detail-by-booking-id")
    public TicketDetailResponse getTicketDetail(@RequestParam Long bookingId)
    {
        return bookingService.getTicketDetailByBookingId(bookingId);
    }

}
