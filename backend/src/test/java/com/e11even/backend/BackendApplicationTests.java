package com.e11even.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BackendApplicationTests {

	@Test
	void applicationClass_shouldHaveSpringBootAnnotation() {
		assertTrue(BackendApplication.class.isAnnotationPresent(SpringBootApplication.class));
	}

}
