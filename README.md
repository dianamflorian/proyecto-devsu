# Proyecto DevSu - Microservicios

Sistema de microservicios para gestión de clientes y movimientos bancarios.

##  Arquitectura

- **ms-clientes**: Gestión de clientes (puerto 8080)
- **ms-movimientos**: Gestión de movimientos bancarios (puerto 8081)
- **PostgreSQL**: Base de datos (puerto 5433)

##  Cómo ejecutar con Docker

```bash
docker-compose up --build

📋 Endpoints
Clientes
POST /api/clientes - Crear cliente
GET /api/clientes - Listar clientes
GET /api/clientes/{id} - Obtener cliente
PUT /api/clientes/{id} - Actualizar cliente
DELETE /api/clientes/{id} - Eliminar cliente

Cuentas
POST /api/cuentas - Crear cuenta
GET /api/cuentas - Listar cuentas

Movimientos
POST /api/movimientos - Registrar movimiento
GET /api/movimientos/cuenta/{numeroCuenta} - Listar movimientos

🛠️ Tecnologías
Java 17
Spring Boot 7.0.5
PostgreSQL 18
Docker & Docker Compose
Maven

📁 Estructura

proyecto-devsu/
├── ms-clientes/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── ms-movimientos/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml
└── README.md


👤 Autor
Diana Florian
