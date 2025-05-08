package com.example.FlightBooking.Repositories;


import com.example.FlightBooking.Enum.Roles;
import com.example.FlightBooking.Models.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Hidden;

@Repository
@RepositoryRestResource
@Hidden
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(Long id);
    Optional<Users> findByStripeCustomerId(String stripeCustomerId);
    List<Users> findByRole(Roles role);
}


