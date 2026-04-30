package com.udea.parcial.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    @DisplayName("Product - equals y hashCode")
    void testProductEqualsAndHashCode() {
        Product p1 = new Product("Laptop", "SKU-001", "Gaming", new BigDecimal("3000000"));
        Product p2 = new Product("Mouse", "SKU-002", "Wireless", new BigDecimal("50000"));
        
        assertEquals(p1, p1);
        assertEquals(p1, p2);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "string");
        
        assertEquals(p1.hashCode(), p1.hashCode());
    }

    @Test
    @DisplayName("Almacen - equals y hashCode")
    void testAlmacenEqualsAndHashCode() {
        Almacen a1 = new Almacen("Almacén 1", "Medellín", "Calle 1", "3001234567");
        Almacen a2 = new Almacen("Almacén 1", "Medellín", "Calle 1", "3001234567");
        
        assertEquals(a1, a1);
        assertEquals(a1, a2);
        assertNotEquals(a1, null);
        
        assertEquals(a1.hashCode(), a1.hashCode());
    }

    @Test
    @DisplayName("Inventory - equals y hashCode")
    void testInventoryEqualsAndHashCode() {
        Almacen almacen = new Almacen("Test", "City", "Address", "Phone");
        Product product = new Product("Test", "SKU", "Desc", new BigDecimal("100"));
        
        Inventory i1 = new Inventory(almacen, product, 10);
        //Inventory i2 = new Inventory(almacen, product, 20);
        
        assertEquals(i1, i1);
        assertNotEquals(i1, null);
        
        assertEquals(i1.hashCode(), i1.hashCode());
    }

    @Test
    @DisplayName("Inventory - setStock actualiza lastUpdated")
    void testInventorySetStockUpdatesTimestamp() throws InterruptedException {
        Almacen almacen = new Almacen("Test", "City", "Address", "Phone");
        Product product = new Product("Test", "SKU", "Desc", new BigDecimal("100"));
        Inventory inventory = new Inventory(almacen, product, 10);
        
        var firstUpdate = inventory.getLastUpdated();
        Thread.sleep(10);
        inventory.setStock(20);
        var secondUpdate = inventory.getLastUpdated();
        
        assertNotEquals(firstUpdate, secondUpdate);
        assertEquals(20, inventory.getStock());
    }

    @Test
    @DisplayName("Product - Todos los getters y setters")
    void testProductGettersAndSetters() {
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("TEST-123");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("999.99"));
        
        assertEquals("Test Product", product.getName());
        assertEquals("TEST-123", product.getSku());
        assertEquals("Test Description", product.getDescription());
        assertEquals(new BigDecimal("999.99"), product.getPrice());
        assertNull(product.getId());
    }

    @Test
    @DisplayName("Almacen - Todos los getters y setters")
    void testAlmacenGettersAndSetters() {
        Almacen almacen = new Almacen();
        almacen.setNombre("Almacén Test");
        almacen.setCiudad("Medellín");
        almacen.setDireccion("Calle Test");
        almacen.setTelefono("1234567");
        
        assertEquals("Almacén Test", almacen.getNombre());
        assertEquals("Medellín", almacen.getCiudad());
        assertEquals("Calle Test", almacen.getDireccion());
        assertEquals("1234567", almacen.getTelefono());
        assertNull(almacen.getId());
    }

    @Test
    @DisplayName("Inventory - Todos los getters y setters")
    void testInventoryGettersAndSetters() {
        Almacen almacen = new Almacen("Test", "City", "Address", "Phone");
        Product product = new Product("Test", "SKU", "Desc", new BigDecimal("100"));
        
        Inventory inventory = new Inventory();
        inventory.setAlmacen(almacen);
        inventory.setProduct(product);
        inventory.setStock(25);
        
        assertEquals(almacen, inventory.getAlmacen());
        assertEquals(product, inventory.getProduct());
        assertEquals(25, inventory.getStock());
        assertNotNull(inventory.getLastUpdated());
    }
}