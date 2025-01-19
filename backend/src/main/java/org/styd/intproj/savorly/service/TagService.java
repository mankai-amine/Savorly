package org.styd.intproj.savorly.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.Embedding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.styd.intproj.savorly.dto.TagResponse;
import org.styd.intproj.savorly.dto.TagViewModel;
import org.styd.intproj.savorly.dto.TagPassModel;
import org.styd.intproj.savorly.repository.TagRepository;
import org.styd.intproj.savorly.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EmbeddingService embeddingService;

    private static final Logger logger = LoggerFactory.getLogger(TagService.class);

    /**
     * get all tags with pageable
     */
    public Page<Tag> getAllTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return tagRepository.findAll(pageable);
    }

    /**
     * fuzzy search with title
     */
    public List<TagViewModel> findTagsByTitleLike(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        logger.info("Searching tags with title: {}", title);
        List<Tag> tags = tagRepository.findByTitleLike("%" + title + "%");

        if (tags.isEmpty()) {
            logger.warn("No tags found for title: {}", title);
        }

        return convertToTagViewModelList(tags);
    }

    /**
     * find nearest tags with embedding or title
     */
    public List<TagViewModel> findNearestTags(TagPassModel tagPassModel) {
        if (tagPassModel.getEmbedding() == null && tagPassModel.getTitle() == null) {
            throw new IllegalArgumentException("Either embedding or title is required");
        }

        List<Tag> tags = (tagPassModel.getEmbedding() != null)
                ? getTagsForNearestEmbedding(tagPassModel.getEmbedding())
                : tagRepository.findByTitleLike("%" + tagPassModel.getTitle() + "%");

        return convertToTagViewModelList(tags);
    }

    /**
     * return nearest L2 distance
     */
    public List<Tag> getTagsForNearestEmbedding(float[] embedding) {
        return tagRepository.findNearestTags(embedding);
    }

    /**
     * create
     */
    @Transactional
    public TagResponse createTag(TagViewModel tagViewModel) {
        validateTagInput(tagViewModel);

        // generate embedding
        float[] embedding = generateEmbedding(tagViewModel);

        // save in the database
        int savedTagCount = tagRepository.saveTag(
                tagViewModel.getTitle().trim(),
                tagViewModel.getIngredients().trim(),
                tagViewModel.getDescription().trim(),
                embedding
        );

        if (savedTagCount > 0) {
            logger.info("Created new tag: {}", tagViewModel.getTitle());
            return new TagResponse("Saved successfully", List.of(tagViewModel));
        }
        throw new RuntimeException("Failed to save tag");
    }

    /**
     * update
     */
    @Transactional
    public TagResponse updateTag(TagViewModel tagViewModel) {
        if (tagViewModel.getId() == null) {
            throw new IllegalArgumentException("Tag ID cannot be null");
        }

        Tag existingTag = tagRepository.findById(tagViewModel.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagViewModel.getId()));

        // generate new embedding
        float[] embedding = generateEmbedding(tagViewModel);

        // update the database
        int updatedTagCount = tagRepository.updateTag(
                tagViewModel.getId(),
                tagViewModel.getTitle().trim(),
                tagViewModel.getIngredients().trim(),
                tagViewModel.getDescription().trim(),
                embedding
        );

        if (updatedTagCount > 0) {
            logger.info("Updated tag with id: {}", tagViewModel.getId());
            return new TagResponse("Updated successfully", List.of(tagViewModel));
        }
        throw new RuntimeException("Failed to update tag");
    }

    /**
     * delete
     */
    @Transactional
    public void deleteTag(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Tag ID cannot be null");
        }

        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found with id: " + id);
        }

        tagRepository.deleteById(id);
        logger.info("Deleted tag with id: {}", id);
    }

    /**
     * validation for tagViewModel(without id)
     */
    private void validateTagInput(TagViewModel tagViewModel) {
        if (tagViewModel.getTitle() == null || tagViewModel.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        tagRepository.findByTitle("%" + tagViewModel.getTitle().trim() + "%")
                .ifPresent(tag -> {
                    throw new IllegalArgumentException("Title already exists");
                });
    }

    /**
     * generate embedding of the words list in tagViewModel
     */
    private float[] generateEmbedding(TagViewModel tagViewModel) {
        String combinedText = tagViewModel.getTitle().trim() + " " +
                tagViewModel.getIngredients().trim() + " " +
                tagViewModel.getDescription().trim();

        List<String> wordsList = Arrays.asList(combinedText.split("\\s+"));
        List<Embedding> tagEmbeddings = embeddingService.getEmbeddings(wordsList);

        if (tagEmbeddings.isEmpty()) {
            throw new RuntimeException("Failed to generate embeddings");
        }

        return tagEmbeddings.get(0).getOutput();
    }

    /**
     * convert List<Tag> to List<TagViewModel>
     */
    private List<TagViewModel> convertToTagViewModelList(List<Tag> tags) {
        return tags.stream()
                .map(tag -> new TagViewModel(tag.getId(), tag.getTitle(), tag.getDescription(), tag.getIngredients()))
                .collect(Collectors.toList());
    }
}
