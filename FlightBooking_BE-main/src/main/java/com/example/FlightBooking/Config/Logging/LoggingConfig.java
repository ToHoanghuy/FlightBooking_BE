package com.example.FlightBooking.Config.Logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import jakarta.annotation.PostConstruct;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Configuration
public class LoggingConfig {

    @Value("${logging.file.booking:logs/booking.log}")
    private String bookingLogFile;
    
    @Value("${logging.file.flight:logs/flight.log}")
    private String flightLogFile;
    
    @Value("${logging.pattern.file:%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n}")
    private String filePattern;
    
    @PostConstruct
    public void init() {
        configureBookingLogger();
        configureFlightLogger();
    }
    
    private void configureBookingLogger() {
        try {
            // Create logs directory if it doesn't exist
            File logDir = ResourceUtils.getFile("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            
            // Configure booking logger
            Logger bookingLogger = loggerContext.getLogger("booking-logger");
            bookingLogger.setAdditive(false); // Don't inherit appenders from parent
            
            // Clear existing appenders
            bookingLogger.detachAndStopAllAppenders();
            
            // Create file appender for booking logs
            RollingFileAppender<ch.qos.logback.classic.spi.ILoggingEvent> bookingAppender = new RollingFileAppender<>();
            bookingAppender.setContext(loggerContext);
            bookingAppender.setName("booking-file-appender");
            bookingAppender.setFile(bookingLogFile);
            
            // Set encoder pattern
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern(filePattern);
            encoder.setCharset(StandardCharsets.UTF_8);
            encoder.start();
            bookingAppender.setEncoder(encoder);
            
            // Set rolling policy
            SizeAndTimeBasedRollingPolicy<ch.qos.logback.classic.spi.ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
            rollingPolicy.setContext(loggerContext);
            rollingPolicy.setParent(bookingAppender);
            rollingPolicy.setFileNamePattern("logs/booking.%d{yyyy-MM-dd}.%i.log");
            rollingPolicy.setMaxHistory(10); // Keep 10 log files
            rollingPolicy.setMaxFileSize(FileSize.valueOf("10MB"));
            rollingPolicy.setTotalSizeCap(FileSize.valueOf("100MB"));
            rollingPolicy.start();
            
            bookingAppender.setRollingPolicy(rollingPolicy);
            bookingAppender.start();
            
            bookingLogger.addAppender(bookingAppender);
        } catch (Exception e) {
            System.err.println("Error configuring booking logger: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void configureFlightLogger() {
        try {
            // Create logs directory if it doesn't exist
            File logDir = ResourceUtils.getFile("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            
            // Configure flight logger
            Logger flightLogger = loggerContext.getLogger("flight-logger");
            flightLogger.setAdditive(false); // Don't inherit appenders from parent
            
            // Clear existing appenders
            flightLogger.detachAndStopAllAppenders();
            
            // Create file appender for flight logs
            RollingFileAppender<ch.qos.logback.classic.spi.ILoggingEvent> flightAppender = new RollingFileAppender<>();
            flightAppender.setContext(loggerContext);
            flightAppender.setName("flight-file-appender");
            flightAppender.setFile(flightLogFile);
            
            // Set encoder pattern
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern(filePattern);
            encoder.setCharset(StandardCharsets.UTF_8);
            encoder.start();
            flightAppender.setEncoder(encoder);
            
            // Set rolling policy
            SizeAndTimeBasedRollingPolicy<ch.qos.logback.classic.spi.ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
            rollingPolicy.setContext(loggerContext);
            rollingPolicy.setParent(flightAppender);
            rollingPolicy.setFileNamePattern("logs/flight.%d{yyyy-MM-dd}.%i.log");
            rollingPolicy.setMaxHistory(10); // Keep 10 log files
            rollingPolicy.setMaxFileSize(FileSize.valueOf("10MB"));
            rollingPolicy.setTotalSizeCap(FileSize.valueOf("100MB"));
            rollingPolicy.start();
            
            flightAppender.setRollingPolicy(rollingPolicy);
            flightAppender.start();
            
            flightLogger.addAppender(flightAppender);
        } catch (Exception e) {
            System.err.println("Error configuring flight logger: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
