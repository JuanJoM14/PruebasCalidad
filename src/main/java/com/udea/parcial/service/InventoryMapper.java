package com.udea.parcial.service;

import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inv) {
        InventoryResponse dto = new InventoryResponse();
        dto.setInventoryId(inv.getId());
        dto.setAlmacenId(inv.getAlmacen().getId());
        dto.setAlmacenNombre(inv.getAlmacen().getNombre());
        dto.setProductId(inv.getProduct().getId());
        dto.setProductName(inv.getProduct().getName());
        dto.setProductDescription(inv.getProduct().getDescription());
        dto.setSku(inv.getProduct().getSku());
        dto.setPrice(inv.getProduct().getPrice());
        dto.setStock(inv.getStock());
        dto.setLastUpdated(inv.getLastUpdated());
        return dto;
    }
}
