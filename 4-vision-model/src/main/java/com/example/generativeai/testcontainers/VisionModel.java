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

@Slf4j
public class VisionModel {

    private static final String MODEL_NAME = "moondream:1.8b";

    @SneakyThrows
    public static void main(String[] args) {

//        var container = new OllamaContainer("ollama/ollama:0.5.7")
//                .withReuse(true);
//
//        container.start();
//        container.execInContainer("ollama", "pull", MODEL_NAME);
//
//        var endpoint = container.getEndpoint();

        /*
        Cannot make it work at my machine in Docker/Testcontainers, potentially OOM
        colima start --runtime docker --memory 8 --dns 8.8.8.8

         ggml-cpu.c:8482: GGML_ASSERT(i01 >= 0 && i01 < ne01) failed
        /usr/bin/ollama(+0xdc2cf0)[0xca5ed5d62cf0]
        /usr/bin/ollama(ggml_abort+0x11c)[0xca5ed5d6315c]
        /usr/bin/ollama(+0xd80f48)[0xca5ed5d20f48]
        /usr/bin/ollama(+0xd9e5a4)[0xca5ed5d3e5a4]
        /usr/bin/ollama(+0xd9e82c)[0xca5ed5d3e82c]
        /lib/aarch64-linux-gnu/libc.so.6(+0x7d5c8)[0xe22d45a2d5c8]
        /lib/aarch64-linux-gnu/libc.so.6(+0xe5edc)[0xe22d45a95edc]
         */


        // ollama run moondream:1.8b
        var endpoint = "http://localhost:11434"; // default port of Ollama

        UserMessage userMessage = UserMessage.from(TextContent.from("What do you see?"),
                ImageContent.from(readImageInBase64("/computer.jpeg"), "image/jpeg"));

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(endpoint)
                .modelName(MODEL_NAME)
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