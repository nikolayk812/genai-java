package com.example.generativeai.testcontainers;

import com.example.generativeai.testcontainers.agents.ChatAgent;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HowTo extends Base {

	static String question = "How I can enable verbose logging in Testcontainers Desktop?";

	public static void main(String[] args) {

		String straightAnswer = getStraightAnswer();
		log.info("Question: {} - Straight Answer: {}", question, straightAnswer);

		String raggedAnswer = getRaggedAnswer();
		log.info("Question: {} - Ragged Answer: {}", question, raggedAnswer);

	}

	public static String getStraightAnswer() {
		ChatAgent straight = AiServices.builder(ChatAgent.class).chatLanguageModel(chatModel()).build();
		return straight.chat(question);
	}

	public static String getRaggedAnswer() {
		ChatAgent ragged = AiServices.builder(ChatAgent.class)
			.chatLanguageModel(chatModel())
			.contentRetriever(contentRetriever())
			.build();
		return ragged.chat(question);
	}

}