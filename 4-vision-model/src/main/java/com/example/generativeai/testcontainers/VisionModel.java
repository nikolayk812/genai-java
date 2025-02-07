package com.example.generativeai.testcontainers;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

@Slf4j
public class VisionModel {

    @SneakyThrows
    public static void main(String[] args) {
//		OllamaContainer ollama = new OllamaContainer(DockerImageName.parse("ilopezluna/moondream:0.3.13-1.8b")
//			.asCompatibleSubstituteFor("ollama/ollama:0.3.13")).withReuse(true);
//		ollama.start();
//
        var endpoint = "http://localhost:11434";

        UserMessage userMessage = UserMessage.from(TextContent.from("What do you see?"),
                ImageContent.from(getImageInBase64("/computer.jpeg"), "image/jpeg"));

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(endpoint)
                .modelName("moondream:1.8b")
                .build();

        var response = model.chat(userMessage);
        log.info("Response from LLM -> {}", response.aiMessage().text());
    }

    private static String getImageInBase64(String name) throws IOException {
        URL resourceUrl = VisionModel.class.getResource(name);
        if (resourceUrl == null) {
            throw new IOException("Could not find resource " + name);
        }

        byte[] fileContent = FileUtils.readFileToByteArray(new File(resourceUrl.getFile()));
        return Base64.getEncoder().encodeToString(fileContent);
    }

}