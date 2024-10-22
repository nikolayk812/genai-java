package com.example.generativeai.testcontainers;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class HelloWorld {

	public static void main(String[] args) {
		OllamaContainer ollamaContainer = new OllamaContainer(
				DockerImageName.parse("ilopezluna/llama3.2:0.3.13-1b").asCompatibleSubstituteFor("ollama/ollama"))
			.withReuse(true);
		ollamaContainer.start();
		ChatLanguageModel model = OllamaChatModel.builder()
			.baseUrl(ollamaContainer.getEndpoint())
			.modelName("llama3.2:1b")
			.build();
		String answer = model.generate("Provide 3 short bullet points explaining why Java is awesome");
		log.info("Response from LLM (\uD83E\uDD16)-> {}", answer);
	}

}
