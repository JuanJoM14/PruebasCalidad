package com.udea.parcial.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;

class InventoryResponseAssemblerTest {

    private InventoryResponseAssembler inventoryResponseAssembler;

    @BeforeEach
    void setUp() {
        inventoryResponseAssembler = new InventoryResponseAssembler();
    }

    @Test
    void toInventoryModelShouldReturnEntityModelWithContentAndLinks() {
        // Arrange
        InventoryResponse dto = new InventoryResponse();
        dto.setAlmacenId(1L);

        String version = "v1";
        Long almacenId = 1L;

        // Act
        EntityModel<InventoryResponse> result = inventoryResponseAssembler
                .toInventoryModel(dto, version, almacenId);

        // Assert
        assertNotNull(result);
        assertEquals(dto, result.getContent());
        assertTrue(result.hasLink("self"));
        assertTrue(result.hasLink("almacen"));
    }

    @Test
    void toInventoryModelShouldCreateSelfLinkUsingProvidedAlmacenId() {
        // Arrange
        InventoryResponse dto = new InventoryResponse();
        dto.setAlmacenId(1L);

        String version = "v1";
        Long almacenId = 5L;

        // Act
        EntityModel<InventoryResponse> result = inventoryResponseAssembler
                .toInventoryModel(dto, version, almacenId);

        // Assert
        assertTrue(result.hasLink("self"));
        assertTrue(result.getRequiredLink("self").getHref().contains("almacenId=5"));
    }

    @Test
    void toInventoryModelShouldCreateAlmacenLinkUsingDtoAlmacenId() {
        // Arrange
        InventoryResponse dto = new InventoryResponse();
        dto.setAlmacenId(3L);

        String version = "v1";
        Long almacenId = 1L;

        // Act
        EntityModel<InventoryResponse> result = inventoryResponseAssembler
                .toInventoryModel(dto, version, almacenId);

        // Assert
        assertTrue(result.hasLink("almacen"));
        assertTrue(result.getRequiredLink("almacen").getHref().contains("almacenId=3"));
    }

    @Test
    void toCreatedInventoryModelShouldReturnEntityModelWithContentAndLinks() {
        // Arrange
        InventoryResponse dto = new InventoryResponse();
        dto.setAlmacenId(1L);

        InventoryRequest request = new InventoryRequest();

        String version = "v1";

        // Act
        EntityModel<InventoryResponse> result = inventoryResponseAssembler
                .toCreatedInventoryModel(dto, version, request);

        // Assert
        assertNotNull(result);
        assertEquals(dto, result.getContent());
        assertTrue(result.hasLink("self"));
        assertTrue(result.hasLink("inventario_por_almacen"));
    }

    @Test
    void toCreatedInventoryModelShouldCreateInventoryByStoreLinkUsingDtoAlmacenId() {
        // Arrange
        InventoryResponse dto = new InventoryResponse();
        dto.setAlmacenId(7L);

        InventoryRequest request = new InventoryRequest();

        String version = "v1";

        // Act
        EntityModel<InventoryResponse> result = inventoryResponseAssembler
                .toCreatedInventoryModel(dto, version, request);

        // Assert
        assertTrue(result.hasLink("inventario_por_almacen"));
        assertTrue(result.getRequiredLink("inventario_por_almacen").getHref().contains("almacenId=7"));
    }
}