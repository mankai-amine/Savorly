package org.styd.intproj.savorly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.styd.intproj.savorly.entity.Favourite;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.entity.User;
import org.styd.intproj.savorly.exception.GlobalExceptionHandler;
import org.styd.intproj.savorly.repository.FavouriteRepository;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.UserRepository;
import org.styd.intproj.savorly.service.RecipeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Unit Test: Use @Mock and @InjectMocks, and Mockito is responsible for injecting mock dependencies.
//Integration Test: Use @MockBean or @MockitoBean to let the Spring Boot test environment manage mock dependencies.
//If you use @SpringBootTest but don't want to actually call the database, use @MockBean / @MockitoBean.
//If you only test a Service class and don't want Spring to get involved, use @Mock.

@TestPropertySource("classpath:applicationTest.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Import(GlobalExceptionHandler.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RecipeRepository recipeRepository;

    @MockitoBean
    private FavouriteRepository favouriteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private Recipe mockRecipe;
    private Favourite mockFavourite;

    @BeforeEach
    void setUp() {
        // Mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("felix");

        // Mock Recipe
        mockRecipe = new Recipe();
        mockRecipe.setId(10L);
        mockRecipe.setName("Test Recipe");

        // Mock Favourite
        mockFavourite = new Favourite();
        mockFavourite.setUser(mockUser);
        mockFavourite.setRecipe(mockRecipe);
    }


    @Test
    @DisplayName("Test add a favourite recipe - Success")
    void addFavourite_Success() throws Exception {
        Long recipeId = 10L;

        when(userRepository.findByUsername("felix")).thenReturn(mockUser);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(favouriteRepository.save(Mockito.any(Favourite.class))).thenReturn(mockFavourite);

        mockMvc.perform(post("/api/favourites/{recipeId}", recipeId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODE2MzU2MiwiZXhwIjoxNzM4MjQ5OTYyfQ.fYyvu6HS1lb0DRPBKVuIX8Cql-zKusevTcdZ7_i6qHA") // Mock JWT token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value(mockUser.getUsername()))
                .andExpect(jsonPath("$.recipe.id").value(mockRecipe.getId()));
    }


    @Test
    @DisplayName("Test add a favourite recipe - User Not Found")
    void addFavourite_UserNotFound() throws Exception {
        Long recipeId = 10L;

        when(userRepository.findByUsername("felix")).thenReturn(null); // No user found

        mockMvc.perform(post("/api/favourites/{recipeId}", recipeId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODE2MzU2MiwiZXhwIjoxNzM4MjQ5OTYyfQ.fYyvu6HS1lb0DRPBKVuIX8Cql-zKusevTcdZ7_i6qHA") // Mock JWT token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Test add a favourite recipe - Recipe Not Found")
    void addFavourite_RecipeNotFound() throws Exception {
        Long recipeId = 10L;

        Recipe mockRecipe = new Recipe();
        mockRecipe.setId(recipeId);
        mockRecipe.setName("favourite recipe");
        mockRecipe.setIngredients("sugar");
        mockRecipe.setInstructions("just 1 step:");

        // Mock authentication (Prevents 403 Forbidden)
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("felix");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext); // Set security context

        // Mock userRepository to return a valid user
        User mockUser = new User();
        mockUser.setUsername("felix");
        when(userRepository.findByUsername("felix")).thenReturn(mockUser);

        // Mock repository behavior
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty()); // Recipe NOT found

        mockMvc.perform(post("/api/favourites/{recipeId}", recipeId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODE2MzU2MiwiZXhwIjoxNzM4MjQ5OTYyfQ.fYyvu6HS1lb0DRPBKVuIX8Cql-zKusevTcdZ7_i6qHA") // Mock JWT token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect 404 when recipe is missing
    }


    @Test
    @DisplayName("Test add a favourite recipe - Unexpected Error")
    void addFavourite_BadRequest() throws Exception {
        Long recipeId = 10L;

        when(userRepository.findByUsername("felix")).thenReturn(mockUser);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(favouriteRepository.save(Mockito.any(Favourite.class))).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/favourites/{recipeId}", recipeId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODE2MzU2MiwiZXhwIjoxNzM4MjQ5OTYyfQ.fYyvu6HS1lb0DRPBKVuIX8Cql-zKusevTcdZ7_i6qHA") // Mock JWT token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}