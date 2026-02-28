package com.devsu.movimientos.service;

import com.devsu.movimientos.client.ClienteClient;
import com.devsu.movimientos.dto.MovimientoDTO;
import com.devsu.movimientos.dto.ReporteDTO;
import com.devsu.movimientos.entity.Movimiento;
import com.devsu.movimientos.repository.MovimientoRepository;
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
        if (movimientoDTO.getValor() == null || movimientoDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El valor del movimiento debe ser positivo");
        }
        if (movimientoDTO.getNumeroCuenta() == null || movimientoDTO.getNumeroCuenta().isBlank()) {
            throw new RuntimeException("El numeroCuenta es obligatorio");
        }
        if (movimientoDTO.getTipo() == null || movimientoDTO.getTipo().isBlank()) {
            throw new RuntimeException("El tipo es obligatorio");
        }

        BigDecimal saldoAnterior = movimientoRepository
                .findTopByNumeroCuentaOrderByFechaDesc(movimientoDTO.getNumeroCuenta())
                .map(Movimiento::getSaldo)
                .orElse(BigDecimal.ZERO);

        BigDecimal saldoNuevo;
        String tipo = movimientoDTO.getTipo().trim();

        if (tipo.equalsIgnoreCase("Deposito")) {
            saldoNuevo = saldoAnterior.add(movimientoDTO.getValor());
        } else if (tipo.equalsIgnoreCase("Retiro")) {
            saldoNuevo = saldoAnterior.subtract(movimientoDTO.getValor());
            if (saldoNuevo.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Saldo no disponible");
            }
        } else {
            throw new RuntimeException("Tipo de movimiento inválido (use Deposito o Retiro)");
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setNumeroCuenta(movimientoDTO.getNumeroCuenta());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo(movimientoDTO.getTipo());
        movimiento.setValor(movimientoDTO.getValor());
        movimiento.setSaldo(saldoNuevo);
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
        String clienteNombre = "";
        BigDecimal saldoInicial = BigDecimal.ZERO;
        Boolean estado = true;

        try {
            var cuenta = clienteClient.obtenerCuentaPorNumero(movimiento.getNumeroCuenta());

            if (cuenta != null) {
                saldoInicial = cuenta.getSaldoInicial() != null ? cuenta.getSaldoInicial() : BigDecimal.ZERO;
                estado = cuenta.getEstado() != null ? cuenta.getEstado() : true;

                if (cuenta.getClienteId() != null) {
                    var cliente = clienteClient.obtenerCliente(cuenta.getClienteId());
                    if (cliente != null && cliente.getNombre() != null) {
                        clienteNombre = cliente.getNombre();
                    }
                }
            }
        } catch (Exception e) {
            // opcional: loggear para ver por qué no se llena cliente
        }

        return new ReporteDTO(
                movimiento.getFecha(),
                clienteNombre,
                movimiento.getNumeroCuenta(),
                movimiento.getTipo(),
                saldoInicial,
                estado,
                movimiento.getValor(),
                movimiento.getSaldo()
        );
    }
}