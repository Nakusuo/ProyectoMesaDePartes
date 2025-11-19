-- =====================================================
-- BASE DE DATOS MESA DE PARTES PNP - VERSIÓN COMPLETA Y ACTUALIZADA
-- Fecha: 19 de Noviembre de 2025
-- Incluye: Derivaciones, Notificaciones, Trazabilidad, Reportes y BITÁCORA UNIFICADA
-- IMPORTANTE: Bitácora ahora tiene un solo registro por documento con entrada y salida
-- =====================================================

DROP DATABASE IF EXISTS mesa_partes_db;
CREATE DATABASE mesa_partes_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mesa_partes_db;

-- =====================================================
-- TABLA: ÁREAS (Departamentos PNP y Áreas de Trabajo)
-- =====================================================
CREATE TABLE areas (
    ID_area INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    sigla VARCHAR(20) UNIQUE,
    tipo ENUM('DEPARTAMENTO_PNP','AREA_TRABAJO') DEFAULT 'DEPARTAMENTO_PNP' COMMENT 'DEPARTAMENTO_PNP: Áreas oficiales de la PNP (para documentos), AREA_TRABAJO: Áreas de trabajo del sistema (para usuarios)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: ROLES
-- =====================================================
CREATE TABLE roles (
    ID_rol INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: USUARIOS
-- =====================================================
CREATE TABLE usuarios (
    ID_usuario INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tipo_contrato ENUM('CAS','LOCADOR','PNP') NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) UNIQUE,
    email VARCHAR(150) UNIQUE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    ID_area INT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_area) REFERENCES areas(ID_area),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_activo (activo),
    INDEX idx_area (ID_area)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: RELACIÓN USUARIOS-ROLES
-- =====================================================
CREATE TABLE usuario_roles (
    ID_usuario_rol INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_usuario INT UNSIGNED NOT NULL,
    ID_rol INT UNSIGNED NOT NULL,
    FOREIGN KEY (ID_usuario) REFERENCES usuarios(ID_usuario) ON DELETE CASCADE,
    FOREIGN KEY (ID_rol) REFERENCES roles(ID_rol) ON DELETE CASCADE,
    UNIQUE KEY unique_usuario_rol (ID_usuario, ID_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: TIPOS DE DOCUMENTO
-- =====================================================
CREATE TABLE tipos_documento (
    ID_tipo_documento INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: DOCUMENTOS (CON NUEVOS ESTADOS)
-- =====================================================
CREATE TABLE documentos (
    ID_documento INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    numero_documento VARCHAR(100),
    estado ENUM('Asignado','Recibido','En_Proceso','Observado','Finalizado','Salida') DEFAULT 'Asignado' COMMENT 'Asignado: Registrado y asignado | Recibido: Usuario lo vio | En_Proceso: Procesando | Observado: Con observaciones | Finalizado: Completo con informe | Salida: Salió del sistema',
    remitente VARCHAR(200) NOT NULL,
    destinatario VARCHAR(200),
    fecha_ingreso DATETIME NOT NULL,
    archivo_url VARCHAR(255),
    ID_usuario_registro INT UNSIGNED,
    ID_tipo_documento INT UNSIGNED NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_usuario_registro) REFERENCES usuarios(ID_usuario) ON DELETE SET NULL,
    FOREIGN KEY (ID_tipo_documento) REFERENCES tipos_documento(ID_tipo_documento),
    INDEX idx_codigo (codigo),
    INDEX idx_estado (estado),
    INDEX idx_fecha_ingreso (fecha_ingreso),
    INDEX idx_usuario_registro (ID_usuario_registro),
    INDEX idx_tipo_documento (ID_tipo_documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: HOJAS DE TRÁMITE
-- =====================================================
CREATE TABLE hojas_tramite (
    ID_hoja_tramite INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    numero_ht VARCHAR(50),
    ID_documento INT UNSIGNED,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE CASCADE,
    INDEX idx_documento (ID_documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: TRÁMITES (ASIGNACIÓN DE DOCUMENTOS)
-- =====================================================
CREATE TABLE tramites (
    ID_tramite INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_documento INT UNSIGNED,
    ID_usuario_creador INT UNSIGNED,
    ID_usuario_asignado INT UNSIGNED,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE CASCADE,
    FOREIGN KEY (ID_usuario_creador) REFERENCES usuarios(ID_usuario) ON DELETE SET NULL,
    FOREIGN KEY (ID_usuario_asignado) REFERENCES usuarios(ID_usuario) ON DELETE SET NULL,
    INDEX idx_documento (ID_documento),
    INDEX idx_usuario_asignado (ID_usuario_asignado),
    INDEX idx_usuario_creador (ID_usuario_creador)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: DERIVACIONES (Para RF2 - Derivar documentos)
-- =====================================================
CREATE TABLE derivaciones (
    ID_derivacion INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_documento INT UNSIGNED NOT NULL,
    ID_area_origen INT UNSIGNED,
    ID_area_destino INT UNSIGNED NOT NULL,
    ID_usuario_deriva INT UNSIGNED NOT NULL,
    ID_usuario_recibe INT UNSIGNED,
    fecha_derivacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_recepcion DATETIME,
    observaciones TEXT,
    estado VARCHAR(20) DEFAULT 'PENDIENTE' COMMENT 'PENDIENTE, RECIBIDO, RECHAZADO',
    prioridad VARCHAR(20) DEFAULT 'NORMAL' COMMENT 'BAJA, NORMAL, ALTA, URGENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE CASCADE,
    FOREIGN KEY (ID_area_origen) REFERENCES areas(ID_area) ON DELETE SET NULL,
    FOREIGN KEY (ID_area_destino) REFERENCES areas(ID_area) ON DELETE CASCADE,
    FOREIGN KEY (ID_usuario_deriva) REFERENCES usuarios(ID_usuario) ON DELETE CASCADE,
    FOREIGN KEY (ID_usuario_recibe) REFERENCES usuarios(ID_usuario) ON DELETE SET NULL,
    INDEX idx_documento (ID_documento),
    INDEX idx_area_destino (ID_area_destino),
    INDEX idx_estado (estado),
    INDEX idx_fecha_derivacion (fecha_derivacion),
    INDEX idx_prioridad (prioridad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: NOTIFICACIONES (Para RF6 - Sistema de notificaciones)
-- =====================================================
CREATE TABLE notificaciones (
    ID_notificacion INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_usuario INT UNSIGNED NOT NULL,
    ID_documento INT UNSIGNED,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL COMMENT 'REGISTRO, DERIVACION, CAMBIO_ESTADO, ASIGNACION',
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_lectura DATETIME,
    url VARCHAR(255) COMMENT 'URL para redirigir al hacer clic',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_usuario) REFERENCES usuarios(ID_usuario) ON DELETE CASCADE,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE SET NULL,
    INDEX idx_usuario (ID_usuario),
    INDEX idx_leida (leida),
    INDEX idx_fecha_creacion (fecha_creacion),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: SALIDAS DE DOCUMENTOS
-- =====================================================
CREATE TABLE salidas_documento (
    ID_salida_documento INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_documento INT UNSIGNED NOT NULL,
    ID_tipo_documento INT UNSIGNED,
    numero_documento_salida VARCHAR(100),
    destinatario_salida VARCHAR(200),
    ID_usuario_salida INT UNSIGNED,
    fecha_salida DATETIME,
    observacion TEXT,
    archivo_cargo_url VARCHAR(255),
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE CASCADE,
    FOREIGN KEY (ID_usuario_salida) REFERENCES usuarios(ID_usuario) ON DELETE SET NULL,
    FOREIGN KEY (ID_tipo_documento) REFERENCES tipos_documento(ID_tipo_documento),
    INDEX idx_documento (ID_documento),
    INDEX idx_fecha_salida (fecha_salida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: BITÁCORA UNIFICADA (Para auditoría completa)
-- NUEVO: Un solo registro por documento con entrada Y salida
-- =====================================================
CREATE TABLE bitacora (
    ID_bitacora BIGINT PRIMARY KEY AUTO_INCREMENT,
    ID_documento INT UNSIGNED NOT NULL UNIQUE,
    codigo_documento VARCHAR(50) NOT NULL,
    titulo_documento VARCHAR(200) NOT NULL,
    tipo_documento VARCHAR(100),
    
    -- Datos de ENTRADA
    tiene_entrada BOOLEAN DEFAULT FALSE,
    remitente VARCHAR(200),
    fecha_entrada DATETIME,
    usuario_entrada VARCHAR(200),
    numero_documento_entrada VARCHAR(100),
    archivo_entrada_url VARCHAR(255),
    
    -- Datos de SALIDA
    tiene_salida BOOLEAN DEFAULT FALSE,
    destinatario VARCHAR(200),
    fecha_salida DATETIME,
    usuario_salida VARCHAR(200),
    numero_documento_salida VARCHAR(100),
    observaciones_salida TEXT,
    archivo_salida_url VARCHAR(255),
    
    -- Metadatos
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE CASCADE,
    INDEX idx_codigo_documento (codigo_documento),
    INDEX idx_fecha_entrada (fecha_entrada),
    INDEX idx_fecha_salida (fecha_salida),
    INDEX idx_tiene_entrada (tiene_entrada),
    INDEX idx_tiene_salida (tiene_salida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INSERTAR ÁREAS DE TRABAJO DEL SISTEMA (para usuarios)
-- =====================================================
INSERT INTO areas (nombre, sigla, tipo) VALUES
('Mesa de Partes', 'MDP', 'AREA_TRABAJO'),
('Sistemas', 'SIS', 'AREA_TRABAJO'),
('Desarrollo', 'DEV', 'AREA_TRABAJO'),
('Redes', 'RED', 'AREA_TRABAJO'),
('Soporte Técnico', 'ST', 'AREA_TRABAJO');

-- =====================================================
-- INSERTAR DEPARTAMENTOS PNP (para documentos/remitentes)
-- =====================================================
INSERT INTO areas (nombre, sigla, tipo) VALUES
('Comandancia General de la PNP', 'COMGEN', 'DEPARTAMENTO_PNP'),
('Estado Mayor General de la PNP', 'EMG', 'DEPARTAMENTO_PNP'),
('Inspectoría General de la PNP', 'IGPNP', 'DEPARTAMENTO_PNP'),
('Comando de Operaciones Policiales de la PNP', 'COOPNP', 'DEPARTAMENTO_PNP'),
('Secretaria Ejecutiva de la PNP', 'SECEJE', 'DEPARTAMENTO_PNP'),
('Dirección de Educación y Doctrina Policial de la PNP', 'DIREED', 'DEPARTAMENTO_PNP'),
('Dirección Nacional de Orden y Seguridad de la PNP', 'DIRNOS', 'DEPARTAMENTO_PNP'),
('Dirección Nacional de Investigación Criminal de la PNP', 'DIRNIC', 'DEPARTAMENTO_PNP'),
('Dirección de Operaciones Especiales de la PNP', 'DIROES', 'DEPARTAMENTO_PNP'),
('Dirección de Asuntos Internacionales de la PNP', 'DIRASINT', 'DEPARTAMENTO_PNP'),
('Dirección de Aviación Policial de la PNP', 'DIRAVPOL', 'DEPARTAMENTO_PNP'),
('Dirección de Criminalística de la PNP', 'DIRCRI', 'DEPARTAMENTO_PNP'),
('Dirección de Inteligencia de la PNP', 'DIRIN', 'DEPARTAMENTO_PNP'),
('Dirección de Sanidad Policial de la PNP', 'DIRSAPOL', 'DEPARTAMENTO_PNP'),
('Dirección de Antidrogas de la PNP', 'DIRANDRO', 'DEPARTAMENTO_PNP'),
('Dirección Contra el Terrorismo de la PNP', 'DIRCOTE', 'DEPARTAMENTO_PNP'),
('Dirección Contra la Corrupción de la PNP', 'DIRCOCOR', 'DEPARTAMENTO_PNP'),
('Dirección Contra la Trata de Personas y Tráfico Ilícito de Migrantes de la PNP', 'DIRCTPTIM', 'DEPARTAMENTO_PNP'),
('Dirección de Investigación de Lavado de Activos de la PNP', 'DIRILA', 'DEPARTAMENTO_PNP'),
('Dirección de Medio Ambiente de la PNP', 'DIRMEAMB', 'DEPARTAMENTO_PNP'),
('Dirección de la Policía Fiscal de la PNP', 'DIRPOFIS', 'DEPARTAMENTO_PNP'),
('Dirección de Seguridad Ciudadana y Policía Comunitaria de la PNP', 'DIRSECIU', 'DEPARTAMENTO_PNP'),
('Dirección de Seguridad de Estado de la PNP', 'DIRSEEST', 'DEPARTAMENTO_PNP'),
('Dirección de Seguridad Integral de la PNP', 'DIRSEINT', 'DEPARTAMENTO_PNP'),
('Dirección de Tránsito, Transporte y Seguridad Vial de la PNP', 'DIRTTSP', 'DEPARTAMENTO_PNP'),
('Dirección de Turismo de la PNP', 'DIRTUR', 'DEPARTAMENTO_PNP'),
('Dirección de Bienestar y Apoyo al Policía de la PNP', 'DIRBAP', 'DEPARTAMENTO_PNP'),
('Dirección de Administración de la PNP', 'DIRADM', 'DEPARTAMENTO_PNP'),
('Dirección de Planeamiento Institucional de la PNP', 'DIREPI', 'DEPARTAMENTO_PNP'),
('Dirección de Tecnología de la Información y Comunicación de la PNP', 'DIRTIC', 'DEPARTAMENTO_PNP'),
('Dirección de Asesoría Jurídica de la PNP', 'DIRASJUR', 'DEPARTAMENTO_PNP'),
('Dirección de Comunicación e Imagen Institucional de la PNP', 'DIRCIMA', 'DEPARTAMENTO_PNP'),
('Dirección de Gestión Documental de la PNP', 'DIRGEDO', 'DEPARTAMENTO_PNP'),
('Dirección de Recursos Humanos de la PNP', 'DIRREHUM', 'DEPARTAMENTO_PNP'),
('Oficina de Administración', 'OFAD', 'DEPARTAMENTO_PNP'),
('Oficina de Recursos Humanos', 'RRHH', 'DEPARTAMENTO_PNP'),
('Oficina de Logística', 'LOG', 'DEPARTAMENTO_PNP'),
('Oficina Legal', 'LEG', 'DEPARTAMENTO_PNP'),
('Dirección General', 'DIRGEN', 'DEPARTAMENTO_PNP'),
('Secretaría General', 'SECGEN', 'DEPARTAMENTO_PNP');

-- =====================================================
-- INSERTAR ROLES DEL SISTEMA
-- =====================================================
INSERT INTO roles (nombre) VALUES
('Administrador'),
('Mesa de Partes'),
('Trabajador'),
('Jefatura');

-- =====================================================
-- INSERTAR USUARIOS (Contraseña: 123456 para todos)
-- Hash BCrypt: $2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m
-- =====================================================
INSERT INTO usuarios (tipo_contrato, nombre, apellido, telefono, username, password_hash, ID_area) VALUES
('LOCADOR','Marius','De Paz Salazar','987654321','mdepaz','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',2),
('LOCADOR','Edwin','Cisneros Buendía','987654322','ecisneros','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',3),
('LOCADOR','Anderson','Ccorimanya Huachos','987654323','accori','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',1),
('LOCADOR','Jonathan','Chiclla Melo','987654324','jchiclla','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',4),
('LOCADOR','Gersson','Huamán García','987654325','ghuaman','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',5),
('LOCADOR','Oliver','Suárez Tinoco','987654327','osuarez','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',2),
('LOCADOR','Marcela','Rodríguez Munaylla','987654326','nakusu','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',2);

-- =====================================================
-- ASIGNAR ROLES A USUARIOS
-- =====================================================
INSERT INTO usuario_roles (ID_usuario, ID_rol) VALUES
(1,3), -- mdepaz - Trabajador
(2,3), -- ecisneros - Trabajador
(3,2), -- accori - Mesa de Partes
(4,3), -- jchiclla - Trabajador
(5,4), -- ghuaman - Jefatura
(6,3), -- osuarez - Trabajador
(7,1); -- nakusu - Administrador

-- =====================================================
-- INSERTAR TIPOS DE DOCUMENTO
-- =====================================================
INSERT INTO tipos_documento (nombre) VALUES
('Oficio'),
('Correo'),
('Memorándum'),
('Informe'),
('Resolución'),
('Solicitud'),
('Carta'),
('Acta'),
('Circular'),
('Directiva');

-- =====================================================
-- INSERTAR DOCUMENTOS DE EJEMPLO
-- =====================================================
INSERT INTO documentos (codigo, titulo, descripcion, ID_tipo_documento, numero_documento, remitente, fecha_ingreso, ID_usuario_registro, estado)
VALUES
('DOC-000001', 'Solicitud de combustible', 'Solicitud de suministro de combustible para operaciones', 1, 'OF-2025-256', 'DIRANDRO - Dirección de Antidrogas', '2025-09-01 08:00:00', 3, 'Asignado'),
('DOC-000002', 'Resultados de pericia', 'Envío de resultados de pericia criminalística', 2, 'COR-2025-123', 'DIRCRI - Dirección de Criminalística', '2025-09-05 09:30:00', 3, 'Recibido'),
('DOC-000003', 'Pedido de información adicional', 'Solicitud de información complementaria para investigación', 3, 'MEM-2025-456', 'DIRNIC - Dirección Nacional de Investigación Criminal', '2025-09-10 10:15:00', 3, 'En_Proceso'),
('DOC-000004', 'Informe de operaciones aéreas', 'Reporte mensual de operaciones de aviación policial', 4, 'INF-2025-789', 'DIRAVPOL - Dirección de Aviación Policial', '2025-09-12 14:00:00', 3, 'Finalizado'),
('DOC-000005', 'Solicitud de materiales educativos', 'Pedido de material didáctico para capacitaciones', 1, 'OF-2025-987', 'DIREED - Dirección de Educación y Doctrina Policial', '2025-09-15 11:45:00', 3, 'Asignado'),
('DOC-000006', 'Informe de incidentes de seguridad', 'Reporte mensual de incidentes de ciberseguridad', 4, 'INF-2025-321', 'DIRTIC - Dirección de Tecnología de la Información', '2025-09-20 16:30:00', 3, 'En_Proceso'),
('DOC-000007', 'Solicitud de renovación de licencias', 'Pedido de renovación de licencias de software', 6, 'SOL-2025-654', 'DIRTIC - Dirección de Tecnología de la Información', '2025-09-25 10:00:00', 3, 'Observado'),
('DOC-000008', 'Solicitud de personal adicional', 'Requerimiento de 3 efectivos para refuerzo', 1, 'OF-2025-145', 'DIRNOS - Dirección Nacional de Orden y Seguridad', '2025-10-01 09:00:00', 3, 'Asignado'),
('DOC-000009', 'Informe de operativo antidroga', 'Reporte de operativo realizado en la selva central', 4, 'INF-2025-555', 'DIRANDRO - Dirección de Antidrogas', '2025-10-05 15:20:00', 3, 'Recibido'),
('DOC-000010', 'Solicitud de mantenimiento vehicular', 'Mantenimiento preventivo de unidades móviles', 6, 'SOL-2025-789', 'DIRADM - Dirección de Administración', '2025-10-10 11:30:00', 3, 'Salida');

-- =====================================================
-- INSERTAR TRÁMITES (ASIGNACIÓN DE DOCUMENTOS A USUARIOS)
-- =====================================================
INSERT INTO tramites (ID_documento, ID_usuario_creador, ID_usuario_asignado)
VALUES
(1, 3, 2),  -- DOC-000001: Asignado a Edwin Cisneros
(2, 3, 1),  -- DOC-000002: Asignado a Marius De Paz
(3, 3, 4),  -- DOC-000003: Asignado a Jonathan Chiclla
(4, 3, 5),  -- DOC-000004: Asignado a Gersson Huamán
(5, 3, 6),  -- DOC-000005: Asignado a Oliver Suárez
(6, 3, 2),  -- DOC-000006: Asignado a Edwin Cisneros
(7, 3, 1),  -- DOC-000007: Asignado a Marius De Paz
(8, 3, 4),  -- DOC-000008: Asignado a Jonathan Chiclla
(9, 3, 6),  -- DOC-000009: Asignado a Oliver Suárez
(10, 3, 2); -- DOC-000010: Asignado a Edwin Cisneros

-- =====================================================
-- INSERTAR HOJAS DE TRÁMITE
-- =====================================================
INSERT INTO hojas_tramite (numero_ht, ID_documento)
VALUES
('HT-2025-001', 1),
('HT-2025-002', 2),
('HT-2025-003', 3),
('HT-2025-004', 6),
('HT-2025-005', 7);

-- =====================================================
-- VISTA: DOCUMENTOS CON TRAZABILIDAD COMPLETA
-- Para facilitar consultas de RF3 (trazabilidad)
-- =====================================================
CREATE OR REPLACE VIEW vista_documentos_trazabilidad AS
SELECT 
    d.ID_documento,
    d.codigo,
    d.titulo,
    d.estado,
    d.fecha_ingreso,
    d.remitente,
    td.nombre AS tipo_documento,
    u.nombre AS usuario_registro_nombre,
    u.apellido AS usuario_registro_apellido,
    COUNT(DISTINCT der.ID_derivacion) AS total_derivaciones,
    TIMESTAMPDIFF(HOUR, d.fecha_ingreso, NOW()) AS tiempo_total_horas,
    MAX(der.fecha_derivacion) AS ultima_derivacion,
    (SELECT a.nombre FROM derivaciones der2 
     JOIN areas a ON der2.ID_area_destino = a.ID_area 
     WHERE der2.ID_documento = d.ID_documento 
     ORDER BY der2.fecha_derivacion DESC LIMIT 1) AS area_actual,
    (SELECT CONCAT(u2.nombre, ' ', u2.apellido) FROM derivaciones der3
     JOIN usuarios u2 ON der3.ID_usuario_recibe = u2.ID_usuario
     WHERE der3.ID_documento = d.ID_documento
     ORDER BY der3.fecha_derivacion DESC LIMIT 1) AS usuario_actual
FROM documentos d
LEFT JOIN tipos_documento td ON d.ID_tipo_documento = td.ID_tipo_documento
LEFT JOIN usuarios u ON d.ID_usuario_registro = u.ID_usuario
LEFT JOIN derivaciones der ON d.ID_documento = der.ID_documento
GROUP BY d.ID_documento, d.codigo, d.titulo, d.estado, d.fecha_ingreso, 
         d.remitente, td.nombre, u.nombre, u.apellido;

-- =====================================================
-- VISTA: ESTADÍSTICAS POR ÁREA
-- Para reportes RF5
-- =====================================================
CREATE OR REPLACE VIEW vista_estadisticas_areas AS
SELECT 
    a.ID_area,
    a.nombre AS area_nombre,
    a.sigla,
    COUNT(DISTINCT der.ID_derivacion) AS total_derivaciones,
    COUNT(DISTINCT CASE WHEN der.estado = 'PENDIENTE' THEN der.ID_derivacion END) AS derivaciones_pendientes,
    COUNT(DISTINCT CASE WHEN der.estado = 'RECIBIDO' THEN der.ID_derivacion END) AS derivaciones_recibidas,
    AVG(CASE 
        WHEN der.fecha_recepcion IS NOT NULL 
        THEN TIMESTAMPDIFF(HOUR, der.fecha_derivacion, der.fecha_recepcion) 
    END) AS tiempo_promedio_recepcion_horas
FROM areas a
LEFT JOIN derivaciones der ON a.ID_area = der.ID_area_destino
WHERE a.tipo = 'DEPARTAMENTO_PNP'
GROUP BY a.ID_area, a.nombre, a.sigla;

-- =====================================================
-- PROCEDIMIENTO ALMACENADO: Obtener estadísticas de rendimiento
-- Para RNF1 - Validar tiempos de respuesta
-- =====================================================
DROP PROCEDURE IF EXISTS sp_obtener_estadisticas_rendimiento;

DELIMITER //
CREATE PROCEDURE sp_obtener_estadisticas_rendimiento()
BEGIN
    SELECT 
        'Documentos' AS entidad,
        COUNT(*) AS total,
        AVG(TIMESTAMPDIFF(HOUR, fecha_ingreso, NOW())) AS tiempo_promedio_horas
    FROM documentos
    UNION ALL
    SELECT 
        'Derivaciones' AS entidad,
        COUNT(*) AS total,
        AVG(CASE 
            WHEN fecha_recepcion IS NOT NULL 
            THEN TIMESTAMPDIFF(HOUR, fecha_derivacion, fecha_recepcion)
        END) AS tiempo_promedio_horas
    FROM derivaciones
    UNION ALL
    SELECT 
        'Notificaciones' AS entidad,
        COUNT(*) AS total,
        AVG(CASE 
            WHEN fecha_lectura IS NOT NULL 
            THEN TIMESTAMPDIFF(MINUTE, fecha_creacion, fecha_lectura)
        END) AS tiempo_promedio_horas
    FROM notificaciones;
END //
DELIMITER ;

-- =====================================================
-- TRIGGER: Notificar automáticamente al derivar documento
-- =====================================================
DROP TRIGGER IF EXISTS trg_notificar_derivacion;

DELIMITER //
CREATE TRIGGER trg_notificar_derivacion
AFTER INSERT ON derivaciones
FOR EACH ROW
BEGIN
    IF NEW.ID_usuario_recibe IS NOT NULL THEN
        INSERT INTO notificaciones (
            ID_usuario, 
            ID_documento, 
            titulo, 
            mensaje, 
            tipo
        )
        SELECT 
            NEW.ID_usuario_recibe,
            NEW.ID_documento,
            'Documento derivado a su área',
            CONCAT('Se le ha derivado el documento ', d.codigo, ': ', d.titulo),
            'DERIVACION'
        FROM documentos d
        WHERE d.ID_documento = NEW.ID_documento;
    END IF;
END //
DELIMITER ;

-- =====================================================
-- TRIGGER: Registrar ENTRADA en bitácora UNIFICADA
-- =====================================================
DROP TRIGGER IF EXISTS trg_bitacora_entrada_documento;

DELIMITER //
CREATE TRIGGER trg_bitacora_entrada_documento
AFTER INSERT ON documentos
FOR EACH ROW
BEGIN
    INSERT INTO bitacora (
        ID_documento,
        codigo_documento,
        titulo_documento,
        tipo_documento,
        tiene_entrada,
        remitente,
        fecha_entrada,
        usuario_entrada,
        numero_documento_entrada,
        archivo_entrada_url
    )
    SELECT 
        NEW.ID_documento,
        NEW.codigo,
        NEW.titulo,
        td.nombre,
        TRUE,
        NEW.remitente,
        NEW.fecha_ingreso,
        CONCAT(u.nombre, ' ', u.apellido),
        NEW.numero_documento,
        NEW.archivo_url
    FROM tipos_documento td
    LEFT JOIN usuarios u ON NEW.ID_usuario_registro = u.ID_usuario
    WHERE td.ID_tipo_documento = NEW.ID_tipo_documento;
END //
DELIMITER ;

-- =====================================================
-- TRIGGER: Actualizar bitácora con SALIDA (UNIFICADO)
-- =====================================================
DROP TRIGGER IF EXISTS trg_bitacora_salida_documento;

DELIMITER //
CREATE TRIGGER trg_bitacora_salida_documento
AFTER INSERT ON salidas_documento
FOR EACH ROW
BEGIN
    -- Intentar actualizar el registro existente
    UPDATE bitacora b
    INNER JOIN documentos d ON b.ID_documento = d.ID_documento
    INNER JOIN usuarios u ON NEW.ID_usuario_salida = u.ID_usuario
    SET 
        b.tiene_salida = TRUE,
        b.destinatario = NEW.destinatario_salida,
        b.fecha_salida = NEW.fecha_salida,
        b.usuario_salida = CONCAT(u.nombre, ' ', u.apellido),
        b.numero_documento_salida = NEW.numero_documento_salida,
        b.observaciones_salida = NEW.observacion,
        b.archivo_salida_url = NEW.archivo_cargo_url
    WHERE b.ID_documento = NEW.ID_documento;
    
    -- Si no existe registro de entrada, crear uno solo con salida
    IF ROW_COUNT() = 0 THEN
        INSERT INTO bitacora (
            ID_documento,
            codigo_documento,
            titulo_documento,
            tipo_documento,
            tiene_entrada,
            tiene_salida,
            destinatario,
            fecha_salida,
            usuario_salida,
            numero_documento_salida,
            observaciones_salida,
            archivo_salida_url
        )
        SELECT 
            d.ID_documento,
            d.codigo,
            d.titulo,
            td.nombre,
            FALSE,
            TRUE,
            NEW.destinatario_salida,
            NEW.fecha_salida,
            CONCAT(u.nombre, ' ', u.apellido),
            NEW.numero_documento_salida,
            NEW.observacion,
            NEW.archivo_cargo_url
        FROM documentos d
        LEFT JOIN tipos_documento td ON d.ID_tipo_documento = td.ID_tipo_documento
        LEFT JOIN usuarios u ON NEW.ID_usuario_salida = u.ID_usuario
        WHERE d.ID_documento = NEW.ID_documento;
    END IF;
END //
DELIMITER ;

-- =====================================================
-- POBLAR BITÁCORA CON DOCUMENTOS EXISTENTES
-- =====================================================

-- Insertar todos los documentos que ya existen como ENTRADA
INSERT IGNORE INTO bitacora (
    ID_documento,
    codigo_documento,
    titulo_documento,
    tipo_documento,
    tiene_entrada,
    remitente,
    fecha_entrada,
    usuario_entrada,
    numero_documento_entrada,
    archivo_entrada_url
)
SELECT 
    d.ID_documento,
    d.codigo,
    d.titulo,
    td.nombre,
    TRUE,
    d.remitente,
    d.fecha_ingreso,
    CONCAT(u.nombre, ' ', u.apellido),
    d.numero_documento,
    d.archivo_url
FROM documentos d
LEFT JOIN tipos_documento td ON d.ID_tipo_documento = td.ID_tipo_documento
LEFT JOIN usuarios u ON d.ID_usuario_registro = u.ID_usuario;

-- Actualizar con información de SALIDA donde exista
UPDATE bitacora b
INNER JOIN salidas_documento s ON b.ID_documento = s.ID_documento
LEFT JOIN usuarios u ON s.ID_usuario_salida = u.ID_usuario
SET 
    b.tiene_salida = TRUE,
    b.destinatario = s.destinatario_salida,
    b.fecha_salida = s.fecha_salida,
    b.usuario_salida = CONCAT(u.nombre, ' ', u.apellido),
    b.numero_documento_salida = s.numero_documento_salida,
    b.observaciones_salida = s.observacion,
    b.archivo_salida_url = s.archivo_cargo_url;

-- =====================================================
-- MENSAJES DE CONFIRMACIÓN Y VERIFICACIÓN
-- =====================================================
SELECT '✅ Base de datos creada exitosamente con BITÁCORA UNIFICADA' AS mensaje;
SELECT '' AS separador;

SELECT '🔐 CREDENCIALES DE ACCESO' AS titulo;
SELECT 'Todos los usuarios tienen la contraseña: 123456' AS info;
SELECT '' AS separador;

SELECT '📊 ESTADÍSTICAS DE LA BASE DE DATOS' AS titulo;
SELECT COUNT(*) AS Total_Areas FROM areas;
SELECT COUNT(*) AS Total_Usuarios FROM usuarios;
SELECT COUNT(*) AS Total_Documentos FROM documentos;
SELECT '' AS separador;

SELECT '✅ Tabla bitácora UNIFICADA creada exitosamente' AS mensaje;
SELECT '✅ Triggers configurados correctamente (derivación + bitácora unificada)' AS mensaje;
SELECT CONCAT('📊 ', COUNT(*), ' registros insertados en bitácora') AS mensaje FROM bitacora;

-- Estadísticas de bitácora
SELECT 
    COUNT(*) AS total_registros,
    SUM(tiene_entrada) AS con_entrada,
    SUM(tiene_salida) AS con_salida,
    SUM(tiene_entrada AND tiene_salida) AS con_ambos,
    SUM(tiene_entrada AND NOT tiene_salida) AS solo_entrada,
    SUM(NOT tiene_entrada AND tiene_salida) AS solo_salida
FROM bitacora;

SELECT '' AS separador;
SELECT '🎉 ¡TODO LISTO! Sistema completo con BITÁCORA UNIFICADA funcionando' AS mensaje_final;
SELECT '💡 Usuario Administrador: nakusu / 123456' AS tip_1;
SELECT '💡 Usuario Mesa de Partes: accori / 123456' AS tip_2;
SELECT '💡 NUEVO: Bitácora ahora tiene UN SOLO REGISTRO por documento' AS tip_3;
SELECT '💡 NUEVO: Entrada y Salida en la misma fila' AS tip_4;
