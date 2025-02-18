package com.example.generativeai.testcontainers;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

// ./gradlew 1-hello-world:run --console=plain

@Slf4j
public class HelloWorld {

    @SneakyThrows
    public static void main(String[] args) {
        ChatLanguageModel model;

        model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .logRequests(true)
                .build();

//        model =  OpenAiChatModel.builder()
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .modelName("gpt-4o")
//                .build();

        var answer = model.chat("Provide 3 very short bullet points explaining " +
                "why Java in 2025 is still awesome");

        log.info("Response from LLM -> {}", answer);
    }

}
