# Docker — Backend POS

Orquesta bases de datos, Eureka, config-server, microservicios y api-gateway en un solo stack.

## Requisitos

- Docker Desktop
- Archivo `.env` con credenciales GitHub (para config-server)

## Arranque rápido

```powershell
cd ms
copy .env.example .env
# Editar .env: CONFIG_GIT_USERNAME, CONFIG_GIT_PASSWORD (PAT de GitHub)
docker compose up -d --build
```

Espera 2–3 minutos a que todos los servicios arranquen y se registren en Eureka.

## Verificación

```powershell
# Estado de contenedores
docker compose ps

# Eureka (opcional)
start http://localhost:8761

# Auth e2e
.\verify-auth-e2e.ps1
```

Login: `admin` / `admin123`  
Gateway: `http://localhost:4040`

## Servicios

| Contenedor | Puerto host | Descripción |
|---|---|---|
| pos-api-gateway | 4040 | Punto de entrada HTTP |
| pos-registry-server | 8761 | Eureka |
| pos-inventory-db | 5433 | PostgreSQL POS + auth |
| pos-clientes-db | 5434 | PostgreSQL clientes |
| pos-ms-auth | (interno) | Autenticación JWT |
| pos-service | (interno) | Ventas, productos, caja |
| pos-ms-clientes | (interno) | Clientes |
| pos-config-server | (interno) | Config centralizada (Git) |

## Variables de entorno (.env)

| Variable | Descripción |
|---|---|
| `CONFIG_GIT_USERNAME` | Usuario GitHub |
| `CONFIG_GIT_PASSWORD` | Personal Access Token con acceso al repo |
| `CONFIG_GIT_URI` | URL del repo (default: pos-service.git) |
| `APPLICATION_JWT_SECRET` | Secreto JWT para ms-auth |
| `CORS_ORIGIN` | Origen permitido para Angular (default: localhost:4200) |

## Config en GitHub

El config-server clona la carpeta `Config/` del repositorio remoto. Los archivos locales en [`../Config/`](../Config/) usan hostnames Docker (`db-inventory`, `db-clientes`).

**Importante:** sube los cambios de `Config/*.yml` al repositorio Git antes de levantar Docker en otro entorno:

```bash
git add Config/
git commit -m "Config Docker hostnames"
git push
```

Los perfiles `application-docker.yml` de cada microservicio también sobreescriben datasource/Eureka para funcionar sin depender solo del config-server.

## Frontend Angular

Con el stack levantado:

```bash
cd ../pos-frontend
npm start
```

Abre `http://localhost:4200` — el frontend apunta a `http://localhost:4040`.

## Comandos útiles

```powershell
docker compose logs -f api-gateway
docker compose logs -f ms-auth
docker compose down
docker compose down -v   # elimina volúmenes DB (re-seed)
```

## Notas

- `docker-compose-auth.yml` (MySQL) está **obsoleto** — ms-auth usa PostgreSQL (`db-inventory`).
- El primer `docker compose up --build` tarda varios minutos (6 builds Maven).
- Perfil Spring activo en contenedores: `docker`.
