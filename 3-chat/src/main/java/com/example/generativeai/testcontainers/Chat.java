package com.example.generativeai.testcontainers;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.out;
import static java.util.Collections.synchronizedList;

// ./gradlew runChatApp --console=plain

@Slf4j
public class Chat {

    @SneakyThrows
    public static void main(String[] args) {

        var model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .build();

        out.print("\nYou: ");

        // Set up the scanner to read user input
        var scanner = new Scanner(System.in);

        var conversation = synchronizedList(new ArrayList<ChatMessage>());
        conversation.add(SystemMessage.from("Keep your responses brief and concise."));

        var future = new CompletableFuture<>();

        // Enter a conversation loop
        while (true) {
            var userInput = scanner.nextLine();

            if (userInput.isEmpty()) {
                out.print("\nYou: ");
                continue;
            }

            // Exit the loop if the user types 'exit'
            if (userInput.equalsIgnoreCase("exit")) {
                out.println("Ending chat session.");
                future.complete(null);
                break;
            }

            // Add user message to the conversation
            conversation.add(UserMessage.from(userInput));

            // Generate AI response
            model.generate(conversation, new StreamingResponseHandler<>() {
                @Override
                public void onNext(String token) {
                    out.print(token);
                }

                @Override
                public void onError(Throwable error) {
                    future.completeExceptionally(error);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    conversation.add(response.content());
                    out.println();
                    out.print("\nYou: ");
                }
            });
        }

        future.join();
    }

}
