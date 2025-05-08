package com.example.FlightBooking.Components.Adapter;

import com.example.FlightBooking.DTOs.Request.Booking.BookingRequestDTO;
import com.example.FlightBooking.DTOs.Request.Booking.CombineBookingRequestDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import java.util.Set;

import jakarta.mail.MessagingException;

public interface PaymentProcessor {
    String createCustomer(String email) throws StripeException;
    String getCustomerId(String token) throws StripeException;
    String createSetupIntent(String customerId);
    String attachPaymentMethod(String customerId, String paymentMethodId);
    String getSetupIntentId(String token) throws StripeException;
    String getPaymentMethodId(String token) throws StripeException;
    String getStripeClientSecret(String token) throws StripeException;
    PaymentIntent processPayment(String token, Long idVoucher, double amount, Long flightId, CombineBookingRequestDTO combineBookingRequestDTO) throws StripeException, MessagingException;

}
