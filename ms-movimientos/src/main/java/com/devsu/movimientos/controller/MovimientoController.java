package com.devsu.movimientos.controller;

import com.devsu.movimientos.dto.MovimientoDTO;
import com.devsu.movimientos.dto.ReporteDTO;
import com.devsu.movimientos.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrarMovimiento(@RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO nuevoMovimiento = movimientoService.registrarMovimiento(movimientoDTO);
        return new ResponseEntity<>(nuevoMovimiento, HttpStatus.CREATED);
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientos(@PathVariable String numeroCuenta) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerMovimientos(numeroCuenta);
        return new ResponseEntity<>(movimientos, HttpStatus.OK);
    }

    @GetMapping("/reporte/{numeroCuenta}")
    public ResponseEntity<List<ReporteDTO>> generarReporte(
            @PathVariable String numeroCuenta,
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        List<ReporteDTO> reporte = movimientoService.generarReporte(numeroCuenta, fechaInicio, fechaFin);
        return new ResponseEntity<>(reporte, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerMovimiento(@PathVariable Long id) {
        MovimientoDTO movimiento = movimientoService.obtenerMovimiento(id);
        return new ResponseEntity<>(movimiento, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}