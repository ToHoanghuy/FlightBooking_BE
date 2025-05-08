package com.example.FlightBooking.Services.UserReview;

import com.example.FlightBooking.Models.UserReview;
import com.example.FlightBooking.Repositories.UserReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserReviewService {
    @Autowired
    private UserReviewRepository userReviewRepository;

    public void addReview(UserReview review) {
        userReviewRepository.save(review);
    }

}
