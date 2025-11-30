FleetGuard360 es un sistema de monitoreo de flota con backend en Render, frontend en Vercel y documentación pública en Swagger, construido como demostración de despliegue cloud de bajo costo con actualizaciones en tiempo real.

## Enlaces del proyecto

- Frontend desplegado: https://despliguecloudfront.onrender.com/  
- Backend Swagger UI: https://desplieguecloudback.onrender.com/swagger-ui/index.html  
- Backend (código): https://github.com/fabricaudea/DespliegueCloudBack[1]
- Frontend (código): https://github.com/fabricaudea/frontend[1]

## Descripción general

FleetGuard360 es un MVP funcional que permite:

- Gestionar una flota de vehículos (alta, edición, baja lógica, cambio de estado).[2]
- Visualizar la ubicación actual de cada vehículo en un mapa en tiempo real.   
- Recibir actualizaciones continuas mediante Server-Sent Events (SSE) sin necesidad de hacer polling.   
- Exponer una API REST documentada con Swagger/OpenAPI para facilitar pruebas e integración.[3]

A nivel de modelo de dominio, la entidad central es Vehicle, con campos como id, licensePlate, model, capacity, status, latitude, longitude, speed, heading y lastUpdate, que permiten gestionar tanto el estado operativo como la posición y el movimiento de cada vehículo.[3]

## Arquitectura e infraestructura cloud

### Componentes principales

- Backend (Render):
  - Spring Boot (Java) como framework principal.[2]
  - JPA/Hibernate para persistencia, con Vehicle mapeado a la tabla vehicles.[3]
  - API REST para CRUD de vehículos, actualización de estado y ubicación.[2]
  - Server-Sent Events (SSE) para streaming de actualizaciones en tiempo real desde /api/stream/vehicles.   
  - Documentación automática con springdoc-openapi y Swagger UI en /swagger-ui/index.html.[3]
  - Desplegado como Web Service en Render usando la integración con GitHub.[4][1]

- Frontend (Vercel):
  - React + TypeScript como stack principal.[5]
  - Integración con el backend mediante un cliente HTTP tipado y modelos compartidos (Vehicle, VehicleCreate, VehicleUpdate).   
  - Mapa en tiempo real implementado con Leaflet + OpenStreetMap, evitando costes de API.   
  - Página de flota que combina mapa, tabla de vehículos y formularios de alta/edición.[5]
  - Desplegado en Vercel, aprovechando el plan Hobby gratuito para proyectos personales/MVP.[6][7]

- Documentación:
  - Swagger UI accesible en producción para inspeccionar y probar cada endpoint del backend.[3]

### Flujo de datos (alto nivel)

- El frontend obtiene el listado inicial de vehículos con GET /api/vehicles y los pinta en tabla y mapa.[5]
- Las actualizaciones de ubicación se envían vía POST /api/vehicles/{id}/location con latitude, longitude, speed y heading.[3]
- El backend persiste la nueva posición y actualiza lastUpdate mediante métodos @PrePersist/@PreUpdate en Vehicle.[3]
- El endpoint SSE /api/stream/vehicles emite eventos de actualización que el frontend consume a través de EventSource, actualizando el estado de React en tiempo real.   
- El componente Map renderiza marcadores en Leaflet por cada vehículo, con estilos según el estado y la orientación según heading.   

## Proceso de desarrollo y despliegue

### 1. Backend local

- Se creó el proyecto Spring Boot con la entidad Vehicle y el enum VehicleStatus, incluyendo campos de tracking: latitude, longitude, speed, heading y lastUpdate.[3]
- Se implementaron los siguientes endpoints:
  - CRUD de vehículos: GET /api/vehicles, GET /api/vehicles/{id}, POST /api/vehicles, PUT /api/vehicles/{id}, DELETE /api/vehicles/{id}.[2]
  - Cambio de estado: PATCH /api/vehicles/{id}/status.[2]
  - Actualización de ubicación: POST /api/vehicles/{id}/location.[3]
  - Streaming en tiempo real: GET /api/stream/vehicles (SSE).   
  - Health check: GET /api/health.[2]
- Se escribieron pruebas unitarias para la capa de servicio y controlador, validando creación, actualización, borrado lógico y filtrado por estado.[8][2]

### 2. Despliegue del backend en Render

Se evaluaron varias plataformas PaaS para hospedar el backend:

- Heroku:
  - Plataforma muy conocida, pero sin free tier clásico estable en 2025, lo que dificulta mantener un demo persistente sin coste.[9][10][4]

- Railway:
  - Plataforma moderna y cómoda, pero basada en créditos de uso que pueden agotarse rápido con servicios 24/7, generando incertidumbre para demos continuas.[11][12][4]

- Fly.io:
  - Ofrece despliegue cercano al usuario y control a bajo nivel, pero su modelo de precios y free trial en horas/recursos hace menos simple mantener un backend demo siempre encendido sin supervisión constante.[13][14][15]

- Render (elegida):
  - Proporciona un free tier con aproximadamente 750 horas/mes para Web Services, lo que es suficiente para mantener un backend activo todo el mes para un proyecto de demostración.[16][4][9]
  - Integra despliegue continuo desde GitHub y soporta servicios web, workers y bases de datos Postgres, cubriendo la mayoría de escenarios típicos de un backend de flota.[12][9][16]
  - Ofrece un modelo de costes más predecible que alternativas basadas en créditos, reduciendo el riesgo de cortes inesperados por agotamiento de saldo.[4][11][12]

Por estas razones se seleccionó Render como plataforma para el backend de FleetGuard360.

Pasos de despliegue:

- Publicación del código en GitHub: fabricaudea/DespliegueCloudBack.[1]
- Creación de un Web Service en Render vinculado al repositorio.[4][1]
- Configuración de comandos de build (mvn clean package) y start (java -jar target/*.jar).[1]
- Ajuste de variables de entorno y perfiles.[1]
- Verificación de /api/health y de Swagger UI en producción.[3]

### 3. Adaptación e integración del frontend

Partiendo de un frontend existente con login mock y gestión de flota basada en datos simulados, se realizaron los siguientes cambios:

- Sustitución de la cap de mocks por llamadas reales a la API del backend en Render, a través de http.ts y lib/api/vehicles.ts.[5]
- Actualización de los tipos Vehicle, VehicleCreate y VehicleUpdate en el frontend para alinearlos con el modelo del backend (incluyendo latitude, longitude, speed, heading y lastUpdate).   
- Implementación del módulo de seguimiento en tiempo real usando EventSource conectado a /api/stream/vehicles.   
- Creación del componente Map.tsx basado en Leaflet para mostrar los vehículos en tiempo real con iconos y colores según su estado.   
- Modificación de la página de flota (FleetPage) para:
  - Cargar vehículos al montar el componente.
  - Suscribirse a SSE y actualizar el estado con cada evento de vehículo actualizado.
  - Mostrar el mapa y la tabla dentro de tarjetas (Cards) con estilos coherentes con el resto del dashboard.[5]

### 4. Despliegue del frontend en Vercel

Para el frontend se eligió Vercel por:

- Su excelente integración con proyectos basados en React y frameworks modernos, y su orientación natural al despliegue de frontends.[7][17]
- Un plan Hobby gratuito con límites cómodos para este tipo de demo (número de peticiones y ancho de banda razonables para pocas sesiones concurrentes).[6][7]
- Despliegues automáticos en cada push a la rama principal en GitHub.[7]

Pasos de despliegue:

- Publicación del código en GitHub: fabricaudea/frontend.[1]
- Importación del proyecto en Vercel desde ese repositorio.[7]
- Configuración de variables de entorno, especialmete NEXT_PUBLIC_API_BASE_URL apuntando al backend en Render.[7]
- Verificación de la aplicación en https://despliguecloudfront.onrender.com/, comprobando:
  - Login mock.   
  - Visualización de la flota en tabla.[5]
  - Mapa con marcadores y actualizaciones en tiempo real mediante SSE.   

## Tecnologías usadas y su aplicación

### Backend

- Spring Boot:
  - Controladores REST para vehículos y ubicación.[2]
  - Endpoint SSE para streaming de cambios.   
  - Health check y manejo de errores.[2]

- JPA / Hibernate:
  - Entidad Vehicle con anotaciones @Entity, @Table, @Column y @Enumerated, mapeada a la tabla vehicles.[3]

- springdoc-openapi + Swagger UI:
  - Generación automática de documentación para todos los endpoints expuestos.[3]

### Frontend

- React + TypeScript:
  - Uso de hooks (useState, useEffect) para control de estado, carga inicial y ciclo de vida de la suscripción SSE.[5]
  - Modelos tipados para vehículos y tracking, compartiendo la semantica con el backend.   

- Leaflet + OpenStreetMap:
  - Renderizado de mapas sin coste de licencias ni necesidad de API key.   
  - Marcadores personalizados con colores y orientaciones según el estado y heading del vehículo.   

- UI:
  - Componentes reutilizables para barra de navegación, sidebar, tablas, formularios y diálogos de confirmación.[5]

### Cloud

- Render:
  - Plataforma PaaS para el backend con free tier que permite mantener el servicio activo para demostración sin coste económico directo.[9][16][4]

- Vercel:
  - Plataforma orientada a frontends modernos, con despliegue automático y CDN global.[6][7]

## Comparativa de plataformas y decisión de infraestructura

Durante el diseño se analizaron distintas opciones para el backend:

- Heroku:
  - Ha perdido su atractivo como plataforma gratuita persistente tras los cambios de su modelo de free tier, dificultando el uso para demos permanentes en 2025.[10][9][4]

- Railway:
  - Aunque ofrece una experiencia muy sencilla, su modelo a base de créditos iniciales y consumo variable hace menos predecible la vida útil de un demo siempre encendido.[11][12][4]

- Fly.io:
  - A pesar de su potencia y flexibilidad, el enfoque de recursos y el esquema de prueba gratuita pueden generar mayor complejidad en el control de costes para un MVP simple.[14][15][13]

- Render:
  - Ofrece una combinación muy atractiva de:
    - Free tier con horas suficientes para un Web Service activo.[16][9][4]
    - Despliegue directo desde GitHub y soporte para aplicaciones Java/Spring Boot.[9][1]
    - Modelo de precios simple y razonablemente predecible, adecuado para demostraciones y entornos académicos.[12][16][4]

Por estas razones se eligió la arquitectura:

- Backend: Render (Web Service con Spring Boot).[16][4][9]
- Frontend: Vercel (React/TypeScript).[6][7]

## Cómo ejecutar el proyecto

### Backend (local)

1) Clonar el repositorio:

- git clone https://github.com/fabricaudea/DespliegueCloudBack.git  

2) Construir y ejecutar:

- mvn clean package  
- java -jar target/*.jar  

3) Acceder a:

- API: http://localhost:8080/api/vehicles  
- Swagger UI: http://localhost:8080/swagger-ui.html  

### Frontend (local)

1) Clonar el repositorio:

- git clone https://github.com/fabricaudea/frontend.git  

2) Crear un archivo .env.local con:

- NEXT_PUBLIC_API_BASE_URL apuntando al backend (por ejemplo, http://localhost:8080 o la URL de Render).   

3) Instalar dependencias y ejecutar:

- npm install  
- npm run dev  
- Navegar a http://localhost:5173/  

### Producción

- Frontend: https://despliguecloudfront.onrender.com/  
- Backend Swagger: https://desplieguecloudback.onrender.com/swagger-ui/index.html  

En producción, basta con:

- Acceder al frontend.
- Realizar login (mock).
- Navegar a la sección de flota.
- Verificar que la tabla y el mapa muestran vehículos y responden a las actualizaciones de ubicación en tiempo real.

Este README resume el proceso seguido, las decisiones de arquitectura y las tecnologías aplicadas para construir y desplegar FleetGuard360 como un sistema demostrativo de tracking de flota en tiempo real sobre infraestructura cloud de bajo costo.[4][9][6][7]
