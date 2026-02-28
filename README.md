# CoreBank Lite — API de Clientes, Cuentas y Movimientos (Prueba Técnica DevSu)

Implementación de una API tipo **core bancario simplificado** (core banking “lite”) basada en **microservicios** con Spring Boot.

- **ms-clientes**: gestión de **Clientes** y **Cuentas**.
- **ms-movimientos**: gestión de **Movimientos** y generación de **Reporte**.
- La comunicación entre microservicios se realiza desde **ms-movimientos → ms-clientes** usando **OpenFeign**, para validar cuentas y completar el nombre del **Cliente** en el reporte.

> ¿Esto es un sistema financiero?  
> Sí: es un **modelo reducido** de un sistema financiero (core banking simplificado). En producción normalmente se agregan seguridad, auditoría, concurrencia/locking, idempotencia formal, observabilidad, etc. Aquí se implementan las reglas y flujos esenciales solicitados por la prueba.

---

## Tecnologías

- Java / Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- Docker + Docker Compose
- OpenFeign
- Pruebas unitarias (Mockito) y de integración

---

## Arquitectura (alto nivel)

- Separación por microservicios (dominio funcional):
  - `ms-clientes`: maestro de clientes y cuentas
  - `ms-movimientos`: transacciones de movimientos y reporte
- Integración entre servicios:
  - **Síncrona HTTP** con OpenFeign (`ms-movimientos` consulta `ms-clientes`)

> Nota sobre el enunciado: se menciona “comunicación asíncrona” como deseable.  
> Esta solución utiliza integración **síncrona** por simplicidad y trazabilidad en la prueba; puede extenderse a mensajería (Kafka/RabbitMQ) como mejora.

---

## Puertos y URLs

### Desde tu PC (Postman)
- **ms-clientes**: `http://localhost:8090`
- **ms-movimientos**: `http://localhost:8081`
- **PostgreSQL**: `localhost:5433` (puerto expuesto del contenedor)

### Entre contenedores (Docker network)
- `ms-movimientos` consume `ms-clientes` usando:
  - `http://ms-clientes:8080` (puerto interno del contenedor)

---

## Base de datos

El contenedor de PostgreSQL utiliza el puerto expuesto **5433** hacia el host.  
Se incluye el script:

- `BaseDatos.sql` (creación de BD/tablas principales)

> Si corres con `spring.jpa.hibernate.ddl-auto=update`, Hibernate puede crear/actualizar el esquema automáticamente.  
> Aun así se incluye el script porque lo solicita la prueba.

### Persistencia de datos (evitar que se borren al reiniciar)
- Usar:
  - `spring.jpa.hibernate.ddl-auto=update`
- Evitar:
  - `docker-compose down -v` (borra el volumen y por lo tanto los datos)

---

## Levantar con Docker Compose

```bash
docker-compose up -d --build
```

Verificar estado:
```bash
docker ps
```

---

## Variables de entorno relevantes

En `docker-compose.yml` (**ms-movimientos**):

- `CLIENTES_SERVICE_URL=http://ms-clientes:8080`

En `application.properties` (**ms-movimientos**):

- `clientes.service.url=${CLIENTES_SERVICE_URL:http://localhost:8090}`

---

## Endpoints principales

### ms-clientes (Base URL: `http://localhost:8090`)

#### Crear cliente
`POST /api/clientes`

```json
{
  "nombre": "Marianela Montalvo",
  "genero": "F",
  "edad": 30,
  "identificacion": "1234567890",
  "direccion": "Bogotá",
  "telefono": "3000000000",
  "contrasena": "1234",
  "estado": true
}
```

#### Listar clientes
`GET /api/clientes`

#### Obtener cliente por id
`GET /api/clientes/{id}`

#### Crear cuenta
`POST /api/cuentas`

```json
{
  "numeroCuenta": "CUENTA-002",
  "tipo": "Ahorro",
  "saldoInicial": 0,
  "saldo": 0,
  "estado": true,
  "clienteId": 1
}
```

#### Obtener cuenta por número
`GET /api/cuentas/numero/{numeroCuenta}`

---

### ms-movimientos (Base URL: `http://localhost:8081`)

#### Registrar movimiento
`POST /api/movimientos`

```json
{
  "numeroCuenta": "CUENTA-002",
  "tipo": "Deposito",
  "valor": 6000,
  "descripcion": "Consignación"
}
```

#### Movimientos por cuenta
`GET /api/movimientos/cuenta/{numeroCuenta}`

#### Reporte
`GET /api/movimientos/reporte/{numeroCuenta}?fechaInicio=YYYY-MM-DDTHH:MM:SS&fechaFin=YYYY-MM-DDTHH:MM:SS`

Ejemplo:
`http://localhost:8081/api/movimientos/reporte/CUENTA-002?fechaInicio=2026-02-01T00:00:00&fechaFin=2026-02-28T23:59:59`

Formato de respuesta (según requerimiento):

```json
[
  {
    "Fecha": "2026-02-28T02:02:48.802747",
    "Cliente": "Marianela Montalvo",
    "Numero Cuenta": "CUENTA-002",
    "Tipo": "Deposito",
    "Saldo Inicial": 0.00,
    "Estado": true,
    "Movimiento": 6000.00,
    "Saldo Disponible": 6000.00
  }
]
```

---

## Validación de endpoints (Postman)

Se incluyen archivos para validar rápidamente los casos de uso:

- `DevSu-Integracion.postman_collection.json`
- `DevSu-Local.postman_environment.json`

### Ejecución
1. Levantar servicios:
   ```bash
   docker-compose up -d --build
   ```
2. Importar en Postman la **Collection** y el **Environment**
3. Seleccionar el environment **DevSu-Local**
4. Ejecutar requests en el orden sugerido:
   - Crear clientes
   - Crear cuentas
   - Registrar movimientos
   - Consultar movimientos
   - Generar reporte

---

## Aspectos aplicados (alineados a prácticas de producción)

Además de cumplir con los endpoints solicitados, se implementaron ajustes típicos de APIs transaccionales/financieras para mejorar robustez, claridad de errores y repetibilidad de pruebas:

- **Códigos HTTP correctos para errores de negocio (400/404/409)**  
  Se evita responder **500** ante validaciones esperables, permitiendo a los consumidores manejar errores de forma consistente.
  - `409 Conflict`: saldo insuficiente, recursos duplicados  
  - `404 Not Found`: cuenta/cliente inexistente  
  - `400 Bad Request`: request inválido (campos requeridos, tipo inválido, cuenta inactiva, etc.)

- **Colección Postman re-ejecutable (idempotencia práctica en recursos maestros)**  
  Al intentar crear un cliente/cuenta existente, el servicio responde **409 Conflict** en lugar de 500, haciendo que la colección pueda ejecutarse múltiples veces sin “ruido” por duplicados.
  
  Ejemplo de respuesta (cliente ya existente):
  ```json
  {
    "status": 409,
    "error": "Conflict",
    "message": "Ya existe un cliente con identificacion: ID-JO-001"
  }
  ```

- **Validación de saldo previa a retiros**  
  Un retiro que deja el saldo negativo responde **409 Conflict** con el mensaje `Saldo no disponible`.

- **Saldo inicial correcto cuando no hay movimientos previos**  
  Si una cuenta no tiene movimientos registrados, el saldo base se obtiene desde el servicio de cuentas (**ms-clientes**) en vez de asumir `0`, alineándose al comportamiento esperado.

Estas decisiones mejoran la resiliencia del sistema, la experiencia de integración (API-first) y la automatización de pruebas (Postman/Newman).

---

## Manejo de errores (API)

Manejo consistente para evitar 500 ante escenarios de negocio:

- **409 Conflict**: recursos duplicados (cliente por `identificacion`, cuenta por `numeroCuenta`)
- **404 Not Found**: recursos inexistentes (cuenta no encontrada al registrar movimientos)
- **400 Bad Request**: request inválido (campos requeridos, tipo de movimiento inválido, cuenta inactiva, etc.)
- **409 Conflict**: saldo insuficiente para retiros

---

## Pruebas

Incluye:
- Pruebas unitarias con Mockito
- Pruebas de integración

Ejecutar (Maven):
```bash
mvn test
```

---

## Notas

- El reporte completa el nombre del **Cliente** consultando a **ms-clientes** vía Feign (cuenta → clienteId → cliente).
- Si un cliente/cuenta no existe, el servicio responde **404** (no 500).
