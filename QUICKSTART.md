# Guía de Inicio Rápido - Payment Initiation Service

## Opción 1: Ejecutar con Mock SOAP Interno (Recomendado para pruebas)

### 1. Habilitar el Mock SOAP
Crear archivo `src/main/resources/application-mock.properties`:
```properties
soap.mock.enabled=true
server.port=8080
```

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mock
```

O simplemente cambiar `soap.mock.enabled=true` en `application.properties`

### 3. Probar los endpoints
La aplicación estará disponible en `http://localhost:8080`

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

# Consultar orden (usar el paymentOrderId retornado)
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001

# Consultar estado
curl http://localhost:8080/payment-initiation/payment-orders/PO-0001/status
```

## Opción 2: Ejecutar con Servicio SOAP Real

### 1. Configurar URL del servicio SOAP
En `application.properties`:
```properties
soap.legacy.url=http://tu-servidor-soap:8081/legacy/payments
soap.mock.enabled=false
```

### 2. Ejecutar
```bash
mvn spring-boot:run
```

## Importar Colección Postman

1. Abrir Postman
2. Importar el archivo `postman_collection.json` (adjunto en la prueba)
3. Ejecutar las requests en orden:
   - POST Initiate PaymentOrder
   - GET Retrieve PaymentOrder
   - GET Retrieve PaymentOrder Status

## Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte
mvn clean test

# Ejecutar solo tests de integración
mvn test -Dtest=PaymentOrderIntegrationTest
```

## Verificar Compilación

```bash
mvn clean package
```

El JAR ejecutable estará en `target/TestJava-1.0-SNAPSHOT.jar`

## Ejecutar JAR

```bash
java -jar target/TestJava-1.0-SNAPSHOT.jar
```

Con mock habilitado:
```bash
java -jar target/TestJava-1.0-SNAPSHOT.jar --soap.mock.enabled=true
```

## Logs

Los logs se mostrarán en consola. Nivel DEBUG para:
- `com.test.trestproject`: Componentes de la aplicación
- `org.springframework.ws`: Cliente SOAP

## Troubleshooting

### Error: "Connection refused" al conectar SOAP
- Verificar que el servicio SOAP legacy esté corriendo
- O habilitar el mock: `soap.mock.enabled=true`

### Error: "Payment order not found"
- Verificar que el `paymentOrderId` existe
- Los datos se almacenan en memoria, se pierden al reiniciar

### Puerto 8080 en uso
Cambiar el puerto en `application.properties`:
```properties
server.port=8081
```
