package testcontainers;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.ContainerFetchException;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;

@Slf4j
public class VisionModelStepByStep {

	@SneakyThrows
	public static void main(String[] args) {
		String imageName = "tc-ollama-moondream";
		OllamaContainer ollama = new OllamaContainer(
				DockerImageName.parse(imageName).asCompatibleSubstituteFor("ollama/ollama:0.3.13"));
		try {
			ollama.start();
		}
		catch (ContainerFetchException ex) {
			// If image doesn't exist, create it. Subsequent runs will reuse the image.
			createImage(imageName);
			ollama.start();
		}

		UserMessage userMessage = UserMessage.from(TextContent.from("What do you see?"),
				ImageContent.from(getImageInBase64("/computer.jpeg"), "image/jpeg"));

		ChatLanguageModel model = OllamaChatModel.builder()
			.baseUrl(ollama.getEndpoint())
			.modelName("moondream:latest")
			.build();
		Response<AiMessage> generate = model.generate(userMessage);
		log.info("Response from LLM (\uD83E\uDD16)-> {}", generate.content().text());
	}

	public static void createImage(String imageName) throws IOException, InterruptedException {
		OllamaContainer ollama = new OllamaContainer("ollama/ollama:0.3.13");
		ollama.start();
		ollama.execInContainer("ollama", "pull", "moondream");
		ollama.commitToImage(imageName);
	}

	private static String getImageInBase64(String name) throws IOException {
		URL resourceUrl = VisionModelStepByStep.class.getResource(name);
		byte[] fileContent = FileUtils.readFileToByteArray(new File(resourceUrl.getFile()));
		return Base64.getEncoder().encodeToString(fileContent);
	}

}