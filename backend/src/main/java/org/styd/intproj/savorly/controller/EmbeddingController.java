package org.styd.intproj.savorly.controller;

import org.styd.intproj.savorly.dto.TagViewModel;
import org.styd.intproj.savorly.entity.Tag;
import org.styd.intproj.savorly.service.OpenAiEmbeddingService;
import org.styd.intproj.savorly.service.TagService;
import jakarta.validation.Valid;
import org.springframework.ai.embedding.Embedding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.dto.ErrorResponse;
import org.styd.intproj.savorly.dto.TagPassModel;
import org.styd.intproj.savorly.dto.SearchResponse;
import org.styd.intproj.savorly.service.EmbeddingService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    //@Autowired
    //private OpenAiEmbeddingService embeddingService;
    @Autowired
    private TagService tagService;

//    @PostMapping(value = "/parse", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> processSearchQuery(@Valid @RequestBody TagPassModel tagPassModel) {
//        try {
//            String query = tagPassModel.getTitle();
//            if (query != null && !query.isEmpty()) {
//                // Parse the query to extract keywords
//                List<String> keywords = parseKeywords(query);
//
//                // Step 1: Get embeddings for keywords
//                List<float[]> keywordVectors = new ArrayList<>();
//                for (String keyword : keywords) {
//                    float[] vector = embeddingService.getEmbeddingForKeyword(keyword);
//                    keywordVectors.add(vector);
//                }
//
//                // Step 2: Get a single embedding vector for the entire query
//                float[] queryVector = embeddingService.getEmbeddingForQuery(query);
//                if (queryVector == null) {
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body(new ErrorResponse("serverError", "Failed to generate query embedding."));
//                }
//
//                // Step 3: Use the query vector to retrieve the nearest tags
//                List<Tag> tags = tagService.getTagsForNearestEmbedding(queryVector);
//
//                // Step 4: Map tags to TagViewModel for the response
//                if (!tags.isEmpty()) {
//                    List<TagViewModel> tagViewModels = tags.stream()
//                            .map(tag -> new TagViewModel(tag.getId(), tag.getTitle(), tag.getDescription(), tag.getIngredients()))
//                            .collect(Collectors.toList());
//
//                    // Create and return the final response
//                    SearchResponse response = new SearchResponse();
//                    response.setKeywordVectors(keywordVectors);
//                    response.setQueryVector(queryVector);
//                    response.setTags(tagViewModels);
//
//                    return ResponseEntity.status(HttpStatus.OK).body(response);
//                }
//
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ErrorResponse("notFound", "No matching tags found."));
//            }
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new ErrorResponse("invalidInput", "Query is empty"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ErrorResponse("serverError", "Failed to process search query: " + e.getMessage()));
//        }
//    }







//    @PostMapping(value = "/parse", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> processSearchQuery(@Valid @RequestBody TagPassModel tagPassModel) {
//        try {
//            String query = tagPassModel.getTitle();
//            if (query != null && !query.trim().isEmpty()) {
//                // Parse the query to extract keywords
//                List<String> keywords = parseKeywords(query.trim());
//
//                // Step 1: Get embedding vectors for individual keywords if there are keywords
//                List<Embedding> keywordVectors = new ArrayList<>();
//                if (!keywords.isEmpty()) {
//                    String keywordString = String.join(", ", keywords);
//                    System.out.println("Keywords: " + keywordString);
//                    keywordVectors = embeddingService.processTagWithGenericLogic(tagPassModel, true);
//                }
//
//                // Step 2: Get a single embedding vector for the entire query
//                List<Embedding> queryVector = embeddingService.processTagWithGenericLogic(tagPassModel, false);
//
//                // Step 3: Use the query vector and keyword vectors to retrieve the nearest tags
//                List<Tag> tags = new ArrayList<>();
//                if (!keywordVectors.isEmpty()) {
//                    for (Embedding embedding : keywordVectors) {
//                        tags.addAll(tagService.getTagsForNearestEmbedding(embedding.getOutput()));
//                    }
//                }
//
//                if (!queryVector.isEmpty()) {
//                    tags.addAll(tagService.getTagsForNearestEmbedding(queryVector.getFirst().getOutput()));
//                }
//
//                // Step 4: Map tags to TagViewModel for the response
//                if (!tags.isEmpty()) {
//                    List<TagViewModel> tagViewModels = tags.stream()
//                            .map(tag -> new TagViewModel(tag.getId(), tag.getTitle(), tag.getDescription(), tag.getIngredients()))
//                            .collect(Collectors.toList());
//
//                    // Create and return the final response
//                    SearchResponse response = new SearchResponse();
//                    response.setKeywordVectors(keywordVectors);
//                    response.setQueryVector(queryVector);
//                    response.setTags(tagViewModels);
//
//                    return ResponseEntity.status(HttpStatus.OK).body(response);
//                }
//
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ErrorResponse("notFound", "No matching tags found."));
//            }
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new ErrorResponse("invalidInput", "Query is empty"));
//        } catch (Exception e) {
//            e.printStackTrace(); // Log the exception for debugging
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ErrorResponse("serverError", "Failed to process search query: " + e.getMessage()));
//        }
//    }
//
//
//
//
//
//    @PostMapping(value = "/words", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> processAddTag(@Valid @RequestBody TagPassModel tagPassModel) {
//        try {
//            List<Embedding> getWordsEmbeddings = embeddingService.processTagWithGenericLogic(tagPassModel, true);
//
//            if (getWordsEmbeddings.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//            return ResponseEntity.status(HttpStatus.OK).body(getWordsEmbeddings);
//        }
//        catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ErrorResponse("serverError", "Vector not found"));
//        }
//    }
//
//    @PostMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> processAddTagQuery(@Valid @RequestBody TagPassModel tagPassModel) {
//        try {
//            List<Embedding> getListEmbeddings = embeddingService.processTagWithGenericLogic(tagPassModel, false);
//            if (getListEmbeddings.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//            return ResponseEntity.status(HttpStatus.OK).body(getListEmbeddings);
//        }
//        catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ErrorResponse("serverError", "Vector not found"));
//        }
//    }

//    private List<Embedding> processTagWithGenericLogic(TagPassModel tagPassModel, boolean spreadWords) {
//        List<Embedding> embeddings = new ArrayList<>();
//        try {
//            if (tagPassModel.getTitle() != null && !tagPassModel.getTitle().trim().isEmpty()) {
//                String title = tagPassModel.getTitle().trim(); // remove the space on both start and end of string
//                System.out.println("title : " + title);
//
//                List<String> texts = spreadWords ? Arrays.asList(title.split("\\s+")) : List.of(title);
//
//                // get embeddings
//                for (String text : texts) {
//                    if (!text.isEmpty()) { // avoid passing blank string
//                        List<Embedding> embedding = embeddingService.getEmbeddings(Collections.singletonList(text));
//                        if (embedding != null) {
//                            embeddings.addAll(embedding);
//                        }
//                    }
//                }
//
//                // print
//                for (int i = 0; i < embeddings.size(); i++) {
//                    System.out.println("Embedding for text " + (i + 1) + ": " + Arrays.toString(embeddings.get(i).getOutput()));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // log the exception
//        }
//        return embeddings;
//    }


    private List<String> parseKeywords(String query) {
        // Load predefined list of keywords for matching
        List<String> predefinedKeywords = Arrays.asList(
                "Spaghetti", "Pasta", "Lasagna", "Pizza", "Burger", "Steak", "Chicken Wings", "Caesar Salad",
                "Grilled Cheese", "Hot Dog", "French Fries", "Tacos", "Quesadilla", "Burrito", "Fish and Chips",
                "Roast Beef", "Meatloaf", "Fried Chicken", "Mashed Potatoes", "Gravy", "Mac and Cheese", "Barbecue Ribs",
                "Chili", "Gumbo", "Jambalaya", "Clam Chowder", "Lobster Roll", "Crab Cakes", "Shrimp Scampi",
                "Fettuccine Alfredo", "Risotto", "Paella", "Ratatouille", "Crepes", "Quiche", "Beef Wellington",
                "Chicken Parmesan", "Eggs Benedict", "Bagel", "Cobb Salad", "Buffalo Wings", "Sloppy Joe", "Cornbread",
                "Pancakes", "Waffles", "Donuts", "Brownies", "Cheesecake", "Apple Pie", "Pumpkin Pie", "Blueberry Muffin",
                "Chocolate Chip Cookie", "Ice Cream", "Gelato", "Sorbet", "Tiramisu", "Pavlova", "Eclairs", "Croissant",
                "Baguette", "Ciabatta", "Bruschetta", "Caprese Salad", "Carpaccio", "Prosciutto", "Salmon Tartare",
                "Crème Brûlée", "Potato Gratin", "Duck Confit", "Foie Gras", "Coq au Vin", "Beef Bourguignon", "Bouillabaisse",
                "Escargot", "Ravioli", "Gnocchi", "Cannelloni", "Biscotti", "Panettone", "Tart", "Pecan Pie",
                "Banoffee Pie", "Scones", "Yorkshire Pudding", "Sticky Toffee Pudding", "Black Forest Cake", "Red Velvet Cake",
                "Carrot Cake", "Omelette", "Frittata", "Shakshuka", "Churros", "Flan", "Soufflé", "Fondue", "Mousse"
        );

            if (query == null || query.trim().isEmpty()) {
                return Collections.emptyList();
            }

            query = query.toLowerCase().trim();
            List<String> queryWords = Arrays.asList(query.split("\\s+"));
            List<String> predefinedKeywordsLowerCase = predefinedKeywords.stream()
                    .map(String::toLowerCase)
                    .toList();

            List<String> matchedKeywords = new ArrayList<>();

            // traverse
            for (int i = 0; i < queryWords.size(); i++) {
                for (int j = i; j < queryWords.size(); j++) {
                    String phrase = String.join(" ", queryWords.subList(i, j + 1));

                    if (predefinedKeywordsLowerCase.contains(phrase)) {
                        matchedKeywords.add(phrase);
                    }
                }
            }

            return matchedKeywords;
        }

    }
