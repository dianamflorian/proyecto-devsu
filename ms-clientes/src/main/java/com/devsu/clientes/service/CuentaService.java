package com.devsu.clientes.service;

import com.devsu.clientes.dto.CuentaDTO;
import com.devsu.clientes.entity.Cuenta;
import com.devsu.clientes.entity.Cliente;
import com.devsu.clientes.repository.CuentaRepository;
import com.devsu.clientes.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        Cliente cliente = clienteRepository.findById(cuentaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setTipo(cuentaDTO.getTipo());
        cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        cuenta.setSaldo(cuentaDTO.getSaldoInicial());
        cuenta.setEstado(true);
        cuenta.setFechaCreacion(LocalDateTime.now());
        cuenta.setCliente(cliente);

        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return convertToDTO(cuentaGuardada);
    }

    public CuentaDTO obtenerCuenta(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return convertToDTO(cuenta);
    }

    public List<CuentaDTO> obtenerTodasCuentas() {
        return cuentaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setTipo(cuentaDTO.getTipo());
        cuenta.setSaldo(cuentaDTO.getSaldo());
        cuenta.setEstado(cuentaDTO.getEstado());

        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return convertToDTO(cuentaActualizada);
    }

    public void eliminarCuenta(Long id) {
        cuentaRepository.deleteById(id);
    }

    private CuentaDTO convertToDTO(Cuenta cuenta) {
        return new CuentaDTO(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipo(),
                cuenta.getSaldoInicial(),
                cuenta.getSaldo(),
                cuenta.getEstado(),
                cuenta.getCliente().getId()
        );
    }
}