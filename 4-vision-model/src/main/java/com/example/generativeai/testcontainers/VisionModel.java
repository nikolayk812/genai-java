package com.example.generativeai.testcontainers;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

// ollama run moondream:1.8b

// ./gradlew 4-vision-model:run --console=plain

@Slf4j
public class VisionModel {

    @SneakyThrows
    public static void main(String[] args) {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("What do you see?"),
                ImageContent.from(readImageInBase64("/computer.jpeg"), "image/jpeg")
        );

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("moondream:1.8b")
                .logRequests(true)
                .build();

        var response = model.chat(userMessage);
        log.info("Response from LLM -> {}", response.aiMessage().text());
    }


    private static String readImageInBase64(String name) throws IOException {
        URL resourceUrl = VisionModel.class.getResource(name);
        if (resourceUrl == null) {
            throw new IOException("Could not find resource " + name);
        }

        byte[] fileContent = FileUtils.readFileToByteArray(new File(resourceUrl.getFile()));
        return Base64.getEncoder().encodeToString(fileContent);
    }

}