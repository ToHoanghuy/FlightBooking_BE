package com.example.FlightBooking.Services.DecoratorService;

import com.example.FlightBooking.Models.Decorator.Meals;
import com.example.FlightBooking.Repositories.Decorator.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {
    @Autowired
    private MealRepository mealRepository;

    public Meals createNewMeal(Meals meals) {
        return mealRepository.save(meals);
    }

    public Meals getMealById(Long id) {
        return mealRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found with this id: " + id));
    }

    public List<Meals> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meals updateMeals(Long id, Meals meal) {
        Meals meals = getMealById(id);
        meals.setDescription(meal.getDescription());
        meals.setPrice(meal.getPrice());
        return mealRepository.save(meals);
    }

    public void deleteService(Long id) {
        mealRepository.deleteById(id);
    }
}
