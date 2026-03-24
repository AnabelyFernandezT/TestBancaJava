# Solución Técnica - Payment Initiation Service

## Resumen Ejecutivo

Se ha implementado exitosamente un **microservicio REST** que cumple con el estándar **BIAN (Banking Industry Architecture Network)** para la iniciación de pagos. El servicio actúa como una **fachada REST** que internamente se comunica con un **servicio SOAP legacy**.

## Características Implementadas

### ✅ 1. API REST BIAN-Compliant
- **POST** `/payment-initiation/payment-orders` - Iniciar orden de pago (Initiate)
- **GET** `/payment-initiation/payment-orders/{id}` - Consultar orden de pago (Retrieve)  
- **GET** `/payment-initiation/payment-orders/{id}/status` - Consultar estado (Retrieve BQ)

### ✅ 2. Cliente SOAP
- Configuración completa de cliente SOAP con Spring Web Services
- Mapeo automático XML ↔ Java con JAXB
- Manejo de errores y logging
- Dos operaciones:
  - `SubmitPaymentOrder`: Enviar orden al sistema legacy
  - `GetPaymentOrderStatus`: Consultar estado en sistema legacy

### ✅ 3. Arquitectura en Capas
```
┌─────────────────────┐
│  REST Controllers   │ ← Endpoints REST
├─────────────────────┤
│  Service Layer      │ ← Lógica de negocio
├─────────────────────┤
│  SOAP Client        │ ← Comunicación con legacy
├─────────────────────┤
│  Repository         │ ← Almacenamiento en memoria
└─────────────────────┘
```

### ✅ 4. Modelos de Datos
- **DTOs REST**: Request/Response siguiendo nomenclatura BIAN
- **Entidades de Dominio**: Modelo interno (PaymentOrder)
- **Modelos SOAP**: Request/Response con anotaciones JAXB
- Mapeo bidireccional entre capas

### ✅ 5. Configuración y Seguridad
- Propiedades externalizadas en `application.properties`
- Validación de entrada con Jakarta Validation
- Manejo global de excepciones
- Logging estructurado (SLF4J)

### ✅ 6. Testing
- Mock SOAP interno para pruebas sin dependencias externas
- Test de integración end-to-end
- Colección Postman incluida
- Habilitación de mock por configuración

### ✅ 7. Documentación
- README.md completo con arquitectura y ejemplos
- QUICKSTART.md con guía paso a paso
- Comentarios Javadoc en componentes clave
- Ejemplos de uso con cURL

## Estructura del Proyecto

```
TestJava/
├── pom.xml                          # Configuración Maven + Spring Boot
├── README.md                        # Documentación principal
├── QUICKSTART.md                    # Guía de inicio rápido
├── src/
│   ├── main/
│   │   ├── java/com/test/trestproject/
│   │   │   ├── Main.java            # Application @SpringBootApplication
│   │   │   ├── controller/           # REST Controllers
│   │   │   │   ├── PaymentOrderController.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── service/              # Business Logic
│   │   │   │   ├── PaymentOrderService.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/           # Data Access
│   │   │   │   └── PaymentOrderRepository.java
│   │   │   ├── domain/               # Domain Entities
│   │   │   │   └── PaymentOrder.java
│   │   │   ├── dto/                  # REST DTOs
│   │   │   │   ├── PaymentOrderRequest.java
│   │   │   │   ├── PaymentOrderResponse.java
│   │   │   │   ├── PaymentOrderStatusResponse.java
│   │   │   │   ├── AccountReference.java
│   │   │   │   └── InstructedAmount.java
│   │   │   ├── soap/                 # SOAP Integration
│   │   │   │   ├── client/
│   │   │   │   │   └── PaymentOrderSoapClient.java
│   │   │   │   ├── config/
│   │   │   │   │   └── SoapClientConfig.java
│   │   │   │   └── model/
│   │   │   │       ├── SubmitPaymentOrderRequest.java
│   │   │   │       ├── SubmitPaymentOrderResponse.java
│   │   │   │       ├── GetPaymentOrderStatusRequest.java
│   │   │   │       └── GetPaymentOrderStatusResponse.java
│   │   │   └── mock/                 # Mock SOAP Service
│   │   │       ├── PaymentOrderSoapMockEndpoint.java
│   │   │       └── SoapMockConfig.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-mock.properties
│   │       └── wsdl/
│   │           └── PaymentOrderService.wsdl
│   └── test/
│       └── java/com/test/trestproject/
│           └── PaymentOrderIntegrationTest.java
└── .gitignore
```

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.1.5 | Framework de aplicación |
| Spring Web | 3.1.5 | REST API |
| Spring WS | 4.0.x | Cliente SOAP |
| Maven | 3.6+ | Gestión de dependencias |
| Lombok | 1.18.x | Reducción de boilerplate |
| JAXB | 4.0.x | Binding XML-Java |
| SLF4J + Logback | 2.0.x | Logging |
| JUnit 5 | 5.9.x | Testing |

## Flujo de Datos

### 1. Iniciar Orden de Pago
```
Cliente → POST /payment-initiation/payment-orders
         ↓
    PaymentOrderController
         ↓
    PaymentOrderService
         ↓ (mapeo REST → SOAP)
    PaymentOrderSoapClient
         ↓
    Servicio SOAP Legacy
         ↓ (respuesta)
    PaymentOrderService
         ↓ (guardar en repo)
    PaymentOrderRepository
         ↓ (mapeo entidad → DTO)
    PaymentOrderController
         ↓
    Cliente ← 201 Created + PaymentOrderResponse
```

### 2. Consultar Estado
```
Cliente → GET /payment-initiation/payment-orders/{id}/status
         ↓
    PaymentOrderController
         ↓
    PaymentOrderService
         ↓
    PaymentOrderSoapClient (GetPaymentOrderStatus)
         ↓
    Servicio SOAP Legacy
         ↓ (respuesta con estado actualizado)
    PaymentOrderService
         ↓ (actualizar repo)
    PaymentOrderRepository
         ↓
    Cliente ← 200 OK + PaymentOrderStatusResponse
```

## Decisiones de Diseño

### 1. **Patrón Repository**
Se usa un repositorio en memoria (`ConcurrentHashMap`) para:
- Simplificar la implementación sin base de datos
- Mantener caché local de órdenes creadas
- Facilitar testing

**Producción**: Migrar a JPA + base de datos relacional

### 2. **Mock SOAP Interno**
Se implementa un endpoint SOAP mock que:
- Permite testing sin dependencias externas
- Se activa/desactiva por configuración
- Simula respuestas del sistema legacy
- Usa el mismo contrato (WSDL)

### 3. **Separación de Modelos**
- **DTOs REST**: Para contratos de API pública
- **Entidades de Dominio**: Para lógica de negocio
- **Modelos SOAP**: Para integración legacy

Esto permite evolucionar cada capa independientemente.

### 4. **Manejo de Fechas**
- Entrada/Salida: String ISO-8601 (`"2025-10-31"`)
- Interno: `LocalDateTime` / `LocalDate`
- SOAP: String según WSDL (`xsd:date`, `xsd:dateTime`)

### 5. **Idempotencia**
La operación `POST` genera un ID único (`PO-XXXX`) desde el sistema legacy, garantizando que cada request crea una nueva orden.

## Cumplimiento BIAN

| Concepto BIAN | Implementación |
|--------------|----------------|
| **Service Domain** | Payment Initiation |
| **Control Record** | PaymentOrder |
| **Action: Initiate** | POST /payment-orders |
| **Action: Retrieve** | GET /payment-orders/{id} |
| **Behavior Qualifier** | Status |
| **Action: Retrieve BQ** | GET /payment-orders/{id}/status |
| **Asset Type** | PaymentOrder |

## Testing

### Ejecutar con Mock
```bash
# Opción 1: Cambiar application.properties
soap.mock.enabled=true

# Opción 2: Usar perfil
mvn spring-boot:run -Dspring-boot.run.profiles=mock
```

### Ejecutar Tests
```bash
mvn test
```

### Probar con Postman
1. Importar `postman_collection.json`
2. Ejecutar requests en orden
3. Verificar respuestas

## Próximos Pasos para Producción

1. **Persistencia**: Integrar JPA + PostgreSQL/MySQL
2. **Seguridad**: 
   - OAuth2 / JWT para autenticación
   - HTTPS obligatorio
   - Encriptación de datos sensibles
3. **Resiliencia**:
   - Circuit Breaker (Resilience4j)
   - Retry con backoff
   - Timeout configuration
4. **Observabilidad**:
   - Actuator endpoints
   - Prometheus metrics
   - Distributed tracing (Zipkin/Jaeger)
5. **Validaciones de Negocio**:
   - Validación de IBAN
   - Límites de montos
   - Business rules engine
6. **Documentación API**:
   - OpenAPI/Swagger
   - API versioning
7. **CI/CD**:
   - Pipeline automatizado
   - Docker containerization
   - Kubernetes deployment

## Conclusión

✅ **Solución completa y funcional** que cumple con todos los requisitos de la prueba técnica:

- ✅ REST API BIAN-compliant
- ✅ Integración con SOAP legacy
- ✅ Arquitectura limpia y mantenible
- ✅ Testing implementado
- ✅ Mock integrado para desarrollo
- ✅ Documentación completa
- ✅ Pronto para compilar y ejecutar

El proyecto está listo para demostración y puede extenderse fácilmente para producción siguiendo las recomendaciones indicadas.
