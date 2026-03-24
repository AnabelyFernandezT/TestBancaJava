# 🎓 Instrucciones para el Evaluador

## Bienvenido Evaluador 👋

Este documento le guiará para evaluar rápidamente todos los entregables de la prueba técnica.

---

## ⚡ Quick Evaluation (5 minutos)

### 1. Verificar Compilación y Calidad ✅
```bash
cd TestJava
mvn clean verify
```

**Resultado esperado**: `BUILD SUCCESS`

Esto ejecuta automáticamente:
- ✅ Compilación
- ✅ Tests unitarios
- ✅ Tests de integración
- ✅ Checkstyle
- ✅ SpotBugs
- ✅ JaCoCo (verifica cobertura >= 80%)

### 2. Ver Reporte de Cobertura 📊
```bash
# En Windows
start target\site\jacoco\index.html

# En Mac/Linux
open target/site/jacoco/index.html
```

**Verificar**: Cobertura >= 80% en líneas y branches

### 3. Ejecutar con Docker 🐳
```bash
docker-compose up -d
```

**Esperar 30 segundos** para que inicie, luego:

```bash
# Verificar health
curl http://localhost:8080/actuator/health

# Ver Swagger UI
curl http://localhost:8080/swagger-ui.html
# O abrir en navegador: http://localhost:8080/swagger-ui.html
```

### 4. Probar API REST 🌐
```bash
# Crear orden de pago
curl -X POST http://localhost:8080/payment-initiation/payment-orders \
  -H "Content-Type: application/json" \
  -d "{\"externalReference\":\"EXT-TEST\",\"debtorAccount\":{\"iban\":\"EC12DEBTOR\"},\"creditorAccount\":{\"iban\":\"EC98CREDITOR\"},\"instructedAmount\":{\"amount\":100.50,\"currency\":\"USD\"},\"remittanceInformation\":\"Pago de prueba\",\"requestedExecutionDate\":\"2025-12-31\"}"

# Copiar el paymentOrderId de la respuesta y consultar
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001

# Consultar estado
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001/status
```

### 5. Detener Docker
```bash
docker-compose down
```

---

## 📋 Checklist de Evaluación Detallada

### ✅ Entregable 1: Documentación
- [ ] README.md existe y está completo
- [ ] Incluye contexto y decisiones de migración
- [ ] Incluye pasos para ejecución local
- [ ] Incluye pasos para ejecución con Docker
- [ ] Incluye sección de uso de IA con prompts y respuestas

**Archivos a revisar**:
- `README.md`
- `QUICKSTART.md`
- `SOLUCION.md`
- `ENTREGABLES.md` (este archivo)

### ✅ Entregable 2: OpenAPI
- [ ] Archivo `openapi.yml` existe
- [ ] Contiene especificación OpenAPI 3.0
- [ ] Documenta los 3 endpoints
- [ ] Incluye schemas y ejemplos

**Verificar**:
```bash
cat openapi.yml | head -20
```

### ✅ Entregable 3: Pruebas y Cobertura
- [ ] Tests unitarios implementados
- [ ] Tests de integración implementados
- [ ] Tests E2E implementados
- [ ] Cobertura >= 80%

**Archivos a revisar**:
```
src/test/java/com/test/trestproject/
├── PaymentOrderIntegrationTest.java        # E2E
├── controller/PaymentOrderControllerTest.java    # Integration
├── service/PaymentOrderServiceTest.java          # Unit
├── repository/PaymentOrderRepositoryTest.java   # Unit
└── dto/PaymentOrderRequestValidationTest.java  # Unit
```

**Ejecutar tests**:
```bash
mvn test
mvn jacoco:report
```

**Ver reporte**: `target/site/jacoco/index.html`

### ✅ Entregable 4: Calidad
- [ ] Checkstyle configurado
- [ ] SpotBugs configurado
- [ ] `mvn verify` ejecuta sin fallos

**Verificar Checkstyle**:
```bash
mvn checkstyle:check
```

**Verificar SpotBugs**:
```bash
mvn spotbugs:check
```

**Archivos de configuración**:
- `checkstyle.xml`
- `checkstyle-suppressions.xml`
- `spotbugs-exclude.xml`

### ✅ Entregable 5: Docker
- [ ] Dockerfile existe
- [ ] docker-compose.yml existe
- [ ] Dockerfile usa multi-stage build
- [ ] docker-compose levanta servicios correctamente

**Archivos a revisar**:
- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`

**Probar**:
```bash
docker build -t payment-service:test .
docker-compose up -d
docker-compose ps
docker-compose logs payment-service
docker-compose down
```

### ✅ Entregable 6: Evidencia de IA
- [ ] Carpeta `ai/` existe
- [ ] `ai/prompts.md` contiene prompts utilizados
- [ ] `ai/decisions.md` contiene decisiones de diseño
- [ ] `ai/generations/` contiene ejemplos de código generado

**Archivos a revisar**:
```
ai/
├── prompts.md
├── decisions.md
└── generations/
    ├── README.md
    └── PaymentOrderController.java.example
```

---

## 🔍 Puntos Clave de Evaluación

### Arquitectura
✅ Implementa patrón de capas (Controller → Service → Client → Repository)
✅ Separación de DTOs REST, Domain y SOAP models
✅ Cliente SOAP con Spring Web Services
✅ Mock SOAP interno para testing

### Código
✅ Spring Boot 3.1.5 con Java 17
✅ Uso de Lombok para reducir boilerplate
✅ Validación con Jakarta Bean Validation
✅ Manejo centralizado de excepciones
✅ Logging apropiado

### Testing
✅ Tests unitarios de service, repository, DTOs
✅ Tests de integración de controllers
✅ Tests E2E con TestRestTemplate
✅ Cobertura >= 80%

### Calidad
✅ Checkstyle sin violaciones
✅ SpotBugs sin bugs detectados
✅ JaCoCo reporta cobertura
✅ `mvn verify` pasa exitosamente

### Docker
✅ Multi-stage build (image optimizada)
✅ Docker Compose funcional
✅ Health checks configurados
✅ Variables de entorno externalizadas

### Documentación
✅ README completo y profesional
✅ OpenAPI spec completo
✅ Guías de inicio rápido
✅ Evidencia de uso de IA

---

## 📊 Rúbrica de Evaluación Sugerida

| Criterio | Peso | Puntos | Notas |
|----------|------|--------|-------|
| **Funcionalidad** | 25% | | |
| - API REST funcional | 10% | | 3 endpoints BIAN |
| - Integración SOAP | 10% | | Cliente funcional |
| - Manejo de errores | 5% | | Global exception handler |
| **Calidad de Código** | 25% | | |
| - Checkstyle | 8% | | Sin violaciones |
| - SpotBugs | 8% | | Sin bugs |
| - Arquitectura | 9% | | Capas bien definidas |
| **Testing** | 20% | | |
| - Cobertura >= 80% | 10% | | JaCoCo report |
| - Tests unitarios | 5% | | Service, Repo, DTOs |
| - Tests integración | 5% | | Controller, E2E |
| **Docker** | 15% | | |
| - Dockerfile | 8% | | Multi-stage |
| - docker-compose | 7% | | Funcional |
| **Documentación** | 10% | | |
| - README completo | 5% | | Con todas las secciones |
| - OpenAPI | 5% | | Spec completa |
| **IA** | 5% | | |
| - Evidencia de prompts | 2.5% | | ai/prompts.md |
| - Decisiones documentadas | 2.5% | | ai/decisions.md |
| **TOTAL** | **100%** | | |

---

## 🚀 Tips para Evaluación Rápida

### Si tiene solo 10 minutos:
1. Ejecutar `mvn clean verify` (3 min)
2. Revisar reporte JaCoCo (2 min)
3. Ejecutar `docker-compose up -d` y probar un endpoint (3 min)
4. Revisar README.md (2 min)

### Si tiene 30 minutos:
1. Todo lo anterior (10 min)
2. Revisar código fuente (estructura, patrones) (10 min)
3. Revisar tests unitarios (5 min)
4. Revisar evidencia de IA (5 min)

### Si tiene 1 hora:
1. Todo lo anterior (30 min)
2. Revisar implementación SOAP client (10 min)
3. Revisar configuraciones (Checkstyle, SpotBugs, Docker) (10 min)
4. Probar más endpoints con Postman/Swagger (10 min)

---

## ❓ Preguntas Frecuentes

**P: ¿Por qué usa repositorio in-memory?**
R: Decision pragmática para MVP. El código está diseñado para migrar fácilmente a JPA (ver `ai/decisions.md`).

**P: ¿Cómo se garantiza que el código fue generado por IA?**
R: Carpeta `ai/` contiene evidencia completa (prompts, decisiones, fragmentos generados).

**P: ¿Por qué cobertura de 80% y no 100%?**
R: 80% es el estándar de industria que balancea calidad y esfuerzo. 100% incluiría código trivial (getters/setters).

**P: ¿El servicio SOAP debe estar corriendo?**
R: No. El proyecto incluye un mock SOAP interno activable con `soap.mock.enabled=true`.

**P: ¿Funciona en Java 11?**
R: No. Requiere Java 17 (uso de Jakarta EE, records, etc.).

---

## 📞 Contacto

Para preguntas o aclaraciones sobre el proyecto, revisar:
- README.md (documentación principal)
- SOLUCION.md (explicación detallada)
- ai/decisions.md (decisiones arquitectónicas)

---

**Fecha**: Marzo 2026
**Proyecto**: Payment Initiation Service - BIAN Compliant
**Estado**: ✅ LISTO PARA EVALUACIÓN
