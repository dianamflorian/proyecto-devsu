package com.devsu.movimientos.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO { //se creo cuentaDTO no existia
    private Long id;
    private String numeroCuenta;
    private String tipo;
    private BigDecimal saldoInicial;
    private BigDecimal saldo;
    private Boolean estado;
    private Long clienteId;
}