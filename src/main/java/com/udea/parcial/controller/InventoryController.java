package com.udea.parcial.controller;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventario", description = "API para gestión de inventarios de productos por almacén")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ApiVersionValidator versionValidator;

    public InventoryController(InventoryService inventoryService,
                               ApiVersionValidator versionValidator) {
        this.inventoryService = inventoryService;
        this.versionValidator = versionValidator;
    }

    @Operation(summary = "Consultar inventario por almacén",
            description = "Obtiene todos los productos y sus cantidades en stock para un almacén específico. "
                    + "Se requiere el header X-API-Version con valor 'v1'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Versión de API no soportada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Almacén no encontrado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getInventory(
            @Parameter(description = "Versión de la API (requerido: 'v1')", required = true, example = "v1")
            @RequestHeader("X-API-Version") String version,
            @Parameter(description = "ID del almacén a consultar", required = true, example = "1")
            @RequestParam Long almacenId) {

        Optional<ResponseEntity<String>> versionError = versionValidator.validate(version);
        if (versionError.isPresent()) return versionError.get();

        List<EntityModel<InventoryResponse>> responseList = inventoryService
                .getInventoryByAlmacen(almacenId)
                .stream()
                .map(dto -> toHateoasModel(dto, version, almacenId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Registrar nuevo producto en inventario",
            description = "Crea un nuevo producto y lo registra en el inventario de un almacén específico. "
                    + "Se requiere el header X-API-Version con valor 'v1'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto e inventario creados exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Versión de API no soportada o datos inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Almacén no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createInventory(
            @Parameter(description = "Versión de la API (requerido: 'v1')", required = true, example = "v1")
            @RequestHeader("X-API-Version") String version,
            @Parameter(description = "Datos del producto y cantidad inicial en stock", required = true)
            @RequestBody InventoryRequest request) {

        Optional<ResponseEntity<String>> versionError = versionValidator.validate(version);
        if (versionError.isPresent()) return versionError.get();

        InventoryResponse dto = inventoryService.createInventory(request);
        EntityModel<InventoryResponse> model = addCreateLinks(dto, version, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    // ── helpers HATEOAS ────────────────────────────────────────────────────

    private EntityModel<InventoryResponse> toHateoasModel(InventoryResponse dto,
                                                           String version,
                                                           Long almacenId) {
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(InventoryController.class)
                        .getInventory(version, almacenId))
                .withSelfRel();

        Link almacenLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(InventoryController.class)
                        .getInventory(version, dto.getAlmacenId()))
                .withRel("almacen");

        return EntityModel.of(dto, selfLink, almacenLink);
    }

    private EntityModel<InventoryResponse> addCreateLinks(InventoryResponse dto,
                                                           String version,
                                                           InventoryRequest request) {
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(InventoryController.class)
                        .createInventory(version, request))
                .withSelfRel();

        Link getAllLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(InventoryController.class)
                        .getInventory(version, dto.getAlmacenId()))
                .withRel("inventario_por_almacen");

        return EntityModel.of(dto, selfLink, getAllLink);
    }
}
