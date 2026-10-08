# Verificación end-to-end del flujo de auth (requiere servicios levantados)
# Uso: .\verify-auth-e2e.ps1

$ErrorActionPreference = "Stop"
$gateway = "http://localhost:4040"
$loginBody = @{ username = "admin"; password = "admin123" } | ConvertTo-Json

Write-Host "1. Login via gateway..."
$loginResponse = Invoke-RestMethod -Method POST -Uri "$gateway/auth-server/auth/login" `
    -ContentType "application/json" -Body $loginBody
$token = $loginResponse.accessToken
if (-not $token) { throw "Login no devolvió accessToken" }
Write-Host "   OK - token recibido"

Write-Host "2. Acceso protegido SIN token (esperado 401)..."
try {
    Invoke-WebRequest -Method GET -Uri "$gateway/pos-venta-service/product" -ErrorAction Stop | Out-Null
    throw "Se esperaba 401 sin token"
} catch {
    if ($_.Exception.Response.StatusCode.value__ -ne 401) { throw }
    Write-Host "   OK - 401 Unauthorized"
}

Write-Host "3. Acceso protegido CON token..."
$headers = @{ Authorization = "Bearer $token" }
$products = Invoke-RestMethod -Method GET -Uri "$gateway/pos-venta-service/product" -Headers $headers
Write-Host "   OK - respuesta recibida ($($products.Count) productos)"

Write-Host "`nVerificación e2e completada."
