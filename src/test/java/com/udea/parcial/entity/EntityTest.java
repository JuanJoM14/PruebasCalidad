package com.udea.parcial.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    @DisplayName("Product - equals y hashCode")
    void testProductEqualsAndHashCode() {
        // Arrange
        Product p1 = new Product("Laptop", "SKU-001", "Gaming", new BigDecimal("3000000"));
        Product p2 = new Product("Mouse", "SKU-002", "Wireless", new BigDecimal("50000"));

        p1.setId(1L);
        p2.setId(1L);

        // Act
        boolean equalsWithSameId = p1.equals(p2);
        boolean equalsWithNull = p1.equals(null);
        boolean equalsWithDifferentType = p1.equals("string");
        int firstHashCode = p1.hashCode();
        int secondHashCode = p2.hashCode();

        // Assert
        assertEquals(true, equalsWithSameId);
        assertNotEquals(null, p1);
        assertEquals(false, equalsWithNull);
        assertEquals(false, equalsWithDifferentType);
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    @DisplayName("Almacen - equals y hashCode")
    void testAlmacenEqualsAndHashCode() {
        // Arrange
        Almacen a1 = new Almacen("Almacén 1", "Medellín", "Calle 1", "3001234567");
        Almacen a2 = new Almacen("Almacén 2", "Bogotá", "Calle 2", "3007654321");

        a1.setId(10L);
        a2.setId(10L);

        // Act
        boolean equalsWithSameId = a1.equals(a2);
        int firstHashCode = a1.hashCode();
        int secondHashCode = a2.hashCode();

        // Assert
        assertEquals(true, equalsWithSameId);
        assertNotEquals(null, a1);
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    @DisplayName("Inventory - equals y hashCode")
    void testInventoryEqualsAndHashCode() {
        // Arrange
        Almacen almacen = new Almacen("Test", "City", "Address", "Phone");
        Product product = new Product("Test", "SKU", "Desc", new BigDecimal("100"));
        
        Inventory i1 = new Inventory(almacen, product, 10);
        Inventory i2 = new Inventory(almacen, product, 20);

        i1.setId(5L);
        i2.setId(5L);

        // Act
        boolean equalsWithSameId = i1.equals(i2);
        boolean equalsWithDifferentType = i1.equals("string");
        int firstHashCode = i1.hashCode();
        int secondHashCode = i2.hashCode();

        // Assert
        assertEquals(true, equalsWithSameId);
        assertNotEquals(null, i1);
        assertEquals(false, equalsWithDifferentType);
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    @DisplayName("Inventory - setStock actualiza lastUpdated")
    void testInventorySetStockUpdatesTimestamp() {
        // Arrange
        Almacen almacen = new Almacen("Test", "City", "Address", "Phone");
        Product product = new Product("Test", "SKU", "Desc", new BigDecimal("100"));
        Inventory inventory = new Inventory(almacen, product, 10);
        
        // Act
        var firstUpdate = inventory.getLastUpdated();
        inventory.setStock(20);
        var secondUpdate = inventory.getLastUpdated();
        
        // Assert
        assertNotEquals(firstUpdate, secondUpdate);
        assertEquals(20, inventory.getStock());
    }

    @Test
    @DisplayName("Product - Todos los getters y setters")
    void testProductGettersAndSetters() {
        // Arrange
        Product product = new Product();

        // Act
        product.setName("Test Product");
        product.setSku("TEST-123");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("999.99"));
        
        // Assert
        assertEquals("Test Product", product.getName());
        assertEquals("TEST-123", product.getSku());
        assertEquals("Test Description", product.getDescription());
        assertEquals(new BigDecimal("999.99"), product.getPrice());
        assertNull(product.getId());
    }

    @Test
    @DisplayName("Almacen - Todos los getters y setters")
    void testAlmacenGettersAndSetters() {
        // Arrange
        Almacen almacen = new Almacen();

        // Act
        almacen.setNombre("Almacén Test");
        almacen.setCiudad("Medellín");
        almacen.setDireccion("Calle Test");
        almacen.setTelefono("1234567");
        
        // Assert
        assertEquals("Almacén Test", almacen.getNombre());
        assertEquals("Medellín", almacen.getCiudad());
        assertEquals("Calle Test", almacen.getDireccion());
        assertEquals("1234567", almacen.getTelefono());
        assertNull(almacen.getId());
    }

    @Test
    @DisplayName("Inventory - Todos los getters y setters")
    void testInventoryGettersAndSetters() {
        // Arrange
        Almacen almacen = new Almacen("Test", "City", "Address", "Phone");
        Product product = new Product("Test", "SKU", "Desc", new BigDecimal("100"));
        
        Inventory inventory = new Inventory();

        // Act
        inventory.setAlmacen(almacen);
        inventory.setProduct(product);
        inventory.setStock(25);
        
        // Assert
        assertEquals(almacen, inventory.getAlmacen());
        assertEquals(product, inventory.getProduct());
        assertEquals(25, inventory.getStock());
        assertNotNull(inventory.getLastUpdated());
    }
}