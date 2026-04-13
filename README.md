# Proyecto Sistemas Distribuidos - Orquestador SOAP (ServerApp)

Este proyecto es la pieza central (Nodo Orquestador/API Gateway) dise\u00F1ado para recibir cargas de trabajo a trav\u00E9s de un est\u00E1ndar corporativo (SOAP) y derivarlas internamente hacia una red de microservicios externos usando peticiones HTTP limpias.

Est\u00E1 desarrollado en **Java 21**, potenciado por **Quarkus** para arranques ultra-r\u00E1pidos y **Apache CXF** para el manejo limpio y robusto de las especificaciones XML-SOAP.

---

## \ud83c\udfd7\ufe0f Arquitectura del Proyecto (Hexagonal / Ports and Adapters)

Para garantizar que este sistema sobreviva a intercambios de infraestructura en el futuro, se ha implementado la **Arquitectura Hexagonal**. El sistema opera alrededor de cuatro dominios maestros de negocio: **Auth**, **User**, **Node**, y **BD**. Cada dominio sigue un esquema estricto de independencia estructural:

### 1. Puertos de Entrada (`endpoint`)
- Interceptan el mundo exterior a trav\u00E9s del m\u00F3dulo de **Apache CXF**. Aqu\u00ED habitan servicios como `AuthEndpoint` y `NodeEndpoint` anotados con `@WebService`. Su simple trabajo es rutear las envolturas SOAP y extraer cabeceras HTTP sin poseer conocimiento de l\u00F3gica interna.

### 2. El N\u00FAcleo de la Aplicaci\u00F3n (`service` y `port`)
- **`enfok.server.ports.port`**: Se declaran los orquestadores (Puertos). Es puramente abstracto.
- **`enfok.server.service`**: Aqu\u00ED residen orquestadores (ej. `AuthOrchestatorService`) de la Capa de Negocio, que inyectan los repositorios de salida, validan excepciones personalizadas, hacen revisiones anti-nulos, y mantienen limpia la aplicaci\u00F3n de vulnerabilidades antes de rebotar la petici\u00F3n real a la base de datos distribuida o Python/Go.

### 3. Los Adaptadores de Salida (`repository` y `adapter`)
- **`enfok.server.repository`**: Maneja la red cruda. Emplea el `HttpClient` de Java 11/21 para emitir las llamadas (POST/GET/PUT) RESTful a los microservicios aut\u00E9nticos dictados en las variables locales de nuestro entorno (`Config.java` / `.env`).

---

## \u2699\ufe0f Componentes Clave de Infraestructura

- \ud83d\udee1\ufe0f **Interceptor de Seguridad Gen\u00E9rico (`TokenValidationInterceptor`)**: Un Firewall global pegado a CXF. Analiza las cabeceras `Authorization: Bearer <token>` de las peticiones protegidas. Si un cliente no provee un Token validado por el Microservicio de Auth temporalmente, Quarkus detiene el request al instante devolviendo *Connection Fault*.
- \ud83c\udf10 **Validaci\u00F3n Activa de Nodos (`RuntimeValidator`)**: Quarkus lanza una revisi\u00F3n de pulsos (`ping`) a todos los puertos de microservicios (.env) disponibles de forma as\u00EDncrona en la clase `Main` para alertarnos qu\u00E9 parte del cl\u00FAster est\u00E1 colgada previniendo fallos tard\u00EDos en el entorno de desarrollo.
- \u26aa\ufe0f **Balanceador de Carga Interno (`NodeLoadBalancer`)**: Determina un estado pseudo-Round-Robin consultando todos los nodos trabajadores de base de datos antes de adjudicar masivas conversiones de imagen.

---

## \ud83d\udee0\ufe0f Modo MOCK (Desarrollo Local Offline)

Este proyecto incluye un entorno de Simulaci\u00F3n (Mock Mode) de un interruptor para aquellos desarrolladores que no tengan levantados los contenedores paralelos de Python o bases de datos de identidad de Go.

**C\u00F3mo habilitarlo:** 
La variable est\u00E1 en `Config.java` como `MOCK_SERVICES = true` (Activado por defecto localmente). Bajo esta configuraci\u00F3n, todos los repositorios saltar\u00E1n el Firewall de HTTP sin excepciones, y las lecturas/consultas retornar\u00E1n objetos `Dummy` en Java y textos preestablecidos. Podr\u00E1s consumir endopints en paz sin que tu App muera por interdependencia local.

---

## \ud83d\ude80 Despliegue y Pruebas

### Correr en `Dev Mode`
Gracias al Live-Coding de **Quarkus**, si abres una terminal en `server-app/` y digitas:
\`\`\`bash
./mvnw compile quarkus:dev
\`\`\`
La aplicaci\u00F3n se mantendr\u00E1 fresca. Modificar el c\u00F3digo de Java en VSCode se ver\u00E1 reflejado autom\u00E1ticamente (Hot-reload).

### Probar los Endpoints SOAP
Abre el archivo `endpoints_completos.rest` bajo la carpeta `server-app/` teniendo la extensi\u00F3n de **REST Client** de VSCode instalada. Encontrar\u00E1s los XML ya estructurados (con un Token universal si est\u00E1s en Mock Mode) listos para darle clic a `Send Request`.

(O si gustas explorar p\u00E1ginas WSDL generadas: `http://localhost:8080/services/auth?wsdl`).
