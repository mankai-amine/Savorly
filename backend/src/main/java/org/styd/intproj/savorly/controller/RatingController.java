package org.styd.intproj.savorly.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.dto.RatingDTO;
import org.styd.intproj.savorly.entity.Rating;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.entity.User;
import org.styd.intproj.savorly.repository.RatingRepository;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.UserRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/user/{recipeId}")
    public ResponseEntity<Integer> getUserRecipeRating(@PathVariable Long recipeId,
                                                             Authentication authentication) {
        String username = authentication.getName();
        try {
            Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
            if (recipeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Long currRecipeId = recipeOptional.get().getId();

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Long currUserId = userOptional.get().getId();

            Optional<Rating> ratingOptional = Optional.ofNullable(
                    ratingRepository.findByRecipeIdAndUserId(currRecipeId, currUserId));
            if (ratingOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Integer currRating = ratingOptional.get().getRating();

            return ResponseEntity.ok(currRating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<Double> getAverageRatingForRecipe(@PathVariable Long recipeId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Long currRecipeId = recipeOptional.get().getId();

        Optional<Double> averageRating = Optional.ofNullable(ratingRepository.getAverageRatingByRecipeId(currRecipeId));
        if (averageRating.isEmpty()) {
            Double noRatings = 0.0;
            return ResponseEntity.ok().body(noRatings);
        }
        return ResponseEntity.ok().body(averageRating.get());
    }

    @PostMapping("/create/{recipeId}")
    public ResponseEntity<Rating> addRating(@PathVariable Long recipeId,
                                            @Valid @RequestBody RatingDTO ratingDTO,
                                            Authentication authentication) {
        String username = authentication.getName();
        try {
            Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
            if (recipeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Recipe currRecipe = recipeOptional.get();

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            User currUser = userOptional.get();

            if (ratingRepository.findByRecipeIdAndUserId(currRecipe.getId(), currUser.getId()) != null) {
                return ResponseEntity.badRequest().build();
            }

            Rating rating = new Rating();
            rating.setAuthor(currUser);
            rating.setRecipe(currRecipe);
            rating.setRating(ratingDTO.getRating());
            ratingRepository.save(rating);

            return ResponseEntity.status(HttpStatus.CREATED).body(rating);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
