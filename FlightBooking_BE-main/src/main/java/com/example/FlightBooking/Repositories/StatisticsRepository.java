package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.Statistics;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
@Hidden
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    @Query("SELECT SUM(s.amount) FROM Statistics s")
    Double getTotalRevenue();

    @Query("SELECT SUM(s.amount) FROM Statistics s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByDateRange(Timestamp startDate, Timestamp endDate);
}
