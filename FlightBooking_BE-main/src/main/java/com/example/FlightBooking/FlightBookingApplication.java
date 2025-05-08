package com.example.FlightBooking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "FlightBooking API Document", version = "1.0.0", description = "This is OpenAPI for Flight Booking Management apis."))
public class FlightBookingApplication {
	@Value("${openai.api-key}")
	private String openaiApiKey;
	public static void main(String[] args) {
		SpringApplication.run(FlightBookingApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate()
	{
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(((request, body, execution) -> {
			request.getHeaders().add("Authorization", "Bearer "+openaiApiKey);
			return  execution.execute(request, body);
		}));
		return restTemplate;
	}
}
