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

    private static final String VERSION_OK = "v1";
    private static final String VERSION_BAD = "v2";
    private static final String VERSION_ERROR_MESSAGE = "Unsupported API version";

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
        String version = VERSION_BAD;
        Long almacenId = 1L;

        when(versionValidator.validate(version))
            .thenReturn(Optional.of(buildUnsupportedVersionResponse()));

        // Act
        ResponseEntity<?> response = controller.getInventory(version, almacenId);

        // Assert
        assertBadRequestUnsupportedVersion(response);
        verify(inventoryService, never()).getInventoryByAlmacen(any());
        verify(responseAssembler, never()).toInventoryModel(any(), any(), any());
    }

    @Test
    @DisplayName("getInventory - Debe retornar inventario mapeado correctamente")
    void testGetInventory_Success() {
        // Arrange
        String version = VERSION_OK;
        Long almacenId = 1L;

        InventoryResponse dto1 = buildInventoryResponse(100L, 1L, "Almacen Central");
        InventoryResponse dto2 = buildInventoryResponse(101L, 1L, "Almacen Central");

        when(versionValidator.validate(version)).thenReturn(Optional.empty());
        when(inventoryService.getInventoryByAlmacen(almacenId)).thenReturn(List.of(dto1, dto2));
        when(responseAssembler.toInventoryModel(any(InventoryResponse.class), eq(version), eq(almacenId)))
                .thenAnswer(invocation -> EntityModel.of(invocation.getArgument(0)));

        // Act
        ResponseEntity<?> response = controller.getInventory(version, almacenId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<EntityModel<InventoryResponse>> body = getBodyAsInventoryList(response);

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
        String version = VERSION_BAD;
        InventoryRequest request = new InventoryRequest();

        when(versionValidator.validate(version))
            .thenReturn(Optional.of(buildUnsupportedVersionResponse()));

        // Act
        ResponseEntity<?> response = controller.createInventory(version, request);

        // Assert
        assertBadRequestUnsupportedVersion(response);
        verify(inventoryService, never()).createInventory(any(InventoryRequest.class));
        verify(responseAssembler, never()).toCreatedInventoryModel(any(), any(), any());
    }

    @Test
    @DisplayName("createInventory - Debe crear producto e inventario exitosamente")
    void testCreateInventory_Success() {
        // Arrange
        String version = VERSION_OK;
        InventoryRequest request = buildInventoryRequest();
        InventoryResponse serviceDto = buildCreatedInventoryResponse();

        when(versionValidator.validate(version)).thenReturn(Optional.empty());
        when(inventoryService.createInventory(request)).thenReturn(serviceDto);
        when(responseAssembler.toCreatedInventoryModel(serviceDto, version, request))
            .thenReturn(EntityModel.of(serviceDto));

        // Act
        ResponseEntity<?> response = controller.createInventory(version, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        EntityModel<InventoryResponse> body = getBodyAsInventoryModel(response);
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

    private static ResponseEntity<String> buildUnsupportedVersionResponse() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(VERSION_ERROR_MESSAGE);
    }

    private static InventoryRequest buildInventoryRequest() {
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(1L);
        request.setProductName("Mouse");
        request.setProductDescription("Mouse inalambrico");
        request.setSku("MOU-001");
        request.setStock(12);
        return request;
    }

    private static InventoryResponse buildInventoryResponse(Long inventoryId,
                                                            Long almacenId,
                                                            String almacenNombre) {
        InventoryResponse dto = new InventoryResponse();
        dto.setInventoryId(inventoryId);
        dto.setAlmacenId(almacenId);
        dto.setAlmacenNombre(almacenNombre);
        return dto;
    }

    private static InventoryResponse buildCreatedInventoryResponse() {
        InventoryResponse dto = new InventoryResponse();
        dto.setInventoryId(20L);
        dto.setAlmacenId(1L);
        dto.setAlmacenNombre("Almacen Norte");
        dto.setProductId(5L);
        dto.setProductName("Mouse");
        dto.setProductDescription("Mouse inalambrico");
        dto.setSku("MOU-001");
        dto.setStock(12);
        return dto;
    }

    private static void assertBadRequestUnsupportedVersion(ResponseEntity<?> response) {
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(VERSION_ERROR_MESSAGE, response.getBody());
    }

    @SuppressWarnings("unchecked")
    private static List<EntityModel<InventoryResponse>> getBodyAsInventoryList(ResponseEntity<?> response) {
        assertNotNull(response.getBody());
        return (List<EntityModel<InventoryResponse>>) response.getBody();
    }

    @SuppressWarnings("unchecked")
    private static EntityModel<InventoryResponse> getBodyAsInventoryModel(ResponseEntity<?> response) {
        assertNotNull(response.getBody());
        return (EntityModel<InventoryResponse>) response.getBody();
    }
}
