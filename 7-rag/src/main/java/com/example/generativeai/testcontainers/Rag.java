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

import java.util.List;

@Slf4j
public class Rag {

    public static void main(String[] args) {
        ChatLanguageModel chatModel = buildChatModel();


        // prepare RAG: insert data to pgvector

        EmbeddingModel embeddingModel = buildEmbeddingModel();

        EmbeddingStore<TextSegment> store = buildEmbeddingStore();

        ingestion(embeddingModel, store);


        // search relevant embeddings in RAG
        Embedding queryEmbedding = embeddingModel.embed("What is my favourite sport?").content();

        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder().
                queryEmbedding(queryEmbedding).maxResults(1).build();

        List<EmbeddingMatch<TextSegment>> relevant = store.search(searchRequest).matches();
        if (relevant.isEmpty()) {
            log.info("No relevant content found.");
            System.exit(0);
        }

        var relevantText = relevant.getFirst().embedded().text();


        // augment original client request/query

        String response = chatModel.chat("""
                What is your favourite sport?
                
                Answer the question considering the following relevant content, be super confident:
                %s
                """.formatted(relevantText));

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
        return OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text:v1.5")
                .build();
    }

    private static ChatLanguageModel buildChatModel() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .build();
    }

    /*
    docker run -d --name pgvector-test \
      -e POSTGRES_USER=test \
      -e POSTGRES_PASSWORD=test \
      -e POSTGRES_DB=test \
      -p 5432:5432 \
      pgvector/pgvector:pg16
     */
    private static PgVectorEmbeddingStore buildEmbeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("test")
                .user("test")
                .password("test")
                .table("test")
                .dimension(768)
                .build();
    }

}
