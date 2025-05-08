package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Enum.SupportRequestStatus;
import com.example.FlightBooking.Models.SupportSession;
import com.example.FlightBooking.Models.Users;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface SupportSessionRepository extends JpaRepository<SupportSession, Long> {
    SupportSession findByCustomerIdAndStatus(Long customerId, String status);
}
