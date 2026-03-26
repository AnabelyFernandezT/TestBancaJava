# Prompts Utilizados con IA para la Solución


## Prompt 1: Análisis Inicial y Configuración del Proyecto

**Respuesta (resumida):**
La IA analizó los requerimientos y propuso:
1. Crear un microservicio REST con Spring Boot 3.1.5
2. Implementar endpoints siguiendo BIAN (POST, GET, GET /status)
3. Integrar con servicio SOAP legacy usando Spring Web Services
4. Implementar arquitectura en capas (Controller → Service → SOAP Client → Repository)
5. Usar Java 17, Maven, Lombok para reducir boilerplate

---

## Prompt 2: Arquitectura y Estructura de Proyecto

**Acciones tomadas por la IA:**
1. Actualizó pom.xml con dependencias de Spring Boot, Web Services, JAXB, Lombok
2. Creó estructura de paquetes siguiendo Clean Architecture
3. Configuró plugin JAXB para generar clases desde WSDL

---

## Prompt 3: Implementación de DTOs y Modelos

Genera los estrustura de request, response y dto de los endponints

**Generación automática:**
La IA generó:
- `AccountReference.java` - Para representar cuentas bancarias (IBAN)
- `InstructedAmount.java` - Para monto e divisa
- `PaymentOrderRequest.java` - Request DTO con validaciones Jakarta
- `PaymentOrderResponse.java` - Response DTO completo
- `PaymentOrderStatusResponse.java` - DTO específico para consulta de estado

**Decisión clave:**
Separar modelos REST (DTOs) de modelos SOAP para desacoplamiento.

---

## Prompt 4: Cliente SOAP

**Generación:**
La IA creó:
1. Modelos SOAP con anotaciones JAXB (`@XmlRootElement`, `@XmlElement`)
2. `SoapClientConfig.java` - Configuración de `WebServiceTemplate`, `Jaxb2Marshaller`
3. `PaymentOrderSoapClient.java` - Cliente para invocar operaciones SOAP

**Patrones aplicados:**
- Configuración externalizada (URL del servicio en properties)
- Manejo de excepciones con logging
- Marshalling/Unmarshalling automático

---

## Prompt 5: Capa de Servicio y Lógica de Negocio

**Generación:**
- `PaymentOrderService.java` con tres métodos principales:
  - `initiatePaymentOrder()`: Mapea REST → SOAP, invoca cliente, guarda en repo
  - `retrievePaymentOrder()`: Consulta desde repositorio
  - `retrievePaymentOrderStatus()`: Consulta SOAP y actualiza repo

**Mapeo implementado:**
| Campo REST | Campo SOAP |
|-----------|-----------|
| externalReference | externalId |
| debtorAccount.iban | debtorIban |
| creditorAccount.iban | creditorIban |
| instructedAmount.amount | amount |

---

## Prompt 6: Controladores REST

**Generación:**
- `PaymentOrderController.java` con tres endpoints BIAN
- `GlobalExceptionHandler.java` para manejo centralizado de errores
- Validación automática con `@Valid`
- HTTP status codes correctos (201 Created, 200 OK, 404 Not Found)

---

## Prompt 7: Mock SOAP para Testing

Genera untesting para validar que la solucion y las respuestas implementadas sean correctas

**Prompt implícito:**
La IA implementó proactivamente un mock SOAP interno para facilitar testing.

**Generación:**
- `PaymentOrderSoapMockEndpoint.java` - Endpoint SOAP mock
- `SoapMockConfig.java` - Configuración condicional
- Se activa con `soap.mock.enabled=true`

---

