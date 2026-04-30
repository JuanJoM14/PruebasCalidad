package com.udea.parcial.karate;

import com.intuit.karate.junit5.Karate;


public class KarateTestRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }

    @Karate.Test
    Karate testConsultarInventarioExistente() {
        return Karate.run()
                .scenarioName("Consultar inventario de un almacén existente")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testConsultarSinHeader() {
        return Karate.run()
                .scenarioName("Consultar inventario sin header de versión")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testConsultarVersionIncorrecta() {
        return Karate.run()
                .scenarioName("Consultar inventario con versión incorrecta")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testConsultarAlmacenVacio() {
        return Karate.run()
                .scenarioName("Consultar inventario de almacén sin productos")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testRegistrarNuevoProducto() {
        return Karate.run()
                .scenarioName("Registrar nuevo producto en inventario")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testRegistrarAlmacenInexistente() {
        return Karate.run()
                .scenarioName("Registrar producto con almacén inexistente")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testRegistrarSinVersion() {
        return Karate.run()
                .scenarioName("Registrar producto sin versión de API")
                .relativeTo(getClass());
    }


    @Karate.Test
    Karate testCrearYVerificar() {
        return Karate.run()
                .scenarioName("Crear producto y verificar que aparece en el inventario del almacén")
                .relativeTo(getClass());
    }

}
