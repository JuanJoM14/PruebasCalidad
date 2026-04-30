package com.udea.parcial.karate;

import com.intuit.karate.junit5.Karate;
import com.udea.parcial.ParcialApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Runner para ejecutar todas las pruebas de integración de Karate
 * Ejecuta todos los archivos .feature en el classpath
 */
@SpringBootTest(
    classes = ParcialApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class KarateTestRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }
}
