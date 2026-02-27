package com.devsu.clientes.controller;

import com.devsu.clientes.dto.CuentaDTO;
import com.devsu.clientes.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(@RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO nuevaCuenta = cuentaService.crearCuenta(cuentaDTO);
        return new ResponseEntity<>(nuevaCuenta, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaDTO> obtenerCuenta(@PathVariable Long id) {
        CuentaDTO cuenta = cuentaService.obtenerCuenta(id);
        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> obtenerTodasCuentas() {
        List<CuentaDTO> cuentas = cuentaService.obtenerTodasCuentas();
        return new ResponseEntity<>(cuentas, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaDTO> actualizarCuenta(@PathVariable Long id, @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(id, cuentaDTO);
        return new ResponseEntity<>(cuentaActualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        cuentaService.eliminarCuenta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}