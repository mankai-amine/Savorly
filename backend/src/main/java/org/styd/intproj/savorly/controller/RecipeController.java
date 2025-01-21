package org.styd.intproj.savorly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.entity.Recipe;
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
            //return ResponseEntity.ok(savedRecipe);
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

// Felix's Code
// package org.styd.intproj.savorly.controller;

// import org.springframework.data.domain.Page;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.styd.intproj.savorly.dto.RecipeResponse;
// import org.styd.intproj.savorly.dto.RecipeViewModel;
// import org.styd.intproj.savorly.entity.Recipe;
// import org.styd.intproj.savorly.service.RecipeService;

// import jakarta.validation.Valid;
// import java.util.List;

// @RestController
// @RequestMapping("/api/recipes")
// public class RecipeController {

//     private final RecipeService recipeService;

//     public RecipeController(RecipeService recipeService) {
//         this.recipeService = recipeService;
//     }

//     /**
//      * fuzzy search for name/ingredients/instructions
//      */
//     @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<RecipeResponse> searchRecipes(@RequestParam String field, @RequestParam String value) {
//         List<Recipe> recipes = recipeService.searchRecipes(field, value);

//         if (recipes.isEmpty()) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body(new RecipeResponse("notFound", recipes));
//         }

//         return ResponseEntity.ok(new RecipeResponse("success", recipes));
//     }

//     /**
//      * return all recipes with pageable
//      */
//     @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<Page<Recipe>> getAllRecipes(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
//         Page<Recipe> recipes = recipeService.getAllRecipes(page, size);
//         return ResponseEntity.ok(recipes);
//     }

//     /**
//      * return recipe with id
//      */
//     @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
//         Recipe recipe = recipeService.getRecipeById(id);
//         return ResponseEntity.ok(recipe);
//     }

//     /**
//      * create
//      */
//     @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeViewModel recipeViewModel) {
//         RecipeResponse response = recipeService.createRecipe(recipeViewModel);
//         System.out.println("response message : "+response.toString());
//         return ResponseEntity.status(HttpStatus.CREATED).body(response);
//     }

//     /**
//      * update
//      */
//     @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<RecipeResponse> updateRecipe(@Valid @RequestBody Recipe recipe) {
//         RecipeResponse response = recipeService.updateRecipe(recipe);
//         return ResponseEntity.ok(response);
//     }

//     /**
//      * delete
//      */
//     @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<Void> deleteRecipe(@RequestParam Long id) {
//         recipeService.deleteRecipe(id);
//         return ResponseEntity.noContent().build();
//     }

//     //create in transactional way
//     @PostMapping(value = "create",produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<RecipeResponse> createRecipeAndTagWithEmbedding(@Valid @RequestBody RecipeViewModel recipeViewModel) {
//         RecipeResponse response = recipeService.createRecipeAndTagWithEmbedding(recipeViewModel);
//         return ResponseEntity.ok(response);
//     }

//     //update in transactional way
//     @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<RecipeResponse> updateRecipeAndTagWithEmbedding(@Valid @RequestBody Recipe recipe) {
//         RecipeResponse response = recipeService.updateRecipeAndTagWithEmbedding(recipe);
//         return ResponseEntity.ok(response);
//     }
// }
