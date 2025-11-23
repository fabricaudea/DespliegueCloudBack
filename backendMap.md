# Backend FleetGuard360

## Punto de entrada (Main)
- `MonitoringServiceApplication`
    - Arranca la aplicación Spring Boot.
    - Escanea paquetes y registra controllers, services, repositorios, etc.
    - Configura el contexto de Spring y las propiedades del proyecto.

## Capa Controller (API REST)

### `VehicleController`
- Expone endpoints REST para gestionar vehículos.
- CRUD de vehículos (listar, crear, actualizar, eliminar).
- Endpoint para actualizar la ubicación de un vehículo.
- Llama a `VehicleService` para toda la lógica de negocio.

### `SseController`
- Expone el endpoint SSE `/api/stream/vehicles`.
- Registra clientes que quieren recibir actualizaciones en tiempo real.
- Delegación total en `SseService` para gestionar emisores SSE.

### `HealthController`
- Expone el endpoint `/api/health`.
- Devuelve información de estado del servicio.
- Puede consultar `SseService` para saber cuántos clientes SSE están conectados.
- Útil para monitoreo y pruebas de despliegue.

## Capa Service (Lógica de negocio)

### `VehicleService`
- Orquesta las operaciones sobre vehículos.
- Usa `VehicleRepository` para acceder a la base de datos.
- Convierte entidades `Vehicle` a `VehicleDTO` y viceversa.
- Valida datos de entrada (DTOs).
- Actualiza ubicación, estado y fecha de última actualización.
- Notifica a `SseService` cuando hay cambios relevantes (por ejemplo, nueva ubicación).

### `SseService`
- Gestiona la lista de clientes SSE conectados (`SseEmitter`).
- Registra nuevos clientes cuando se conectan al stream.
- Envía eventos `vehicleUpdate` a todos los clientes activos.
- Elimina emisores que fallen o cierren la conexión.
- Es el puente del tiempo real entre backend y frontend.

## Capa Repository (Acceso a datos)

### `VehicleRepository`
- Extiende `JpaRepository<Vehicle, Long>`.
- Provee operaciones CRUD estándar sobre la entidad `Vehicle`.
- Puede tener métodos adicionales como:
    - `findByLicensePlate(...)`
    - `findByStatus(...)`
- Abstrae las consultas a la base de datos.

## Capa Entity (Modelo de datos)

### `Vehicle`
- Representa la tabla de vehículos en la base de datos.
- Atributos típicos:
    - `id`, `licensePlate`, `model`, `capacity`
    - `latitude`, `longitude`, `speed`, `heading`
    - `status`, `lastUpdate`
- Relacionado con `VehicleStatus`.

### `VehicleStatus`
- Enumeración del estado del vehículo.
- Ejemplos:
    - `AVAILABLE`
    - `IN_USE`
    - `MAINTENANCE`
    - `INACTIVE`
- Facilita filtrar y validar estados de operación.

## Capa DTO (Transferencia de datos)

### `VehicleDTO`
- Representación de un vehículo para respuestas API.
- Contiene los datos que el frontend necesita ver.
- Evita exponer detalles internos de la entidad JPA.

### `CreateVehicleDTO`
- Estructura para recibir datos al crear o editar un vehículo.
- Usado en operaciones POST/PUT.
- Suele incluir placa, modelo, capacidad, estado inicial.

### `VehicleLocationUpdateDTO`
- Estructura específica para actualizar la ubicación.
- Atributos típicos:
    - `latitude`, `longitude`, `speed`, `heading`
- Utilizada por el endpoint de actualización de ubicación.

## Inicialización y datos de prueba

### `DataInitializer`
- Se ejecuta al iniciar la aplicación.
- Crea vehículos de ejemplo en la base de datos.
- Útil para pruebas locales y demostraciones.
- Usa `VehicleRepository` para guardar los datos iniciales.

## Flujo Tiempo Real (Resumen lógico)

- Frontend envía nueva ubicación a `VehicleController`.
- `VehicleController` delega en `VehicleService`.
- `VehicleService`:
    - Busca el `Vehicle` en la base de datos.
    - Actualiza sus campos de ubicación y estado.
    - Guarda los cambios con `VehicleRepository`.
    - Convierte a `VehicleDTO`.
    - Llama a `SseService.broadcastVehicleUpdate(...)`.
- `SseService` envía un evento `vehicleUpdate` a todos los clientes SSE conectados.
- Frontend (EventSource) recibe el evento y actualiza el mapa en tiempo real.

