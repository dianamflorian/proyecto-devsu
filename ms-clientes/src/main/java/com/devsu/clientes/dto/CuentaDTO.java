package com.devsu.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    private Long id;
    private String numeroCuenta;
    private String tipo;
    private BigDecimal saldoInicial;
    private BigDecimal saldo;
    private Boolean estado;
    private Long clienteId;
}