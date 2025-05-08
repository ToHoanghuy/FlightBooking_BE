package com.example.FlightBooking.Services.DecoratorService;

import com.example.FlightBooking.Models.Decorator.BaggageFees;
import com.example.FlightBooking.Models.Decorator.Meals;
import com.example.FlightBooking.Repositories.Decorator.BaggageFeeRepository;
import com.example.FlightBooking.Repositories.Decorator.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaggageFeeService {
    @Autowired
    private BaggageFeeRepository baggageFeeRepository;

    public BaggageFees createNewMeal(BaggageFees baggageFees) {
        return baggageFeeRepository.save(baggageFees);
    }

    public BaggageFees getBaggageFeeById(Long id) {
        return baggageFeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found with this id: " + id));
    }

    public List<BaggageFees> getAll() {
        return baggageFeeRepository.findAll();
    }

    public BaggageFees updateBaggageFee(Long id, BaggageFees baggageFees) {
        BaggageFees baggageFee = getBaggageFeeById(id);
        baggageFee.setFeePerKg(baggageFees.getFeePerKg());
        baggageFee.setWeightThreshold(baggageFees.getWeightThreshold());
        return baggageFeeRepository.save(baggageFees);
    }

    public void deleteService(Long id) {
        baggageFeeRepository.deleteById(id);
    }
}
