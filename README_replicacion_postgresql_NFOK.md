# Guía de Replicación Lógica PostgreSQL - Proyecto NFOK

Este documento detalla la configuración necesaria para sincronizar datos en tiempo real entre un servidor Publicador (Origen) y un servidor Suscriptor (Réplica/Escucha).

---

# 🌍 Entorno de Red

- **Publicador (Host):** `10.152.164.57`
- **Suscriptor (Réplica):** `10.152.164.54`
- **Bases de Datos:** `auth_db`, `image_processing_db`
- **Usuario de Replicación:** `enfok`

---

# 1. Configuración del Servidor PUBLICADOR (10.152.164.57)

## A. Modificar archivos de configuración

Entrar a la carpeta de configuración PostgreSQL:

```bash
cd /etc/postgresql/16/main/
```

## Editar `postgresql.conf`

```bash
sudo nano postgresql.conf
```

Asegúrate de que estas líneas estén activas:

```conf
listen_addresses = '*'
wal_level = logical
max_replication_slots = 10
max_wal_senders = 10
```

## Editar `pg_hba.conf`

```bash
sudo nano pg_hba.conf
```

Agregar al final:

```conf
# Permite al Suscriptor entrar a las BDs
host    auth_db               enfok    10.152.164.54/32    md5
host    image_processing_db   enfok    10.152.164.54/32    md5

# Permitir replicación lógica
host    replication           enfok    10.152.164.54/32    md5
```

## Reiniciar PostgreSQL

```bash
sudo systemctl restart postgresql
```

---

# B. Configuración SQL (Publicador)

Entrar a PostgreSQL:

```bash
sudo -u postgres psql
```

## Crear usuario de replicación

```sql
CREATE USER enfok WITH PASSWORD 'enfok' REPLICATION;
```

Si el usuario ya existe:

```sql
ALTER USER enfok WITH REPLICATION;
```

## Crear publicaciones

### Base `auth_db`

```sql
\c auth_db

CREATE PUBLICATION pub_auth
FOR ALL TABLES;
```

### Base `image_processing_db`

```sql
\c image_processing_db

CREATE PUBLICATION pub_image
FOR ALL TABLES;
```

---

# 2. Configuración del Firewall (UFW)

## Eliminar bloqueos previos

```bash
sudo ufw delete deny 5432
```

## Permitir acceso al Suscriptor

```bash
sudo ufw allow from 10.152.164.54 to any port 5432 proto tcp
```

## Verificar reglas

```bash
sudo ufw status
```

---

# 3. Preparación del Servidor SUSCRIPTOR (10.152.164.54)

## Exportar esquemas desde el Publicador

```bash
sudo -u postgres pg_dump -s auth_db > auth_schema.sql

sudo -u postgres pg_dump -s image_processing_db > image_schema.sql
```

## Enviar archivos al Suscriptor

```bash
scp auth_schema.sql usuario@10.152.164.54:/home/usuario/

scp image_schema.sql usuario@10.152.164.54:/home/usuario/
```

## Crear bases de datos en el Suscriptor

```bash
sudo -u postgres psql -c "CREATE DATABASE auth_db;"

sudo -u postgres psql -c "CREATE DATABASE image_processing_db;"
```

## Importar esquemas

```bash
sudo -u postgres psql -d auth_db < auth_schema.sql

sudo -u postgres psql -d image_processing_db < image_schema.sql
```

---

# 4. Activar la Suscripción

## Suscripción para `auth_db`

```bash
sudo -u postgres psql -d auth_db
```

```sql
CREATE SUBSCRIPTION sub_auth
CONNECTION 'host=10.152.164.57 port=5432 user=enfok password=enfok dbname=auth_db'
PUBLICATION pub_auth;
```

## Suscripción para `image_processing_db`

```sql
\c image_processing_db

CREATE SUBSCRIPTION sub_image
CONNECTION 'host=10.152.164.57 port=5432 user=enfok password=enfok dbname=image_processing_db'
PUBLICATION pub_image;
```

---

# 5. Verificación y Mantenimiento

## Verificar en el Publicador

```sql
SELECT * FROM pg_stat_replication;
```

## Verificar en el Suscriptor

```sql
SELECT * FROM pg_stat_subscription;
```

La columna `active` debe aparecer en `t`.

---

# 6. Prueba de Replicación

## En el Publicador

```sql
INSERT INTO image_status(name, description)
VALUES ('TEST', 'Replica funcionando');
```

## En el Suscriptor

```sql
SELECT * FROM image_status;
```

El registro debe aparecer automáticamente.

---

# ⚠️ Notas Finales

## Problemas de autenticación

Si aparece:

```text
FATAL: password authentication failed
```

Revisa si debes usar:

```conf
scram-sha-256
```

en lugar de:

```conf
md5
```

en `pg_hba.conf`.

## Cambios estructurales

La replicación lógica NO replica cambios de estructura.

Si agregas columnas o tablas nuevas en el Publicador:

- Debes crearlas manualmente en el Suscriptor.
- De lo contrario, la replicación puede detenerse.

---

# ✅ Comandos útiles

## Ver publicaciones

```sql
SELECT * FROM pg_publication;
```

## Ver suscripciones

```sql
SELECT * FROM pg_subscription;
```

## Eliminar suscripción

```sql
DROP SUBSCRIPTION sub_auth;
```

## Eliminar publicación

```sql
DROP PUBLICATION pub_auth;
```
