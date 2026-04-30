package com.udea.parcial.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApiVersionValidator {

    private static final String SUPPORTED_VERSION = "v1";

    /**
     * Valida la versión del header X-API-Version.
     * Retorna un Optional vacío si la versión es válida,
     * o un Optional con la respuesta de error si no lo es.
     */
    public Optional<ResponseEntity<String>> validate(String version) {
        if (!SUPPORTED_VERSION.equals(version)) {
            return Optional.of(ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported API version"));
        }
        return Optional.empty();
    }
}
