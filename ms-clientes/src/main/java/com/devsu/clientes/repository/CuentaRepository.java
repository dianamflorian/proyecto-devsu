package com.devsu.clientes.repository;

import com.devsu.clientes.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Cuenta findByNumeroCuenta(String numeroCuenta);
}