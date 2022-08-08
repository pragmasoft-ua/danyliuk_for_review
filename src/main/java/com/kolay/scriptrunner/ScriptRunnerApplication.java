package com.kolay.scriptrunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * REST API wrapper around the GraalJs javascript interpreter.
 */
@SpringBootApplication
public class ScriptRunnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScriptRunnerApplication.class, args);
	}

}
