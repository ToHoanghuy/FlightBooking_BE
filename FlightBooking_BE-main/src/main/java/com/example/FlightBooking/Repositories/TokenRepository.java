package com.example.FlightBooking.Repositories;

import com.example.FlightBooking.Models.Tokens;
import com.example.FlightBooking.Models.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Hidden;
import springfox.documentation.annotations.ApiIgnore;

@Repository
@RepositoryRestResource
@Hidden
public interface TokenRepository extends JpaRepository<Tokens, Long> {

}
