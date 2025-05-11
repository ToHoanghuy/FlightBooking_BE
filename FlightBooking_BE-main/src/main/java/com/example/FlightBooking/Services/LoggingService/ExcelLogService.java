package com.example.FlightBooking.Services.LoggingService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class ExcelLogService {    @Value("${auth.excel.log-file-path:D:/flight_booking_auth_logs.xlsx}")
    private String excelLogFilePath;
    
    @Value("${flight.excel.log-file-path:D:/flight_booking_flight_logs.xlsx}")
    private String flightExcelLogFilePath;

    @Value("${auth.excel.sheet-name:AuthenticationLogs}")
    private String sheetName;

    @Value("${flight.excel.sheet-name:FlightOperationsLog}")
    private String flightSheetName;

    // Additional sheet name for flight logs
    private static final String[] FLIGHT_HEADERS = {
        "Timestamp", "API", "Admin/UserID", "Event Type", "Flight ID", "Reason", "Original Time", "New Time", "Message", "Details"
    };

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      private final String[] HEADERS = {
        "Timestamp", "API", "Username/UserID", "Status", "IP Address", "User Agent", "Message", "Details"
    };    @PostConstruct
    public void init() {
        try {
            createAuthExcelFileIfNotExists();
            createFlightExcelFileIfNotExists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAuthExcelFileIfNotExists() throws IOException {
        File file = new File(excelLogFilePath);
        if (!file.exists()) {
            // Create directory if it doesn't exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            // Create new Excel file with headers
            try (Workbook workbook = new XSSFWorkbook()) {
                // Create Authentication sheet with headers
                Sheet authSheet = workbook.createSheet(sheetName);
                
                // Create header style
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                
                // Create auth header row
                Row authHeaderRow = authSheet.createRow(0);
                for (int i = 0; i < HEADERS.length; i++) {
                    Cell cell = authHeaderRow.createCell(i);
                    cell.setCellValue(HEADERS[i]);
                    cell.setCellStyle(headerStyle);
                    authSheet.autoSizeColumn(i);
                }
                
                // Save the workbook to a file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
            }
        }
    }
    
    private void createFlightExcelFileIfNotExists() throws IOException {
        File file = new File(flightExcelLogFilePath);
        if (!file.exists()) {
            // Create directory if it doesn't exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            // Create new Excel file with headers
            try (Workbook workbook = new XSSFWorkbook()) {
                // Create Flight sheet with headers
                Sheet flightSheet = workbook.createSheet(flightSheetName);
                
                // Create header style
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                
                // Create flight header row
                Row flightHeaderRow = flightSheet.createRow(0);
                for (int i = 0; i < FLIGHT_HEADERS.length; i++) {
                    Cell cell = flightHeaderRow.createCell(i);
                    cell.setCellValue(FLIGHT_HEADERS[i]);
                    cell.setCellStyle(headerStyle);
                    flightSheet.autoSizeColumn(i);
                }
                
                // Save the workbook to a file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
            }
        }
    }

    public synchronized void logAuthEvent(String apiName, String username, String status, 
            String ipAddress, String userAgent, String message, String additionalDetails) {
        
        try {
            // Determine if file exists
            File file = new File(excelLogFilePath);
            boolean isNewFile = !file.exists();
            
            // Read existing workbook or create new one
            Workbook workbook;
            if (isNewFile) {
                workbook = new XSSFWorkbook();
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
            }
            
            // Create cell styles for different status types
            CellStyle successStyle = workbook.createCellStyle();
            successStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            successStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle failureStyle = workbook.createCellStyle();
            failureStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            failureStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle attemptStyle = workbook.createCellStyle();
            attemptStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            attemptStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle registrationStyle = workbook.createCellStyle();
            registrationStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            registrationStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Get or create sheet
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
                
                // Create header row with style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < HEADERS.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(HEADERS[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.autoSizeColumn(i);
                }
            }
            
            // Find the next row index
            int rowIndex = sheet.getLastRowNum() + 1;
            
            // Create the new row
            Row row = sheet.createRow(rowIndex);
            
            // Choose cell style based on status
            CellStyle rowStyle;
            if (status.contains("SUCCESS")) {
                rowStyle = successStyle;
            } else if (status.contains("FAILURE")) {
                rowStyle = failureStyle;
            } else if (status.contains("REGISTRATION")) {
                rowStyle = registrationStyle;
            } else {
                rowStyle = attemptStyle;
            }
            
            // Fill data with style
            for (int i = 0; i < 8; i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(rowStyle);
                
                switch (i) {
                    case 0: cell.setCellValue(LocalDateTime.now().format(FORMATTER)); break; // Timestamp
                    case 1: cell.setCellValue(apiName); break; // API
                    case 2: cell.setCellValue(username); break; // Username
                    case 3: cell.setCellValue(status); break; // Status
                    case 4: cell.setCellValue(ipAddress); break; // IP Address
                    case 5: cell.setCellValue(userAgent); break; // User Agent
                    case 6: cell.setCellValue(message); break; // Message
                    case 7: cell.setCellValue(additionalDetails); break; // Details
                }
            }
            
            // Auto size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write the workbook to the file
            try (FileOutputStream fileOut = new FileOutputStream(excelLogFilePath)) {
                workbook.write(fileOut);
            }
            
            // Close the workbook
            workbook.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logAuthenticationEvent(String apiName, String username, Map<String, Object> requestInfo, 
            String status, String message) {
        
        String ipAddress = (requestInfo != null && requestInfo.get("remoteAddr") != null) ? 
                requestInfo.get("remoteAddr").toString() : "unknown";
                
        String userAgent = (requestInfo != null && requestInfo.get("userAgent") != null) ? 
                requestInfo.get("userAgent").toString() : "unknown";
                
        StringBuilder detailsBuilder = new StringBuilder();
        if (requestInfo != null) {
            requestInfo.forEach((key, value) -> {
                // Skip remoteAddr and userAgent as they're already used
                if (!key.equals("remoteAddr") && !key.equals("userAgent")) {
                    detailsBuilder.append(key).append(": ").append(value).append("; ");
                }
            });
        }
        
        logAuthEvent(apiName, username, status, ipAddress, userAgent, message, detailsBuilder.toString());
    }
    
    public void logBookingEvent(String apiName, String userId, Map<String, Object> requestInfo, 
            String status, String message) {
        
        String ipAddress = (requestInfo != null && requestInfo.get("remoteAddr") != null) ? 
                requestInfo.get("remoteAddr").toString() : "unknown";
                
        String userAgent = (requestInfo != null && requestInfo.get("userAgent") != null) ? 
                requestInfo.get("userAgent").toString() : "unknown";
                
        StringBuilder detailsBuilder = new StringBuilder();
        if (requestInfo != null) {
            requestInfo.forEach((key, value) -> {
                // Skip remoteAddr and userAgent as they're already used
                if (!key.equals("remoteAddr") && !key.equals("userAgent")) {
                    detailsBuilder.append(key).append(": ").append(value).append("; ");
                }
            });
        }
        
        // Add a booking style with blue color
        try {
            File file = new File(excelLogFilePath);
            Workbook workbook;
            
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
            } else {
                workbook = new XSSFWorkbook();
            }
            
            CellStyle bookingStyle = workbook.createCellStyle();
            bookingStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            bookingStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        logAuthEvent(apiName, userId, status, ipAddress, userAgent, message, detailsBuilder.toString());
    }    public void logFlightEvent(String apiName, String userId, Map<String, Object> flightInfo, 
            String eventType, String message) {
        
        // Extract key details from flightInfo
        String flightId = (flightInfo != null && flightInfo.get("flightId") != null) ? 
                flightInfo.get("flightId").toString() : "unknown";
                
        String reason = (flightInfo != null && flightInfo.get("reason") != null) ? 
                flightInfo.get("reason").toString() : "";
                
        String originalTime = (flightInfo != null && flightInfo.get("originalTime") != null) ? 
                flightInfo.get("originalTime").toString() : "";
                
        String newTime = (flightInfo != null && flightInfo.get("newTime") != null) ? 
                flightInfo.get("newTime").toString() : "";
        
        // Build additional details string
        StringBuilder detailsBuilder = new StringBuilder();
        if (flightInfo != null) {
            flightInfo.forEach((key, value) -> {
                // Skip fields we've already extracted
                if (!key.equals("flightId") && !key.equals("reason") && 
                    !key.equals("originalTime") && !key.equals("newTime")) {
                    detailsBuilder.append(key).append(": ").append(value).append("; ");
                }
            });
        }
        
        try {
            // Determine if file exists
            File file = new File(flightExcelLogFilePath);
            boolean isNewFile = !file.exists();
            
            // Read existing workbook or create new one
            Workbook workbook;
            if (isNewFile) {
                workbook = new XSSFWorkbook();
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
            }
            
            // Create cell styles for different event types
            CellStyle delayStyle = workbook.createCellStyle();
            delayStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            delayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle cancelStyle = workbook.createCellStyle();
            cancelStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());
            cancelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle scheduleStyle = workbook.createCellStyle();
            scheduleStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            scheduleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle failureStyle = workbook.createCellStyle();
            failureStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            failureStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Get or create flight sheet
            Sheet sheet = workbook.getSheet(flightSheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(flightSheetName);
                
                // Create header row with style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < FLIGHT_HEADERS.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(FLIGHT_HEADERS[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.autoSizeColumn(i);
                }
            }
            
            // Find the next row index
            int rowIndex = sheet.getLastRowNum() + 1;
            
            // Create the new row
            Row row = sheet.createRow(rowIndex);
            
            // Choose cell style based on event type
            CellStyle rowStyle;
            if (eventType.contains("DELAY")) {
                rowStyle = delayStyle;
            } else if (eventType.contains("CANCEL")) {
                rowStyle = cancelStyle;
            } else if (eventType.contains("SCHEDULE")) {
                rowStyle = scheduleStyle;
            } else if (eventType.contains("FAILURE")) {
                rowStyle = failureStyle;
            } else {
                rowStyle = scheduleStyle; // Default
            }
            
            // Fill data with style
            for (int i = 0; i < FLIGHT_HEADERS.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(rowStyle);
                
                switch (i) {
                    case 0: cell.setCellValue(LocalDateTime.now().format(FORMATTER)); break; // Timestamp
                    case 1: cell.setCellValue(apiName); break; // API
                    case 2: cell.setCellValue(userId); break; // Admin/UserID
                    case 3: cell.setCellValue(eventType); break; // Event Type
                    case 4: cell.setCellValue(flightId); break; // Flight ID
                    case 5: cell.setCellValue(reason); break; // Reason
                    case 6: cell.setCellValue(originalTime); break; // Original Time
                    case 7: cell.setCellValue(newTime); break; // New Time
                    case 8: cell.setCellValue(message); break; // Message
                    case 9: cell.setCellValue(detailsBuilder.toString()); break; // Details
                }
            }
              // Auto size columns
            for (int i = 0; i < FLIGHT_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write the workbook to the flight-specific Excel file
            try (FileOutputStream fileOut = new FileOutputStream(flightExcelLogFilePath)) {
                workbook.write(fileOut);
            }
            
            // Close the workbook
            workbook.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
