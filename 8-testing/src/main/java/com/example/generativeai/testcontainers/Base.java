package com.example.generativeai.testcontainers;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocumentsRecursively;

public class Base {

	static String model = "llama3.2";
	static String tag = "3b";
	static String modelName = "%s:%s".formatted(model, tag);

	static ChatLanguageModel chatModel() {
		OllamaContainer ollamaContainer = new OllamaContainer(
				DockerImageName.parse("ilopezluna/%s:0.3.13-%s".formatted(model, tag))
					.asCompatibleSubstituteFor("ollama/ollama"))
			.withReuse(true);
		ollamaContainer.start();
		return OllamaChatModel.builder()
			.baseUrl(ollamaContainer.getEndpoint())
			.modelName(modelName)
			.temperature(0d)
			.topK(1)
			.seed(42)
			.logRequests(true)
			.build();
	}

	static ContentRetriever contentRetriever() {
		var store = store();
		var model = embeddingModel();
		List<Document> txtDocuments = loadDocumentsRecursively(toPath("knowledge/txt/"), new TextDocumentParser());

		EmbeddingStoreIngestor.builder()
			.embeddingStore(store)
			.embeddingModel(model)
			.documentSplitter(new DocumentByParagraphSplitter(1024, 100))
			.build()
			.ingest(txtDocuments);

		return EmbeddingStoreContentRetriever.builder()
			.embeddingModel(model)
			.embeddingStore(store)
			.maxResults(3)
			.minScore(0.7)
			.build();
	}

	private static Path toPath(String fileName) {
		try {
			URL fileUrl = Base.class.getClassLoader().getResource(fileName);
			return Paths.get(fileUrl.toURI());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static EmbeddingModel embeddingModel() {
		var ollama = new OllamaContainer(
				DockerImageName.parse("ilopezluna/all-minilm:0.3.13-22m").asCompatibleSubstituteFor("ollama/ollama"))
			.withReuse(true);
		ollama.start();
		return OllamaEmbeddingModel.builder().baseUrl(ollama.getEndpoint()).modelName("all-minilm:22m").build();
	}

	private static EmbeddingStore<TextSegment> store() {
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
