package org.styd.intproj.savorly.service;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.Embedding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.styd.intproj.savorly.dto.RecipeResponse;
import org.styd.intproj.savorly.dto.RecipeViewModel;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.repository.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * get all recipes
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    /**
     * get pageable recipes
     */
    public Page<Recipe> getAllRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return recipeRepository.findAll(pageable);
    }

    /**
     * get single recipe
     */
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + id));
    }

    /**
     * fuzzy search for name/ingredients/instructions
     */
    public List<Recipe> searchRecipes(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        String likeValue = "%" + value.trim() + "%";

        return switch (field.toLowerCase()) {
            case "name" -> recipeRepository.findByNameLike(likeValue);
            case "ingredients" -> recipeRepository.findByIngredientsLike(likeValue);
            case "instructions" -> recipeRepository.findByInstructionsLike(likeValue);
            default -> throw new IllegalArgumentException("Invalid search field: " + field);
        };
    }

    //create
    @Transactional
    public RecipeResponse createRecipe(RecipeViewModel recipeViewModel) {
        validateRecipeInput(recipeViewModel); // call the overload method

        // check if exists the recipe with the same name
        if (recipeRepository.findByName(recipeViewModel.getName()).isPresent()) {
            throw new IllegalArgumentException("A recipe with the same name already exists");
        }

        // convert
        Recipe recipe = new Recipe();
        recipe.setName(recipeViewModel.getName());
        recipe.setIngredients(recipeViewModel.getIngredients());
        recipe.setInstructions(recipeViewModel.getInstructions());
        recipe.setPicture(recipeViewModel.getPicture());

        Recipe newRecipe = recipeRepository.save(recipe);
        logger.info("Created new recipe with ID: {}", newRecipe.getId());

        return new RecipeResponse("success", List.of(newRecipe));
    }

    /**
     * update
     */
    @Transactional
    public RecipeResponse updateRecipe(Recipe recipe) {
        validateRecipeInput(recipe);

        Recipe existingRecipe = recipeRepository.findById(recipe.getId())
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipe.getId()));

        // only when picture is null or empty, use the old data
        if (recipe.getPicture() == null || recipe.getPicture().trim().isEmpty()) {
            recipe.setPicture(existingRecipe.getPicture());
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return new RecipeResponse("success", List.of(updatedRecipe));
    }

    /**
     * delete
     */
    @Transactional
    public void deleteRecipe(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Recipe ID cannot be null");
        }
        if (!recipeRepository.existsById(id)) {
            throw new EntityNotFoundException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
        logger.info("Deleted recipe with id: {}", id);
    }

    /**
     * validation for id and name
     */
    private void validateRecipeInput(RecipeViewModel recipeViewModel) {
        if (recipeViewModel.getName() == null || recipeViewModel.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("The name of recipe cannot be empty");
        }
    }

    //overload
    private void validateRecipeInput(Recipe recipe) {
        if (recipe.getId() == null) {
            throw new IllegalArgumentException("Recipe ID cannot be null");
        }
        if (recipe.getName() == null || recipe.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("The name of recipe cannot be empty");
        }
    }
}