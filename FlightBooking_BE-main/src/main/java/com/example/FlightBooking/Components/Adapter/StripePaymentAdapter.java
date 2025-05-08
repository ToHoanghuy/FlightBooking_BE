package com.example.FlightBooking.Components.Adapter;

import com.example.FlightBooking.DTOs.Request.Booking.BookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.Booking.CombineBookingRequestDTO;
import com.example.FlightBooking.Services.PaymentService.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import jakarta.mail.MessagingException;

@Service
public class StripePaymentAdapter implements PaymentProcessor{
    @Autowired
    private PaymentService paymentService;

    @Override
    public String createCustomer(String email) throws StripeException {
        return paymentService.createStripeCustomer(email);
    }

    @Override
    public String getCustomerId(String token) throws StripeException{
        return paymentService.getStripeCustomerId(token);
    }

    @Override
    public String createSetupIntent(String customerId) {
        return "";
    }

    @Override
    public String attachPaymentMethod(String customerId, String paymentMethodId) {
        return "";
    }

    @Override
    public String getSetupIntentId(String token) throws StripeException{
        return paymentService.getStripeSetupIntentId(token);
    }

    @Override
    public String getPaymentMethodId(String token) throws StripeException {
        return paymentService.getPaymentMethodId(token);
    }
    @Override
    public String getStripeClientSecret(String token) throws StripeException
    {
        return paymentService.getStripeClientSecret(token);
    }

    @Override
    public PaymentIntent processPayment(String token, Long ibVoucher, double amount,
                                        Long flightId, CombineBookingRequestDTO combineBookingRequestDTO) throws StripeException, MessagingException {
        return paymentService.createPaymentIntent(token, ibVoucher, amount, combineBookingRequestDTO);
    }
}
