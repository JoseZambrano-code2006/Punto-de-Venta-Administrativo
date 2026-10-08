# POS QSR — Frontend Angular

SPA Angular + Material para el sistema POS. Consume todas las APIs vía **api-gateway** (`http://localhost:4040`).

## Requisitos

- Node.js 20+
- Backend levantado (gateway 4040, ms-auth, pos-service, ms-clientes, Eureka, PostgreSQL)

## Desarrollo

```bash
npm install
npm start
```

Abrir `http://localhost:4200`

**Login:** `admin` / `admin123`

## Flujo típico

1. Iniciar sesión
2. **Caja** → abrir caja con una cuenta
3. **Punto de venta** → registrar ventas
4. **Órdenes** / **Reportes** → consultar ventas
5. **Movimientos** → ingresos/egresos de caja
6. **Caja** → cerrar caja

## Módulos

| Ruta | Funcionalidad |
|------|---------------|
| `/login` | Autenticación JWT |
| `/dashboard` | Resumen del día |
| `/pos` | Punto de venta (TPV) |
| `/orders` | Historial de órdenes |
| `/cash-box` | Apertura/cierre de caja |
| `/cash-movements` | Movimientos de caja |
| `/products` | CRUD productos |
| `/categories` | CRUD categorías |
| `/clients` | Clientes (ms-clientes) |
| `/accounts` | Cuentas de caja |
| `/reports` | Reportes por fecha |

## Build producción

```bash
npm run build
```

Artefactos en `dist/pos-frontend/`.
