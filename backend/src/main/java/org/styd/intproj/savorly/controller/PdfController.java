package org.styd.intproj.savorly.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.service.RecipeService;
import org.styd.intproj.savorly.entity.Recipe;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;
import org.styd.intproj.savorly.service.PdfWithS3Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private RecipeService recipeservice;

    @Autowired
    private PdfWithS3Service pdfWithS3Service;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id, Authentication authentication) throws  IOException, DocumentException {
        if(id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Recipe recipe = recipeservice.getRecipeById(id);
        if(recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Recipe> recipes = List.of(recipe);
        List<Map<String, Object>> quereyResults = recipes.stream().map(recipeItem -> {
            Map<String, Object> map = new LinkedHashMap<>(); // keep the order, so not HashMap
            //map.put("id", recipeItem.getId());
            map.put("name", recipeItem.getName());
            map.put("ingredients", recipeItem.getIngredients());
            map.put("instructions", recipeItem.getInstructions());
            map.put("picture", recipeItem.getPicture());
            //map.put("authorId", recipeItem.getAuthorId());
            return map;
        }).collect(Collectors.toList());

        //System.out.println("The queryResults are : "+quereyResults.toString());
        ByteArrayOutputStream pdfStream = pdfWithS3Service.generatedPdfStream(quereyResults);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=query_results.pdf");
        headers.setContentLength(pdfStream.size());

        id = null;
        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
    }
}