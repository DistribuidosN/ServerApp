# Proyecto Sistemas Distribuidos - Orquestador SOAP (ServerApp)

Este proyecto es la pieza central (Nodo Orquestador) diseñado para recibir cargas de trabajo pesadas (`Batches`) a través de un estándar corporativo (SOAP) y derivarlas internamente hacia una red de workers distribuidos (por ejemplo, nodos en Python o Go) usando gRPC.

Está desarrollado en **Java**, potenciado por **Quarkus** para arranques ultra-rápidos y **Apache CXF** para el manejo limpio y robusto de las especificaciones XML-SOAP.

---

## 🏗️ Arquitectura del Proyecto (Puertos y Adaptadores)

Para garantizar que este sistema sobreviva a cambios futuros (ej. si pasamos de una base de datos MySQL a Mongo, o si pasamos de gRPC a RabbitMQ), se ha implementado estrictamente la **Arquitectura Hexagonal**.

Todo el sistema está gobernado por abstracciones de la capa de Puertos (`port`):

### 1. El Núcleo de la Aplicación (`port` & `service`)
- **`enfok.server.port`**: Contiene la definición de **todas** nuestras reglas. Es puramente abstracto. No sabe qué base de datos existe ni qué protocolo de red usamos para mandar datos a Python. Agrupa Puertos de Entrada (`BatchOrchestratorService`) y Puertos de Salida (`BatchRepository`, `NodeGrpcClient`, `BatchMapper`).
- **`enfok.server.service` (El Responsable)**: Aquí vive el `BatchOrchestratorServiceImpl`, quien coordina la música. Inyecta los puertos de forma abstraída para recibir una petición, limpiarla con el Mapper, almacenar su historial en el Repository, e invocar el procesamiento remoto en gRPC.

### 2. Los Adaptadores de Entrada (Inbound Adapters)
- **`enfok.server.endpoint`**: Posee el `BatchEndpoint` anotado con `@WebService`. Es la máscara tecnológica HTTP. Su único trabajo es atajar el XML (SOAP Payload), desempaquetarlo a un DTO de Java y arrojárselo al Puerto de Entrada de nuestro Orquestador. No evalúa ninguna regla de negocio.

### 3. Los Adaptadores de Salida (Outbound Adapters)
Implementan las exigencias de nuestros puertos:
- **`enfok.server.repository`**: Actualmente contiene `BatchRepositoryImpl` que simula el almacenamiento transaccional en un Mapa de Memoria RAM.
- **`enfok.server.grpc`**: Contiene `NodeGrpcClientImpl`, responsable de aislar las complejidades de generar y enviar stubs de Protobuf a otro Servidor (Python).
- **`enfok.server.utility` (Mapper)**: Aísla la tarea sistemática de convertir DTOs "sucios" de red (`BatchRequestDto`) a Entidades de negocio confiables (`BatchRecord`).

---

## 🛠️ Tecnologías y Estructura

- **Quarkus**: Framework Supersonic Subatomic Java (optimizado para microservicios y Serverless).
- **Apache CXF (JAX-WS)**: Librería oficial de Quarkus para lidiar con el contrato de Web Services (`@WebService`, `@WebMethod`, `@WebFault`).
- **Inyección de Dependencias (CDI)**: Toda la conexión fluida entre interfaces e implementaciones ocurre automágicamente a través de `@ApplicationScoped` y `@Inject`.

### Manejo Centralizado de Errores
Se han configurado interceptores de CXF (`SoapFaultInterceptor`) y Excepciones nativas etiquetadas por el dominio (`BatchNotFoundException` mapeada con `@WebFault`) para garantizar que los desbordamientos de pila de Java no se filtren a la red, entregándose como formatos `SOAP Default Faults` legibles.

---

## 🚀 Correr la Aplicación Local (Modo Dev)

Gracias a Quarkus, puedes correr el servidor en "Live Coding Mode". Si editas cualquier clase Java y das Guardar, el servidor se auto-recargará detrás de escenas sin necesidad de reinicios.

1. Abre la terminal dentro de la carpeta `server-app`.
2. Ejecuta:
   ```bash
   ./mvnw compile quarkus:dev
   ```

*(Alternativamente, puedes simplemente darle clic al botón de "Play" desde el archivo `src/main/java/enfok/server/Main.java`).*

---

## 🧪 Cómo Probar la API

Una vez el servidor esté indicando que arrancó (el puerto por defecto es el `8080`), dispones de múltiples formas de probarlo:

1. **Revisar el Contrato WSDL Oficial (Navegador)**
   Ve a: http://localhost:8080/services/batches?wsdl y podrás leer el XML puro que describe las funciones y variables expuestas.
2. **Quarkus Dev UI**
   La consola ultra amigable de Quarkus: http://localhost:8080/q/dev-ui (Podrás seleccionar el recuadro de "Apache CXF" y mandar llamadas Web de prueba al endpoint fácilmente).
3. **Archivo Local (`prueba.rest`)**
   Puedes instalar la extensión *REST Client* en VS Code. Ve al archivo `prueba.rest` en la raíz del proyecto y haz clic en el botón `Send Request` flotante. Contiene un empaquetado `Envelope` de prueba perfecto y un escenario de Error Forzado (`BatchNotFoundException`).
