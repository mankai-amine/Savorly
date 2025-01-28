package org.styd.intproj.savorly.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.itextpdf.text.DocumentException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.service.PdfWithS3Service;
import org.styd.intproj.savorly.service.RecipeService;
import org.styd.intproj.savorly.service.S3Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@TestPropertySource("classpath:applicationTest.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class PdfControllerTest {


    @Autowired
    private PdfController pdfController;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RecipeService recipeService;

    @Mock
    private PdfWithS3Service pdfWithS3Service;

    @Mock
    private S3Service s3Service;

    @DisplayName("Test post an exists long number.")
    @Test
    public void testExportPdf_Success() throws Exception {
        // Arrange
        Long recipeId = 1L;
        Recipe mockRecipe = new Recipe();
        mockRecipe.setId(recipeId);
        mockRecipe.setName("Test Recipe");
        mockRecipe.setIngredients("Test Ingredients");
        mockRecipe.setInstructions("Test Instructions");
        mockRecipe.setPicture("Test Picture");

        when(recipeService.getRecipeById(recipeId)).thenReturn(mockRecipe);
        when(pdfWithS3Service.generatedPdfStream(any())).thenReturn(new ByteArrayOutputStream());

        // Act & Assert
        mockMvc.perform(get("/api/pdf/{id}", recipeId)
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk());
    }

    @DisplayName("Test post a negative long number.")
    @Test
    public void testExportPdf_RecipeNotFound() throws Exception {
        // Arrange
        Long recipeId = -99L;
        when(recipeService.getRecipeById(recipeId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/pdf/{id}", recipeId)
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Test post a string.")
    @Test
    public void testExportPdf_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pdf/{id}", "cc")
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().is5xxServerError());
    }
}
