package com.example.FlightBooking.Components.FactoryMethod;

public class BusinessClassSeatCreator extends SeatCreator{
    @Override
    public SeatFactory createSeatFactory() {
        return new BusinessClassSeatFactory();
    }
}
