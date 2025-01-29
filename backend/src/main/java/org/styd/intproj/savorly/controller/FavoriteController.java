package org.styd.intproj.savorly.controller;

import com.amazonaws.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.entity.Favourite;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.entity.User;
import org.styd.intproj.savorly.repository.FavouriteRepository;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.UserRepository;
import org.styd.intproj.savorly.service.S3Service;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favourites")
public class FavoriteController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    S3Service s3Service;

    @GetMapping
    public ResponseEntity<List<Recipe>> getFavouriteRecipes(Authentication authentication) {
        String username = authentication.getName();
        try {
            Long currUserId = userRepository.findByUsername(username).getId();
            List<Recipe> favouriteRecipes = favouriteRepository.getFavouriteByUserId(currUserId);

            for (Recipe recipe : favouriteRecipes) {
                if (recipe.getPicture() != null && !recipe.getPicture().trim().isEmpty()) {
                    String pictureUrl = recipe.getPicture();
                    recipe.setPicture(s3Service.generateUrl(pictureUrl, HttpMethod.GET));
                }
            }

            return ResponseEntity.ok(favouriteRecipes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<Favourite> addFavourite(@PathVariable Long recipeId, Authentication authentication) {
        String username = authentication.getName();

        try {
            // Fetch the current user
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            User user = userOptional.get();

            // Fetch the recipe
            Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
            if (recipeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Recipe recipe = recipeOptional.get();

            Favourite favourite = new Favourite();
            favourite.setUser(user);
            favourite.setRecipe(recipe);

            Favourite savedFavourite = favouriteRepository.save(favourite);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFavourite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Favourite> deleteFavourite(@PathVariable Long recipeId, Authentication authentication) {
        String username = authentication.getName();

        try {
            Long currUserId = userRepository.findByUsername(username).getId();

            Favourite toDelete = favouriteRepository.findByRecipeIdAndUserId(recipeId, currUserId);
            if (toDelete == null) {
                return ResponseEntity.notFound().build();
            }

            favouriteRepository.delete(toDelete);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
