package com.example.FlightBooking.DTOs.Request;

import lombok.Data;

@Data
public class PaymentMethodDTO {
    private String stripePaymentMethodId;
    private String cardLast4;
    private String cardBrand;

    public PaymentMethodDTO(String stripePaymentMethodId, String cardLast4, String cardBrand) {
        this.stripePaymentMethodId = stripePaymentMethodId;
        this.cardLast4 = cardLast4;
        this.cardBrand = cardBrand;
    }
}
