package com.udea.parcial.controller;

import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.entity.Almacen;
import com.udea.parcial.entity.Inventory;
import com.udea.parcial.entity.Product;
import com.udea.parcial.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import({ApiVersionValidator.class, InventoryResponseAssembler.class})
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    private Almacen almacen;
    private Product product;
    private Inventory inventory;
    private InventoryResponse inventoryResponse;

    @BeforeEach
    void setUp() {
        almacen = new Almacen("Almacén Central", "Medellín", "Calle 10", "3001234567");
        product = new Product("Laptop", "SKU-001", "Laptop gaming", new BigDecimal("3000000"));
        inventory = new Inventory(almacen, product, 50);
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryResponse = new InventoryResponse();
        inventoryResponse.setAlmacenId(1L);
        inventoryResponse.setAlmacenNombre("Almacén Central");
        inventoryResponse.setProductName("Laptop");
        inventoryResponse.setProductDescription("Laptop gaming");
        inventoryResponse.setSku("SKU-001");
        inventoryResponse.setPrice(new BigDecimal("3000000"));
        inventoryResponse.setStock(50);
    }

    @Test
    @DisplayName("GET /inventory - Con versión correcta debe retornar 200")
    void testGetInventory_WithCorrectVersion() throws Exception {
        when(inventoryService.getInventoryByAlmacen(1L)).thenReturn(List.of(inventoryResponse));

        mockMvc.perform(get("/inventory")
                .header("X-API-Version", "v1")
                .param("almacenId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /inventory - Sin versión debe retornar 400")
    void testGetInventory_WithoutVersion() throws Exception {
        mockMvc.perform(get("/inventory")
                .param("almacenId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /inventory - Con versión incorrecta debe retornar 400")
    void testGetInventory_WithWrongVersion() throws Exception {
        mockMvc.perform(get("/inventory")
                .header("X-API-Version", "v2")
                .param("almacenId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsupported API version"));
    }

    @Test
    @DisplayName("GET /inventory - Con almacén vacío debe retornar lista vacía")
    void testGetInventory_EmptyWarehouse() throws Exception {
        when(inventoryService.getInventoryByAlmacen(999L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inventory")
                .header("X-API-Version", "v1")
                .param("almacenId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /inventory - Crear inventario exitosamente")
    void testCreateInventory_Success() throws Exception {
        when(inventoryService.createInventory(any())).thenReturn(inventoryResponse);

        String requestBody = """
            {
                "almacenId": 1,
                "productName": "Laptop",
                "productDescription": "Laptop gaming",
                "sku": "SKU-001",
                "price": 3000000,
                "stock": 50
            }
            """;

        mockMvc.perform(post("/inventory")
                .header("X-API-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    @DisplayName("POST /inventory - Con almacén inexistente debe retornar 404")
    void testCreateInventory_AlmacenNotFound() throws Exception {
        when(inventoryService.createInventory(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "El almacén no existe"));

        String requestBody = """
            {
                "almacenId": 999,
                "productName": "Laptop",
                "productDescription": "Test",
                "sku": "SKU-999",
                "price": 1000000,
                "stock": 10
            }
            """;

        mockMvc.perform(post("/inventory")
                .header("X-API-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /inventory - Sin versión debe retornar 400")
    void testCreateInventory_WithoutVersion() throws Exception {
        String requestBody = """
            {
                "almacenId": 1,
                "productName": "Test",
                "productDescription": "Test",
                "sku": "TEST",
                "price": 100000,
                "stock": 5
            }
            """;

        mockMvc.perform(post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}