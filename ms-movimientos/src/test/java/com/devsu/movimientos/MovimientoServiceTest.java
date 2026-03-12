package com.devsu.movimientos;

import com.devsu.movimientos.client.ClienteClient;
import com.devsu.movimientos.dto.CuentaDTO;
import com.devsu.movimientos.dto.MovimientoDTO;
import com.devsu.movimientos.entity.Movimiento;
import com.devsu.movimientos.exception.BadRequestException;
import com.devsu.movimientos.exception.RecursoNoEncontradoException;
import com.devsu.movimientos.exception.SaldoNoDisponibleException;
import com.devsu.movimientos.repository.MovimientoRepository;
import com.devsu.movimientos.service.MovimientoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private MovimientoService movimientoService;

    private Movimiento movimiento;
    private MovimientoDTO movimientoDTO;
    private CuentaDTO cuentaDTO;

    @BeforeEach
    void setUp() {
        movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setNumeroCuenta("478758");
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo("Deposito");
        movimiento.setValor(new BigDecimal("500.00"));
        movimiento.setSaldo(new BigDecimal("2500.00"));
        movimiento.setDescripcion("Deposito inicial");

        movimientoDTO = new MovimientoDTO(null, "478758", null, "Deposito",
                new BigDecimal("500.00"), null, "Deposito inicial");

        cuentaDTO = new CuentaDTO(1L, "478758", "Ahorro", new BigDecimal("2000.00"),
                new BigDecimal("2000.00"), true, 1L);
    }

    @Test
    void registrarMovimiento_deposito_debeActualizarSaldo() {
        when(movimientoRepository.findTopByNumeroCuentaOrderByFechaDesc("478758"))
                .thenReturn(Optional.of(movimiento));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        MovimientoDTO resultado = movimientoService.registrarMovimiento(movimientoDTO);

        assertNotNull(resultado);
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }

    @Test
    void registrarMovimiento_retiro_conSaldoSuficiente_debeActualizarSaldo() {
        Movimiento movimientoConSaldo = new Movimiento();
        movimientoConSaldo.setId(1L);
        movimientoConSaldo.setNumeroCuenta("478758");
        movimientoConSaldo.setFecha(LocalDateTime.now());
        movimientoConSaldo.setTipo("Retiro");
        movimientoConSaldo.setValor(new BigDecimal("200.00"));
        movimientoConSaldo.setSaldo(new BigDecimal("1800.00"));

        MovimientoDTO retiroDTO = new MovimientoDTO(null, "478758", null, "Retiro",
                new BigDecimal("200.00"), null, "Retiro de cajero");

        when(movimientoRepository.findTopByNumeroCuentaOrderByFechaDesc("478758"))
                .thenReturn(Optional.of(movimiento));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoConSaldo);

        MovimientoDTO resultado = movimientoService.registrarMovimiento(retiroDTO);

        assertNotNull(resultado);
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }

    @Test
    void registrarMovimiento_retiro_conSaldoInsuficiente_debeLanzarExcepcion() {
        Movimiento movimientoSaldoBajo = new Movimiento();
        movimientoSaldoBajo.setSaldo(new BigDecimal("100.00"));

        MovimientoDTO retiroDTO = new MovimientoDTO(null, "478758", null, "Retiro",
                new BigDecimal("500.00"), null, "Retiro excesivo");

        when(movimientoRepository.findTopByNumeroCuentaOrderByFechaDesc("478758"))
                .thenReturn(Optional.of(movimientoSaldoBajo));

        assertThrows(SaldoNoDisponibleException.class,
                () -> movimientoService.registrarMovimiento(retiroDTO));
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void registrarMovimiento_sinMovimientosPrevios_debeLlamarClienteService() {
        when(movimientoRepository.findTopByNumeroCuentaOrderByFechaDesc("478758"))
                .thenReturn(Optional.empty());
        when(clienteClient.obtenerCuentaPorNumero("478758")).thenReturn(cuentaDTO);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        MovimientoDTO resultado = movimientoService.registrarMovimiento(movimientoDTO);

        assertNotNull(resultado);
        verify(clienteClient, times(1)).obtenerCuentaPorNumero("478758");
    }

    @Test
    void registrarMovimiento_conValorNulo_debeLanzarExcepcion() {
        MovimientoDTO dtoInvalido = new MovimientoDTO(null, "478758", null, "Deposito",
                null, null, null);

        assertThrows(BadRequestException.class,
                () -> movimientoService.registrarMovimiento(dtoInvalido));
    }

    @Test
    void registrarMovimiento_conValorNegativo_debeLanzarExcepcion() {
        MovimientoDTO dtoInvalido = new MovimientoDTO(null, "478758", null, "Deposito",
                new BigDecimal("-100.00"), null, null);

        assertThrows(BadRequestException.class,
                () -> movimientoService.registrarMovimiento(dtoInvalido));
    }

    @Test
    void registrarMovimiento_sinNumeroCuenta_debeLanzarExcepcion() {
        MovimientoDTO dtoInvalido = new MovimientoDTO(null, null, null, "Deposito",
                new BigDecimal("100.00"), null, null);

        assertThrows(BadRequestException.class,
                () -> movimientoService.registrarMovimiento(dtoInvalido));
    }

    @Test
    void registrarMovimiento_conTipoInvalido_debeLanzarExcepcion() {
        MovimientoDTO dtoInvalido = new MovimientoDTO(null, "478758", null, "Transferencia",
                new BigDecimal("100.00"), null, null);

        when(movimientoRepository.findTopByNumeroCuentaOrderByFechaDesc("478758"))
                .thenReturn(Optional.of(movimiento));

        assertThrows(BadRequestException.class,
                () -> movimientoService.registrarMovimiento(dtoInvalido));
    }

    @Test
    void obtenerMovimientos_debeRetornarLista() {
        when(movimientoRepository.findByNumeroCuenta("478758")).thenReturn(List.of(movimiento));

        List<MovimientoDTO> resultado = movimientoService.obtenerMovimientos("478758");

        assertEquals(1, resultado.size());
        assertEquals("478758", resultado.get(0).getNumeroCuenta());
    }

    @Test
    void obtenerMovimiento_conIdExistente_debeRetornarMovimiento() {
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));

        MovimientoDTO resultado = movimientoService.obtenerMovimiento(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerMovimiento_conIdInexistente_debeLanzarExcepcion() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> movimientoService.obtenerMovimiento(99L));
    }

    @Test
    void eliminarMovimiento_debeInvocarDeleteById() {
        doNothing().when(movimientoRepository).deleteById(1L);

        movimientoService.eliminarMovimiento(1L);

        verify(movimientoRepository, times(1)).deleteById(1L);
    }
}
