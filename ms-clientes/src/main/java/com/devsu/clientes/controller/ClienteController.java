package com.devsu.clientes.controller;

import com.devsu.clientes.dto.ClienteDTO;
import com.devsu.clientes.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable Long id) {
        try {
            ClienteDTO cliente = clienteService.obtenerCliente(id);
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodosClientes() {
        List<ClienteDTO> clientes = clienteService.obtenerTodosClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(id, clienteDTO);
        return new ResponseEntity<>(clienteActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}