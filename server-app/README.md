# Enfok Server App - SOAP API Gateway & Load Balancer

Este proyecto es el n\u00FAcleo orquestador y la puerta de enlace (API Gateway) SOAP construida en Java 21 utilizando el framework **Quarkus** con **Apache CXF**. 

El sistema act\u00FAa como coordinador central en un ecosistema distribuido, comunic\u00E1ndose v\u00EDa peticiones REST con microservicios subyacentes encargados de Autenticaci\u00F3n, Bases de Datos, y Procesamiento Activo de im\u00E1genes.

## 🏗 Arquitectura del Sistema
El proyecto fue dise\u00F1ado bajo el patr\u00F3n de **Arquitectura Hexagonal (Ports & Adapters)** para garantizar un acoplamiento nulo entre los contratos SOAP expuestos y las l\u00F3gicas de infraestructura subyacente.

La arquitectura se divide en 3 capas principales por cada m\u00F3dulo (Auth, User, BD, Node):
1. **Endpoint (Driver):** Expone los servicios v\u00EDa SOAP (WSDL) (`AuthEndpoint.java`, etc.).
2. **Orchestrator Service (Puerto Interno):** Centraliza la l\u00F3gica de negocio y las comprobaciones defensivas de nulos o excepciones antes de rebotar la petici\u00F3n (`AuthOrchestatorService.java`).
3. **Repository (Adaptador):** Realiza las interacciones crudas, gestionando el cliente HTTP (`HttpClient`) para invocar a los microservicios externos (`AuthRepository.java`).

### 🛡 Interceptor de Seguridad Global
Se implement\u00F3 un Interceptor en Apache CXF (`TokenValidationInterceptor`) para la fase `PRE_PROTOCOL`. Esto significa que todas las peticiones a endpoints restringidos deben portar un Token en la cabecera `Authorization: Bearer <token>`, de lo contrario son abortados antes de llegar al cuerpo SOAP.

### 🌐 Validador de Red (RuntimeValidator)
Al iniciar Quarkus, el monitor de red (`RuntimeValidator` y `NetworkValidator`) autom\u00E1ticamente comprueba mediante pings `GET` la disponibilidad de los microservicios subyacentes mapeados en el `.env`. Si detecta un ciber-corte, la terminal alerta `[FALLO]` en los endpoints desconectados.

### ⚖ Modulo de Balanceo de Cargas (NodeLoadBalancer)
Para el env\u00EDo de im\u00E1genes masivas, el orquestador delega al `NodeLoadBalancer`, un algoritmo pseudo *Round-Robin* que consulta al Service Registry (`List<Node>`) y adjudica la carga al nodo adecuado repartiendo equitativamente el peso computacional de las conversiones de imagen.

### 🧪 Mock Mode (Desarrollo Local)
El proyecto cuenta con un conmutador de desarrollo local global en `Config.java` (`MOCK_SERVICES=true`). Cuando se encuentra activado, todos los repositorios y validadores de red interceptan las llamadas devolviendo objetos prefabricados (*Dummys*) e impidiendo ca\u00EDdas de sistema cuando se desarrolla y documenta la arquitectura sin requerir tener levantado Python, Go u otros lenguajes al mismo tiempo.

## 🚀 Despliegue y Compilaci\u00F3n

### Iniciar en MODO DESARROLLO (Hot-Reload)
\`\`\`bash
./mvnw compile quarkus:dev
\`\`\`

### Compilar a Artefacto Final
\`\`\`bash
./mvnw package
\`\`\`

## 📌 Variables de Entorno
Crea un archivo \`.env\` en el ra\u00EDz de la carpeta utilizando como gu\u00EDa los puertos configurados (o apunta a `localhost` en MockMode):
\`\`\`env
NODE=http://tu-nodo-procesamiento:5000
BD_AUTH=http://tu-nodo-auth:8080
BD_SYSTEM=http://tu-nodo-db:8080
\`\`\`
