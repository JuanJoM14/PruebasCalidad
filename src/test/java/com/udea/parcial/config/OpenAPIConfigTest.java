package com.udea.parcial.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAPIConfigTest {

    @Test
    @DisplayName("OpenAPIConfig - Bean creado correctamente")
    void testCustomOpenAPIBean() {
        OpenAPIConfig config = new OpenAPIConfig();
        OpenAPI openAPI = config.customOpenAPI();
        
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("API de Inventario - Parcial UdeA", openAPI.getInfo().getTitle());
        assertEquals("v1", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getServers());
        assertEquals(2, openAPI.getServers().size());
        assertNotNull(openAPI.getComponents());
    }
}