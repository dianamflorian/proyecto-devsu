package com.devsu.clientes;

import com.devsu.clientes.dto.CuentaDTO;
import com.devsu.clientes.entity.Cliente;
import com.devsu.clientes.entity.Cuenta;
import com.devsu.clientes.exception.BadRequestException;
import com.devsu.clientes.exception.RecursoNoEncontradoException;
import com.devsu.clientes.repository.ClienteRepository;
import com.devsu.clientes.repository.CuentaRepository;
import com.devsu.clientes.service.CuentaService;
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
public class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private CuentaService cuentaService;

    private Cliente cliente;
    private Cuenta cuenta;
    private CuentaDTO cuentaDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");
        cliente.setIdentificacion("1234567890");
        cliente.setContrasena("1234");
        cliente.setEstado(true);

        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipo("Ahorro");
        cuenta.setSaldoInicial(new BigDecimal("2000.00"));
        cuenta.setSaldo(new BigDecimal("2000.00"));
        cuenta.setEstado(true);
        cuenta.setFechaCreacion(LocalDateTime.now());
        cuenta.setCliente(cliente);

        cuentaDTO = new CuentaDTO(null, "478758", "Ahorro", new BigDecimal("2000.00"),
                new BigDecimal("2000.00"), true, 1L);
    }

    @Test
    void crearCuenta_debeRetornarCuentaCreada() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        CuentaDTO resultado = cuentaService.crearCuenta(cuentaDTO);

        assertNotNull(resultado);
        assertEquals("478758", resultado.getNumeroCuenta());
        assertEquals("Ahorro", resultado.getTipo());
        assertEquals(new BigDecimal("2000.00"), resultado.getSaldoInicial());
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void crearCuenta_conClienteInexistente_debeLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        CuentaDTO dtoConClienteInexistente = new CuentaDTO(null, "478758", "Ahorro",
                new BigDecimal("2000.00"), new BigDecimal("2000.00"), true, 99L);

        assertThrows(RecursoNoEncontradoException.class,
                () -> cuentaService.crearCuenta(dtoConClienteInexistente));
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void obtenerCuenta_conIdExistente_debeRetornarCuenta() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        CuentaDTO resultado = cuentaService.obtenerCuenta(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("478758", resultado.getNumeroCuenta());
    }

    @Test
    void obtenerCuenta_conIdInexistente_debeLanzarExcepcion() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> cuentaService.obtenerCuenta(99L));
    }

    @Test
    void obtenerCuentaPorNumero_conNumeroExistente_debeRetornarCuenta() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));

        CuentaDTO resultado = cuentaService.obtenerCuentaPorNumero("478758");

        assertNotNull(resultado);
        assertEquals("478758", resultado.getNumeroCuenta());
    }

    @Test
    void obtenerCuentaPorNumero_conNumeroInexistente_debeLanzarExcepcion() {
        when(cuentaRepository.findByNumeroCuenta("999999")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> cuentaService.obtenerCuentaPorNumero("999999"));
    }

    @Test
    void obtenerCuentaPorNumero_conNumeroNulo_debeLanzarExcepcion() {
        assertThrows(BadRequestException.class,
                () -> cuentaService.obtenerCuentaPorNumero(null));
    }

    @Test
    void obtenerTodasCuentas_debeRetornarLista() {
        when(cuentaRepository.findAll()).thenReturn(List.of(cuenta));

        List<CuentaDTO> resultado = cuentaService.obtenerTodasCuentas();

        assertEquals(1, resultado.size());
        assertEquals("478758", resultado.get(0).getNumeroCuenta());
    }

    @Test
    void actualizarCuenta_conIdExistente_debeRetornarCuentaActualizada() {
        CuentaDTO actualizacion = new CuentaDTO(null, "478758", "Corriente",
                new BigDecimal("2000.00"), new BigDecimal("1500.00"), true, 1L);

        Cuenta cuentaActualizada = new Cuenta();
        cuentaActualizada.setId(1L);
        cuentaActualizada.setNumeroCuenta("478758");
        cuentaActualizada.setTipo("Corriente");
        cuentaActualizada.setSaldoInicial(new BigDecimal("2000.00"));
        cuentaActualizada.setSaldo(new BigDecimal("1500.00"));
        cuentaActualizada.setEstado(true);
        cuentaActualizada.setCliente(cliente);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaActualizada);

        CuentaDTO resultado = cuentaService.actualizarCuenta(1L, actualizacion);

        assertNotNull(resultado);
        assertEquals("Corriente", resultado.getTipo());
        assertEquals(new BigDecimal("1500.00"), resultado.getSaldo());
    }

    @Test
    void actualizarCuenta_conIdInexistente_debeLanzarExcepcion() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> cuentaService.actualizarCuenta(99L, cuentaDTO));
    }

    @Test
    void eliminarCuenta_debeInvocarDeleteById() {
        doNothing().when(cuentaRepository).deleteById(1L);

        cuentaService.eliminarCuenta(1L);

        verify(cuentaRepository, times(1)).deleteById(1L);
    }
}
