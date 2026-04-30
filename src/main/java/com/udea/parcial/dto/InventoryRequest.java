package com.udea.parcial.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class InventoryRequest {
    private Long almacenId;
    private String productName;
    private String productDescription;
    private String sku;
    private BigDecimal price;
    private int stock;
}
