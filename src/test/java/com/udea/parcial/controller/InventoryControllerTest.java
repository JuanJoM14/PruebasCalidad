package com.udea.parcial.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.udea.parcial.entity.Almacen;
import com.udea.parcial.entity.Inventory;
import com.udea.parcial.entity.Product;
import com.udea.parcial.repository.AlmacenRepository;
import com.udea.parcial.repository.InventoryRepository;
import com.udea.parcial.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias del controlador InventoryController")
class InventoryControllerTest {

    @Mock
    private InventoryRepository inventoryRepo;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private AlmacenRepository almacenRepo;

    @InjectMocks
    private InventoryController controller;

    @Test
    @DisplayName("getInventory - Debe retornar 400 cuando la version no es v1")
    void testGetInventory_InvalidVersion() {
        // Arrange
        String version = "v2";
        Long almacenId = 1L;

        // Act
        ResponseEntity<?> response = controller.getInventory(version, almacenId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unsupported API version", response.getBody());
        verify(inventoryRepo, never()).findByAlmacenId(anyLong());
    }

    @Test
    @DisplayName("getInventory - Debe retornar inventario mapeado correctamente")
    void testGetInventory_Success() {
        // Arrange
        String version = "v1";
        Long almacenId = 1L;

        Almacen almacen = new Almacen();
        almacen.setId(almacenId);
        almacen.setNombre("Almacen Central");

        Product product = new Product();
        product.setId(10L);
        product.setName("Teclado");
        product.setDescription("Teclado mecanico");
        product.setSku("TEC-001");
        product.setPrice(new BigDecimal("250000.00"));

        Inventory inventory = new Inventory(almacen, product, 30);
        inventory.setId(100L);
        inventory.setLastUpdated(LocalDateTime.of(2024, 1, 10, 8, 30));

        when(inventoryRepo.findByAlmacenId(almacenId)).thenReturn(Collections.singletonList(inventory));

        // Act
        ResponseEntity<?> response = controller.getInventory(version, almacenId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        List<EntityModel<InventoryResponse>> body = (List<EntityModel<InventoryResponse>>) response.getBody();

        assertEquals(1, body.size());
        InventoryResponse dto = body.get(0).getContent();

        assertNotNull(dto);
        assertEquals(100L, dto.getInventoryId());
        assertEquals(1L, dto.getAlmacenId());
        assertEquals("Almacen Central", dto.getAlmacenNombre());
        assertEquals(10L, dto.getProductId());
        assertEquals("Teclado", dto.getProductName());
        assertEquals("Teclado mecanico", dto.getProductDescription());
        assertEquals("TEC-001", dto.getSku());
        assertEquals(new BigDecimal("250000.00"), dto.getPrice());
        assertEquals(30, dto.getStock());
        assertEquals(LocalDateTime.of(2024, 1, 10, 8, 30), dto.getLastUpdated());

        assertTrue(body.get(0).getLink("self").isPresent());
        assertTrue(body.get(0).getLink("almacen").isPresent());

        verify(inventoryRepo, times(1)).findByAlmacenId(almacenId);
    }

    @Test
    @DisplayName("createInventory - Debe retornar 400 cuando la version no es v1")
    void testCreateInventory_InvalidVersion() {
        // Arrange
        String version = "v2";
        InventoryRequest request = new InventoryRequest();

        // Act
        ResponseEntity<?> response = controller.createInventory(version, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unsupported API version", response.getBody());
        verify(almacenRepo, never()).findById(anyLong());
        verify(productRepo, never()).save(any(Product.class));
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("createInventory - Debe retornar 404 cuando el almacen no existe")
    void testCreateInventory_AlmacenNotFound() {
        // Arrange
        String version = "v1";
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(99L);

        when(almacenRepo.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = controller.createInventory(version, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("El almacén no existe", response.getBody());
        verify(almacenRepo, times(1)).findById(99L);
        verify(productRepo, never()).save(any(Product.class));
        verify(inventoryRepo, never()).save(any(Inventory.class));
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
        request.setPrice(new BigDecimal("120000.00"));
        request.setStock(12);

        Almacen almacen = new Almacen();
        almacen.setId(1L);
        almacen.setNombre("Almacen Norte");

        when(almacenRepo.findById(1L)).thenReturn(Optional.of(almacen));
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });
        when(inventoryRepo.save(any(Inventory.class))).thenAnswer(invocation -> {
            Inventory saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

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
        assertEquals(new BigDecimal("120000.00"), dto.getPrice());
        assertEquals(12, dto.getStock());
        assertNotNull(dto.getLastUpdated());

        assertTrue(body.getLink("self").isPresent());
        assertTrue(body.getLink("inventario_por_almacen").isPresent());

        verify(almacenRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).save(any(Product.class));
        verify(inventoryRepo, times(1)).save(any(Inventory.class));
    }
}
