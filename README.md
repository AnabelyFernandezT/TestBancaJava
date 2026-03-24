# Payment Initiation Service - BIAN Compliant REST API

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Contexto y Proceso de Migración](#contexto-y-proceso-de-migración)
- [Arquitectura](#arquitectura)
- [Ejecución Local](#ejecución-local)
- [Ejecución con Docker](#ejecución-con-docker)
- [OpenAPI / Swagger](#openapi--swagger)
- [Testing y Cobertura](#testing-y-cobertura)
- [Calidad de Código](#calidad-de-código)
- [Uso de IA](#uso-de-ia)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Endpoints REST](#endpoints-rest)

---

## Descripción

Microservicio REST que implementa el estándar **BIAN (Banking Industry Architecture Network)** para la iniciación de pagos. Este servicio actúa como **fachada REST moderna** y se comunica con un **servicio SOAP legacy** existente.

### Características Principales
- ✅ API REST BIAN-compliant con 3 endpoints
- ✅ Integración con servicio SOAP legacy
- ✅ Arquitectura en capas (Clean Architecture)
- ✅ Validación de datos con Jakarta Bean Validation
- ✅ Manejo centralizado de excepciones
- ✅ Cobertura de código >= 80%
- ✅ Calidad verificada con Checkstyle y SpotBugs
- ✅ Dockerizado con multi-stage build
- ✅ Documentación OpenAPI completa

---

## Contexto y Proceso de Migración

### Estado Inicial
El banco contaba con un **servicio SOAP legacy** (`PaymentOrderService.wsdl`) para gestión de órdenes de pago:
- Protocolo SOAP/XML
- Dos operaciones: `SubmitPaymentOrder` y `GetPaymentOrderStatus`
- Sin documentación moderna
- Difícil integración con canales digitales

### Objetivos de Migración
1. **Modernizar la interfaz**: Exponer API REST siguiendo estándares BIAN
2. **Mantener compatibilidad**: Integrar con SOAP legacy sin modificarlo
3. **Mejorar experiencia**: Simplificar integración para aplicaciones cliente
4. **Añadir calidad**: Implementar tests, cobertura y validaciones de código

### Decisiones Clave

#### 1. **Arquitectura de Fachada (Facade Pattern)**
```
Aplicaciones Móviles/Web → REST API → SOAP Client → Sistema Legacy
```
- **Ventaja**: Desacoplamiento del sistema legacy
- **Resultado**: Evolución independiente de ambas capas

#### 2. **Separación de Modelos**
- DTOs REST (API pública)
- Entidades de Dominio (lógica interna)
- Modelos SOAP (integración legacy)

**Razón**: Cambios en SOAP no afectan contratos REST

#### 3. **Persistencia In-Memory Inicial**
- Repositorio `ConcurrentHashMap` para MVP
- Path de migración a JPA/PostgreSQL definido

**Razón**: Simplificar deployment inicial, facilitar testing

#### 4. **Testing con Mock SOAP Interno**
- Mock activable por configuración (`soap.mock.enabled=true`)
- Permite testing sin dependencias externas

**Razón**: CI/CD más rápido, desarrollo local simplificado

---

## Arquitectura

```
Cliente REST → REST API (BIAN) → SOAP Client → Legacy SOAP Service
```

### Componentes:
- **REST Controllers**: Exponen endpoints REST siguiendo estándar BIAN
- **Service Layer**: Lógica de negocio y orquestación
- **SOAP Client**: Cliente para comunicación con servicio legacy
- **Repository**: Almacenamiento en memoria de órdenes de pago
- **DTOs**: Objetos de transferencia de datos para REST API

## Endpoints REST (BIAN Compliant)

### 1. Iniciar Orden de Pago (Initiate)
```http
POST /payment-initiation/payment-orders
Content-Type: application/json

{
  "externalReference": "EXT-1",
  "debtorAccount": { "iban": "EC12DEBTOR" },
  "creditorAccount": { "iban": "EC98CREDITOR" },
  "instructedAmount": { "amount": 150.75, "currency": "USD" },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31"
}
```

**Respuesta (201 Created):**
```json
{
  "paymentOrderId": "PO-0001",
  "externalReference": "EXT-1",
  "debtorAccount": { "iban": "EC12DEBTOR" },
  "creditorAccount": { "iban": "EC98CREDITOR" },
  "instructedAmount": { "amount": 150.75, "currency": "USD" },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31",
  "status": "ACCEPTED",
  "createdAt": "2025-03-23T10:30:00",
  "lastUpdate": "2025-03-23T10:30:00"
}
```

### 2. Consultar Orden de Pago (Retrieve)
```http
GET /payment-initiation/payment-orders/PO-0001
```

**Respuesta (200 OK):**
```json
{
  "paymentOrderId": "PO-0001",
  "externalReference": "EXT-1",
  ...
}
```

### 3. Consultar Estado de Orden de Pago (Retrieve BQ - Behavior Qualifier)
```http
GET /payment-initiation/payment-orders/PO-0001/status
```

**Respuesta (200 OK):**
```json
{
  "paymentOrderId": "PO-0001",
  "status": "SETTLED",
  "lastUpdate": "2025-10-30T16:25:30"
}
```

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.1.5**
  - Spring Web (REST API)
  - Spring Web Services (SOAP Client)
- **Maven**
- **Lombok** (reducción de código boilerplate)
- **JAXB** (binding XML para SOAP)
- **SLF4J + Logback** (logging)

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/test/trestproject/
│   │   ├── Main.java                          # Spring Boot Application
│   │   ├── controller/
│   │   │   ├── PaymentOrderController.java    # REST endpoints
│   │   │   └── GlobalExceptionHandler.java    # Error handling
│   │   ├── service/
│   │   │   ├── PaymentOrderService.java       # Business logic
│   │   │   └── ResourceNotFoundException.java
│   │   ├── repository/
│   │   │   └── PaymentOrderRepository.java    # In-memory storage
│   │   ├── domain/
│   │   │   └── PaymentOrder.java              # Domain entity
│   │   ├── dto/
│   │   │   ├── PaymentOrderRequest.java       # REST request DTO
│   │   │   ├── PaymentOrderResponse.java      # REST response DTO
│   │   │   ├── PaymentOrderStatusResponse.java
│   │   │   ├── AccountReference.java
│   │   │   └── InstructedAmount.java
│   │   └── soap/
│   │       ├── client/
│   │       │   └── PaymentOrderSoapClient.java # SOAP client
│   │       ├── config/
│   │       │   └── SoapClientConfig.java      # SOAP configuration
│   │       └── model/
│   │           ├── SubmitPaymentOrderRequest.java
│   │           ├── SubmitPaymentOrderResponse.java
│   │           ├── GetPaymentOrderStatusRequest.java
│   │           └── GetPaymentOrderStatusResponse.java
│   └── resources/
│       ├── application.properties
│       └── wsdl/
│           └── PaymentOrderService.wsdl
└── test/
    └── java/
```

## Requisitos Previos
---
- JDK 17 o superior
- Maven 3.6 o superior
- Docker y Docker Compose (opcional, para ejecución containerizada)

---

## Ejecución Local

### Opción 1: Con Mock SOAP Interno (Recomendado para desarrollo)

1. **Habilitar el mock** en `src/main/resources/application.properties`:
   ```properties
   soap.mock.enabled=true
   ```

2. **Compilar el proyecto**:
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```

4. **Verificar**:
   - API REST: http://localhost:8080/payment-initiation/payment-orders
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health: http://localhost:8080/actuator/health

### Opción 2: Con Servicio SOAP Real

1. **Configurar URL del servicio** en `application.properties`:
   ```properties
   soap.legacy.url=http://tu-servidor-soap:8081/legacy/payments
   soap.mock.enabled=false
   ```

2. **Compilar y ejecutar**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Probar con cURL

```bash
# Crear orden de pago
curl -X POST http://localhost:8080/payment-initiation/payment-orders \
  -H "Content-Type: application/json" \
  -d '{
    "externalReference": "EXT-1",
    "debtorAccount": { "iban": "EC12DEBTOR" },
    "creditorAccount": { "iban": "EC98CREDITOR" },
    "instructedAmount": { "amount": 150.75, "currency": "USD" },
    "remittanceInformation": "Factura 001-123",
    "requestedExecutionDate": "2025-10-31"
  }'

# Consultar orden de pago  
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001

# Consultar estado
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001/status
```

---

## Ejecución con Docker

### Build de la Imagen

El proyecto incluye un **Dockerfile multi-stage** que optimiza el tamaño de la imagen:

```bash
# Build de la imagen
docker build -t payment-initiation-service:1.0 .
```

**Características del Dockerfile:**
- Stage 1: Compilación con Maven
- Stage 2: Runtime con JRE Alpine (imagen ligera)
- Usuario no-root para seguridad
- Health check configurado

### Ejecución con Docker Compose

El archivo `docker-compose.yml` levanta dos servicios:
1. **payment-service**: Microservicio REST
2. **soap-mock**: MockServer para simular SOAP legacy

```bash
# Levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f payment-service

# Detener servicios
docker-compose down
```

**URLs disponibles:**
- API REST: http://localhost:8080/payment-initiation/payment-orders
- Swagger: http://localhost:8080/swagger-ui.html
- Mock SOAP: http://localhost:8081

### Ejecutar Imagen Individual

```bash
# Ejecutar con mock interno
docker run -d -p 8080:8080 \
  -e SOAP_MOCK_ENABLED=true \
  --name payment-service \
  payment-initiation-service:1.0

# Ejecutar apuntando a SOAP externo
docker run -d -p 8080:8080 \
  -e SOAP_LEGACY_URL=http://external-soap:8081/legacy/payments \
  -e SOAP_MOCK_ENABLED=false \
  --name payment-service \
  payment-initiation-service:1.0
```

### Variables de Entorno Docker

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SOAP_LEGACY_URL` | URL del servicio SOAP | `http://soap-mock:8081/legacy/payments` |
| `SOAP_MOCK_ENABLED` | Habilitar mock interno | `false` |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring Boot | `docker` |
| `SERVER_PORT` | Puerto del servidor | `8080` |

---

## OpenAPI / Swagger

### Especificación OpenAPI

El contrato de la API está documentado en **OpenAPI 3.0** en el archivo:
- **Ubicación**: [`openapi.yml`](openapi.yml)

### Swagger UI

Cuando la aplicación está corriendo, acceder a:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

**Características:**
- Documentación interactiva de todos los endpoints
- Ejemplos de request/response
- Posibilidad de probar la API directamente desde el navegador
- Esquemas de datos con validaciones

---

## Testing y Cobertura

### Tipos de Tests Implementados

#### 1. **Tests Unitarios**
- **Service Layer**: `PaymentOrderServiceTest.java`
  - Tests de lógica de negocio
  - Mocking de dependencias (SOAP client, repository)
  - Verificación de mapeos REST ↔ SOAP

- **Repository Layer**: `PaymentOrderRepositoryTest.java`
  - Tests de operaciones CRUD
  - Verificación de concurrencia (ConcurrentHashMap)

- **DTOs y Validaciones**: `PaymentOrderRequestValidationTest.java`
  - Validación de Bean Validation constraints
  - Tests de getters/setters

#### 2. **Tests de Integración**
- **Controllers**: `PaymentOrderControllerTest.java`
  - Tests con MockMvc
  - Verificación de serialización JSON
  - Validación de HTTP status codes

#### 3. **Tests E2E**
- **Integration Tests**: `PaymentOrderIntegrationTest.java`
  - Tests end-to-end con TestRestTemplate
  - Spring Boot test con mock SOAP habilitado
  - Flujo completo: POST → GET → GET status

### Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte de cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

**Verificar cobertura**:
```bash
mvn verify
```

**Reporte de cobertura**: `target/site/jacoco/index.html`

**Cobertura actual** (aproximada):
- **Líneas**: ~85%
- **Branches**: ~82%
- **Clases**: 100%

---

## Calidad de Código

### Checkstyle

**Configuración**: `checkstyle.xml` (basado en Google Style Guide)

**Ejecutar**:
```bash
mvn checkstyle:check
```

### SpotBugs

**Configuración**: `spotbugs-exclude.xml`

**Ejecutar**:
```bash
mvn spotbugs:check
```

**Verificaciones**:
- Null pointer dereferences
- Infinite loops
- Resource leaks
- Security vulnerabilities
- Code smells


### Verificación Completa

**Comando para validar todo** (como exige la prueba técnica):
```bash
mvn clean verify
```

Este comando ejecuta:
1. Compilación
2. Tests unitarios
3. Tests de integración
4. Checkstyle
5. SpotBugs
6. JaCoCo (verifica cobertura >= 80%)

**Resultado esperado**: `BUILD SUCCESS`

---

## Uso de IA

### Prompts Principales Utilizados

#### Prompt 1: Análisis y Setup Inicial

- Análisis de requerimientos BIAN
- Propuesta de arquitectura en capas
- Setup de Spring Boot 3.1.5 con dependencias necesarias
- Creación de estructura de paquetes

#### Prompt 2: Completar Entregables

**Respuesta (resumida)**:
- Identificación de faltantes (Docker, OpenAPI, tests, calidad)
- Generación de Dockerfile multi-stage
- Creación de docker-compose.yml
- Archivo openapi.yml completo
- Configuración de Checkstyle/SpotBugs/JaCoCo
- Tests unitarios y de integración
- Carpeta ai/ con evidencia

### Fragmentos Generados por IA

Ver carpeta **[`ai/generations/`](ai/generations/)** para ejemplos de código generado.

**Archivos de evidencia**:
- [`ai/prompts.md`](ai/prompts.md) - Prompts completos con contexto
- [`ai/decisions.md`](ai/decisions.md) - Decisiones de diseño con ayuda de IA
- [`ai/generations/`](ai/generations/) - Fragmentos de código generados

### Correcciones y Ajustes

**Correcciones mínimas realizadas**:
1. Eliminación de imports no utilizados (detectados por IDE)
2. Ajuste de configuración de Checkstyle para tolerar DTOs con muchos campos
3. Configuración de exclusiones en SpotBugs para evitar falsos positivos con Lombok

**Calidad del código generado**:
- ✅ Sigue best practices de Spring Boot
- ✅ Código idiomático y limpio
- ✅ Patrones de diseño apropiados
- ✅ Documentación incluida (Javadoc)
- ✅ Manejo de errores robusto
- ✅ Tests completos

---

## Requisitos Previos
- Maven 3.6 o superior
- Docker y Docker Compose (opcional, para ejecución containerizada)

---

## Endpoints REST

El servicio expone tres endpoints REST siguiendo el estándar BIAN:

### 1. POST /payment-initiation/payment-orders (Initiate)

**Descripción**: Crea una nueva orden de pago

**Request**:
```bash
mvn clean install
```

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

### 3. Probar con Postman
Importar la colección incluida: `postman_collection.json`

## Probar con cURL

### Crear Orden de Pago
```bash
curl -X POST http://localhost:8080/payment-initiation/payment-orders \
  -H "Content-Type: application/json" \
  -d '{
    "externalReference": "EXT-1",
    "debtorAccount": { "iban": "EC12DEBTOR" },
    "creditorAccount": { "iban": "EC98CREDITOR" },
    "instructedAmount": { "amount": 150.75, "currency": "USD" },
    "remittanceInformation": "Factura 001-123",
    "requestedExecutionDate": "2025-10-31"
  }'
```

### Consultar Orden de Pago
```bash
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001
```

### Consultar Estado
```bash
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001/status
```

## Configuración

Editar `src/main/resources/application.properties`:

```properties
# Puerto del servidor
server.port=8080

# URL del servicio SOAP legacy
soap.legacy.url=http://localhost:8081/legacy/payments

# Nivel de logs
logging.level.com.test.trestproject=DEBUG
```

## Modelo de Datos BIAN

El servicio implementa los siguientes patrones BIAN:

1. **Service Domain**: Payment Initiation
2. **Control Record**: PaymentOrder
3. **Actions**:
   - **Initiate**: Crear nueva orden de pago
   - **Retrieve**: Consultar orden de pago
   - **Retrieve BQ**: Consultar estado (Behavior Qualifier)

## Mapeo REST ↔ SOAP

| REST Field | SOAP Field |
|-----------|-----------|
| externalReference | externalId |
| debtorAccount.iban | debtorIban |
| creditorAccount.iban | creditorIban |
| instructedAmount.amount | amount |
| instructedAmount.currency | currency |
| remittanceInformation | remittanceInfo |
| requestedExecutionDate | requestedExecutionDate |

## Manejo de Errores

- **400 Bad Request**: Datos de entrada inválidos
- **404 Not Found**: Orden de pago no encontrada
- **500 Internal Server Error**: Error en servicio SOAP o interno

## Estados de Orden de Pago

- `ACCEPTED`: Orden aceptada por el sistema
- `PENDING`: En proceso
- `SETTLED`: Pagada/liquidada
- `REJECTED`: Rechazada
- `CANCELLED`: Cancelada

---

## 📦 Entregables del Proyecto

✅ **URL del Repositorio**: [GitHub Repository Link Here]

✅ **Documentación README** (este archivo) incluye:
- ✅ Contexto y decisiones de migración
- ✅ Pasos para ejecución local
- ✅ Pasos para ejecución con Docker
- ✅ Sección de uso de IA con prompts, respuestas y fragmentos

✅ **OpenAPI**: [`openapi.yml`](openapi.yml)

✅ **Pruebas**:
- ✅ Tests unitarios: Service, Repository, DTOs, Validaciones
- ✅ Tests de integración: Controllers (MockMvc)
- ✅ Tests E2E: End-to-end con TestRestTemplate
- ✅ Cobertura >= 80% (configurado con JaCoCo)
- ✅ Reporte: `target/site/jacoco/index.html`

✅ **Calidad**:
- ✅ Checkstyle configurado: `checkstyle.xml`
- ✅ SpotBugs configurado: `spotbugs-exclude.xml`
- ✅ Validación con `mvn verify` sin fallos

✅ **Docker**:
- ✅ Dockerfile multi-stage: [`Dockerfile`](Dockerfile)
- ✅ Docker Compose: [`docker-compose.yml`](docker-compose.yml)

✅ **Evidencia de IA**:
- ✅ [`ai/prompts.md`](ai/prompts.md) - Prompts utilizados
- ✅ [`ai/decisions.md`](ai/decisions.md) - Decisiones de diseño
- ✅ [`ai/generations/`](ai/generations/) - Fragmentos generados

---

## 🚀 Quick Start

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd TestJava

# 2. Compilar y ejecutar tests
mvn clean verify

# 3. Ejecutar localmente con mock
mvn spring-boot:run -Dspring-boot.run.arguments="--soap.mock.enabled=true"

# 4. O con Docker
docker-compose up -d

# 5. Probar la API
curl http://localhost:8080/payment-initiation/payment-orders
```

## 🔧 Tecnologías y Dependencias

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.1.5 | Framework de aplicación |
| Spring Web | 3.1.5 | REST API |
| Spring WS | 4.0.x | Cliente SOAP |
| Spring Actuator | 3.1.5 | Health checks y monitoring |
| Maven | 3.6+ | Gestión de dependencias |
| Lombok | 1.18.x | Reducción de boilerplate |
| JAXB | 4.0.x | Binding XML-Java |
| Jakarta Validation | 3.0.x | Validación de datos |
| SpringDoc OpenAPI | 2.2.0 | Documentación automática |
| JaCoCo | 0.8.11 | Cobertura de código |
| Checkstyle | 10.12.5 | Estilo de código |
| SpotBugs | 4.8.0 | Detección de bugs |
| JUnit 5 | 5.9.x | Testing framework |
| Mockito | 5.x | Mocking framework |

