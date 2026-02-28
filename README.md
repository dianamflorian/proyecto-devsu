# DevSu - Microservicios (Clientes y Movimientos)

Solución basada en microservicios con Spring Boot:

- **ms-clientes**: gestión de **Clientes** y **Cuentas**.
- **ms-movimientos**: gestión de **Movimientos** y generación de **Reporte**.

La comunicación entre microservicios se realiza desde **ms-movimientos → ms-clientes** (OpenFeign) para completar el campo **Cliente** en el reporte.

---

## Tecnologías
- Java / Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- Docker + Docker Compose
- OpenFeign
- Tests unitarios (Mockito) y de integración

---

## Puertos y URLs

### Desde tu PC (Postman)
- **ms-clientes**: `http://localhost:8090`
- **ms-movimientos**: `http://localhost:8081`
- **PostgreSQL**: `localhost:5433` (puerto expuesto del contenedor)

### Entre contenedores (Docker Network)
- **ms-movimientos** consume **ms-clientes** usando:
  - `http://ms-clientes:8080` (puerto interno del contenedor)

---

## Bases de datos (2 BD)
Este proyecto usa **dos bases de datos**:
- `devsu_clientes` (tablas: `clientes`, `cuentas`)
- `devsu_movimientos` (tabla: `movimientos`)

Se incluye un script en la raíz del repositorio:

- `BaseDatos.sql` (crea ambas BD y tablas principales)

> Nota: Si corres con JPA (`ddl-auto=update`), Hibernate puede crear/actualizar el esquema automáticamente.  
> Aun así se incluye el script porque lo solicita la prueba.

---

## Persistencia de datos (evitar que se borren al reiniciar)
Asegúrate de NO usar `create-drop`, porque eso borra tablas/datos cuando se detiene el servicio.

Recomendado para desarrollo:
- `spring.jpa.hibernate.ddl-auto=update`

> También evita ejecutar `docker-compose down -v` si quieres conservar los datos (borra el volumen de Postgres).

---

## Levantar con Docker Compose
```bash
docker-compose up -d --build
```

---

## Variables de entorno relevantes
En `docker-compose.yml` (ms-movimientos):
- `CLIENTES_SERVICE_URL=http://ms-clientes:8080`

En `application.properties` (ms-movimientos):
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
```text
http://localhost:8081/api/movimientos/reporte/CUENTA-002?fechaInicio=2026-02-01T00:00:00&fechaFin=2026-02-28T23:59:59
```

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

## Validación de endpoints (Postman)
Se incluye una colección de Postman para validar rápidamente los casos de uso de la prueba técnica:

- `DevSu-Integracion.postman_collection.json`
- `DevSu-Local.postman_environment.json`

### Ejecución
1. Levantar servicios con Docker Compose:
   ```bash
   docker-compose up -d --build
   ```
2. Importar en Postman la **Collection** y el **Environment**
3. Seleccionar el environment `DevSu-Local`
4. Ejecutar requests en el orden sugerido:
   - Crear clientes
   - Crear cuentas
   - Registrar movimientos
   - Consultar movimientos
   - Generar reporte

---

## Manejo de errores (API)
Se implementó manejo de errores consistente con REST para evitar respuestas 500 en validaciones de negocio:

- `409 Conflict`: recursos duplicados (ej. cliente existente por `identificacion`, cuenta existente por `numeroCuenta`)
- `404 Not Found`: recursos inexistentes (ej. cuenta no encontrada al registrar movimientos)
- `400 Bad Request`: request inválido (campos requeridos, tipo de movimiento inválido, cuenta inactiva, etc.)
- `409 Conflict`: saldo insuficiente para retiros

Esto permite que la colección Postman sea **re-ejecutable** sin fallar con errores 500 por registros ya existentes.

---

## Pruebas
Incluye:
- Pruebas unitarias con Mockito
- Pruebas de integración

Ejecutar (si usas Maven):
```bash
mvn test
```

---

## Notas
- El reporte completa el nombre del **Cliente** consultando a **ms-clientes** vía Feign (cuenta → clienteId → cliente).
- Si un cliente/cuenta no existe, el servicio responde **404** (no 500).
