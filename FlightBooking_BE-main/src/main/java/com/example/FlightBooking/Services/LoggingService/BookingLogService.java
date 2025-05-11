package com.example.FlightBooking.Services.LoggingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class BookingLogService {
    private static final Logger logger = LoggerFactory.getLogger("booking-logger");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private ExcelLogService excelLogService;
    
    /**
     * Logs booking events
     * 
     * @param apiName Name of the API endpoint
     * @param userId User ID making the booking
     * @param requestInfo Additional request information as key-value pairs
     * @param status Booking status (success/failure)
     * @param message Additional message
     */
    public void logBookingEvent(String apiName, String userId, Map<String, Object> requestInfo, String status, String message) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========== BOOKING EVENT ==========\n");
        logBuilder.append("Time: ").append(LocalDateTime.now().format(formatter)).append("\n");
        logBuilder.append("API: ").append(apiName).append("\n");
        logBuilder.append("User ID: ").append(userId).append("\n");
        logBuilder.append("Status: ").append(status).append("\n");
        
        if (requestInfo != null && !requestInfo.isEmpty()) {
            logBuilder.append("Request Info: {\n");
            requestInfo.forEach((key, value) -> logBuilder.append("  ").append(key).append(": ").append(value).append("\n"));
            logBuilder.append("}\n");
        }
        
        if (message != null && !message.isEmpty()) {
            logBuilder.append("Message: ").append(message).append("\n");
        }
        
        logBuilder.append("================================\n");
        
        // Log to file
        logger.info(logBuilder.toString());
        
        // Log to Excel
        excelLogService.logBookingEvent(apiName, userId, requestInfo, status, message);
    }
    
    /**
     * Logs booking failures
     * 
     * @param apiName Name of the API endpoint
     * @param userId User ID attempting to book
     * @param requestInfo Additional request information
     * @param errorMessage Error message
     */
    public void logBookingFailure(String apiName, String userId, Map<String, Object> requestInfo, String errorMessage) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========== BOOKING FAILURE ==========\n");
        logBuilder.append("Time: ").append(LocalDateTime.now().format(formatter)).append("\n");
        logBuilder.append("API: ").append(apiName).append("\n");
        logBuilder.append("User ID: ").append(userId).append("\n");
        
        if (requestInfo != null && !requestInfo.isEmpty()) {
            logBuilder.append("Request Info: {\n");
            requestInfo.forEach((key, value) -> logBuilder.append("  ").append(key).append(": ").append(value).append("\n"));
            logBuilder.append("}\n");
        }
        
        logBuilder.append("Error: ").append(errorMessage).append("\n");
        logBuilder.append("==================================\n");
        
        // Log to file
        logger.error(logBuilder.toString());
        
        // Log to Excel
        excelLogService.logBookingEvent(apiName, userId, requestInfo, "FAILURE", "Error: " + errorMessage);
    }
}
