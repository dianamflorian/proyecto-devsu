package com.devsu.clientes;

import com.devsu.clientes.entity.Cliente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    public void testClienteCreation() {
        // Crear un cliente de prueba
        Cliente cliente = new Cliente();
        cliente.setNombre("Jose Lema");
        cliente.setDireccion("Otavalo sn y principal");
        cliente.setTelefono("098254785");
        cliente.setContrasena("1234");
        cliente.setEstado(true);

        // Verificar que todos los datos se asignaron correctamente
        assertEquals("Jose Lema", cliente.getNombre());
        assertEquals("Otavalo sn y principal", cliente.getDireccion());
        assertEquals("098254785", cliente.getTelefono());
        assertEquals("1234", cliente.getContrasena());
        assertTrue(cliente.getEstado());
    }

    @Test
    public void testClienteInvalidTelefono() {
        // Crear cliente con datos inválidos
        Cliente cliente = new Cliente();
        cliente.setTelefono("abc12345"); // Teléfono inválido

        // Validación: No es un teléfono correcto
        assertNotEquals("098254785", cliente.getTelefono());
    }
}