package com.udea.parcial.karate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Clase de utilidades para usar en pruebas de Karate
 * Puede ser invocada desde archivos .feature usando:
 * * def utils = Java.type('com.udea.parcial.karate.TestUtils')
 */
public class TestUtils {

    /**
     * Genera un UUID aleatorio
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Genera un SKU único basado en timestamp
     */
    public static String generateSKU() {
        return "SKU-" + System.currentTimeMillis();
    }

    /**
     * Obtiene el timestamp actual en formato ISO
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * Genera un nombre de producto aleatorio
     */
    public static String generateProductName() {
        return "Producto_Test_" + System.currentTimeMillis();
    }

    /**
     * Valida formato de SKU
     */
    public static boolean isValidSKU(String sku) {
        return sku != null && sku.matches("^[A-Z0-9-]+$");
    }

    /**
     * Genera un precio aleatorio entre min y max
     */
    public static double generateRandomPrice(double min, double max) {
        return min + (Math.random() * (max - min));
    }

    /**
     * Genera un stock aleatorio entre min y max
     */
    public static int generateRandomStock(int min, int max) {
        return min + new java.util.Random().nextInt((max - min) + 1);
    }
}
