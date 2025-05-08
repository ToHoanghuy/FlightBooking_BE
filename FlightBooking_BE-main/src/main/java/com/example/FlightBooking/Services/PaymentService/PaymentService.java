package com.example.FlightBooking.Services.PaymentService;

import com.example.FlightBooking.Components.TemplateMethod.TicketEmailSender;
import com.example.FlightBooking.DTOs.Request.Booking.BookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.Booking.CombineBookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.Passenger.PassengerDTO;
import com.example.FlightBooking.Models.Airlines;
import com.example.FlightBooking.Models.Airports;
import com.example.FlightBooking.Models.Booking;
import com.example.FlightBooking.Models.Decorator.Vouchers;
import com.example.FlightBooking.Models.Flights;
import com.example.FlightBooking.Models.Passengers;
import com.example.FlightBooking.Models.Planes;
import com.example.FlightBooking.Models.Statistics;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.AirportsRepository;
import com.example.FlightBooking.Repositories.BookingRepository;
import com.example.FlightBooking.Repositories.Decorator.VoucherRepository;
import com.example.FlightBooking.Repositories.FlightRepository;
import com.example.FlightBooking.Repositories.PaymentMethodRepository;
import com.example.FlightBooking.Repositories.PlaneRepository;
import com.example.FlightBooking.Repositories.StatisticsRepository;
import com.example.FlightBooking.Repositories.UserRepository;
import com.example.FlightBooking.Services.AuthJWT.JwtService;
import com.example.FlightBooking.Services.BookingService.BookingService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SetupIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Value("${stripe.api.secretKey}")
    private String stripeSecretKey;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketEmailSender ticketEmailSender;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PlaneRepository planeRepository;

    @Autowired
    private AirportsRepository airportsRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    private final BookingService bookingService;
    @Autowired
    public PaymentService(@Lazy BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String getStripeClientSecret(String token) throws StripeException
    {
        String username = jwtService.getUsername(token);
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with this username: " + username));
        SetupIntent setupIntent = SetupIntent.retrieve(users.getSetupIntentId());
        return setupIntent.getClientSecret();
    }

    public String createStripeCustomer(String email) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .build();
        Customer customer = Customer.create(params);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        user.setStripeCustomerId(customer.getId());
        userRepository.save(user);
        return customer.getId();
    }

    public String getStripeCustomerId(String token) {
        String username = jwtService.getUsername(token);
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with this username: " + username));
        return user.getStripeCustomerId();
    }

    public String getStripeSetupIntentId (String token) throws StripeException
    {
        String username = jwtService.getUsername(token);
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with this username: " + username));
        return users.getSetupIntentId();
    }

    public SetupIntent createSetupIntent(String customerId) throws StripeException {
        SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                .setCustomer(customerId)
                .addPaymentMethodType("card")
                .build();
        return SetupIntent.create(params);
    }

    public void attachPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();
        paymentMethod.attach(attachParams);
        Users user = userRepository.findByStripeCustomerId(customerId).orElseThrow(() -> new RuntimeException("User not found with this customerId: " + customerId));
        com.example.FlightBooking.Models.PaymentMethod newPaymentMethod = new com.example.FlightBooking.Models.PaymentMethod();
        newPaymentMethod.setUser(user);
        newPaymentMethod.setStripePaymentMethodId(paymentMethod.getId());
        newPaymentMethod.setCardLast4(paymentMethod.getCard().getLast4());
        newPaymentMethod.setCardBrand(paymentMethod.getCard().getBrand());
        paymentMethodRepository.save(newPaymentMethod);
    }

    public PaymentIntent createPaymentIntent(String token, Long idVoucher, double amount, CombineBookingRequestDTO combineBookingRequestDTO) throws StripeException, MessagingException {
        Long discount;
        if (idVoucher == 0) {
            discount = 0L;
        } else {
            Vouchers voucher = voucherRepository.findById(idVoucher).orElseThrow();
            discount = voucher.getDiscountAmount();
        }

        String customerId = getStripeCustomerId(token);
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * (1 - discount / 100) * 100)) // amount in cents
                .setCurrency("usd")
                .setCustomer(customerId)
                .setPaymentMethod(getPaymentMethodId(token))
                .setConfirm(true)
                .setOffSession(true)
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        String username = jwtService.getUsername(token);
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with this username: " + username));

        for (BookingRequestDTO bookingRequestDTO : combineBookingRequestDTO.getBookingRequests()) {
            Booking booking = new Booking();
            booking.setFlightId(bookingRequestDTO.getFlightId());
            booking.setBookerFullName(bookingRequestDTO.getBookerFullName());
            booking.setBookerEmail(bookingRequestDTO.getBookerEmail());
            booking.setBookerPhoneNumber(bookingRequestDTO.getBookerPhoneNumber());
            booking.setUserId(bookingRequestDTO.getUserId());
            booking.setBookingDate(Timestamp.valueOf(LocalDateTime.now()));

            List<Passengers> passengers = bookingRequestDTO.getPassengers().stream().map(dto -> {
                Passengers passenger = new Passengers();
                passenger.setFullName(dto.getFullName());
                passenger.setEmail(dto.getEmail());
                passenger.setPersonalId(dto.getPersonalId());
                passenger.setSeatNumber(dto.getSeatNumber());
                passenger.setBooking(booking);
                return passenger;
            }).collect(Collectors.toList());
            booking.setPassengers(passengers);
            bookingRepository.save(booking);

            try {
                Set<String> seatNumbers = bookingRequestDTO.getPassengers().stream()
                        .map(PassengerDTO::getSeatNumber)
                        .collect(Collectors.toSet());
                bookingService.bookSeats(bookingRequestDTO.getFlightId(), seatNumbers);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Statistics statistics = new Statistics();
            statistics.setUserId(user.getId());
            statistics.setAmount(amount);
            statistics.setFlightId(bookingRequestDTO.getFlightId());
            statisticsRepository.save(statistics);

            // Send email tickets
            sendEmailTickets(bookingRequestDTO);
        }

        return paymentIntent;
    }
    public String chargeSavedCard(String email, String paymentMethodId, double amount, CombineBookingRequestDTO combineBookingRequestDTO) throws StripeException, MessagingException {

       Users user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        String customerId = user.getStripeCustomerId();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))  // amount in cents
                .setCurrency("usd")
                .setCustomer(customerId)
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .setOffSession(true)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        for (BookingRequestDTO bookingRequestDTO : combineBookingRequestDTO.getBookingRequests()) {
            Booking booking = new Booking();
            booking.setFlightId(bookingRequestDTO.getFlightId());
            booking.setBookerFullName(bookingRequestDTO.getBookerFullName());
            booking.setBookerEmail(bookingRequestDTO.getBookerEmail());
            booking.setBookerPhoneNumber(bookingRequestDTO.getBookerPhoneNumber());
            booking.setUserId(bookingRequestDTO.getUserId());
            booking.setBookingDate(Timestamp.valueOf(LocalDateTime.now()));

            List<Passengers> passengers = bookingRequestDTO.getPassengers().stream().map(dto -> {
                Passengers passenger = new Passengers();
                passenger.setFullName(dto.getFullName());
                passenger.setEmail(dto.getEmail());
                passenger.setPersonalId(dto.getPersonalId());
                passenger.setSeatNumber(dto.getSeatNumber());
                passenger.setBooking(booking);
                return passenger;
            }).collect(Collectors.toList());
            booking.setPassengers(passengers);
            bookingRepository.save(booking);

            try {
                Set<String> seatNumbers = bookingRequestDTO.getPassengers().stream()
                        .map(PassengerDTO::getSeatNumber)
                        .collect(Collectors.toSet());
                bookingService.bookSeats(bookingRequestDTO.getFlightId(), seatNumbers);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Statistics statistics = new Statistics();
            statistics.setUserId(user.getId());
            statistics.setAmount(amount);
            statistics.setFlightId(bookingRequestDTO.getFlightId());
            statisticsRepository.save(statistics);

            // Send email tickets
            sendEmailTickets(bookingRequestDTO);
        }
        return paymentIntent.getClientSecret();
    }
    private void sendEmailTickets(BookingRequestDTO bookingRequestDTO) throws MessagingException {
        String email = bookingRequestDTO.getBookerEmail();
        Flights flights = flightRepository.findById(bookingRequestDTO.getFlightId()).orElseThrow();
        Timestamp arrival = flights.getArrivalDate();
        Timestamp departure = flights.getDepartureDate();
        String arrivalTime = formatTimestamp(arrival);
        String departureTime = formatTimestamp(departure);
        Timestamp boardingTime = subtractHours(departure, 1);
        String boardingTimeStr = formatTimestamp(boardingTime);
        Long planeId = flights.getPlaneId();
        Long arrivalAirportId = flights.getArrivalAirportId();
        Long departureAirportId = flights.getDepartureAirportId();

        Planes planes = planeRepository.findById(planeId).orElseThrow();
        String flightNumber = planes.getFlightNumber();
        Airlines airline = planes.getAirline();
        String airlineName = airline.getAirlineName();

        Airports arrivalAirport = airportsRepository.findById(arrivalAirportId).orElseThrow();
        String arrivalCity = arrivalAirport.getAirportName() + ", " + arrivalAirport.getCity();
        String codeArrival = arrivalAirport.getIataCode();
        Airports departureAirport = airportsRepository.findById(departureAirportId).orElseThrow();
        String departureCity = departureAirport.getAirportName() + ", " + departureAirport.getCity();
        String codeDeparture = departureAirport.getIataCode();

        for (PassengerDTO passenger : bookingRequestDTO.getPassengers()) {
            ticketEmailSender.sendTicketToEmail(
                    email, departureTime, arrivalTime, airlineName, codeDeparture, departureCity,
                    codeArrival, passenger.getFullName(), arrivalCity, boardingTimeStr, flightNumber, passenger.getSeatNumber()
            );
        }
    }


    public String getPaymentMethodId(String token) throws StripeException {
        String username = jwtService.getUsername(token);
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with this username: " + username));
        SetupIntent setupIntent = SetupIntent.retrieve(user.getSetupIntentId());
        return setupIntent.getPaymentMethod();
    }
    public String formatTimestamp(Timestamp t){
        // Chuyển đổi timestamp thành đối tượng LocalDateTime
        LocalDateTime dateTime = t.toLocalDateTime();

        // Định dạng đối tượng LocalDateTime thành chuỗi theo định dạng "4:00 PM, Monday August 13 2024"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a, EEEE MMMM d yyyy");
        return dateTime.format(formatter);
    }
    public static Timestamp subtractHours(Timestamp timestamp, int hours) {
        // Chuyển đổi Timestamp sang LocalDateTime
        LocalDateTime dateTime = timestamp.toLocalDateTime();

        // Trừ đi 2 giờ
        LocalDateTime adjustedDateTime = dateTime.minusHours(hours);

        // Chuyển đổi ngược lại thành Timestamp
        return Timestamp.valueOf(adjustedDateTime);
    }
}