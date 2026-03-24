# Decisiones de Diseño Tomadas con Ayuda de IA

## 1. Selección de Stack Tecnológico

### Decisión: Spring Boot 3.1.5 + Java 17
**Razón:**
- Compatibilidad con Jakarta EE (requerido para JAXB en Java 17+)
- Soporte LTS de Java 17
- Spring Boot 3.x es la versión estable actual
- Mejor integración con Spring Web Services

**Alternativas consideradas:**
- Spring Boot 2.x (descartado por deprecación próxima)
- Java 11 (descartado por falta de features modernas)

**Resultado:** Stack moderno y mantenible a largo plazo

---

## 2. Arquitectura en Capas vs Arquitectura Hexagonal

### Decisión: Arquitectura en Capas Simplificada
**Estructura:**
```
Controller → Service → SOAP Client
                ↓
           Repository
```

**Razón:**
- Para un microservicio de tamaño pequeño-mediano, capas simples son suficientes
- Facilita onboarding de nuevos desarrolladores
- Menor complejidad sin sacrificar mantenibilidad

**Alternativa considerada:**
- Hexagonal/Ports & Adapters (descartado por over-engineering para este caso)

**Resultado:** Código limpio y fácil de entender

---

## 3. Persistencia: In-Memory vs Base de Datos

### Decisión: Repository in-memory (ConcurrentHashMap)
**Razón:**
- Simplifica deployment inicial (no requiere BD externa)
- Suficiente para demo y prueba de concepto
- Facilita testing
- Permite cambio a BD real sin modificar Service Layer

**Implementación:**
```java
private final Map<String, PaymentOrder> storage = new ConcurrentHashMap<>();
```

**Path de migración futuro:**
- Cambiar implementación a JPA + PostgreSQL
- Mantener misma interfaz del Repository

**Resultado:** Implementación pragmática que no bloquea evolución

---

## 4. Modelos: DTO Separation vs Entidades Compartidas

### Decisión: Tres tipos de modelos separados
1. **DTOs REST** (`dto/` package) - Para API pública
2. **Entidades de Dominio** (`domain/` package) - Para lógica interna
3. **Modelos SOAP** (`soap/model/` package) - Para integración legacy

**Razón:**
- Desacoplamiento: cambios en SOAP no afectan API REST
- Flexibility: cada capa puede evolucionar independientemente
- Claridad: cada modelo tiene propósito específico

**Trade-off aceptado:**
- Más código (mapeo entre modelos)
- Beneficio: Mantenibilidad a largo plazo

**Resultado:** Alta cohesión, bajo acoplamiento

---

## 5. Manejo de Fechas: String vs LocalDate

### Decisión: String en API REST, LocalDateTime interno
**API REST:**
```json
{
  "requestedExecutionDate": "2025-10-31"
}
```

**Interno:**
```java
private LocalDateTime createdAt;
```

**Razón:**
- REST: String ISO-8601 es estándar en APIs REST
- Interno: LocalDateTime para cálculos y comparaciones
- SOAP: String según WSDL (xsd:date → String)

**Resultado:** Compatibilidad con estándares, tipo-safe internamente

---

## 6. Cliente SOAP: JAX-WS vs Spring WS

### Decisión: Spring Web Services
**Razón:**
- Integración nativa con Spring Boot
- Configuración declarativa (beans + @Configuration)
- Mejor manejo de excepciones
- WebServiceTemplate simplifica invocaciones

**Configuración:**
```java
@Bean
public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
    WebServiceTemplate template = new WebServiceTemplate();
    template.setMarshaller(marshaller);
    template.setUnmarshaller(marshaller);
    return template;
}
```

**Alternativa considerada:**
- JAX-WS con wsimport (más verboso, menor integración)

**Resultado:** Cliente SOAP elegante y testable

---

## 7. Testing: Mock Interno vs Mock Externo

### Decisión: Mock SOAP interno activable por configuración
**Implementación:**
```java
@ConditionalOnProperty(name = "soap.mock.enabled", havingValue = "true")
public class PaymentOrderSoapMockEndpoint { ... }
```

**Razón:**
- Testing sin dependencias externas
- CI/CD no requiere servicios auxiliares
- Mismo código que producción (solo config diferente)
- Facilita desarrollo local

**Activación:**
```properties
soap.mock.enabled=true
```

**Resultado:** Testing rápido y confiable

---

## 8. Validación: Bean Validation vs Validación Manual

### Decisión: Jakarta Bean Validation
**Implementación:**
```java
@NotNull
private String externalReference;

@Valid
private AccountReference debtorAccount;
```

**Razón:**
- Declarativo (menos código)
- Estándar de industria
- Integración automática con Spring MVC
- Mensajes de error consistentes

**Resultado:** Validación robusta con mínimo código

---

## 9. Manejo de Errores: Global vs Local

### Decisión: GlobalExceptionHandler con @RestControllerAdvice
**Implementación:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(...) { ... }
}
```

**Razón:**
- Centralización: un solo lugar para manejo de errores
- Consistencia: todos los endpoints retornan mismo formato
- DRY: evita try-catch duplicado en cada controller

**Resultado:** API con respuestas de error uniformes

---

## 10. Docker: Single vs Multi-Stage Build

### Decisión: Multi-stage Dockerfile
**Estructura:**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
# ... build stage ...

FROM eclipse-temurin:17-jre-alpine
# ... runtime stage ...
```

**Razón:**
- Imagen final más pequeña (solo JRE, no Maven/JDK completo)
- Build reproducible
- Mejor práctica de seguridad (menos superficie de ataque)

**Tamaño resultante:**
- Con multi-stage: ~200MB
- Sin multi-stage: ~600MB+

**Resultado:** Imagen optimizada para producción

---

## 11. Cobertura de Código: Umbral 70% vs 80%

### Decisión: Mínimo 80% de cobertura
**Configuración:**
```xml
<jacoco.coverage.minimum>0.80</jacoco.coverage.minimum>
```

**Razón:**
- Estándar de industria para proyectos críticos (finanzas)
- 80% es equilibrio entre calidad y esfuerzo
- Forza tests de caminos principales y casos edge

**Tipos de tests implementados:**
- Unitarios (Service, Repository, DTOs)
- Integración (Controller con MockMvc)
- E2E (con TestRestTemplate)

**Resultado:** Alta confianza en el código

---

## 12. Calidad: Solo Checkstyle vs Checkstyle + SpotBugs

### Decisión: Ambas herramientas
**Checkstyle:** Estilo y convenciones
**SpotBugs:** Bugs y problemas de seguridad

**Razón:**
- Complementarias (cubren aspectos diferentes)
- Requerido por la prueba técnica
- Mejoran calidad del código entregado

**Configuración:**
- Checkstyle: Google Style adaptado
- SpotBugs: Max effort, Low threshold

**Resultado:** Código con calidad profesional

---

## Resumen de Decisiones

| Aspecto | Decisión | Beneficio Principal |
|---------|----------|---------------------|
| Framework | Spring Boot 3.1.5 | Modernidad y soporte |
| Arquitectura | Capas simples | Claridad y mantenibilidad |
| Persistencia | In-memory | Simplicidad inicial |
| Modelos | Separados (DTO/Domain/SOAP) | Desacoplamiento |
| Cliente SOAP | Spring WS | Integración nativa |
| Testing | Mock interno | Independencia |
| Validación | Bean Validation | Declarativo |
| Errores | Global handler | Consistencia |
| Docker | Multi-stage | Imagen optimizada |
| Cobertura | 80% mínimo | Alta calidad |
| Calidad | Checkstyle + SpotBugs | Doble validación |

---

## Lecciones Aprendidas

1. **Pragmatismo sobre perfección**: In-memory repository es suficiente para inicio
2. **Separación de concerns**: DTOs separados facilitan evolución
3. **Testing primero**: Mock interno permite TDD efectivo
4. **Configuración externalizada**: Facilita diferentes ambientes
5. **Documentación como código**: OpenAPI generado automáticamente

---

