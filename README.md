# Proyecto Sistemas Distribuidos - Orquestador SOAP (ServerApp)

Este proyecto es la pieza central (Nodo Orquestador/API Gateway) diseñado para recibir cargas de trabajo a través de un estándar corporativo (SOAP) y derivarlas internamente hacia una red de microservicios externos usando peticiones HTTP limpias.

Está desarrollado en **Java 21**, potenciado por **Quarkus** para arranques ultra-rápidos y **Apache CXF** para el manejo limpio y robusto de las especificaciones XML-SOAP.

---

## 🏗️ Arquitectura del Proyecto (Hexagonal / Ports and Adapters)

Para garantizar que este sistema sobreviva a intercambios de infraestructura en el futuro, se ha implementado la **Arquitectura Hexagonal**. El sistema opera alrededor de cuatro dominios maestros de negocio: **Auth**, **User**, **Node**, y **BD**. Cada dominio sigue un esquema estricto de independencia estructural:

### 1. Puertos de Entrada (`endpoint`)
- Interceptan el mundo exterior a través del módulo de **Apache CXF**. Aquí habitan servicios como `AuthEndpoint` y `NodeEndpoint` anotados con `@WebService`. Su simple trabajo es rutear las envolturas SOAP y extraer cabeceras HTTP sin poseer conocimiento de lógica interna.

### 2. El Núcleo de la Aplicación (`service` y `port`)
- **`enfok.server.ports.port`**: Se declaran los orquestadores (Puertos). Es puramente abstracto.
- **`enfok.server.service`**: Aquí residen orquestadores (ej. `AuthOrchestatorService`) de la Capa de Negocio, que inyectan los repositorios de salida, validan excepciones personalizadas, hacen revisiones anti-nulos, y mantienen limpia la aplicación de vulnerabilidades antes de rebotar la petición real a la base de datos distribuida o Python/Go.

### 3. Los Adaptadores de Salida (`repository` y `adapter`)
- **`enfok.server.repository`**: Maneja la red cruda. Emplea el `HttpClient` de Java 11/21 para emitir las llamadas (POST/GET/PUT) RESTful a los microservicios auténticos dictados en las variables locales de nuestro entorno (`Config.java` / `.env`).

---

## ⚙️ Componentes Clave de Infraestructura

- 🛡️ **Interceptor de Seguridad Genérico (`TokenValidationInterceptor`)**: Un Firewall global pegado a CXF. Analiza las cabeceras `Authorization: Bearer <token>` de las peticiones protegidas. Si un cliente no provee un Token validado por el Microservicio de Auth temporalmente, Quarkus detiene el request al instante devolviendo *Connection Fault*.
- 🌐 **Validación Activa de Nodos (`RuntimeValidator`)**: Quarkus lanza una revisión de pulsos (`ping`) a todos los puertos de microservicios (.env) disponibles de forma asíncrona en la clase `Main` para alertarnos qué parte del clúster está colgada previniendo fallos tardíos en el entorno de desarrollo.
- ⚖️ **Balanceador de Carga Interno (`NodeLoadBalancer`)**: Determina un estado pseudo-Round-Robin consultando todos los nodos trabajadores de base de datos antes de adjudicar masivas conversiones de imagen.

---

## 🛠️ Modo MOCK (Desarrollo Local Offline)

Este proyecto incluye un entorno de Simulación (Mock Mode) de un interruptor para aquellos desarrolladores que no tengan levantados los contenedores paralelos de Python o bases de datos de identidad de Go.

**Cómo habilitarlo:** 
La variable está en `Config.java` como `MOCK_SERVICES = true` (Activado por defecto localmente). Bajo esta configuración, todos los repositorios saltarán el Firewall de HTTP sin excepciones, y las lecturas/consultas retornarán objetos `Dummy` en Java y textos preestablecidos. Podrás consumir endpoints en paz sin que tu App muera por interdependencia local.

---

## 🚀 Despliegue y Pruebas

### Correr en `Dev Mode`
Gracias al Live-Coding de **Quarkus**, si abres una terminal en `server-app/` y digitas:
```bash
./mvnw compile quarkus:dev
```
La aplicación se mantendrá fresca. Modificar el código de Java en VSCode se verá reflejado automáticamente (Hot-reload).

### Probar los Endpoints SOAP
Abre el archivo `endpoints_completos.rest` bajo la carpeta `server-app/` teniendo la extensión de **REST Client** de VSCode instalada. Encontrarás los XML ya estructurados (con un Token universal si estás en Mock Mode) listos para darle clic a `Send Request`.

(O si gustas explorar páginas WSDL generadas: `http://localhost:8080/services/auth?wsdl`).
