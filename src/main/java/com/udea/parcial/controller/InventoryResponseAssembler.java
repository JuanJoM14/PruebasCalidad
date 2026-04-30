package com.udea.parcial.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;

@Component
public class InventoryResponseAssembler {

    public EntityModel<InventoryResponse> toInventoryModel(InventoryResponse dto,
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

    public EntityModel<InventoryResponse> toCreatedInventoryModel(InventoryResponse dto,
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