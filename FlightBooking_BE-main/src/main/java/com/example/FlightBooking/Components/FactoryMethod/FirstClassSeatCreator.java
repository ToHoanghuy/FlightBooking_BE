package com.example.FlightBooking.Components.FactoryMethod;

import com.stripe.model.tax.Registration;

public class FirstClassSeatCreator extends SeatCreator{
    @Override
    public SeatFactory createSeatFactory() {
        return new FirstClassSeatFactory();
    }
}
