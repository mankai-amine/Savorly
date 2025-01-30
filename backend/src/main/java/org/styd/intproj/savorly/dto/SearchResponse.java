package org.styd.intproj.savorly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.embedding.Embedding;

import java.util.List;

// Define SearchResponse
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private List<Embedding> keywordVectors;
    private List<Embedding> queryVector;
    private List<TagViewModel> tags;

}

