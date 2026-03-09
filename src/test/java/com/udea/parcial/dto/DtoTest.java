package com.udea.parcial.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    @DisplayName("InventoryRequest - Todos los getters y setters")
    void testInventoryRequestGettersAndSetters() {
        InventoryRequest request = new InventoryRequest();
        
        request.setAlmacenId(1L);
        request.setProductName("Test Product");
        request.setProductDescription("Test Description");
        request.setSku("TEST-SKU");
        request.setPrice(new BigDecimal("1000.00"));
        request.setStock(50);
        
        assertEquals(1L, request.getAlmacenId());
        assertEquals("Test Product", request.getProductName());
        assertEquals("Test Description", request.getProductDescription());
        assertEquals("TEST-SKU", request.getSku());
        assertEquals(new BigDecimal("1000.00"), request.getPrice());
        assertEquals(50, request.getStock());
    }

    @Test
    @DisplayName("InventoryResponse - Todos los getters y setters")
    void testInventoryResponseGettersAndSetters() {
        InventoryResponse response = new InventoryResponse();
        LocalDateTime now = LocalDateTime.now();
        
        response.setInventoryId(1L);
        response.setAlmacenId(2L);
        response.setAlmacenNombre("Almacén Test");
        response.setProductId(3L);
        response.setProductName("Product Test");
        response.setProductDescription("Description Test");
        response.setSku("SKU-TEST");
        response.setPrice(new BigDecimal("500.00"));
        response.setStock(100);
        response.setLastUpdated(now);
        
        assertEquals(1L, response.getInventoryId());
        assertEquals(2L, response.getAlmacenId());
        assertEquals("Almacén Test", response.getAlmacenNombre());
        assertEquals(3L, response.getProductId());
        assertEquals("Product Test", response.getProductName());
        assertEquals("Description Test", response.getProductDescription());
        assertEquals("SKU-TEST", response.getSku());
        assertEquals(new BigDecimal("500.00"), response.getPrice());
        assertEquals(100, response.getStock());
        assertEquals(now, response.getLastUpdated());
    }
}