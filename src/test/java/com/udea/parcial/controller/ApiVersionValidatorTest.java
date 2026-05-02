package com.udea.parcial.controller;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApiVersionValidatorTest {

    private ApiVersionValidator apiVersionValidator;

    @BeforeEach
    void setUp() {
        apiVersionValidator = new ApiVersionValidator();
    }

    @Test
    void validateShouldReturnEmptyOptionalWhenVersionIsSupported() {
        // Arrange
        String version = "v1";

        // Act
        Optional<ResponseEntity<String>> result = apiVersionValidator.validate(version);

        // Assert
        assertTrue(result.isEmpty());
    }

    //arrange
    @ParameterizedTest(name = "version: {0}")
    @ValueSource(strings = {"v2"})
    @NullAndEmptySource
    void validateShouldReturnBadRequestWhenVersionIsUnsupportedNullOrEmpty(String version) {
        // Act
        Optional<ResponseEntity<String>> result = apiVersionValidator.validate(version);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(HttpStatus.BAD_REQUEST, result.get().getStatusCode());
        assertEquals("Unsupported API version", result.get().getBody());
    }
}