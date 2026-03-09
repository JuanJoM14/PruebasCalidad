[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=alejandromora05_calidad-26-1&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=alejandromora05_calidad-26-1) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=alejandromora05_calidad-26-1&metric=bugs)](https://sonarcloud.io/summary/new_code?id=alejandromora05_calidad-26-1) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=alejandromora05_calidad-26-1&metric=coverage)](https://sonarcloud.io/summary/new_code?id=alejandromora05_calidad-26-1) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=alejandromora05_calidad-26-1&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=alejandromora05_calidad-26-1) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=alejandromora05_calidad-26-1&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=alejandromora05_calidad-26-1) [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=alejandromora05_calidad-26-1&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=alejandromora05_calidad-26-1)
# API de Gestión de Inventarios - Parcial UdeA

## Descripción
API RESTful para la gestión de inventarios de productos en diferentes almacenes. Desarrollada con Spring Boot y PostgreSQL.

## Características Implementadas

**1. Método GET** - Consultar inventario por almacén  
**2. Método POST** - Registrar productos con stock inicial  
**3. Base de Datos** - PostgreSQL (relacional)  
**4. Documentación** - Swagger/OpenAPI  
**5. Formato JSON** - Todas las respuestas en JSON  
**6. Versionamiento** - Custom Request Header `X-API-Version`  
**7. HATEOAS** - Enlaces hipermedia en respuestas  
**8. Pruebas** - Documentadas con ejemplos  
**9. GitHub** - Repositorio versionado  
**10. Docker** - Contenedores con Docker Compose  

## Requisitos Previos

- Docker y Docker Compose instalados
- Java 17 (solo para desarrollo local)
- Maven 3.8+ (solo para desarrollo local)

## Instalación y Ejecución

### Opción 1: Con Docker (Recomendado)

1. Clonar el repositorio:
```bash
git clone https://github.com/AlejandroMora05/parcial.git
cd parcial