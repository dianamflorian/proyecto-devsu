package com.devsu.clientes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // Indica que esta clase es base para herencia y no se generará tabla
public abstract class Persona {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "genero")
    private String genero;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "identificacion", nullable = false, unique = true)
    private String identificacion;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;
}