package com.example.FlightBooking.Controller.Payment;

import com.example.FlightBooking.Components.Adapter.PaymentProcessor;
import com.example.FlightBooking.DTOs.Request.Booking.CombineBookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.PaymentMethodDTO;
import com.example.FlightBooking.Models.PaymentMethod;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.PaymentMethodRepository;
import com.example.FlightBooking.Repositories.UserRepository;
import com.example.FlightBooking.Services.PaymentService.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/payment")
@Tag(name = "Payment method", description = "APIs for payment and charge to buy or get somethings")
public class PaymentController {

    @Autowired
    private PaymentProcessor paymentProcessor;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @PostMapping("/create-customer")
    public ResponseEntity<?> createCustomer(@RequestParam String email) {
        try {
            Users user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getStripeCustomerId() != null) {
                return ResponseEntity.ok(Collections.singletonMap("customerId", user.getStripeCustomerId()));
            }
            String customerId = paymentService.createStripeCustomer(email);
            if (user == null) {
                user = new Users();
                user.setEmail(email);
            }
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
            return ResponseEntity.ok(Collections.singletonMap("customerId", customerId));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/get-stripe-customer-id")
    public ResponseEntity<?> getStripeCustomerId(@RequestParam String token) {
        try {
            String customerId = paymentService.getStripeCustomerId(token);
            return ResponseEntity.ok(Collections.singletonMap("stripeCustomerId", customerId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @GetMapping("/get-stripe-payment-method-id")
    public ResponseEntity<?> getStripePaymentMethodId(@RequestParam String token) {
        try {
            String customerId = paymentService.getPaymentMethodId(token);
            return ResponseEntity.ok(Collections.singletonMap("stripePaymentMethodId", customerId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/get-stripe-client-secret-id")
    public ResponseEntity<?> getStripeClientSecretId(@RequestParam String token) {
        try {
            String customerId = paymentService.getStripeClientSecret(token);
            return ResponseEntity.ok(Collections.singletonMap("stripeClientSecreteId", customerId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/create-setup-intent")
    public ResponseEntity<?> createSetupIntent(@RequestParam String customerId) {
        try {
            SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                    .setCustomer(customerId)
                    .addPaymentMethodType("card")
                    .build();
            SetupIntent setupIntent = SetupIntent.create(params);
            Map<String, String> responseData = new HashMap<>();
            responseData.put("clientSecret", setupIntent.getClientSecret());
            responseData.put("setupIntentId", setupIntent.getId());

            Users user = userRepository.findByStripeCustomerId(customerId)
                    .orElseThrow(() -> new RuntimeException("User not found with this customerId: " + customerId));
            user.setSetupIntentId(setupIntent.getId());
            userRepository.save(user);

            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/attach-payment-method")
    public ResponseEntity<?> attachPaymentMethod(@RequestParam String customerId, @RequestParam String paymentMethodId) {
        try {
            paymentService.attachPaymentMethod(customerId, paymentMethodId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Payment method attached successfully"));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @PostMapping("/create-payment")
    @Transactional
    public ResponseEntity<?> createPayment(@RequestParam String token, @RequestParam Long idVoucher, @RequestParam double amount, @RequestBody CombineBookingRequestDTO combineBookingRequestDTO) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(token, idVoucher, amount, combineBookingRequestDTO);
            String clientSecret = paymentIntent.getClientSecret();
            return ResponseEntity.ok(Collections.singletonMap("clientSecret", clientSecret));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/payment-methods")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPaymentMethods(@RequestParam String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(user.getId());
        List<PaymentMethodDTO> paymentMethodDTOs = paymentMethods.stream()
                .map(pm -> new PaymentMethodDTO(pm.getStripePaymentMethodId(), pm.getCardLast4(), pm.getCardBrand()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentMethodDTOs);
    }

    @PostMapping("/register-card")
    public ResponseEntity<?> registerCard(
            @RequestParam String customerId,
            @RequestParam String setupIntentId,
            @RequestParam String username
    ) {
        try {
            // Retrieve the setup intent
            SetupIntent setupIntent = SetupIntent.retrieve(setupIntentId);

            // Extract the payment method ID from the setup intent
            String paymentMethodId = setupIntent.getPaymentMethod();

            // Check if payment method ID is null
            if (paymentMethodId == null) {
                return ResponseEntity.status(400).body(Collections.singletonMap("error", "SetupIntent does not have a PaymentMethod."));
            }

            // Retrieve the user by username
            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with this username: " + username));

            // Retrieve the payment method details
            com.stripe.model.PaymentMethod stripePaymentMethod = com.stripe.model.PaymentMethod.retrieve(paymentMethodId);

            if (stripePaymentMethod.getCard() == null) {
                return ResponseEntity.status(400).body(Collections.singletonMap("error", "PaymentMethod does not have card details."));
            }

            // Save payment method details in the database
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setUser(user);
            paymentMethod.setStripePaymentMethodId(paymentMethodId);
            paymentMethod.setCardLast4(stripePaymentMethod.getCard().getLast4());
            paymentMethod.setCardBrand(stripePaymentMethod.getCard().getBrand());
            paymentMethodRepository.save(paymentMethod);

            return ResponseEntity.ok(Collections.singletonMap("message", "Card registered successfully"));

        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/saved-cards")
    public ResponseEntity<?> getSavedCards(@RequestParam String email) {
        try {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
            List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(user.getId());
            List<PaymentMethodDTO> paymentMethodDTOs = paymentMethods.stream()
                    .map(pm -> new PaymentMethodDTO(pm.getStripePaymentMethodId(), pm.getCardLast4(), pm.getCardBrand()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(paymentMethodDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @PostMapping("/charge-saved-card")
    public ResponseEntity<?> chargeSavedCard(
            @RequestParam String email,
            @RequestParam String paymentMethodId,
            @RequestParam double amount,
            @RequestBody CombineBookingRequestDTO combineBookingRequestDTO
    ) {
        try {
            String clientSecret = paymentService.chargeSavedCard(email, paymentMethodId, amount,combineBookingRequestDTO);
            return ResponseEntity.ok(Collections.singletonMap("clientSecret", clientSecret));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

