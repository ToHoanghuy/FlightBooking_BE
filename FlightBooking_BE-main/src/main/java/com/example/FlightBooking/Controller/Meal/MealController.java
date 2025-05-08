package com.example.FlightBooking.Controller.Meal;

import com.example.FlightBooking.Models.Decorator.Meals;
import com.example.FlightBooking.Services.DecoratorService.MealService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Meal Controller ", description = "Service for some flight")
@RequestMapping("/meal")
public class MealController {
    @Autowired
    private MealService mealService;

    @GetMapping("/get")
    public ResponseEntity<Meals> getMealById(@RequestParam Long id)
    {
        return ResponseEntity.ok().body(mealService.getMealById(id));
    }
    @PostMapping("/create")
    public ResponseEntity<Meals> createNewMeal(@RequestParam Meals meals)
    {
        return ResponseEntity.ok().body(mealService.createNewMeal(meals));
    }
}
