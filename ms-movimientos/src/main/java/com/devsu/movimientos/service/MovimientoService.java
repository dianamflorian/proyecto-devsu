package com.devsu.movimientos.service;

import com.devsu.movimientos.dto.MovimientoDTO;
import com.devsu.movimientos.dto.ReporteDTO;
import com.devsu.movimientos.entity.Movimiento;
import com.devsu.movimientos.repository.MovimientoRepository;
import com.devsu.movimientos.client.ClienteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ClienteClient clienteClient;

    public MovimientoDTO registrarMovimiento(MovimientoDTO movimientoDTO) {
        // Validación: verificar que el valor sea positivo
        if (movimientoDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El valor del movimiento debe ser positivo");
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setNumeroCuenta(movimientoDTO.getNumeroCuenta());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo(movimientoDTO.getTipo());
        movimiento.setValor(movimientoDTO.getValor());
        movimiento.setSaldo(movimientoDTO.getSaldo());
        movimiento.setDescripcion(movimientoDTO.getDescripcion());

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        return convertToDTO(movimientoGuardado);
    }

    public List<MovimientoDTO> obtenerMovimientos(String numeroCuenta) {
        return movimientoRepository.findByNumeroCuenta(numeroCuenta)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReporteDTO> generarReporte(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Movimiento> movimientos = movimientoRepository
                .findByNumeroCuentaAndFechaBetween(numeroCuenta, fechaInicio, fechaFin);

        return movimientos.stream()
                .map(this::convertToReporteDTO)
                .collect(Collectors.toList());
    }

    public MovimientoDTO obtenerMovimiento(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
        return convertToDTO(movimiento);
    }

    public void eliminarMovimiento(Long id) {
        movimientoRepository.deleteById(id);
    }

    private MovimientoDTO convertToDTO(Movimiento movimiento) {
        return new MovimientoDTO(
                movimiento.getId(),
                movimiento.getNumeroCuenta(),
                movimiento.getFecha(),
                movimiento.getTipo(),
                movimiento.getValor(),
                movimiento.getSaldo(),
                movimiento.getDescripcion()
        );
    }

    private ReporteDTO convertToReporteDTO(Movimiento movimiento) {
        return new ReporteDTO(
                movimiento.getFecha(),
                "", // Se llenará después
                movimiento.getNumeroCuenta(),
                movimiento.getTipo(),
                BigDecimal.ZERO, // Se llenará después
                "Activo",
                movimiento.getValor(),
                movimiento.getSaldo()
        );
    }
}