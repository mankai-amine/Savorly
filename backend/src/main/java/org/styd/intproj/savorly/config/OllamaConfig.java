package org.styd.intproj.savorly.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.ai.ollama.management.PullModelStrategy;

import java.time.Duration;
import java.util.List;

@Service
public class OllamaConfig {


    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi("http://192.168.1.109:11434"); // use the ollama endpoint yourselves
    }

    @Bean
    public OllamaOptions ollamaOptions() {
        return OllamaOptions.builder()
                .model("nomic-embed-text")
                .build();
    }

    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }

    @Bean
    public ModelManagementOptions modelManagementOptions() {
        return new ModelManagementOptions(
                PullModelStrategy.ALWAYS, // pull strategy
                List.of("nomic-embed-text"), // list of model
                Duration.ofMinutes(5), // timeout limit
                3 // retry times
        );
    }

    @Bean
    public OllamaEmbeddingModel OllamaEmbeddingModel( //the model accept 4 parameters
            OllamaApi ollamaApi,
            OllamaOptions ollamaOptions,
            ObservationRegistry observationRegistry,
            ModelManagementOptions modelManagementOptions) {
        return new OllamaEmbeddingModel(
                ollamaApi,
                ollamaOptions,
                observationRegistry,
                modelManagementOptions
        );
    }
}

