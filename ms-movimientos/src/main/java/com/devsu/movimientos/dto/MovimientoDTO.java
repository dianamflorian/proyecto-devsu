package com.devsu.movimientos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {

    private Long id;
    private String numeroCuenta;
    private LocalDateTime fecha;
    private String tipo;
    private BigDecimal valor;
    private BigDecimal saldo;
    private String descripcion;
}