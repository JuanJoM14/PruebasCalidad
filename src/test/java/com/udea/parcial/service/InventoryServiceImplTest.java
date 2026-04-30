package com.udea.parcial.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.entity.Almacen;
import com.udea.parcial.entity.Inventory;
import com.udea.parcial.entity.Product;
import com.udea.parcial.repository.AlmacenRepository;
import com.udea.parcial.repository.InventoryRepository;
import com.udea.parcial.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias del servicio InventoryServiceImpl")
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AlmacenRepository almacenRepository;

    @Mock
    private InventoryMapper mapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Almacen almacen;
    private Product product;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        almacen = new Almacen();
        almacen.setNombre("Almacén Central");
        almacen.setCiudad("Medellín");
        almacen.setDireccion("Calle 10 # 20-30");
        almacen.setTelefono("3001234567");

        product = new Product();
        product.setName("Laptop Dell XPS 15");
        product.setDescription("Laptop de alta gama");
        product.setSku("DELL-XPS-001");
        product.setPrice(new BigDecimal("4500000.00"));

        inventory = new Inventory(almacen, product, 50);
        inventory.setLastUpdated(LocalDateTime.now());

        lenient().when(mapper.toResponse(any(Inventory.class))).thenAnswer(invocation -> {
            Inventory inv = invocation.getArgument(0);
            InventoryResponse response = new InventoryResponse();
            response.setAlmacenNombre(inv.getAlmacen() != null ? inv.getAlmacen().getNombre() : null);
            response.setAlmacenId(inv.getAlmacen() != null ? inv.getAlmacen().getId() : null);
            response.setProductName(inv.getProduct() != null ? inv.getProduct().getName() : null);
            response.setProductDescription(inv.getProduct() != null ? inv.getProduct().getDescription() : null);
            response.setProductId(inv.getProduct() != null ? inv.getProduct().getId() : null);
            response.setSku(inv.getProduct() != null ? inv.getProduct().getSku() : null);
            response.setPrice(inv.getProduct() != null ? inv.getProduct().getPrice() : null);
            response.setStock(inv.getStock());
            response.setLastUpdated(inv.getLastUpdated());
            response.setInventoryId(inv.getId());
            return response;
        });
    }

    @Test
    @DisplayName("getInventoryByAlmacen - Debe retornar lista de inventarios exitosamente")
    void testGetInventoryByAlmacen_Success() {
        // Arrange
        Long almacenId = 1L;
        Inventory inventory1 = new Inventory(almacen, product, 50);
        
        Product product2 = new Product();
        product2.setName("Mouse Logitech");
        product2.setDescription("Mouse inalámbrico");
        product2.setSku("LOG-MX-001");
        product2.setPrice(new BigDecimal("150000.00"));
        
        Inventory inventory2 = new Inventory(almacen, product2, 100);
        
        List<Inventory> mockInventories = Arrays.asList(inventory1, inventory2);
        
        when(inventoryRepository.findByAlmacenId(almacenId)).thenReturn(mockInventories);

        // Act
        List<InventoryResponse> responses = inventoryService.getInventoryByAlmacen(almacenId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        InventoryResponse response1 = responses.get(0);
        assertEquals("Laptop Dell XPS 15", response1.getProductName());
        assertEquals("DELL-XPS-001", response1.getSku());
        assertEquals(50, response1.getStock());
        assertEquals(new BigDecimal("4500000.00"), response1.getPrice());
        
        InventoryResponse response2 = responses.get(1);
        assertEquals("Mouse Logitech", response2.getProductName());
        assertEquals(100, response2.getStock());

        verify(inventoryRepository, times(1)).findByAlmacenId(almacenId);
    }

    @Test
    @DisplayName("getInventoryByAlmacen - Debe retornar lista vacía cuando no hay inventarios")
    void testGetInventoryByAlmacen_EmptyList() {
        // Arrange
        Long almacenId = 999L;
        when(inventoryRepository.findByAlmacenId(almacenId)).thenReturn(Arrays.asList());

        // Act
        List<InventoryResponse> responses = inventoryService.getInventoryByAlmacen(almacenId);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(inventoryRepository, times(1)).findByAlmacenId(almacenId);
    }

    @Test
    @DisplayName("createInventory - Debe crear inventario exitosamente")
    void testCreateInventory_Success() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(1L);
        request.setProductName("Teclado Mecánico");
        request.setProductDescription("Teclado gaming RGB");
        request.setSku("TEC-MEC-001");
        request.setPrice(new BigDecimal("350000.00"));
        request.setStock(25);

        Almacen testAlmacen = new Almacen();
        testAlmacen.setNombre("Almacén Norte");
        testAlmacen.setCiudad("Bogotá");

        Product savedProduct = new Product();
        savedProduct.setName(request.getProductName());
        savedProduct.setDescription(request.getProductDescription());
        savedProduct.setSku(request.getSku());
        savedProduct.setPrice(request.getPrice());

        Inventory savedInventory = new Inventory(testAlmacen, savedProduct, request.getStock());

        when(almacenRepository.findById(1L)).thenReturn(Optional.of(testAlmacen));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

        // Act
        InventoryResponse response = inventoryService.createInventory(request);

        // Assert
        assertNotNull(response);
        assertEquals("Teclado Mecánico", response.getProductName());
        assertEquals("Teclado gaming RGB", response.getProductDescription());
        assertEquals("TEC-MEC-001", response.getSku());
        assertEquals(new BigDecimal("350000.00"), response.getPrice());
        assertEquals(25, response.getStock());
        assertEquals("Almacén Norte", response.getAlmacenNombre());

        verify(almacenRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("createInventory - Debe lanzar excepción cuando el almacén no existe")
    void testCreateInventory_AlmacenNotFound() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(999L);
        request.setProductName("Producto Test");
        request.setProductDescription("Descripción test");
        request.setSku("TEST-001");
        request.setPrice(new BigDecimal("100000.00"));
        request.setStock(10);

        when(almacenRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            inventoryService.createInventory(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("El almacén no existe", exception.getReason());
        
        verify(almacenRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("createInventory - Verifica que se crea producto con datos correctos")
    void testCreateInventory_ProductCreatedWithCorrectData() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(1L);
        request.setProductName("Monitor Samsung");
        request.setProductDescription("Monitor 27 pulgadas 4K");
        request.setSku("SAM-MON-001");
        request.setPrice(new BigDecimal("1200000.00"));
        request.setStock(15);

        Almacen testAlmacen = new Almacen();
        testAlmacen.setNombre("Almacén Test");
        
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(testAlmacen));

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        inventoryService.createInventory(request);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertEquals("Monitor Samsung", savedProduct.getName());
        assertEquals("Monitor 27 pulgadas 4K", savedProduct.getDescription());
        assertEquals("SAM-MON-001", savedProduct.getSku());
        assertEquals(new BigDecimal("1200000.00"), savedProduct.getPrice());
    }

    @Test
    @DisplayName("createInventory - Verifica que se crea inventario con stock correcto")
    void testCreateInventory_InventoryCreatedWithCorrectStock() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(1L);
        request.setProductName("Producto Test");
        request.setProductDescription("Descripción");
        request.setSku("TEST-001");
        request.setPrice(new BigDecimal("50000.00"));
        request.setStock(75);

        Almacen testAlmacen = new Almacen();
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(testAlmacen));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        inventoryService.createInventory(request);

        // Assert
        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository, times(1)).save(inventoryCaptor.capture());

        Inventory savedInventory = inventoryCaptor.getValue();
        assertEquals(75, savedInventory.getStock());
        assertNotNull(savedInventory.getLastUpdated());
    }
}
