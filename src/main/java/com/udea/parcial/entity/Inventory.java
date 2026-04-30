package com.udea.parcial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventarios")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int stock;

    private LocalDateTime lastUpdated;

    public Inventory(Almacen almacen, Product product, int stock) {
        this.almacen = almacen;
        this.product = product;
        this.stock = stock;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setStock(int stock) {
        this.stock = stock;
        this.lastUpdated = LocalDateTime.now();
    }
}
