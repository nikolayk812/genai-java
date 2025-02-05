package com.example.generativeai.testcontainers;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;

@Slf4j
public class Streaming {

	private static final String MODEL_NAME = "qwen2:0.5b-instruct-q5_1";

	@SneakyThrows
	public static void main(String[] args) {
		var container = new OllamaContainer("ollama/ollama:0.5.7").withReuse(true);

		container.start();
		container.execInContainer("ollama", "pull", MODEL_NAME);

		var model = OllamaStreamingChatModel.builder().baseUrl(container.getEndpoint()).modelName(MODEL_NAME).build();

		model.generate("Give me a detailed and long explanation of why Testcontainers is great",
				new StreamingResponseHandler<>() {
					@Override
					public void onNext(String token) {
						System.out.print(token);
					}

					@Override
					public void onError(Throwable error) {
						System.out.println();
						System.out.println("Error: " + error.getMessage());
						System.exit(1);
					}

					@Override
					public void onComplete(Response<AiMessage> response) {
						System.out.println();
						System.exit(0);
					}
				});

	}

}
