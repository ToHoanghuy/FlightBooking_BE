package com.example.FlightBooking.Controller.UserReview;

import com.example.FlightBooking.Models.Airlines;
import com.example.FlightBooking.Models.UserReview;
import com.example.FlightBooking.Repositories.UserReviewRepository;
import com.example.FlightBooking.Services.AirlineService.AirlinesService;
import com.example.FlightBooking.Services.CloudinaryService.CloudinaryService;
import com.example.FlightBooking.Services.UserReview.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/review")
public class UserReviewController {
    @Autowired
    private UserReviewService userReviewService;

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private AirlinesService airlinesService;

    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> addReview(@RequestParam("userId") Long userId,
                                            @RequestParam("bookingId") Long bookingId,
                                            @RequestParam("reviewDetails") String reviewDetails,
                                            @RequestParam("starRating") Integer starRating,
                                            @RequestPart("file") MultipartFile file) throws IOException {

        try
        {
            String imgUrl = cloudinaryService.uploadUserReview(file);
            UserReview review = UserReview.builder()
                    .userId(userId)
                    .bookingId(bookingId)
                    .reviewDetails(reviewDetails)
                    .starRating(starRating)
                    .imgUrl(imgUrl)
                    .build();
            userReviewService.addReview(review);
            return ResponseEntity.ok("Review added successfully.");
        }
        catch (Exception e)
        {
            return ResponseEntity.ok("Error for add some element");
        }
    }
    @GetMapping
    public List<UserReview> getAllReview()
    {
        return userReviewRepository.findAll();
    }
    @GetMapping("/get-by-user-review-id")
    public ResponseEntity<UserReview> getUserReviewId(@RequestParam Long id)

    {
        try
        {
            return ResponseEntity.ok().body(userReviewRepository.findById(id).orElseThrow());
        }
        catch (Exception e)
        {
            return ResponseEntity.status(403).body(null);
        }
    }

}
