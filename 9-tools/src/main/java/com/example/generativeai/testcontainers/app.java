package com.example.generativeai.testcontainers;

import com.example.generativeai.testcontainers.agents.ChatAgent;
import com.example.generativeai.testcontainers.tools.DockerTools;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class app {

	public static void main(String[] args) {

		ChatAgent agent = AiServices.builder(ChatAgent.class)
			.chatLanguageModel(chatModel())
			.tools(new DockerTools())
			.build();

		String chat = agent.chat("list files in current directory directory");
		log.info("Chat: {}", chat);
	}

	static ChatLanguageModel chatModel() {
		OllamaContainer ollamaContainer = new OllamaContainer(
				DockerImageName.parse("ilopezluna/%s:0.3.13-%s".formatted("llama3.2", "3b"))
					.asCompatibleSubstituteFor("ollama/ollama"))
			.withReuse(true);
		ollamaContainer.start();
		return OllamaChatModel.builder()
			.baseUrl(ollamaContainer.getEndpoint())
			.modelName("llama3.2:3b")
			.temperature(0d)
			.topK(1)
			.seed(42)
			.logRequests(true)
			.logResponses(true)
			.build();
	}

}
