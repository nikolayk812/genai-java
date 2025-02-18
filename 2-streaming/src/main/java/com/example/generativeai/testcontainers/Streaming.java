package com.example.generativeai.testcontainers;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import static java.lang.System.out;

// ./gradlew 2-streaming:run --console=plain

@Slf4j
public class Streaming {

    @SneakyThrows
    public static void main(String[] args) {
        StreamingChatLanguageModel model;

        model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .build();

//        model = OpenAiStreamingChatModel.builder()
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .modelName("gpt-4o")
//                .build();

        var future = new CompletableFuture<>();

        Thread.startVirtualThread(() ->
                model.chat("Provide a detailed and long explanation why Java is awesome in 2025",
                        new StreamingChatResponseHandler() {
                            @Override
                            public void onPartialResponse(String token) {
                                out.print(token);
                            }

                            @Override
                            public void onError(Throwable error) {
                                future.completeExceptionally(error);
                            }

                            @Override
                            public void onCompleteResponse(ChatResponse response) {
                                out.println();
                                future.complete(null);
                            }
                        })
        );

        future.join();
    }

}
