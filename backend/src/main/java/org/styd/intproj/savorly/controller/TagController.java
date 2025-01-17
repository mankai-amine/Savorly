package org.styd.intproj.savorly.controller;

import org.styd.intproj.savorly.dto.RegisterResponse;
import org.styd.intproj.savorly.entity.Tag;
import org.styd.intproj.savorly.dto.TagResponse;
import jakarta.validation.Valid;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.dto.ErrorResponse;
import org.styd.intproj.savorly.repository.TagRepository;
import org.styd.intproj.savorly.service.TagService;
import org.styd.intproj.savorly.dto.TagPassModel;
import org.styd.intproj.savorly.dto.TagViewModel;
import org.styd.intproj.savorly.service.EmbeddingService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private EmbeddingService embeddingService;

    //get the titles with the word(s) input
    @GetMapping(value = "/titles",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTagsForTitle(@RequestParam String title) {
        try {
            System.out.println("title : " + title);
            String likeTitle = "%" + title + "%";
            List<Tag> tags = tagService.getTagsForTitle(likeTitle);

            System.out.println("length of tags : " + tags.size());
            if(!tags.isEmpty()) {
                System.out.println(tags.get(0).getIngredients());
                List<TagViewModel> tagViewModels = tags.stream()
                        .map(tag -> new TagViewModel(tag.getId(), tag.getTitle(), tag.getDescription(),tag.getIngredients()))
                        .collect(Collectors.toList());
//                return ResponseEntity.ok(new TagResponse("success", tagViewModels));
                return ResponseEntity.status(HttpStatus.OK).body(new TagResponse("success", tagViewModels));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("serverError", "Tag not found"));
        }
    }

    //get the nearest 10 tags of the title in put, 10 could be modified in repository class
    @PostMapping(value = "/nearest",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processTagsForNearestEmbedding(@Valid @RequestBody TagPassModel tagPassModel) {
        try {
            if(tagPassModel.getEmbedding() != null) {
                System.out.println("embedding length : " + tagPassModel.getEmbedding().length);
                //get the specific numbers of tags with L2 distance algorithm
                List<Tag> tags = tagService.getTagsForNearestEmbedding(tagPassModel.getEmbedding());

                System.out.println("length of tags : " + tags.size());
                if (!tags.isEmpty()) {
                    System.out.println(tags.get(0).getIngredients());
                    List<TagViewModel> tagViewModels = tags.stream()
                            .map(tag -> new TagViewModel(tag.getId(), tag.getTitle(), tag.getDescription(), tag.getIngredients()))
                            .collect(Collectors.toList());

                    return ResponseEntity.status(HttpStatus.OK).body(new TagResponse("success", tagViewModels));
                }
                else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            else if(tagPassModel.getTitle() != null) {
                String likeTitle = "%" + tagPassModel.getTitle() + "%";
                List<Tag> tags = tagService.getTagsForTitle(likeTitle);
                if (!tags.isEmpty()) {
                    System.out.println(tags.get(0).getIngredients());
                    //convert tag type into tagviewmodel type (by throw the embedding field away)
                    List<TagViewModel> tagViewModels = tags.stream()
                            .map(tag -> new TagViewModel(tag.getId(), tag.getTitle(), tag.getDescription(), tag.getIngredients()))
                            .collect(Collectors.toList());

                    return ResponseEntity.status(HttpStatus.OK).body(new TagResponse("success", tagViewModels));
                }
                else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("serverError", "Tag not found"));
        }
    }

    //bring a new tag record
    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTag(@Valid @RequestBody TagViewModel tagViewModel) {
        try {
            TagResponse response = tagService.createTag(tagViewModel);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("saveError", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("serverError", "Tag creation failed"));
        }
    }


    //update an exists tag
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTag(@Valid @RequestBody TagViewModel tagViewModel) {
        try {
            TagResponse response = tagService.updateTag(tagViewModel);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("updateError", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("serverError", "Tag update failed"));
        }
    }

    //delete
    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteTag(@RequestParam Long id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.status(HttpStatus.OK).body(new TagResponse("Deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("deleteError", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("serverError", "Tag deletion failed"));
        }
    }
}


