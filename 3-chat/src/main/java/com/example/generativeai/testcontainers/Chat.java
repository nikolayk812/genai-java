package com.example.generativeai.testcontainers;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Chat {

    private static final String MODEL_NAME = "llama3.2:1b-instruct-q5_1";

    @SneakyThrows
    public static void main(String[] args) {
        var container = new OllamaContainer("ollama/ollama:0.5.7").withReuse(true);

        container.start();
        container.execInContainer("ollama", "pull", MODEL_NAME);

        var model = OllamaStreamingChatModel.builder()
                .baseUrl(container.getEndpoint())
                .modelName(MODEL_NAME)
                .build();


        System.out.print("\nYou: ");

        // Set up the scanner to read user input
        var scanner = new Scanner(System.in);

        var conversation = Collections.synchronizedList(new ArrayList<ChatMessage>());
        var future = new CompletableFuture<>();

        // Enter a conversation loop
        while (true) {
            var userInput = scanner.nextLine();

            // Exit the loop if the user types 'exit'
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Ending chat session.");
                future.complete(null);
                break;
            }

            // Add user message to the conversation
            conversation.add(UserMessage.from(userInput));

            // Generate AI response
            model.generate(conversation, new StreamingResponseHandler<>() {
                @Override
                public void onNext(String token) {
                    System.out.print(token);
                }

                @Override
                public void onError(Throwable error) {
                    future.completeExceptionally(error);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    conversation.add(response.content());
                    System.out.println(); // Print newline after AI's response
                    System.out.print("\nYou: ");
                }
            });
        }

        future.join();
    }

}
