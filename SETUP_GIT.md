# 🚀 Guía para Subir el Repositorio y Crear Tag

## 📋 Pasos para Subir el Repositorio

### 1. Inicializar Git (si no está inicializado)

```bash
git init
```

### 2. Agregar el Remote

```bash
git remote add origin https://github.com/DonGeeo87/TimerFit-Pro.git
```

### 3. Agregar todos los archivos

```bash
git add .
```

### 4. Hacer el primer commit

```bash
git commit -m "feat: Initial commit - TimerFit Pro v1.0

- Timer profesional con persistencia de estado
- Catálogo de ejercicios con búsqueda y filtros
- Historial automático con Room Database
- Arquitectura MVVM + Clean Architecture
- Jetpack Compose + Material Design 3
- Sistema anti-duplicados
- Múltiples modos de temporizador (Fijo, HIIT, Count Up)
- Rutinas predefinidas"
```

### 5. Cambiar a rama main (si es necesario)

```bash
git branch -M main
```

### 6. Subir al repositorio

```bash
git push -u origin main
```

## 🏷️ Crear Tag v1.0

### Opción 1: Tag Anotado (Recomendado)

```bash
git tag -a v1.0 -m "Release v1.0 - TimerFit Pro

Características principales:
- Timer profesional con persistencia
- Catálogo de ejercicios
- Historial automático
- Arquitectura MVVM + Clean Architecture
- Jetpack Compose + Material Design 3"
```

### Opción 2: Tag Simple

```bash
git tag v1.0
```

### Subir el Tag

```bash
git push origin v1.0
```

### Subir todos los tags

```bash
git push origin --tags
```

## 📝 Comandos Útiles

### Ver tags existentes

```bash
git tag
```

### Ver información de un tag

```bash
git show v1.0
```

### Eliminar tag local

```bash
git tag -d v1.0
```

### Eliminar tag remoto

```bash
git push origin --delete v1.0
```

## 🎯 Checklist Antes de Subir

- [ ] ✅ README.md actualizado
- [ ] ✅ .gitignore configurado
- [ ] ✅ Código compilando sin errores
- [ ] ✅ Tests pasando (si los hay)
- [ ] ✅ Versión en build.gradle.kts correcta (1.0)
- [ ] ✅ No hay archivos sensibles (API keys, etc.)
- [ ] ✅ No hay archivos de build grandes

## 🔄 Actualizar Versión para Próximo Release

Cuando hagas un nuevo release, actualiza:

1. **build.gradle.kts**: `versionName = "1.1"` y `versionCode = 2`
2. **README.md**: Badge de versión
3. **Crear nuevo tag**: `git tag -a v1.1 -m "Release v1.1"`

