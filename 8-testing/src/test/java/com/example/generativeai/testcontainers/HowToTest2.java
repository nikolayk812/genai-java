package com.example.generativeai.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.example.generativeai.testcontainers.Base.embeddingModel;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class HowToTest2 {

	static float[] reference;

	@BeforeAll
	static void setUp() {
		reference = embeddingModel().embed(
				"To enable verbose logging in Testcontainers Desktop you can set the property cloud.logs.verbose to true in the ~/.testcontainers.properties file file or add the --verbose flag when running the cli")
			.content()
			.vector();
	}

	@Test
	void getStraightAnswer() {
		String straightAnswer = HowTo.getStraightAnswer();
		log.info("Straight Answer: {}", straightAnswer);

		float[] vector = embeddingModel().embed(straightAnswer).content().vector();
		double similarity = cosineSimilarity(reference, vector);

		log.info("Similarity: {}", similarity);
		assertTrue(similarity > 0.8);
	}

	@Test
	void getRaggedAnswer() {
		String raggedAnswer = HowTo.getRaggedAnswer();
		log.info("Ragged Answer: {}", raggedAnswer);

		float[] vector = embeddingModel().embed(raggedAnswer).content().vector();
		double similarity = cosineSimilarity(reference, vector);

		log.info("Similarity: {}", similarity);
		assertTrue(similarity > 0.8);
	}

	public static double cosineSimilarity(float[] vectorA, float[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

}