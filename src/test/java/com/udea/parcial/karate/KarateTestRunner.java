package com.udea.parcial.karate;

import com.intuit.karate.junit5.Karate;

/**
 * Runner para ejecutar todas las pruebas de integración de Karate
 * Ejecuta todos los archivos .feature en el classpath
 */
public class KarateTestRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }
}
