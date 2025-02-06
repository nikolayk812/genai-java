package com.example.generativeai.testcontainers;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class Streaming {

    private static final String MODEL_NAME = "qwen2:0.5b-instruct-q5_1";

    @SneakyThrows
    public static void main(String[] args) {
        var container = new OllamaContainer("ollama/ollama:0.5.7").withReuse(true);

        container.start();
        container.execInContainer("ollama", "pull", MODEL_NAME);

        var model = OllamaStreamingChatModel.builder().baseUrl(container.getEndpoint()).modelName(MODEL_NAME).build();

        var future = new CompletableFuture<>();

        Thread.startVirtualThread(() ->
                model.chat("Give me a detailed and long explanation of why Testcontainers is great",
                        new StreamingChatResponseHandler() {
                            @Override
                            public void onPartialResponse(String token) {
                                System.out.print(token);
                            }

                            @Override
                            public void onError(Throwable error) {
                                future.completeExceptionally(error);
                            }

                            @Override
                            public void onCompleteResponse(ChatResponse response) {
                                System.out.println();
                                future.complete(null);
                            }
                        })
        );

        future.join();
    }

}
