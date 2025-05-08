package com.example.FlightBooking.Controller.Review;


import com.example.FlightBooking.Models.Reason;
import com.example.FlightBooking.Models.Review;
import com.example.FlightBooking.Services.CloudinaryService.CloudinaryService;
import com.example.FlightBooking.Services.ReviewService.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/reviews")
@Tag(name = "Review Controller ", description = "Review for some place")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper; // Để chuyển đổi JSON sang đối tượng

    @GetMapping("/getAllReview")
    public List<Review> getAllReviews() {
        return reviewService.findAll();
    }

    @GetMapping("getByID/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public Review createReview(@RequestPart("review") String reviewJson,
                               @RequestPart("reviewFiles") List<MultipartFile> reviewFiles,
                               @RequestPart("questionFiles") List<MultipartFile> questionFiles) throws IOException {
        // Chuyển đổi reviewJson sang đối tượng Review
        Review review = objectMapper.readValue(reviewJson, Review.class);

        // Upload review files và lưu các URL
        List<String> reviewFileUrls = new ArrayList<>();
        for (MultipartFile file : reviewFiles) {
            String url = cloudinaryService.uploadReviewImage(file);
            reviewFileUrls.add(url);
        }
        review.setListImage(reviewFileUrls);

        // Upload question files và lưu các URL
        List<Reason> reasons = review.getList();
        int i = 0;
        for (MultipartFile file : questionFiles) {
            String url = cloudinaryService.uploadReviewImage(file);
            reasons.get(i).setImage(url);
            reasons.get(i).setImageAlt(file.getOriginalFilename());
            i++;
        }
        review.setList(reasons);

        // Lưu review với các URL của tệp tin
        return reviewService.createReviewWithImages(review);
    }


    @PutMapping("update/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review review) {
        review.setId(id);
        return reviewService.save(review);
    }

    @DeleteMapping("delete/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
    }
}
