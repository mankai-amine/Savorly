package org.styd.intproj.savorly.service;

import org.styd.intproj.savorly.dto.TagPassModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.Embedding;
import org.styd.intproj.savorly.service.OpenAiEmbeddingService;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ai.ollama.api.OllamaOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.awt.SystemColor.text;

@Service
public class EmbeddingService {

    //for ollama
    //private final OllamaEmbeddingModel ollamaEmbeddingModel;

    private final OpenAiEmbeddingService openAiEmbeddingService;

    //for ollama injection
//    @Autowired
//    public EmbeddingService(OllamaEmbeddingModel ollamaEmbeddingModel) {
//        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
//    }

    //for openai injection
    @Autowired
    public EmbeddingService(OpenAiEmbeddingService openAiEmbeddingService) {
        this.openAiEmbeddingService = openAiEmbeddingService;
    }


//    OllamaOptions options = OllamaOptions.builder()
//            .model("nomic-embed-text")
//            .build();

    public List<Embedding> getEmbeddings(List<String> texts) {
//        EmbeddingRequest request = new EmbeddingRequest(texts, options);

        try {
            //all print must be disabled in product environment
            System.out.println("Start to get the embedding from ollama");
//            System.out.println("texts in request" +request.getInstructions());
            EmbeddingResponse response = openAiEmbeddingService.getEmbeddingFromOpenAi(texts); //from openai
            //EmbeddingResponse response = ollamaEmbeddingModel.call(request); //from ollama
            System.out.println("return response : " + response );

            System.out.println(Arrays.toString(response.getResults().getFirst().getOutput()));
            return response.getResults();
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(EmbeddingService.class);
            logger.error("Raised error when getting embedding ", e);

            throw new RuntimeException("Failed to get embedding ", e);
        }
    }

    public List<Embedding> processTagWithGenericLogic(TagPassModel tagPassModel, boolean spreadWords) {
        List<Embedding> embeddings = new ArrayList<>();
        try {
            if (tagPassModel.getTitle() != null && !tagPassModel.getTitle().trim().isEmpty()) {
                String title = tagPassModel.getTitle().trim(); // remove the space on both start and end of string
                System.out.println("title : " + title);

                List<String> texts = spreadWords ? Arrays.asList(title.split("\\s+")) : List.of(title);

                // get embeddings
                for (String text : texts) {
                    if (!text.isEmpty()) { // avoid passing blank string
                        List<Embedding> embedding = getEmbeddings(Collections.singletonList(text));
                        if (embedding != null) {
                            embeddings.addAll(embedding);
                        }
                    }
                }

                // print
                for (int i = 0; i < embeddings.size(); i++) {
                    System.out.println("Embedding for text " + (i + 1) + ": " + Arrays.toString(embeddings.get(i).getOutput()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // log the exception
        }
        return embeddings;
    }

}
