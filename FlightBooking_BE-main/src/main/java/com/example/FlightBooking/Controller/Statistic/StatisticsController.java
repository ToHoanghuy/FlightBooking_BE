package com.example.FlightBooking.Controller.Statistic;

import com.example.FlightBooking.Services.Statistic.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/statistic")
public class StatisticsController {
    @Autowired
    private StatisticService statisticsService;

    @GetMapping("/revenue")
    public Double getTotalRevenue() {
        return statisticsService.getTotalRevenue();
    }

    @GetMapping("/revenueByDateRange")
    public Double getTotalRevenueByDateRange(@RequestParam("startDate") Timestamp startDate, @RequestParam("endDate") Timestamp endDate) {
        return statisticsService.getTotalRevenueByDateRange(startDate, endDate);
    }
    @GetMapping("/revenueForCurrentWeek")
    public List<Double> getRevenueForCurrentWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<Double> weeklyRevenue = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime dayStart = startOfWeek.plusDays(i);
            LocalDateTime dayEnd = dayStart.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            Timestamp startDate = Timestamp.valueOf(dayStart);
            Timestamp endDate = Timestamp.valueOf(dayEnd);
            Double revenue = statisticsService.getTotalRevenueByDateRange(startDate, endDate);
            weeklyRevenue.add(revenue != null ? revenue : 0.0);
        }
        return weeklyRevenue;
    }
    @GetMapping("/dailyRevenueForMonth")
    public List<Double> getDailyRevenueForMonth(@RequestParam String month, @RequestParam String year) {
        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month);
        LocalDate startOfMonth = LocalDate.of(yearInt, monthInt, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Double> dailyRevenue = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        for (int i = 0; i < startOfMonth.lengthOfMonth(); i++) {
            LocalDate day = startOfMonth.plusDays(i);
            LocalDateTime dayStart = day.atStartOfDay();
            LocalDateTime dayEnd = day.atTime(23, 59, 59, 999999999);
            Timestamp startDate = Timestamp.valueOf(dayStart.format(formatter));
            Timestamp endDate = Timestamp.valueOf(dayEnd.format(formatter));
            Double revenue = statisticsService.getTotalRevenueByDateRange(startDate, endDate);
            dailyRevenue.add(revenue != null ? revenue : 0.0);
        }
        return dailyRevenue;
    }
}
