package com.example.FlightBooking.Services.Statistic;

import com.example.FlightBooking.Repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class StatisticService {
    @Autowired
    private StatisticsRepository statisticsRepository;

    public Double getTotalRevenue() {
        return statisticsRepository.getTotalRevenue();
    }

    public Double getTotalRevenueByDateRange(Timestamp startDate, Timestamp endDate) {
        return statisticsRepository.getTotalRevenueByDateRange(startDate, endDate);
    }

}
