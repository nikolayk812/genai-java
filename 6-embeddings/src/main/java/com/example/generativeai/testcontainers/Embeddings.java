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

	private static final String MODEL_NAME = "all-minilm:22m";

	public static void main(String[] args) {
		var container = new OllamaContainer(
				DockerImageName.parse("ilopezluna/all-minilm:0.3.13-22m")
						.asCompatibleSubstituteFor("ollama/ollama"))
				.withReuse(true);

		container.start();

		var endpoint = container.getEndpoint();

		EmbeddingModel model = OllamaEmbeddingModel.builder()
			.baseUrl(endpoint)
			.modelName(MODEL_NAME)
			.build();

		var catEmbedding = model.embed("A cat is a small domesticated carnivorous mammal").content();
		var tigerEmbedding = model.embed("A tiger is a large carnivorous feline mammal").content();

		var tcEmbedding = model.embed(
				"Testcontainers is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container")
			.content();

		var dockerEmbedding = model.embed(
				"Docker is a platform designed to help developers build, share, and run container applications. We handle the tedious setup, so you can focus on the code.")
			.content();

		var embeddings = List.of(catEmbedding, tigerEmbedding, tcEmbedding, dockerEmbedding);
		for (int i = 0; i < embeddings.size(); i++) {
			for (int j = i + 1; j < embeddings.size(); j++) {
				double similarity = CosineSimilarity.between(embeddings.get(i), embeddings.get(j));
				log.info("Cosine similarity between embeddings {} and {} is: {}", i, j, similarity);
			}
		}
	}

}
