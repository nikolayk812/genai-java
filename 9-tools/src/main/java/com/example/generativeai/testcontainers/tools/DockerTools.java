package com.example.generativeai.testcontainers.tools;

import dev.langchain4j.agent.tool.Tool;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class DockerTools {

	@Tool("list files in current directory directory")
	String listFiles() {
		GenericContainer alpine = new GenericContainer<>(DockerImageName.parse("alpine"));
		alpine.withFileSystemBind(System.getProperty("user.dir"), "/data")
			.withWorkingDirectory("/data")
			.withCommand("ls");
		alpine.start();
		return alpine.getLogs();
	}

}
