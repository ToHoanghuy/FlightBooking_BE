package com.example.FlightBooking.Components.FactoryMethod;

public class EconomyClassSeatCreator extends SeatCreator{
    @Override
    public SeatFactory createSeatFactory() {
        return new EconomyClassSeatFactory();
    }
}
