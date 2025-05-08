package com.example.FlightBooking.Services.ReviewService;


import com.example.FlightBooking.Models.Reason;
import com.example.FlightBooking.Models.Review;
import com.example.FlightBooking.Repositories.ReviewRepository;
import com.example.FlightBooking.Services.CloudinaryService.CloudinaryService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public Review createReviewWithImages(Review review) {
        return reviewRepository.save(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}

