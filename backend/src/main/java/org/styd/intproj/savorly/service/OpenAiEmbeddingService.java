package org.styd.intproj.savorly.service;


import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class OpenAiEmbeddingService {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.embedding.api-key}")
    private String openAiEmbeddingApiKey;

    private final OpenAiEmbeddingModel openAiEmbeddingModel;

    @Autowired
    public OpenAiEmbeddingService(OpenAiEmbeddingModel openAiEmbeddingModel) {
        this.openAiEmbeddingModel = openAiEmbeddingModel;
    }

    public EmbeddingResponse getEmbeddingFromOpenAi(List<String> inputTextList) {
        return openAiEmbeddingModel.embedForResponse(inputTextList);
    }
}

