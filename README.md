================================================================================
  POS SERVICE — Guía de instalación (Docker + Frontend opcional)
  Repositorio:[ https://github.com/dhamsilver/pos-service.git](https://github.com/JoseZambrano-code2006/Pos-Service-business-manager-Punto-de-Venta-Administrativo-.git)
  Rama recomendada: JorgeCabrera-docker
================================================================================

1. REQUISITOS PREVIOS
--------------------------------------------------------------------------------

  Software necesario:
  - Git
  - Docker Desktop (Windows/Mac) o Docker Engine + Compose (Linux)
  - (Opcional, para la interfaz web) Node.js 20+ y npm

  Recursos recomendados:
  - RAM: mínimo 8 GB (16 GB recomendado)
  - Disco: ~5 GB libres (imágenes Docker + builds Maven)
  - Puertos libres en el host:
      4040  → API Gateway (entrada principal)
      8761  → Eureka (opcional, depuración)
      5433  → PostgreSQL inventario/POS
      5434  → PostgreSQL clientes
      4200  → Frontend Angular (si lo levantas con npm)

  Cuenta GitHub:
  - Necesitas un Personal Access Token (PAT) con permiso de lectura al repo
    (scope: repo, o al menos acceso al repositorio privado si aplica).


2. OBTENER EL CÓDIGO
--------------------------------------------------------------------------------

  Windows (PowerShell):
    git clone (https://github.com/JoseZambrano-code2006/Pos-Service-business-manager-Punto-de-Venta-Administrativo-.git)
    cd pos-service
    git checkout JorgeCabrera-docker

  Linux / macOS:
    git clone (https://github.com/JoseZambrano-code2006/Pos-Service-business-manager-Punto-de-Venta-Administrativo-.git)
    cd pos-service
    git checkout JorgeCabrera-docker

  Estructura relevante:
    pos-service/
    ├── Config/          ← Config centralizada (también la usa config-server)
    ├── ms/              ← Backend dockerizado (AQUÍ se trabaja con Docker)
    └── pos-frontend/    ← Interfaz Angular (opcional)


3. CREAR TOKEN DE GITHUB (PAT)
--------------------------------------------------------------------------------

  1. Entra a GitHub → Settings → Developer settings → Personal access tokens
  2. Genera un token clásico o fine-grained con acceso de lectura al repo
     JoseZambrano-code2006/Pos-Service-business-manager-Punto-de-Venta-Administrativo
  3. Copia el token (empieza por ghp_...) — no lo compartas ni lo subas a Git

  IMPORTANTE:
  - El archivo ms/.env NO debe subirse al repositorio (contiene secretos).
  - Usa ms/.env.example como plantilla.


4. CONFIGURAR VARIABLES DE ENTORNO
--------------------------------------------------------------------------------

  Windows:
    cd ms
    copy .env.example .env

  Linux / macOS:
    cd ms
    cp .env.example .env

  Edita ms/.env y completa:

    CONFIG_GIT_URI=https://github.com/JoseZambrano-code2006/Pos-Service-business-manager-Punto-de-Venta-Administrativo.git
    CONFIG_GIT_USERNAME=tu_usuario_github
    CONFIG_GIT_PASSWORD=ghp_tu_personal_access_token
    APPLICATION_JWT_SECRET=12345678901234567890123456789012
    CORS_ORIGIN=http://localhost:4200

  Notas:
  - APPLICATION_JWT_SECRET debe tener al menos 32 caracteres.
  - CORS_ORIGIN debe coincidir con la URL del frontend (por defecto Angular).


5. LEVANTAR EL BACKEND CON DOCKER
--------------------------------------------------------------------------------

  Desde la carpeta ms/:

    docker compose up -d --build

  Primera ejecución:
  - Puede tardar 10–20 minutos (compila 6 microservicios con Maven).
  - Espera 2–3 minutos adicionales tras el build para que todo se registre.

  Contenedores que se levantan (8 en total):

    pos-inventory-db      PostgreSQL POS + auth        puerto 5433
    pos-clientes-db       PostgreSQL clientes          puerto 5434
    pos-registry-server   Eureka                       puerto 8761
    pos-config-server     Config Server (clona Git)
    pos-ms-auth           Autenticación JWT
    pos-service           Ventas, productos, caja
    pos-ms-clientes       Gestión de clientes
    pos-api-gateway       Gateway HTTP                 puerto 4040


6. VERIFICAR QUE TODO FUNCIONA
--------------------------------------------------------------------------------

  6.1 Estado de contenedores
      docker compose ps

      Todos deben estar "running". Las bases de datos deben estar "healthy".

  6.2 Eureka (opcional)
      Abrir en navegador: http://localhost:8761
      Deben aparecer registrados: config-server, ms-auth, pos-service,
      ms-clientes, api-gateway.

  6.3 Prueba de login manual (PowerShell)

      $body = '{"username":"admin","password":"admin123"}'
      Invoke-RestMethod -Method POST `
        -Uri "http://localhost:4040/auth-server/auth/login" `
        -ContentType "application/json" -Body $body

      Respuesta esperada: JSON con "accessToken".

  6.4 Script automático (Windows, desde ms/)

      .\verify-auth-e2e.ps1

      Verifica: login OK, 401 sin token, 200 con token en productos.

  6.5 Ver logs si algo falla

      docker compose logs -f api-gateway
      docker compose logs -f ms-auth
      docker compose logs -f config-server


7. CREDENCIALES POR DEFECTO
--------------------------------------------------------------------------------

  Usuario POS:  admin
  Contraseña:   admin123

  Gateway API:  http://localhost:4040

  Base de datos inventario (host):
    Host:     localhost
    Puerto:   5433
    DB:       pos_inventory
    Usuario:  servicepos
    Password: admin123

  Base de datos clientes (host):
    Host:     localhost
    Puerto:   5434
    DB:       clientes_db
    Usuario:  clientes-db
    Password: clientes123


8. LEVANTAR EL FRONTEND ANGULAR (OPCIONAL)
--------------------------------------------------------------------------------

  El frontend NO está dockerizado; se ejecuta con Node.js en el host.

  Desde la raíz del repo:

    cd pos-frontend
    npm install
    npm start

  Abrir: http://localhost:4200

  El frontend ya apunta al gateway en http://localhost:4040.
  Inicia sesión con admin / admin123.


9. COMANDOS ÚTILES
--------------------------------------------------------------------------------

  Detener servicios (conserva datos):
    docker compose down

  Detener y borrar volúmenes de BD (reinicia datos desde SQL seed):
    docker compose down -v

  Reconstruir un servicio específico:
    docker compose up -d --build api-gateway

  Ver uso de recursos:
    docker stats

  Reiniciar todo:
    docker compose down
    docker compose up -d --build


10. SOLUCIÓN DE PROBLEMAS
--------------------------------------------------------------------------------

  PROBLEMA: config-server no arranca / error al clonar Git
  CAUSA:    PAT inválido, expirado o sin permisos
  SOLUCIÓN: Regenera el PAT y actualiza ms/.env
            docker compose restart config-server

  PROBLEMA: microservicio no conecta a la base de datos
  CAUSA:    BD aún no está healthy
  SOLUCIÓN: Espera 1–2 min; revisa:
            docker compose logs db-inventory
            docker compose logs db-clientes

  PROBLEMA: gateway responde 502/503
  CAUSA:    Servicios aún registrándose en Eureka
  SOLUCIÓN: Espera 2–3 min; revisa http://localhost:8761

  PROBLEMA: CORS en el frontend
  CAUSA:    Origen distinto al configurado
  SOLUCIÓN: Ajusta CORS_ORIGIN en ms/.env y reinicia api-gateway:
            docker compose restart api-gateway

  PROBLEMA: puerto 4040 u otro ya en uso
  SOLUCIÓN: Cierra el proceso que lo usa o cambia el mapeo en docker-compose.yml

  PROBLEMA: build Maven muy lento o falla por memoria
  SOLUCIÓN: Asigna más RAM a Docker Desktop (Settings → Resources)
            Vuelve a ejecutar: docker compose up -d --build


11. RESUMEN RÁPIDO (COPIAR Y PEGAR)
--------------------------------------------------------------------------------

  git clone (https://github.com/dhamsilver/pos-service.git](https://github.com/JoseZambrano-code2006/Pos-Service-business-manager-Punto-de-Venta-Administrativo-.git)
  cd pos-service
  git checkout JorgeCabrera-docker
  cd ms
  copy .env.example .env          # Windows
  # cp .env.example .env          # Linux/Mac
  # Editar .env con PAT de GitHub
  docker compose up -d --build
  # Esperar 2–3 min
  # Probar: http://localhost:4040
  # Login: admin / admin123

  Frontend (opcional):
  cd ../pos-frontend
  npm install && npm start
  # http://localhost:4200


12. NOTAS IMPORTANTES
--------------------------------------------------------------------------------

  - Perfil Spring activo en contenedores: docker
  - docker-compose-auth.yml (MySQL) está OBSOLETO; no usarlo
  - ms/.env nunca debe commitearse (está en .gitignore)
  - El config-server lee la carpeta Config/ desde GitHub (rama main por defecto).
    Los perfiles application-docker.* en cada microservicio incluyen overrides
    locales para Eureka y bases de datos, por lo que el stack funciona aunque
    no hayas mergeado Config/ a main todavía.
  - Para producción: cambiar contraseñas por defecto, rotar JWT secret y PAT.

================================================================================
  Fin de la guía — POS Service Docker
================================================================================
