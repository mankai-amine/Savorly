package org.styd.intproj.savorly.controller;

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
    private AuthenticationManager authenticationManager;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Recipe>> getFavoriteRecipes(@PathVariable Long userId) {
        List<Recipe> favoriteRecipes = favouriteRepository.getFavouriteByUserId(userId);
        return ResponseEntity.ok(favoriteRecipes);
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<Favourite> addFavorite(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();

        try {
            // Fetch the current user
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            User user = userOptional.get();

            // Fetch the recipe
            Optional<Recipe> recipeOptional = recipeRepository.findById(id);
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Favourite> deleteFavorite(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();

        try {
            Long currUserId = userRepository.findByUsername(username).getId();

            Favourite toDelete = favouriteRepository.findById(id).orElse(null);
            if (toDelete == null) {
                return ResponseEntity.notFound().build();
            }

            Long recipeUserId = toDelete.getUser().getId();
            if (!recipeUserId.equals(currUserId)) {
                return ResponseEntity.badRequest().build();
            }

            favouriteRepository.delete(toDelete);

            return ResponseEntity.ok(toDelete);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
