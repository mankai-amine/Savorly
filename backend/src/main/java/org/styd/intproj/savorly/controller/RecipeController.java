package org.styd.intproj.savorly.controller;

import com.amazonaws.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.dto.RecipeResponse;
import org.styd.intproj.savorly.dto.RecipeViewModel;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.service.RecipeService;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.UserRepository;

import jakarta.validation.Valid;
import org.styd.intproj.savorly.service.S3Service;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    private UserRepository userRepository;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    S3Service s3Service;

    /**
     * fuzzy search for name/ingredients/instructions
     */
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeResponse> searchRecipes(@RequestParam String field, @RequestParam String value) {
        List<Recipe> recipes = recipeService.searchRecipes(field, value);

        if (recipes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RecipeResponse("notFound", recipes));
        }

        return ResponseEntity.ok(new RecipeResponse("success", recipes));
    }

    /**
     * return all recipes with pageable
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    /**
     * return recipe with id
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }

    /**
     * create
     */
    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeViewModel recipeViewModel, Authentication authentication) {
        RecipeResponse response = recipeService.createRecipe(recipeViewModel, authentication);
        System.out.println("response message : " + response.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/mine")
    public ResponseEntity<List<Recipe>> getMyRecipes(Authentication authentication) {
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);

        List<Recipe> recipes = recipeRepository.findByAuthorId(userId);

        for (Recipe recipe : recipes) {
            if (recipe.getPicture() != null && !recipe.getPicture().trim().isEmpty()) {
                String pictureUrl = recipe.getPicture();
                recipe.setPicture(s3Service.generateUrl(pictureUrl, HttpMethod.GET));
            }
        }

        return ResponseEntity.ok(recipes);
    }

    /**
     * update
     */
    @PutMapping(value = "/update/{recipeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable Long recipeId, @Valid @RequestBody Recipe recipe, Authentication authentication) {
        RecipeResponse response = recipeService.updateRecipe(recipe, recipeId, authentication);
        return ResponseEntity.ok(response);
    }

    /**
     * delete
     */
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id, Authentication authentication) {
        recipeService.deleteRecipe(id, authentication);
        return ResponseEntity.noContent().build();
    }

    //create in transactional way
    @PostMapping(value = "create",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeResponse> createRecipeAndTagWithEmbedding(@Valid @RequestBody RecipeViewModel recipeViewModel, Authentication authentication) {
        RecipeResponse response = recipeService.createRecipeAndTagWithEmbedding(recipeViewModel, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //update in transactional way
    @PutMapping(value = "/edit/{recipeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeResponse> updateRecipeAndTagWithEmbedding(@PathVariable Long recipeId, @Valid @RequestBody Recipe recipe, Authentication authentication) {
        RecipeResponse response = recipeService.updateRecipeAndTagWithEmbedding(recipe, recipeId, authentication);
        return ResponseEntity.ok(response);
    }

    //search with the embedding from openai, if fail to get embedding , will return a simple fuzzy search of name
    @GetMapping(value = "/search-embedding", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeResponse> searchNearestRecipes(@RequestParam String keyword) {
        List<Recipe> recipes = recipeService.findNearestRecipes(keyword);

        if (recipes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RecipeResponse("notFound", recipes));
        }

        for (Recipe recipe : recipes) {
            if (recipe.getPicture() != null && !recipe.getPicture().trim().isEmpty()) {
                String pictureUrl = recipe.getPicture();
                recipe.setPicture(s3Service.generateUrl(pictureUrl, HttpMethod.GET));
            }
        }

        return ResponseEntity.ok(new RecipeResponse("success", recipes));
    }

    private Long getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }

}
