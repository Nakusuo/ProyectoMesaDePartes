# 📋 Registro de Cambios - Sistema de Autenticación JWT

## Fecha: 26 de octubre de 2025

---

## 🎯 Objetivo
Solucionar los problemas críticos del sistema de login y registro, implementando autenticación JWT completa con Spring Security y BCrypt.

---

## 📦 Archivos Creados

### 1. **JwtUtils.java** 
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/security/jwt/JwtUtils.java`

**Propósito:** Utilidad para generación y validación de tokens JWT

**Funcionalidades:**
- Generación de tokens JWT con firma HMAC-SHA
- Validación de tokens (expiración, firma, formato)
- Extracción del username desde el token
- Secret key en Base64 (configurado en `application.properties`)
- Tiempo de expiración: 8 horas (28800000 ms)

**Tecnologías:**
- `io.jsonwebtoken` (JJWT 0.12.6)
- Spring Security

---

### 2. **AuthTokenFilter.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/security/jwt/AuthTokenFilter.java`

**Propósito:** Filtro de Spring Security para interceptar y validar tokens JWT en cada petición

**Funcionalidades:**
- Extrae el token JWT del header `Authorization: Bearer <token>`
- Valida el token usando `JwtUtils`
- Carga los detalles del usuario desde `UserDetailsService`
- Establece la autenticación en el contexto de Spring Security
- Manejo de errores (token inválido, expirado, malformado)

**Flujo:**
1. Request → Filtro
2. Extrae token del header
3. Valida token
4. Carga usuario
5. Establece autenticación
6. Continúa con la petición

---

### 3. **AuthEntryPointJwt.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/security/jwt/AuthEntryPointJwt.java`

**Propósito:** Manejador de errores de autenticación no autorizada

**Funcionalidades:**
- Intercepta errores de autenticación (401 Unauthorized)
- Retorna respuesta JSON estructurada con el error
- Logging de intentos de acceso no autorizados
- Formato de respuesta:
  ```json
  {
    "status": 401,
    "error": "Unauthorized",
    "message": "mensaje del error",
    "path": "/api/endpoint"
  }
  ```

---

### 4. **UserDetailsImpl.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/security/services/UserDetailsImpl.java`

**Propósito:** Implementación de `UserDetails` de Spring Security

**Funcionalidades:**
- Adaptador entre modelo `Usuario` y Spring Security
- Mapeo de roles a authorities (`ROLE_` prefix)
- Gestión de estado de cuenta (activo/inactivo)
- Serializable para sesión
- Método builder estático para construcción

**Campos:**
- `idUsuario`: ID del usuario
- `username`: Nombre de usuario
- `email`: Email del usuario
- `password`: Hash de contraseña
- `authorities`: Lista de roles como `GrantedAuthority`

---

### 5. **UserDetailsServiceImpl.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/security/services/UserDetailsServiceImpl.java`

**Propósito:** Servicio para cargar usuarios desde la base de datos

**Funcionalidades:**
- Implementa `UserDetailsService` de Spring Security
- Carga usuario por username desde `UsuarioRepository`
- Lanza `UsernameNotFoundException` si no existe
- Usa carga EAGER de roles para evitar LazyInitializationException
- Convierte entidad `Usuario` a `UserDetailsImpl`

---

### 6. **UserInfoResponse.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/dto/UserInfoResponse.java`

**Propósito:** DTO para respuesta de login exitoso

**Estructura:**
```json
{
  "idUsuario": 1,
  "username": "nakusu",
  "email": "usuario@example.com",
  "roles": ["Administrador", "Mesa de Partes"],
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Uso:** Retornado por el endpoint `/api/auth/login`

---

### 7. **MessageResponse.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/dto/MessageResponse.java`

**Propósito:** DTO para respuestas simples de mensaje

**Estructura:**
```json
{
  "message": "Usuario registrado exitosamente!"
}
```

**Uso:** Respuestas de registro, errores, confirmaciones

---

### 8. **SignupRequest.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/dto/SignupRequest.java`

**Propósito:** DTO para petición de registro de usuario

**Validaciones:**
- `@NotBlank` en campos obligatorios (nombre, apellido, username, password, email)
- `@Email` para validación de formato de email
- `@Size` para longitud mínima de contraseña (6 caracteres)

**Campos:**
- Datos personales: nombre, apellido, teléfono, email
- Credenciales: username, password
- Configuración: tipoContrato, idArea
- Roles: lista de nombres de roles

---

### 9. **registro-usuario.html**
**Ruta:** `frontend/registro-usuario.html`

**Propósito:** Página HTML para registro de nuevos usuarios

**Características:**
- Formulario completo con todos los campos requeridos
- Validación frontend de campos obligatorios
- Select dinámico para tipo de contrato (CAS, LOCADOR, PNP)
- Select para área (cargado desde API)
- Checkboxes para selección múltiple de roles
- Diseño responsivo con estilos personalizados
- Mensajes de éxito y error
- Redirección automática tras registro exitoso

---

### 10. **registro.js**
**Ruta:** `frontend/assets/js/registro.js`

**Propósito:** Lógica JavaScript para registro de usuarios

**Funcionalidades:**
- Carga dinámica de áreas desde `/api/areas`
- Validación de formulario (campos vacíos, email válido)
- Validación de al menos un rol seleccionado
- Envío de petición POST a `/api/auth/registro`
- Manejo de respuestas exitosas y errores
- Deshabilitación de botón durante el registro
- Redirección a login tras 2 segundos de éxito

---

## 🔧 Archivos Modificados

### 1. **pom.xml**
**Cambios:**
- ✅ Agregada dependencia `jjwt-api` versión 0.12.6
- ✅ Agregada dependencia `jjwt-impl` versión 0.12.6 (runtime)
- ✅ Agregada dependencia `jjwt-jackson` versión 0.12.6 (runtime)

**Razón:** Soporte para generación y validación de tokens JWT

---

### 2. **SecurityConfig.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/config/SecurityConfig.java`

**Cambios realizados:**

#### ✅ Configuración de CORS
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:5500"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

#### ✅ Bean de BCryptPasswordEncoder
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

#### ✅ Bean de AuthenticationManager
```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
}
```

#### ✅ Bean de DaoAuthenticationProvider
```java
@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}
```

#### ✅ Configuración de SecurityFilterChain
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
            auth.requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/areas").permitAll()
                .anyRequest().authenticated()
        );

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

**Características:**
- CSRF deshabilitado (API RESTful stateless)
- CORS habilitado para desarrollo local
- Sesiones deshabilitadas (stateless JWT)
- Rutas públicas: `/api/auth/**` y `/api/areas`
- Todas las demás rutas requieren autenticación
- Filtro JWT agregado antes del filtro de autenticación estándar

---

### 3. **AuthController.java**
**Ruta:** `backend/src/main/java/com/pnp/mesadepartes/controller/AuthController.java`

**Cambios realizados:**

#### ✅ Inyección de dependencias
```java
@Autowired
AuthenticationManager authenticationManager;

@Autowired
UsuarioRepository usuarioRepository;

@Autowired
RolRepository rolRepository;

@Autowired
AreaRepository areaRepository;

@Autowired
PasswordEncoder encoder;

@Autowired
JwtUtils jwtUtils;
```

#### ✅ Endpoint de Login (POST /api/auth/login)
```java
@PostMapping("/login")
public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwt));
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Error de autenticación: " + e.getMessage()));
    }
}
```

**Funcionalidades:**
- Autentica usuario con Spring Security
- Genera token JWT tras autenticación exitosa
- Retorna información del usuario + token
- Manejo de credenciales incorrectas (401)

#### ✅ Endpoint de Registro (POST /api/auth/registro)
```java
@PostMapping("/registro")
public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    // Validación de username único
    if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
        return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: El username ya está en uso!"));
    }

    // Validación de email único
    if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: El email ya está en uso!"));
    }

    // Creación de usuario con contraseña hasheada
    Usuario usuario = new Usuario();
    usuario.setNombre(signUpRequest.getNombre());
    usuario.setApellido(signUpRequest.getApellido());
    usuario.setUsername(signUpRequest.getUsername());
    usuario.setEmail(signUpRequest.getEmail());
    usuario.setTelefono(signUpRequest.getTelefono());
    usuario.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
    
    // Validación y asignación de tipo de contrato
    try {
        usuario.setTipoContrato(TipoContrato.valueOf(signUpRequest.getTipoContrato()));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Tipo de contrato inválido"));
    }

    // Asignación de área
    Area area = areaRepository.findById(signUpRequest.getIdArea())
            .orElseThrow(() -> new RuntimeException("Error: Área no encontrada"));
    usuario.setArea(area);

    // Asignación de roles
    Set<Rol> roles = new HashSet<>();
    if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
        for (String roleName : signUpRequest.getRoles()) {
            Rol rol = rolRepository.findByNombreRol(roleName)
                    .orElseThrow(() -> new RuntimeException("Error: Rol " + roleName + " no encontrado"));
            roles.add(rol);
        }
    }

    usuario.setRoles(roles);
    usuario.setActivo(true);
    usuarioRepository.save(usuario);

    return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
}
```

**Validaciones:**
- Username único
- Email único
- Tipo de contrato válido
- Área existente
- Roles válidos
- Encriptación automática de contraseña con BCrypt

---

### 4. **application.properties**
**Ruta:** `backend/src/main/resources/application.properties`

**Cambios realizados:**

#### ✅ Configuración de JWT
```properties
# JWT Configuration
app.jwt.secret=dGhpc0lzQVNlY3JldEtleUZvckpXVFRva2VuR2VuZXJhdGlvbkFuZFZhbGlkYXRpb25JblNwcmluZ0Jvb3Q=
app.jwt.expirationMs=28800000
```

**Valores:**
- `app.jwt.secret`: Clave secreta en Base64 para firma HMAC-SHA
- `app.jwt.expirationMs`: 28800000 ms = 8 horas

#### ✅ Corrección de credenciales de MySQL
```properties
spring.datasource.password=
```

**Razón:** MySQL configurado sin contraseña (root user sin password)

---

### 5. **login.js**
**Ruta:** `frontend/assets/js/login.js`

**Cambios realizados:**

#### ✅ Corrección de rutas de redirección
```javascript
// ANTES:
let redirectTo = '../admin/dashboard.html';
if (data.roles.includes('Trabajador')) {
    redirectTo = '../trabajador/mis-asignaciones.html';
} else if (data.roles.includes('Mesa de Partes')) {
    redirectTo = '../documentos/registro.html';
}

// DESPUÉS:
let redirectTo = 'bitacora.html'; // Dashboard por defecto (Administrador)

if (data.roles.includes('Trabajador')) {
    redirectTo = 'bitacora.html'; // Por ahora todos van al mismo dashboard
} else if (data.roles.includes('Mesa de Partes')) {
    redirectTo = 'registro.html'; // Página de registro de documentos
}
```

**Razón:** Las carpetas `admin/`, `trabajador/`, `documentos/` no existen. Se corrigieron las rutas para apuntar a archivos en la raíz del frontend.

---

## 🗄️ Cambios en Base de Datos

### 1. **Actualización de contraseñas a BCrypt**

**SQL ejecutado:**
```sql
UPDATE usuarios 
SET password_hash = '$2a$10$TeRi1ObzzIK/0DNKLijApu9SZA7l1BgybNUMGpMW/H4hAasC7aHC6' 
WHERE username IN ('nakusu','mdepaz','ecisneros','accori','jchiclla','ghuaman','osuarez');
```

**Resultado:**
- 7 usuarios actualizados
- Contraseña para todos: `12345`
- Hash generado con BCryptPasswordEncoder de Spring Security

**Usuarios afectados:**
1. nakusu (Administrador)
2. mdepaz (Mesa de Partes)
3. ecisneros (Trabajador)
4. accori (Trabajador)
5. jchiclla (Trabajador)
6. ghuaman (Trabajador)
7. osuarez (Trabajador)

---

## 📊 Estructura del Sistema de Autenticación

### Flujo de Login

```
1. Usuario ingresa credenciales en login.html
   ↓
2. login.js envía POST a /api/auth/login
   ↓
3. AuthController recibe petición
   ↓
4. AuthenticationManager autentica con UserDetailsService
   ↓
5. UserDetailsServiceImpl carga usuario desde BD
   ↓
6. BCryptPasswordEncoder compara contraseñas
   ↓
7. Si es válido: JwtUtils genera token JWT
   ↓
8. Se retorna UserInfoResponse con token
   ↓
9. Frontend guarda token en localStorage
   ↓
10. Redirección a dashboard (bitacora.html)
```

### Flujo de Peticiones Autenticadas

```
1. Frontend incluye token en header: Authorization: Bearer <token>
   ↓
2. AuthTokenFilter intercepta petición
   ↓
3. Extrae y valida token con JwtUtils
   ↓
4. Si es válido: Carga usuario y establece autenticación
   ↓
5. Spring Security permite acceso al recurso
   ↓
6. Controller procesa petición
   ↓
7. Retorna respuesta
```

### Flujo de Registro

```
1. Usuario completa formulario en registro-usuario.html
   ↓
2. registro.js envía POST a /api/auth/registro
   ↓
3. AuthController valida datos
   ↓
4. Verifica username y email únicos
   ↓
5. Valida tipo de contrato, área y roles
   ↓
6. Encripta contraseña con BCrypt
   ↓
7. Guarda usuario en BD
   ↓
8. Retorna mensaje de éxito
   ↓
9. Frontend redirige a login
```

---

## 🔐 Configuración de Seguridad

### Endpoints Públicos (Sin autenticación)
- `POST /api/auth/login` - Inicio de sesión
- `POST /api/auth/registro` - Registro de usuarios
- `GET /api/areas` - Listado de áreas (para formulario de registro)

### Endpoints Protegidos (Requieren JWT)
- Todos los demás endpoints del sistema
- Validación automática mediante `AuthTokenFilter`

### Configuración de CORS
- Orígenes permitidos: `http://localhost:3000`, `http://127.0.0.1:5500`
- Métodos: GET, POST, PUT, DELETE, OPTIONS
- Headers: Todos permitidos
- Credentials: Habilitados

---

## 🧪 Credenciales de Prueba

### Usuario Administrador
- **Username:** nakusu
- **Contraseña:** 12345
- **Rol:** Administrador
- **Estado:** ✅ Verificado funcionando

### Usuario Mesa de Partes
- **Username:** mdepaz
- **Contraseña:** 12345
- **Rol:** Mesa de Partes

### Usuarios Trabajadores
- **Username:** ecisneros, accori, jchiclla, ghuaman, osuarez
- **Contraseña:** 12345
- **Rol:** Trabajador

---

## ✅ Verificaciones Realizadas

### Backend
- [x] Aplicación inicia sin errores
- [x] Conexión a MySQL exitosa
- [x] Endpoints de autenticación responden correctamente
- [x] Tokens JWT se generan correctamente
- [x] Validación de tokens funciona
- [x] BCrypt valida contraseñas correctamente

### Frontend
- [x] Formulario de login funciona
- [x] Token se guarda en localStorage
- [x] Redirección a dashboard exitosa
- [x] Formulario de registro carga áreas dinámicamente
- [x] Validaciones frontend operativas

### Base de Datos
- [x] Contraseñas actualizadas a BCrypt
- [x] Formato de hash correcto ($2a$10$...)
- [x] Usuarios pueden autenticarse

---

## 📝 Notas Importantes

### Seguridad
1. **Secret Key JWT:** Actualmente en `application.properties`, en producción debería estar en variables de entorno
2. **CORS:** Configurado para desarrollo local, ajustar para producción
3. **HTTPS:** En producción, todos los endpoints deben usar HTTPS
4. **Rotación de Tokens:** Implementar refresh tokens para sesiones largas

### Performance
1. **Carga de Roles:** Usando EAGER fetch para evitar LazyInitializationException
2. **Token en cada petición:** El filtro se ejecuta en cada request

### Mantenimiento
1. **Endpoint temporal eliminado:** `/api/auth/generar-hash` fue removido del código
2. **Logs:** AuthEntryPointJwt registra intentos de acceso no autorizados
3. **Validaciones:** Tanto en frontend como backend

---

## 🚀 Estado Final

### ✅ Completado
- Sistema de autenticación JWT completamente funcional
- Login y registro operativos
- Encriptación BCrypt implementada
- Seguridad Spring Security configurada
- Frontend conectado correctamente

### 🔄 En Progreso
- Completar páginas del dashboard
- Implementar funcionalidad de gestión de documentos
- Agregar más validaciones de roles

### 📋 Por Hacer
- Implementar logout (limpiar localStorage)
- Agregar refresh tokens
- Proteger rutas del frontend con verificación de token
- Implementar cambio de contraseña
- Agregar recuperación de contraseña
- Tests unitarios y de integración
- Documentación de API con Swagger

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot** 3.5.6
- **Spring Security** 6.2.11
- **JWT** (JJWT 0.12.6)
- **BCrypt** (Spring Security Crypto)
- **MySQL** 8.x
- **JPA/Hibernate**
- **Maven**

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript (ES6+)**
- **Fetch API**

---

## 📞 Soporte y Documentación

Para más información sobre las tecnologías utilizadas:
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [BCrypt Algorithm](https://en.wikipedia.org/wiki/Bcrypt)

---

**Documento generado el:** 26 de octubre de 2025
**Autor:** GitHub Copilot
**Proyecto:** Sistema Mesa de Partes - PNP
