package com.devsu.clientes.service;

import com.devsu.clientes.dto.ClienteDTO;
import com.devsu.clientes.entity.Cliente;
import com.devsu.clientes.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setGenero(clienteDTO.getGenero());
        cliente.setEdad(clienteDTO.getEdad());
        cliente.setIdentificacion(clienteDTO.getIdentificacion());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setContrasena(clienteDTO.getContrasena()); // <-- agregar
        cliente.setEstado(clienteDTO.getEstado() != null ? clienteDTO.getEstado() : true); // opcional

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertToDTO(clienteGuardado);
    }

    public ClienteDTO obtenerCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return convertToDTO(cliente);
    }

    public List<ClienteDTO> obtenerTodosClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(clienteDTO.getNombre());
        cliente.setGenero(clienteDTO.getGenero());
        cliente.setEdad(clienteDTO.getEdad());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setEstado(clienteDTO.getEstado());

        // Si quieres permitir cambiarla solo cuando venga:
        if (clienteDTO.getContrasena() != null) {
            cliente.setContrasena(clienteDTO.getContrasena());
        }

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertToDTO(clienteActualizado);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getContrasena(), // <-- agregar (si NO quieres devolverla, dime y lo cambiamos)
                cliente.getEstado()
        );
    }
}