# Script para Inicializar y Subir TimerFit Pro a GitHub
# Ejecutar: .\setup-repo.ps1

Write-Host "TimerFit Pro - Setup Git Repository" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si git esta instalado
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Git no esta instalado. Por favor instala Git primero." -ForegroundColor Red
    exit 1
}

# Verificar si ya es un repositorio git
if (Test-Path .git) {
    Write-Host "ADVERTENCIA: Ya es un repositorio git. Continuando..." -ForegroundColor Yellow
} else {
    Write-Host "Inicializando repositorio git..." -ForegroundColor Green
    git init
}

# Verificar si el remote ya existe
$remoteExists = git remote get-url origin 2>$null
if ($remoteExists) {
    Write-Host "ADVERTENCIA: Remote 'origin' ya existe: $remoteExists" -ForegroundColor Yellow
    $overwrite = Read-Host "Deseas cambiarlo? (s/n)"
    if ($overwrite -eq "s") {
        git remote set-url origin https://github.com/DonGeeo87/TimerFit-Pro.git
        Write-Host "OK: Remote actualizado" -ForegroundColor Green
    }
} else {
    Write-Host "Agregando remote origin..." -ForegroundColor Green
    git remote add origin https://github.com/DonGeeo87/TimerFit-Pro.git
}

# Agregar todos los archivos
Write-Host "Agregando archivos al staging..." -ForegroundColor Green
git add .

# Verificar si hay cambios para commit
$status = git status --porcelain
if ($status) {
    Write-Host "Creando commit inicial..." -ForegroundColor Green
    git commit -m "feat: Initial commit - TimerFit Pro v1.0

- Timer profesional con persistencia de estado
- Catalogo de ejercicios con busqueda y filtros
- Historial automatico con Room Database
- Arquitectura MVVM + Clean Architecture
- Jetpack Compose + Material Design 3
- Sistema anti-duplicados
- Multiples modos de temporizador (Fijo, HIIT, Count Up)
- Rutinas predefinidas"
    Write-Host "OK: Commit creado" -ForegroundColor Green
} else {
    Write-Host "ADVERTENCIA: No hay cambios para commit" -ForegroundColor Yellow
}

# Cambiar a rama main
Write-Host "Configurando rama main..." -ForegroundColor Green
git branch -M main

# Preguntar si desea subir
$push = Read-Host "Deseas subir al repositorio remoto? (s/n)"
if ($push -eq "s") {
    Write-Host "Subiendo al repositorio..." -ForegroundColor Green
    git push -u origin main
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "OK: Codigo subido exitosamente" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Error al subir. Verifica tus credenciales de GitHub." -ForegroundColor Red
        exit 1
    }
}

# Preguntar si desea crear tag
$tag = Read-Host "Deseas crear el tag v1.0? (s/n)"
if ($tag -eq "s") {
    Write-Host "Creando tag v1.0..." -ForegroundColor Green
    git tag -a v1.0 -m "Release v1.0 - TimerFit Pro

Caracteristicas principales:
- Timer profesional con persistencia
- Catalogo de ejercicios
- Historial automatico
- Arquitectura MVVM + Clean Architecture
- Jetpack Compose + Material Design 3"
    
    Write-Host "Subiendo tag..." -ForegroundColor Green
    git push origin v1.0
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "OK: Tag v1.0 creado y subido exitosamente" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Error al subir tag" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Proceso completado!" -ForegroundColor Cyan
Write-Host ""
Write-Host "Proximos pasos:" -ForegroundColor Yellow
Write-Host "   1. Verifica el repositorio en: https://github.com/DonGeeo87/TimerFit-Pro" -ForegroundColor White
Write-Host "   2. El tag v1.0 deberia estar visible en la seccion de releases" -ForegroundColor White
Write-Host ""
