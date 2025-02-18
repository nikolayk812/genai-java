package com.example.generativeai.testcontainers;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Slf4j
public class Embeddings {

    public static void main(String[] args) {
        EmbeddingModel model = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text:v1.5")
                .build();

        Embedding cat = model.embed("A cat is a small domesticated animal").content();
        Embedding ollama = model.embed("Ollama runs LLMs locally").content();

        double similarity = CosineSimilarity.between(cat, ollama);
        log.info("Cosine similarity between embeddings is: {}", similarity);
    }

}
