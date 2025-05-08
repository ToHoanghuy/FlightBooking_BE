package com.example.FlightBooking.Mapper;

import com.example.FlightBooking.DTOs.Request.Passenger.PassengerDTO;
import com.example.FlightBooking.Models.Passengers;

public class PassengerMapper {
    public static PassengerDTO toDTO(Passengers passenger) {
        PassengerDTO passengerDTO = new PassengerDTO();
        passengerDTO.setFullName(passenger.getFullName());
        passengerDTO.setEmail(passenger.getEmail());
        passengerDTO.setPersonalId(passenger.getPersonalId());
        passengerDTO.setSeatNumber(passenger.getSeatNumber());
        return passengerDTO;
    }

    public static Passengers toEntity(PassengerDTO passengerDTO) {
        Passengers passenger = new Passengers();
        passenger.setFullName(passengerDTO.getFullName());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPersonalId(passengerDTO.getPersonalId());
        passenger.setSeatNumber(passengerDTO.getSeatNumber());
        return passenger;
    }
}