package com.devsu.clientes;

import com.devsu.clientes.dto.ClienteDTO;
import com.devsu.clientes.entity.Cliente;
import com.devsu.clientes.exception.RecursoDuplicadoException;
import com.devsu.clientes.exception.RecursoNoEncontradoException;
import com.devsu.clientes.repository.ClienteRepository;
import com.devsu.clientes.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");
        cliente.setGenero("M");
        cliente.setEdad(35);
        cliente.setIdentificacion("1234567890");
        cliente.setDireccion("Otavalo sn y principal");
        cliente.setTelefono("098254785");
        cliente.setContrasena("1234");
        cliente.setEstado(true);

        clienteDTO = new ClienteDTO(null, "Jose Lema", "M", 35, "1234567890",
                "Otavalo sn y principal", "098254785", "1234", true);
    }

    @Test
    void crearCliente_debeRetornarClienteCreado() {
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteDTO resultado = clienteService.crearCliente(clienteDTO);

        assertNotNull(resultado);
        assertEquals("Jose Lema", resultado.getNombre());
        assertEquals("1234567890", resultado.getIdentificacion());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void crearCliente_conIdentificacionDuplicada_debeLanzarExcepcion() {
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> clienteService.crearCliente(clienteDTO));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void obtenerCliente_conIdExistente_debeRetornarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteDTO resultado = clienteService.obtenerCliente(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Jose Lema", resultado.getNombre());
    }

    @Test
    void obtenerCliente_conIdInexistente_debeLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> clienteService.obtenerCliente(99L));
    }

    @Test
    void obtenerTodosClientes_debeRetornarLista() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<ClienteDTO> resultado = clienteService.obtenerTodosClientes();

        assertEquals(1, resultado.size());
        assertEquals("Jose Lema", resultado.get(0).getNombre());
    }

    @Test
    void actualizarCliente_conIdExistente_debeRetornarClienteActualizado() {
        ClienteDTO actualizacion = new ClienteDTO(null, "Jose Actualizado", "M", 36,
                "1234567890", "Nueva Direccion", "099999999", null, true);

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(1L);
        clienteActualizado.setNombre("Jose Actualizado");
        clienteActualizado.setGenero("M");
        clienteActualizado.setEdad(36);
        clienteActualizado.setIdentificacion("1234567890");
        clienteActualizado.setDireccion("Nueva Direccion");
        clienteActualizado.setTelefono("099999999");
        clienteActualizado.setContrasena("1234");
        clienteActualizado.setEstado(true);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActualizado);

        ClienteDTO resultado = clienteService.actualizarCliente(1L, actualizacion);

        assertNotNull(resultado);
        assertEquals("Jose Actualizado", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void actualizarCliente_conIdInexistente_debeLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> clienteService.actualizarCliente(99L, clienteDTO));
    }

    @Test
    void eliminarCliente_debeInvocarDeleteById() {
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.eliminarCliente(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }
}
