package com.example.generativeai.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class HowToTest1 {

	@Test
	void getStraightAnswer() {
		String straightAnswer = HowTo.getStraightAnswer();
		log.info("Straight Answer: {}", straightAnswer);
		assertTrue(straightAnswer.contains("cloud.logs.verbose = true"));
	}

	@Test
	void getRaggedAnswer() {
		String raggedAnswer = HowTo.getRaggedAnswer();
		log.info("Ragged Answer: {}", raggedAnswer);
		assertTrue(raggedAnswer.contains("cloud.logs.verbose = true"));
	}

}