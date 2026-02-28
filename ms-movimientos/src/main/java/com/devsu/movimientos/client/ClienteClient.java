package com.devsu.movimientos.client;

import com.devsu.movimientos.dto.ClienteDTO;
import com.devsu.movimientos.dto.CuentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-clientes", url = "${clientes.service.url}")
public interface ClienteClient {

    @GetMapping("/api/clientes/{id}")
    ClienteDTO obtenerCliente(@PathVariable("id") Long id);

    @GetMapping("/api/cuentas/numero/{numeroCuenta}")
    CuentaDTO obtenerCuentaPorNumero(@PathVariable("numeroCuenta") String numeroCuenta);
}