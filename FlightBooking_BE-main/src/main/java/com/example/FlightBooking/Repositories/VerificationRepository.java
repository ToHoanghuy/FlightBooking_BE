package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.Verifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Hidden;

@Repository
@RepositoryRestResource
@Hidden
public interface VerificationRepository extends JpaRepository<Verifications, Long> {
    Optional<Verifications> findByCodeOTP(Long codeOTP);
    List<Verifications> deleteByExpireTime(LocalDateTime expireTime);

    Optional<Verifications> deleteByEmail(String email);
    Optional<Verifications> findByEmail(String email);

    //@Transactional
    //@Modifying
    //@Query("DELETE FROM Verification v WHERE v.expireTime < CURRENT_TIMESTAMP")
    //void deleteExpiredVerifications();

}
