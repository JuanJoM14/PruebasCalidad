package com.udea.parcial.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "almacenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 200)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    public Almacen(String nombre, String ciudad, String direccion, String telefono) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.telefono = telefono;
    }
}
