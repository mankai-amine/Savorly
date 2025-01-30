package org.styd.intproj.savorly.service;
import com.amazonaws.HttpMethod;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.Embedding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.styd.intproj.savorly.dto.RecipeResponse;
import org.styd.intproj.savorly.dto.RecipeViewModel;
import org.styd.intproj.savorly.dto.TagPassModel;
import org.styd.intproj.savorly.dto.TagResponse;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.entity.Tag;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.TagRepository;
import org.styd.intproj.savorly.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EmbeddingService embeddingService; //service class dependency injection for getting embedding

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private S3Service s3Service;


    /**
     * get all recipes
     */
    public List<Recipe> getAllRecipes() {

        List<Recipe> allRecipes =  recipeRepository.findAll();
        // get pre-signed link, lambda
        allRecipes.forEach(recipe -> {
            if (recipe.getPicture() != null && !recipe.getPicture().trim().isEmpty()) {
                recipe.setPicture(s3Service.generateUrl(recipe.getPicture(),HttpMethod.GET));
            }
        });

        return allRecipes;
    }


    /**
     * get pageable recipes
     */
    public Page<Recipe> getAllRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Recipe> pageableRecipes = recipeRepository.findAll(pageable);

        // Convert Page to List to modify elements
        List<Recipe> modifiedRecipes = pageableRecipes.getContent().stream()
                .peek(recipe -> {
                    if (recipe.getPicture() != null && !recipe.getPicture().trim().isEmpty()) {
                        recipe.setPicture(s3Service.generateUrl(recipe.getPicture(), HttpMethod.GET));
                    }
                })
                .collect(Collectors.toList());

        // Return a new PageImpl with modified content
        return new PageImpl<>(modifiedRecipes, pageable, pageableRecipes.getTotalElements());
    }

    /**
     * get single recipe
     */
    public Recipe getRecipeById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isEmpty()) {
            throw new EntityNotFoundException("Recipe with id " + id + " not found");
        }
        if (recipe.get().getPicture() != null && !recipe.get().getPicture().trim().isEmpty()) {
            String pictureUrl = recipe.get().getPicture();
            recipe.get().setPicture(s3Service.generateUrl(pictureUrl, HttpMethod.GET));
        }
        return recipe.get();
    }

    /**
     * fuzzy search for name/ingredients/instructions
     */
    public List<Recipe> searchRecipes(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        String likeValue = "%" + value.trim() + "%";

        List<Recipe> fuzzySearchRecipes =  switch (field.toLowerCase()) {
            case "name" -> recipeRepository.findByNameLike(likeValue);
            case "ingredients" -> recipeRepository.findByIngredientsLike(likeValue);
            case "instructions" -> recipeRepository.findByInstructionsLike(likeValue);
            default -> throw new IllegalArgumentException("Invalid search field: " + field);
        };

        fuzzySearchRecipes.forEach(recipe -> {
            if (recipe.getPicture() != null && !recipe.getPicture().trim().isEmpty()) {
                recipe.setPicture(s3Service.generateUrl(recipe.getPicture(), HttpMethod.GET));
            }
        });
        return fuzzySearchRecipes;
    }

    //create
    @Transactional
    public RecipeResponse createRecipe(RecipeViewModel recipeViewModel, Authentication authentication) {
        validateRecipeInput(recipeViewModel); // call the overload method

        // check if exists the recipe with the same name
        if (recipeRepository.findByName(recipeViewModel.getName()).isPresent()) {
            throw new IllegalArgumentException("A recipe with the same name already exists");
        }

        Long currUserId;
        try {
            String username = authentication.getName();
            currUserId = getUserIdFromUsername(username);
        } catch (Exception e) {
            throw new EntityNotFoundException("User not found");
        }

        // convert
        Recipe recipe = new Recipe();
        recipe.setName(recipeViewModel.getName());
        recipe.setIngredients(recipeViewModel.getIngredients());
        recipe.setInstructions(recipeViewModel.getInstructions());
        recipe.setPicture(recipeViewModel.getPicture());
        recipe.setAuthorId(currUserId);

        Recipe newRecipe = recipeRepository.save(recipe);
        logger.info("Created new recipe with ID: {}", newRecipe.getId());

        return new RecipeResponse("success", List.of(newRecipe));
    }

    /**
     * update
     */
    @Transactional
    public RecipeResponse updateRecipe(Recipe recipe, Long recipeId, Authentication authentication) {
        Long currUserId;
        try {
            String username = authentication.getName();
            currUserId = getUserIdFromUsername(username);
        } catch (Exception e) {
            throw new EntityNotFoundException("User not found");
        }
        recipe.setId(recipeId);
        //recipe.setAuthorId(currUserId);

        validateRecipeInput(recipe);

        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipe.getId()));

        if (!existingRecipe.getAuthorId().equals(currUserId)) {
            throw new IllegalArgumentException("This recipe belongs to a different user.");
        }

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
    public void deleteRecipe(Long id, Authentication authentication) {
        Long currUserId;
        try {
            String username = authentication.getName();
            currUserId = getUserIdFromUsername(username);
        } catch (Exception e) {
            throw new EntityNotFoundException("User not found");
        }

        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + id));

        if (!existingRecipe.getAuthorId().equals(currUserId)) {
            throw new IllegalArgumentException("This recipe belongs to a different user.");
        }

        recipeRepository.delete(existingRecipe);
        logger.info("Deleted recipe with id: {}", id);
    }

    //create new record both in recipes table and tags table
    @Transactional //transaction will roll back only when runtime exception or error
    public RecipeResponse createRecipeAndTagWithEmbedding(RecipeViewModel recipeViewModel, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new EntityNotFoundException("User not found");
        }

        Long currUserId;
        try {
            String username = authentication.getName();
            currUserId = getUserIdFromUsername(username);
        } catch (Exception e) {
            throw new EntityNotFoundException("User not found");
        }

        validateRecipeInput(recipeViewModel);
        String textToEmbedding = recipeViewModel.getName()+" "+recipeViewModel.getIngredients()+" "+recipeViewModel.getInstructions();
        List<Embedding> embeddings =  embeddingService.getEmbeddings(List.of(textToEmbedding));

        if (embeddings.isEmpty()) {
            throw new RuntimeException("Failed to generate embeddings for recipe.");
        }

        float[] embeddingArray = embeddings.getFirst().getOutput();

        Recipe recipe = new Recipe();

        recipe.setName(recipeViewModel.getName());
        recipe.setIngredients(recipeViewModel.getIngredients());
        recipe.setInstructions(recipeViewModel.getInstructions());
        recipe.setPicture(recipeViewModel.getPicture());
        recipe.setAuthorId(currUserId);

        Tag tag = new Tag();

        tag.setTitle(recipe.getName());
        tag.setIngredients(recipe.getIngredients());
        tag.setDescription(recipe.getInstructions());
        tag.setEmbedding(embeddingArray);

        //2-way association
        tag.setRecipe(recipe);
        recipe.setTag(tag);

        //save first and then associate
        Recipe savedRecipe = recipeRepository.save(recipe);

        return new RecipeResponse("success", List.of(savedRecipe));

    }

    @Transactional // update exists record both in recipes table and tags table
    public RecipeResponse updateRecipeAndTagWithEmbedding(Recipe recipe, Long recipeId, Authentication authentication) {
        Long currUserId;
        try {
            String username = authentication.getName();
            currUserId = getUserIdFromUsername(username);
        } catch (Exception e) {
            throw new EntityNotFoundException("User not found");
        }

        // Step 1: get exists recipe
        Recipe existsRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with ID: " + recipeId));

        if (!existsRecipe.getAuthorId().equals(currUserId)) {
            throw new IllegalArgumentException("This recipe belongs to a different user.");
        }

        // Step 2: get exists tags
        Tag existsTag = existsRecipe.getTag();
        if (existsTag == null) {
            throw new RuntimeException("Tag not found for Recipe ID: " + recipeId);
        }

        // Step 3: generate new embedding
        String textToEmbedding = recipe.getName() + " " + recipe.getIngredients() + " " + recipe.getInstructions();
        List<Embedding> embeddings = embeddingService.getEmbeddings(List.of(textToEmbedding));
        if (embeddings.isEmpty()) {
            throw new RuntimeException("Failed to generate embeddings for recipe.");
        }
        float[] embeddingArray = embeddings.getFirst().getOutput();

        // Step 4: update fields in recipe
        existsRecipe.setAuthorId(Optional.ofNullable(recipe.getAuthorId()).orElse(existsRecipe.getAuthorId()));
        existsRecipe.setId(existsRecipe.getId());
        existsRecipe.setName(Optional.ofNullable(recipe.getName()).orElse(existsRecipe.getName()));
        existsRecipe.setIngredients(Optional.ofNullable(recipe.getIngredients()).orElse(existsRecipe.getIngredients()));
        existsRecipe.setInstructions(Optional.ofNullable(recipe.getInstructions()).orElse(existsRecipe.getInstructions()));
        existsRecipe.setPicture(Optional.ofNullable(recipe.getPicture()).orElse(existsRecipe.getPicture()));
        // TODO FIX empty string picture overwrite

        // Step 5: update fields in tag
        existsTag.setId(existsTag.getId());
        existsTag.setTitle(existsRecipe.getName());
        existsTag.setIngredients(existsRecipe.getIngredients());
        existsTag.setDescription(existsRecipe.getInstructions());
        existsTag.setEmbedding(embeddingArray);

        existsTag.setRecipe(existsRecipe);
        existsRecipe.setTag(existsTag);

        // Step 6: save in recipes table, not in tags table manually
        try {
            Recipe updatedRecipe = recipeRepository.save(existsRecipe);
            return new RecipeResponse("success", List.of(updatedRecipe));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update recipe and tag", e);
        }
    }

    //find nearest tags with embedding or title, and then return the recipe  corresponding to the tagid
    public List<Recipe> findNearestRecipes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword is required");
        }
        //step1 : get the embedding of the list of the name field
        List<Embedding> embeddingArray = embeddingService.getEmbeddings(Arrays.asList(keyword.split("\\s+")));
        //step2 :
        //if failed to get openai embedding, get the fuzzy search of the keyword, else the nearest L2 distance tags
        List<Tag> tags = embeddingArray.isEmpty() ? tagRepository.findByTitleLike("%" + keyword + "%")
                : tagRepository.findNearestTags(embeddingArray.getFirst().getOutput());
        //step3 : return the recipe with the tagId
        List<Recipe> recipes = new ArrayList<>();
        //find the recipe with the tagId
        for (Tag tag : tags) {
            recipes.add(recipeRepository.findById(tag.getRecipe().getId()).orElseThrow(null)); //orElse can not be used here
        }

        return recipes;
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

    private Long getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }
}