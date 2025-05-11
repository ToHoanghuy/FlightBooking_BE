package com.example.FlightBooking.Services.LoggingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class FlightLogService {
    private static final Logger logger = LoggerFactory.getLogger("flight-logger");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private ExcelLogService excelLogService;
    
    /**
     * Logs flight events such as delays, cancellations and schedule changes
     * 
     * @param apiName Name of the API endpoint
     * @param userId Admin/user ID making the change
     * @param flightInfo Flight information
     * @param eventType Type of event (DELAY, CANCEL, SCHEDULE_CHANGE)
     * @param message Additional message
     */
    public void logFlightEvent(String apiName, String userId, Map<String, Object> flightInfo, String eventType, String message) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========== FLIGHT EVENT ==========\n");
        logBuilder.append("Time: ").append(LocalDateTime.now().format(formatter)).append("\n");
        logBuilder.append("API: ").append(apiName).append("\n");
        logBuilder.append("Admin/User ID: ").append(userId).append("\n");
        logBuilder.append("Event Type: ").append(eventType).append("\n");
        
        if (flightInfo != null && !flightInfo.isEmpty()) {
            logBuilder.append("Flight Info: {\n");
            flightInfo.forEach((key, value) -> logBuilder.append("  ").append(key).append(": ").append(value).append("\n"));
            logBuilder.append("}\n");
        }
        
        if (message != null && !message.isEmpty()) {
            logBuilder.append("Message: ").append(message).append("\n");
        }
        
        logBuilder.append("================================\n");
        
        // Log to file
        logger.info(logBuilder.toString());
        
        // Log to Excel
        excelLogService.logFlightEvent(apiName, userId, flightInfo, eventType, message);
    }
    
    /**
     * Logs flight operation failures
     * 
     * @param apiName Name of the API endpoint
     * @param userId Admin/User ID attempting the operation
     * @param flightInfo Flight information
     * @param errorMessage Error message
     */
    public void logFlightFailure(String apiName, String userId, Map<String, Object> flightInfo, String errorMessage) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========== FLIGHT OPERATION FAILURE ==========\n");
        logBuilder.append("Time: ").append(LocalDateTime.now().format(formatter)).append("\n");
        logBuilder.append("API: ").append(apiName).append("\n");
        logBuilder.append("Admin/User ID: ").append(userId).append("\n");
        
        if (flightInfo != null && !flightInfo.isEmpty()) {
            logBuilder.append("Flight Info: {\n");
            flightInfo.forEach((key, value) -> logBuilder.append("  ").append(key).append(": ").append(value).append("\n"));
            logBuilder.append("}\n");
        }
        
        logBuilder.append("Error: ").append(errorMessage).append("\n");
        logBuilder.append("==========================================\n");
        
        // Log to file
        logger.error(logBuilder.toString());
        
        // Log to Excel
        excelLogService.logFlightEvent(apiName, userId, flightInfo, "FAILURE", "Error: " + errorMessage);
    }
}
