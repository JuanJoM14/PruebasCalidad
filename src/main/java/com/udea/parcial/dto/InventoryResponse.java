package com.udea.parcial.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryResponse {
    private Long inventoryId;
    private Long almacenId;
    private String almacenNombre;
    private Long productId;
    private String productName;
    private String productDescription;
    private String sku;
    private BigDecimal price;
    private int stock;
    private LocalDateTime lastUpdated;
}