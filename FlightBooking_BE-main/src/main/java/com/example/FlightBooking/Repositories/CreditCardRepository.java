package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.CreditCard;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@Hidden
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    CreditCard findByUserId(Long userId);
}
