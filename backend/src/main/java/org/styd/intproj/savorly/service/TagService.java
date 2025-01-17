package org.styd.intproj.savorly.service;

import org.springframework.ai.embedding.Embedding;
import org.springframework.transaction.annotation.Transactional;
import org.styd.intproj.savorly.dto.TagResponse;
import org.styd.intproj.savorly.dto.TagViewModel;
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

    public List<Tag> getTagsForTitle(String title) {
        List<Tag> tagsForTitle = tagRepository.getTagsForTitle(title);
        return tagsForTitle;
    }

    public List<Tag> getTagsForNearestEmbedding(float[] embedding) {
        List<Tag> tagsForNearestEmbedding = tagRepository.getTagsByNearestEmbedding(embedding);
        return tagsForNearestEmbedding;
        }

    @Transactional
    public int saveTag(String title, String ingredients, String description, float[] embedding) {

        return tagRepository.saveTag(title, ingredients, description, embedding);
    }

    @Transactional
    public TagResponse createTag(TagViewModel tagViewModel) {
        // validate the input
        validateTagInput(tagViewModel);

        // combine the words
        String combinedText = tagViewModel.getTitle().trim() + " " +
                tagViewModel.getIngredients().trim() + " " +
                tagViewModel.getDescription().trim();
        List<String> wordsList = Arrays.asList(combinedText.split("\\s+"));

        // generate the embedding
        List<Embedding> tagEmbeddings = embeddingService.getEmbeddings(wordsList);
        if (tagEmbeddings.isEmpty()) {
            throw new RuntimeException("Failed to generate embeddings");
        }

        // save to db
        int savedTagCount = tagRepository.saveTag(
                tagViewModel.getTitle().trim(),
                tagViewModel.getIngredients().trim(),
                tagViewModel.getDescription().trim(),
                tagEmbeddings.get(0).getOutput()
        );

        // return response
        if (savedTagCount > 0) {
            return new TagResponse("Saved successfully", List.of(tagViewModel));
        } else {
            throw new RuntimeException("Failed to save tag");
        }
    }

    private void validateTagInput(TagViewModel tagViewModel) {
        if (tagViewModel.getTitle() == null || tagViewModel.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (!tagRepository.getTagsForTitle("%" + tagViewModel.getTitle().trim() + "%").isEmpty()) {
            throw new IllegalArgumentException("Title already exists");
        }
    }

    @Transactional
    public TagResponse updateTag(TagViewModel tagViewModel) {
        if (tagViewModel.getId() == null) {
            throw new IllegalArgumentException("Tag ID cannot be null");
        }
        Optional<Tag> existingTag = tagRepository.findById(tagViewModel.getId());
        if (existingTag.isEmpty()) {
            throw new IllegalArgumentException("Tag not found");
        }
        String combinedText = tagViewModel.getTitle().trim() + " " + tagViewModel.getIngredients().trim() + " " + tagViewModel.getDescription().trim();
        List<String> wordsList = Arrays.asList(combinedText.split("\\s+"));
        List<Embedding> tagEmbeddings = embeddingService.getEmbeddings(wordsList);
        if (tagEmbeddings.isEmpty()) {
            throw new RuntimeException("Failed to generate embeddings");
        }
        int updatedTagCount = tagRepository.updateTag(tagViewModel.getId(), tagViewModel.getTitle().trim(), tagViewModel.getIngredients().trim(), tagViewModel.getDescription().trim(), tagEmbeddings.get(0).getOutput());
        if (updatedTagCount > 0) {
            return new TagResponse("Updated successfully", List.of(tagViewModel));
        } else {
            throw new RuntimeException("Failed to update tag");
        }
    }

    @Transactional
    public void deleteTag(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Tag ID cannot be null");
        }
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found");
        }
        tagRepository.deleteById(id);
    }


}



