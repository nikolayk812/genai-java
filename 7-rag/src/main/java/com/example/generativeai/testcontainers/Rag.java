package com.example.generativeai.testcontainers;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Slf4j
public class Rag {

	public static void main(String[] args) {
		EmbeddingModel embeddingModel = buildEmbeddingModel();
		EmbeddingStore<TextSegment> store = buildEmbeddingStore();

		ingestion(embeddingModel, store);

		Embedding queryEmbedding = embeddingModel.embed("What is my favourite sport?").content();
		List<EmbeddingMatch<TextSegment>> relevant = store
			.search(EmbeddingSearchRequest.builder().queryEmbedding(queryEmbedding).maxResults(1).build())
			.matches();

		if (relevant.isEmpty()) {
			log.info("No relevant content found.");
			System.exit(0);
		}

		ChatLanguageModel chatModel = buildChatModel();

		String response = chatModel.generate("""
				What is your favourite sport?

				Answer the question considering the following relevant content:
				%s
				""".formatted(relevant.get(0).embedded().text()));

		log.info("Response from LLM (\uD83E\uDD16)-> {}", response);
	}

	private static void ingestion(EmbeddingModel model, EmbeddingStore<TextSegment> store) {
		TextSegment segment1 = TextSegment.from("I like football.");
		Embedding embedding1 = model.embed(segment1).content();
		store.add(embedding1, segment1);

		TextSegment segment2 = TextSegment.from("The weather is good today.");
		Embedding embedding2 = model.embed(segment2).content();
		store.add(embedding2, segment2);
	}

	private static EmbeddingModel buildEmbeddingModel() {
		var ollama = new OllamaContainer(
				DockerImageName.parse("ilopezluna/all-minilm:0.3.13-22m").asCompatibleSubstituteFor("ollama/ollama"));
		ollama.start();
		return OllamaEmbeddingModel.builder().baseUrl(ollama.getEndpoint()).modelName("all-minilm:22m").build();
	}

	private static ChatLanguageModel buildChatModel() {
		OllamaContainer ollamaContainer = new OllamaContainer(
				DockerImageName.parse("ilopezluna/llama3.2:0.3.13-1b").asCompatibleSubstituteFor("ollama/ollama"))
			.withReuse(true);
		ollamaContainer.start();
		return OllamaChatModel.builder()
			.baseUrl(ollamaContainer.getEndpoint())
			.modelName("llama3.2:1b")
			.logRequests(true)
			.build();
	}

	private static PgVectorEmbeddingStore buildEmbeddingStore() {
		var pgVector = new PostgreSQLContainer<>(
				DockerImageName.parse("pgvector/pgvector:pg16").asCompatibleSubstituteFor("postgres"));
		pgVector.start();
		return PgVectorEmbeddingStore.builder()
			.host(pgVector.getHost())
			.port(pgVector.getFirstMappedPort())
			.database(pgVector.getDatabaseName())
			.user(pgVector.getUsername())
			.password(pgVector.getPassword())
			.table("test")
			.dimension(384)
			.build();
	}

}
