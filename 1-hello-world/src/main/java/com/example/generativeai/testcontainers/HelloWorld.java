package com.example.generativeai.testcontainers;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;

// colima start --runtime docker --memory 8 --dns 8.8.8.8

@Slf4j
public class HelloWorld {

    private static final String MODEL_NAME = "llama3.2:1b-instruct-q5_1";

    @SneakyThrows
    public static void main(String[] args) {
        var container = new OllamaContainer("ollama/ollama:0.5.7")
                .withReuse(true);

        container.start();
        container.execInContainer("ollama", "pull", MODEL_NAME);

        ChatLanguageModel model;

        model = OllamaChatModel.builder()
                .baseUrl(container.getEndpoint())
                .modelName(MODEL_NAME).build();

//        model =  OpenAiChatModel.builder()
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .modelName("gpt-3.5-turbo")
//                .build();

        var answer = model.chat("Provide 3 very short bullet points explaining why Java in 2025 is still awesome");

        log.info("Response from LLM -> {}", answer);
    }

}
