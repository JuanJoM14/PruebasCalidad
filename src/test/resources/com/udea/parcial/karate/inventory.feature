Feature: API de Inventario - Pruebas de Integración
  Como usuario del sistema
  Quiero consultar y registrar inventarios
  Para gestionar el stock de productos por almacén

  Background:
    * url baseUrl
    * def apiVersion = 'v1'


  Scenario: Consultar inventario de un almacén existente
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And param almacenId = 1
    When method GET
    Then status 200
    And match response == '#array'
    And match each response contains { inventoryId: '#number', almacenId: 1, productId: '#number' }

  Scenario: Consultar inventario sin header de versión
    Given path '/inventory'
    And param almacenId = 1
    When method GET
    Then status 400

  Scenario: Consultar inventario con versión incorrecta
    Given path '/inventory'
    And header X-API-Version = 'v2'
    And param almacenId = 1
    When method GET
    Then status 400

  Scenario: Consultar inventario de almacén sin productos
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And param almacenId = 999
    When method GET
    Then status 200
    And match response == '#array'
    And match response == []

  Scenario: Registrar nuevo producto en inventario
    * def randomNum = Math.floor(Math.random() * 1000000)
    * def uniqueSKU = 'DELL-XPS-' + randomNum
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And header Content-Type = 'application/json'
    And request
      """
      {
        "almacenId": 1,
        "productName": "Laptop Dell XPS",
        "productDescription": "Laptop de alto rendimiento",
        "sku": "#(uniqueSKU)",
        "price": 2000000.00,
        "stock": 10
      }
      """
    When method POST
    Then status 201
    And match response.inventoryId == '#number'
    And match response.productName == 'Laptop Dell XPS'
    And match response.price == 2000000.00

  Scenario: Registrar producto con almacén inexistente
    * def randomNum = Math.floor(Math.random() * 1000000)
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And header Content-Type = 'application/json'
    And request
      """
      {
        "almacenId": 999999,
        "productName": "Producto Test",
        "productDescription": "Descripción test",
        "sku": "#('TEST-' + randomNum)",
        "price": 100000.00,
        "stock": 5
      }
      """
    When method POST
    Then status 404
  Scenario: Registrar producto sin versión de API
    * def randomNum = Math.floor(Math.random() * 1000000)
    Given path '/inventory'
    And header Content-Type = 'application/json'
    And request
      """
      {
        "almacenId": 1,
        "productName": "Producto Test",
        "productDescription": "Descripción test",
        "sku": "#('TEST-' + randomNum)",
        "price": 50000.00,
        "stock": 3
      }
      """
    When method POST
    Then status 400
  Scenario: Validar estructura completa de respuesta HATEOAS al consultar inventario
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And param almacenId = 1
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].inventoryId == '#number'
    And match response[0].productId == '#number'

  Scenario: Crear producto y verificar que aparece en el inventario del almacén
    # Primero creamos el producto con SKU único
    * def randomNum = Math.floor(Math.random() * 1000000)
    * def uniqueSKU = 'KARATE-TEST-' + randomNum
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And header Content-Type = 'application/json'
    And request
      """
      {
        "almacenId": 1,
        "productName": "Producto Prueba Karate",
        "productDescription": "Producto automatizado",
        "sku": "#(uniqueSKU)",
        "price": 350000.00,
        "stock": 25
      }
      """
    When method POST
    Then status 201
    * def productId = response.productId
    * def inventoryId = response.inventoryId

    # Luego verificamos que aparece en la consulta del inventario
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And param almacenId = 1
    When method GET
    Then status 200
    And match response[*].productId contains productId
    And match response[*].sku contains uniqueSKU

  Scenario Outline: Validar tipos de datos en la creación de inventario
    * def randomNum = Math.floor(Math.random() * 1000000)
    * def uniqueSKU = '<sku>-' + randomNum
    Given path '/inventory'
    And header X-API-Version = apiVersion
    And header Content-Type = 'application/json'
    And request
      """
      {
        "almacenId": <almacenId>,
        "productName": "<productName>",
        "productDescription": "<description>",
        "sku": "#(uniqueSKU)",
        "price": <price>,
        "stock": <stock>
      }
      """
    When method POST
    Then status <expectedStatus>

    Examples:
      | almacenId | productName       | description    | sku         | price    | stock | expectedStatus |
      | 1         | Teclado Mecánico  | Gaming RGB     | TEC-001     | 450000   | 15    | 201            |
      | 1         | Monitor 27 inch   | Full HD IPS    | MON-002     | 850000   | 8     | 201            |
      | 1         | Webcam HD         | 1080p 60fps    | CAM-003     | 180000   | 30    | 201            |
