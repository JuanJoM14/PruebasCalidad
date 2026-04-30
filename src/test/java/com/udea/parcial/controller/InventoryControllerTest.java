package com.udea.parcial.controller;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.service.InventoryService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias del controlador InventoryController")
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ApiVersionValidator versionValidator;

    @Mock
    private InventoryResponseAssembler responseAssembler;

    @InjectMocks
    private InventoryController controller;

    @Test
    @DisplayName("getInventory - Debe retornar 400 cuando la version no es v1")
    void testGetInventory_InvalidVersion() {
        // Arrange
        String version = "v2";
        Long almacenId = 1L;
        ResponseEntity<String> errorResponse = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Unsupported API version");

        when(versionValidator.validate(version)).thenReturn(Optional.of(errorResponse));

        // Act
        ResponseEntity<?> response = controller.getInventory(version, almacenId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unsupported API version", response.getBody());
        verify(inventoryService, never()).getInventoryByAlmacen(any());
        verify(responseAssembler, never()).toInventoryModel(any(), any(), any());
    }

    @Test
    @DisplayName("getInventory - Debe retornar inventario mapeado correctamente")
    void testGetInventory_Success() {
        // Arrange
        String version = "v1";
        Long almacenId = 1L;

        InventoryResponse dto1 = new InventoryResponse();
        dto1.setInventoryId(100L);
        dto1.setAlmacenId(1L);
        dto1.setAlmacenNombre("Almacen Central");

        InventoryResponse dto2 = new InventoryResponse();
        dto2.setInventoryId(101L);
        dto2.setAlmacenId(1L);
        dto2.setAlmacenNombre("Almacen Central");

        when(versionValidator.validate(version)).thenReturn(Optional.empty());
        when(inventoryService.getInventoryByAlmacen(almacenId)).thenReturn(List.of(dto1, dto2));
        when(responseAssembler.toInventoryModel(any(InventoryResponse.class), eq(version), eq(almacenId)))
                .thenAnswer(invocation -> EntityModel.of(invocation.getArgument(0)));

        // Act
        ResponseEntity<?> response = controller.getInventory(version, almacenId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        List<EntityModel<InventoryResponse>> body = (List<EntityModel<InventoryResponse>>) response.getBody();

        assertEquals(2, body.size());
        assertEquals(dto1, body.get(0).getContent());
        assertEquals(dto2, body.get(1).getContent());

        verify(inventoryService, times(1)).getInventoryByAlmacen(almacenId);
        verify(responseAssembler, times(2)).toInventoryModel(any(InventoryResponse.class), eq(version), eq(almacenId));
    }

    @Test
    @DisplayName("createInventory - Debe retornar 400 cuando la version no es v1")
    void testCreateInventory_InvalidVersion() {
        // Arrange
        String version = "v2";
        InventoryRequest request = new InventoryRequest();
        ResponseEntity<String> errorResponse = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Unsupported API version");

        when(versionValidator.validate(version)).thenReturn(Optional.of(errorResponse));

        // Act
        ResponseEntity<?> response = controller.createInventory(version, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unsupported API version", response.getBody());
        verify(inventoryService, never()).createInventory(any(InventoryRequest.class));
        verify(responseAssembler, never()).toCreatedInventoryModel(any(), any(), any());
    }

    @Test
    @DisplayName("createInventory - Debe crear producto e inventario exitosamente")
    void testCreateInventory_Success() {
        // Arrange
        String version = "v1";
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(1L);
        request.setProductName("Mouse");
        request.setProductDescription("Mouse inalambrico");
        request.setSku("MOU-001");
        request.setStock(12);

        InventoryResponse serviceDto = new InventoryResponse();
        serviceDto.setInventoryId(20L);
        serviceDto.setAlmacenId(1L);
        serviceDto.setAlmacenNombre("Almacen Norte");
        serviceDto.setProductId(5L);
        serviceDto.setProductName("Mouse");
        serviceDto.setProductDescription("Mouse inalambrico");
        serviceDto.setSku("MOU-001");
        serviceDto.setStock(12);

        when(versionValidator.validate(version)).thenReturn(Optional.empty());
        when(inventoryService.createInventory(request)).thenReturn(serviceDto);
        when(responseAssembler.toCreatedInventoryModel(serviceDto, version, request))
            .thenReturn(EntityModel.of(serviceDto));

        // Act
        ResponseEntity<?> response = controller.createInventory(version, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        EntityModel<InventoryResponse> body = (EntityModel<InventoryResponse>) response.getBody();
        InventoryResponse dto = body.getContent();

        assertNotNull(dto);
        assertEquals(20L, dto.getInventoryId());
        assertEquals(1L, dto.getAlmacenId());
        assertEquals("Almacen Norte", dto.getAlmacenNombre());
        assertEquals(5L, dto.getProductId());
        assertEquals("Mouse", dto.getProductName());
        assertEquals("Mouse inalambrico", dto.getProductDescription());
        assertEquals("MOU-001", dto.getSku());
        assertEquals(12, dto.getStock());

        verify(inventoryService, times(1)).createInventory(request);
        verify(responseAssembler, times(1)).toCreatedInventoryModel(serviceDto, version, request);
    }
}
