package com.example.FlightBooking.Repositories.Decorator;

import com.example.FlightBooking.Models.Decorator.Vouchers;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
@Hidden
public interface VoucherRepository extends JpaRepository<Vouchers, Long> {
    @Query("SELECT v FROM Vouchers v WHERE v.code = ?1 AND FUNCTION('DATE', v.createdAt) = CURRENT_DATE")
    Optional<Vouchers> findByCodeAndCreatedToday(String code);

}
