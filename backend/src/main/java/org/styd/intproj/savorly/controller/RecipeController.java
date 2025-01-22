package org.styd.intproj.savorly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.repository.FavouriteRepository;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private FavouriteRepository favouriteRepository;


    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Recipe>> getMyRecipes(Authentication authentication) {
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);

        List<Recipe> recipes = recipeRepository.findByAuthorId(userId);

        return ResponseEntity.ok(recipes);
    }

    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe, Authentication authentication) {
        String username = authentication.getName();

        try {
            recipe.setAuthorId(getUserIdFromUsername(username));
            Recipe savedRecipe = recipeRepository.save(recipe);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("edit/{id}")
    public ResponseEntity<Recipe> editRecipe(@PathVariable Long id, @RequestBody Recipe recipe, Authentication authentication) {
        String username = authentication.getName();

        try {
            Long currUserId = getUserIdFromUsername(username);

            Recipe toEdit = recipeRepository.findById(id).orElse(null);
            if (toEdit == null) {
                return ResponseEntity.notFound().build();
            }

            Long recipeAuthorId = toEdit.getAuthorId();
            if (!recipeAuthorId.equals(currUserId)) {
                return ResponseEntity.badRequest().build();
            }

            // TODO check if fields were edited, only overwrite in that case
            toEdit.setName(recipe.getName());
            toEdit.setIngredients(recipe.getIngredients());
            toEdit.setInstructions(recipe.getInstructions());
            toEdit.setPicture(recipe.getPicture());
            recipeRepository.save(toEdit);

            return ResponseEntity.ok(toEdit);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();

        try {
            Long currUserId = getUserIdFromUsername(username);

            Recipe toDelete = recipeRepository.findById(id).orElse(null);
            if (toDelete == null) {
                return ResponseEntity.notFound().build();
            }

            Long recipeAuthorId = toDelete.getAuthorId();
            if (!recipeAuthorId.equals(currUserId)) {
                return ResponseEntity.badRequest().build();
            }

            recipeRepository.delete(toDelete);

            return ResponseEntity.ok(toDelete);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Long getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }

}
