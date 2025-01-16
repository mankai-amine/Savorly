package org.styd.intproj.savorly.service;

import org.styd.intproj.savorly.repository.TagRepository;

import org.styd.intproj.savorly.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getTagsForTitle(String title) {
        List<Tag> tagsForTitle = tagRepository.findTagsForTitle(title);
        return tagsForTitle;
    }

    public List<Tag> getTagsForNearestEmbedding(float[] embedding) {
        List<Tag> tagsForNearestEmbedding = tagRepository.findForNearestEmbedding(embedding);
        return tagsForNearestEmbedding;
        }
}

