package com.devsu.movimientos.repository;

import com.devsu.movimientos.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByNumeroCuenta(String numeroCuenta);
    List<Movimiento> findByNumeroCuentaAndFechaBetween(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Optional<Movimiento> findTopByNumeroCuentaOrderByFechaDesc(String numeroCuenta);
}


