package org.styd.intproj.savorly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.styd.intproj.savorly.entity.Favourite;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.entity.Review;
import org.styd.intproj.savorly.entity.User;
import org.styd.intproj.savorly.exception.GlobalExceptionHandler;
import org.styd.intproj.savorly.repository.FavouriteRepository;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.ReviewRepository;
import org.styd.intproj.savorly.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("classpath:applicationTest.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Import(GlobalExceptionHandler.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RecipeRepository recipeRepository;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @MockitoBean
    private FavouriteRepository favouriteRepository;

    @Autowired
    private ObjectMapper objectMapper;


    private User mockUser;
    private Recipe mockRecipe;
    private Review mockReview;

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

        // Mock Review
        mockReview = new Review();
        mockReview.setId(100L);
        mockReview.setUser(mockUser);
        mockReview.setRecipe(mockRecipe);
        mockReview.setText("Test Review");
        mockReview.setDate(new Date());
    }

    @Test
    @DisplayName("Test get all Reviews for 1 Recipe - Success")
    void getReviews_success () throws Exception {
        Long recipeId = 10L;

        when(userRepository.findByUsername("felix")).thenReturn(mockUser);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(reviewRepository.getReviewsByRecipeId(recipeId)).thenReturn(List.of(mockReview));

        mockMvc.perform(get("/api/reviews/{recipeId}", recipeId)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODA5NjY2NywiZXhwIjoxNzM4MTgzMDY3fQ.GfquoWdYBE35yfloOwfKsE6gi6U1ktQ-gaa18efsg1I\"") // Mock JWT token
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockReview.getId()));
    }


    @Test
    @DisplayName("Test add a Review of 1 recipe - Success")
    void createReview_Success() throws Exception {
        Long recipeId = 10L;

        // Mock dependencies
        when(userRepository.findByUsername("felix")).thenReturn(mockUser);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe)); // Recipe exists
        when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(mockReview);

        mockMvc.perform(post("/api/reviews/{recipeId}", recipeId) // Fix incorrect API path
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODA5NjY2NywiZXhwIjoxNzM4MTgzMDY3fQ.GfquoWdYBE35yfloOwfKsE6gi6U1ktQ-gaa18efsg1I\"") // Use a valid JWT token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Great recipe!\"}")) // Mock JSON content
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.author.username").value("felix")) // Check response JSON
                .andExpect(jsonPath("$.id").value(100)); // Verify review ID
    }

    @Test
    @DisplayName("Test add a Review for 1 Recipe - User Not Found")
    void createReview_UserNotFound() throws Exception {
        Long recipeId = 10L;

        when(userRepository.findByUsername("felix")).thenReturn(null); // No user found

        mockMvc.perform(post("/api/reviews/{recipeId}", recipeId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODA5NjY2NywiZXhwIjoxNzM4MTgzMDY3fQ.GfquoWdYBE35yfloOwfKsE6gi6U1ktQ-gaa18efsg1I") // Mock JWT token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Great recipe!\"}"))
                .andExpect(status().isBadRequest()); //return a status code 400 bad request
    }

    @Test
    @DisplayName("Test add a Review for 1 Recipe - Recipe Not Found")
    void createReview_RecipeNotFound() throws Exception {
        Long recipeId = 10L;

        when(userRepository.findByUsername("felix")).thenReturn(mockUser);
        when(recipeRepository.findById(recipeId)).thenReturn(null); // Recipe NOT exists

        mockMvc.perform(post("/api/reviews/{recipeId}", recipeId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODA5NjY2NywiZXhwIjoxNzM4MTgzMDY3fQ.GfquoWdYBE35yfloOwfKsE6gi6U1ktQ-gaa18efsg1I") // Mock JWT token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Great recipe!\"}"))
                .andExpect(status().isBadRequest()); //return a status code 400 bad request
    }

    @Test
    void deleteFavourite() {
    }
}