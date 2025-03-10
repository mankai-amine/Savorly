package org.styd.intproj.savorly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.entity.Review;
import org.styd.intproj.savorly.entity.User;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.ReviewRepository;
import org.styd.intproj.savorly.repository.UserRepository;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/{recipeId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long recipeId) {
        try {
            List<Review> recipeReviews = reviewRepository.getReviewsByRecipeId(recipeId);
            return ResponseEntity.ok(recipeReviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<Review> createReview(@PathVariable Long recipeId, @RequestBody String text, Authentication authentication) {

        try {
            String username = authentication.getName();

            User user = userRepository.findByUsername(username);
            if (user == null) {
                System.err.println("User not found: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            System.out.println("Authenticated username: " + user);

            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Review review = new Review();
            review.setUser(user);
            review.setRecipe(recipe);
            review.setText(text);
            review.setDate(new Date());
            Review savedReview = reviewRepository.save(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Review> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        String username = authentication.getName();

        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Review toDelete = reviewRepository.findById(reviewId).orElse(null);
            if (toDelete == null) {
                return ResponseEntity.notFound().build();
            }

            reviewRepository.delete(toDelete);

            return ResponseEntity.ok(toDelete);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
