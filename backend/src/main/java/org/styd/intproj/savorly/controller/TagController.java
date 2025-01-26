package org.styd.intproj.savorly.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.dto.TagResponse;
import org.styd.intproj.savorly.dto.TagPassModel;
import org.styd.intproj.savorly.dto.TagViewModel;
import org.styd.intproj.savorly.entity.Tag;
import org.styd.intproj.savorly.service.TagService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * fuzzy search for title
     */
    @GetMapping(value = "/titles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagResponse> getTagsForTitle(@RequestParam String title) {
        List<TagViewModel> tagViewModels = tagService.findTagsByTitleLike(title);

        if (tagViewModels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TagResponse("notFound", tagViewModels));
        }

        return ResponseEntity.ok(new TagResponse("success", tagViewModels));
    }

    /**
     * return nearest tags for embedding or title
     */
    @PostMapping(value = "/nearest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagResponse> processTagsForNearestEmbedding(@Valid @RequestBody TagPassModel tagPassModel) {
        List<TagViewModel> tagViewModels = tagService.findNearestTags(tagPassModel);

        if (tagViewModels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TagResponse("notFound", tagViewModels));
        }

        return ResponseEntity.ok(new TagResponse("success", tagViewModels));
    }

    /**
     * create
     */
    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagViewModel tagViewModel) {
        TagResponse response = tagService.createTag(tagViewModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * update
     */
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagResponse> updateTag(@Valid @RequestBody TagViewModel tagViewModel) {
        TagResponse response = tagService.updateTag(tagViewModel);
        return ResponseEntity.ok(response);
    }

    /**
     * delete
     */
    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTag(@RequestParam Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * get all tags with pageable
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Tag>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Tag> tags = tagService.getAllTags(page, size);
        return ResponseEntity.ok(tags);
    }
}
