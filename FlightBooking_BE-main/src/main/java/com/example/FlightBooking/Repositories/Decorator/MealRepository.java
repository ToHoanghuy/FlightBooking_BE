package com.example.FlightBooking.Repositories.Decorator;

import com.example.FlightBooking.Models.Decorator.Meals;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Hidden
public interface MealRepository extends JpaRepository<Meals, Long> {
}
