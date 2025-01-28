package org.styd.intproj.savorly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.styd.intproj.savorly.dto.RecipeResponse;
import org.styd.intproj.savorly.dto.RecipeViewModel;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.repository.RecipeRepository;
import org.styd.intproj.savorly.repository.UserRepository;
import org.styd.intproj.savorly.service.PdfWithS3Service;
import org.styd.intproj.savorly.service.RecipeService;
import org.styd.intproj.savorly.service.S3Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("classpath:applicationTest.properties")
@AutoConfigureMockMvc
@SpringBootTest
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeService   recipeService;


    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Test get an exists recipe.")
    @Test
    void testSearchRecipes_Success() throws Exception {
        // Mocked Recipe data
        String field = "name";
        String value = "spanish";

        // Act & Assert

        mockMvc.perform(get("/api/recipes/search")
                        .param("field", field)
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$.message").value("success"));  // Check response message
    }

    @DisplayName("Test get a NO exists recipe.")
    @Test
    void testSearchRecipes_NotFound() throws Exception {
        // Mocked Recipe data
        String field = "instructions";
        String value = "hongkong";

        // Act & Assert

        mockMvc.perform(get("/api/recipes/search")
                        .param("field", field)
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())  // Expect 200 OK
                .andExpect(jsonPath("$.message").value("notFound"));  // Check response message
    }

    @DisplayName("Test get ALL recipes.")
    @Test
    void testGetAllRecipes_Success() throws Exception {

        mockMvc.perform(get("/api/recipes/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test get a recipe by an exists id.")
    @Test
    void testGetRecipeById_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/{id}",1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test get a recipe by NOT exists id.")
    @Test
    void testGetRecipeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/recipes/{id}",999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Test get a recipe by string.")
    @Test
    void testGetRecipeById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/recipes/{id}","abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Test get ALL my recipes WITHOUT authentication.")
    @Test
    void getMyRecipes_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/recipes/mine")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @DisplayName("Test get ALL my recipes with authentication.")
    @Test
    void getMyRecipes_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/mine")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWxpeCIsImlhdCI6MTczODAyNDM0OSwiZXhwIjoxNzM4MTEwNzQ5fQ.Yf5MyJSFDPM2XULNis28XrPa-Yh-e2cj6FV_VMzYvR0") // Mock JWT token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRecipe() {
    }

    @DisplayName("Test create recipe with authentication.")
    @Test
    void testCreateRecipeAndTagWithEmbedding_Success() throws Exception {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        recipeViewModel.setName("Test Recipe");
        recipeViewModel.setIngredients("Flour, Sugar");
        recipeViewModel.setInstructions("Mix and bake");
        recipeViewModel.setPicture("");

        Recipe recipe = new Recipe();
        recipe.setId(7L);
        recipe.setName("Test Recipe");
        recipe.setIngredients("Flour, Sugar");
        recipe.setInstructions("Mix and bake");
        recipe.setPicture("");
        recipe.setAuthorId(1L);

        when(recipeService.createRecipeAndTagWithEmbedding(Mockito.any(RecipeViewModel.class), Mockito.any(Authentication.class)))
                .thenReturn(new RecipeResponse("success", List.of(recipe)));

        mockMvc.perform(post("/api/recipes/create")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeViewModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"));

        // ✅ Verify that the mock was actually used
        verify(recipeService, times(1)).createRecipeAndTagWithEmbedding(Mockito.any(), Mockito.any());
    }



}