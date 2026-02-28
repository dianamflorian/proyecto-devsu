package com.devsu.movimientos.exception;

public class SaldoNoDisponibleException extends RuntimeException {
    public SaldoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}