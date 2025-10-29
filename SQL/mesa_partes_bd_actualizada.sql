DROP DATABASE IF EXISTS mesa_partes_db;
CREATE DATABASE mesa_partes_db;
USE mesa_partes_db;

CREATE TABLE areas (
    ID_area INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    sigla VARCHAR(20) UNIQUE,
    tipo ENUM('DEPARTAMENTO_PNP','AREA_TRABAJO') DEFAULT 'DEPARTAMENTO_PNP' COMMENT 'DEPARTAMENTO_PNP: Áreas oficiales de la PNP (para documentos), AREA_TRABAJO: Áreas de trabajo del sistema (para usuarios)'
);

CREATE TABLE roles (
    ID_rol INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

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
    FOREIGN KEY (ID_area) REFERENCES areas(ID_area)
);

CREATE TABLE usuario_roles (
    ID_usuario_rol INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_usuario INT UNSIGNED NOT NULL,
    ID_rol INT UNSIGNED NOT NULL,
    FOREIGN KEY (ID_usuario) REFERENCES usuarios(ID_usuario),
    FOREIGN KEY (ID_rol) REFERENCES roles(ID_rol)
);

CREATE TABLE tipos_documento (
    ID_tipo_documento INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE documentos (
    ID_documento INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    numero_documento VARCHAR(100),
    estado ENUM('Registrado','En Proceso','Observado','Finalizado','Salida') DEFAULT 'Registrado',
    remitente VARCHAR(200) NOT NULL,
    destinatario VARCHAR(200),
    fecha_ingreso DATETIME NOT NULL,
    archivo_url VARCHAR(255),
    ID_usuario_registro INT UNSIGNED,
    ID_tipo_documento INT UNSIGNED NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_usuario_registro) REFERENCES usuarios(ID_usuario),
    FOREIGN KEY (ID_tipo_documento) REFERENCES tipos_documento(ID_tipo_documento)
);

CREATE TABLE hojas_tramite (
    ID_hoja_tramite INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    numero_ht VARCHAR(50),
    ID_documento INT UNSIGNED,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento)
);

CREATE TABLE tramites (
    ID_tramite INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_documento INT UNSIGNED,
    ID_usuario_creador INT UNSIGNED,
    ID_usuario_asignado INT UNSIGNED,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento),
    FOREIGN KEY (ID_usuario_creador) REFERENCES usuarios(ID_usuario),
    FOREIGN KEY (ID_usuario_asignado) REFERENCES usuarios(ID_usuario)
);

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
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento),
    FOREIGN KEY (ID_usuario_salida) REFERENCES usuarios(ID_usuario),
    FOREIGN KEY (ID_tipo_documento) REFERENCES tipos_documento(ID_tipo_documento)
);

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
('Dirección de Recursos Humanos de la PNP', 'DIRREHUM', 'DEPARTAMENTO_PNP');

-- =====================================================
-- INSERTAR ROLES DEL SISTEMA
-- =====================================================
INSERT INTO roles (nombre) VALUES
('Administrador'),
('Mesa de Partes'),
('Trabajador'),
('Jefatura');

-- =====================================================
-- INSERTAR USUARIOS (asignados a ÁREAS DE TRABAJO)
-- =====================================================
INSERT INTO usuarios (tipo_contrato, nombre, apellido, telefono, username, password_hash, ID_area) VALUES
('LOCADOR','Marius','De Paz Salazar','987654321','mdepaz','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',2), -- Sistemas
('LOCADOR','Edwin','Cisneros Buendía','987654322','ecisneros','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',3), -- Desarrollo
('LOCADOR','Anderson','Ccorimanya Huachos','987654323','accori','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',1), -- Mesa de Partes
('LOCADOR','Jonathan','Chiclla Melo','987654324','jchiclla','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',4), -- Redes
('LOCADOR','Gersson','Huamán García','987654325','ghuaman','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',5), -- Soporte Técnico
('LOCADOR','Oliver','Suárez Tinoco','987654327','osuarez','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',2), -- Sistemas
('LOCADOR','Marcela','Rodríguez Munaylla','987654326','nakusu','$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m',2); -- Sistemas (Admin)

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
-- Primero insertamos los documentos
INSERT INTO documentos (codigo, titulo, descripcion, ID_tipo_documento, numero_documento, remitente, fecha_ingreso, ID_usuario_registro)
VALUES
('DOC-000001', 'Solicitud de combustible', 'Solicitud de suministro de combustible para operaciones', 1, 'OF-2025-256', 'DIRANDRO - Dirección de Antidrogas', '2025-09-01 08:00:00', 3),
('DOC-000002', 'Resultados de pericia', 'Envío de resultados de pericia criminalística', 2, 'COR-2025-123', 'DIRCRI - Dirección de Criminalística', '2025-09-05 09:30:00', 3),
('DOC-000003', 'Pedido de información adicional', 'Solicitud de información complementaria para investigación', 3, 'MEM-2025-456', 'DIRNIC - Dirección Nacional de Investigación Criminal', '2025-09-10 10:15:00', 3),
('DOC-000004', 'Informe de operaciones aéreas', 'Reporte mensual de operaciones de aviación policial', 4, 'INF-2025-789', 'DIRAVPOL - Dirección de Aviación Policial', '2025-09-12 14:00:00', 3),
('DOC-000005', 'Solicitud de materiales educativos', 'Pedido de material didáctico para capacitaciones', 1, 'OF-2025-987', 'DIREED - Dirección de Educación y Doctrina Policial', '2025-09-15 11:45:00', 3),
('DOC-000006', 'Informe de incidentes de seguridad', 'Reporte mensual de incidentes de ciberseguridad', 4, 'INF-2025-321', 'DIRTIC - Dirección de Tecnología de la Información', '2025-09-20 16:30:00', 3),
('DOC-000007', 'Solicitud de renovación de licencias', 'Pedido de renovación de licencias de software', 6, 'SOL-2025-654', 'DIRTIC - Dirección de Tecnología de la Información', '2025-09-25 10:00:00', 3),
('DOC-000008', 'Solicitud de personal adicional', 'Requerimiento de 3 efectivos para refuerzo', 1, 'OF-2025-145', 'DIRNOS - Dirección Nacional de Orden y Seguridad', '2025-10-01 09:00:00', 3),
('DOC-000009', 'Informe de operativo antidroga', 'Reporte de operativo realizado en la selva central', 4, 'INF-2025-555', 'DIRANDRO - Dirección de Antidrogas', '2025-10-05 15:20:00', 3),
('DOC-000010', 'Solicitud de mantenimiento vehicular', 'Mantenimiento preventivo de unidades móviles', 6, 'SOL-2025-789', 'DIRADM - Dirección de Administración', '2025-10-10 11:30:00', 3);

-- Ahora insertamos los trámites con usuarios asignados
-- Esto es lo que hace que aparezca "a quién se le asignó" en la bitácora

INSERT INTO tramites (ID_documento, ID_usuario_creador, ID_usuario_asignado)
VALUES
-- DOC-000001: Asignado a Edwin Cisneros (Desarrollo)
(1, 3, 2),

-- DOC-000002: Asignado a Marius De Paz (Sistemas)
(2, 3, 1),

-- DOC-000003: Asignado a Jonathan Chiclla (Redes)
(3, 3, 4),

-- DOC-000004: Asignado a Gersson Huamán (Soporte Técnico - Jefatura)
(4, 3, 5),

-- DOC-000005: Asignado a Oliver Suárez (Sistemas)
(5, 3, 6),

-- DOC-000006: Asignado a Edwin Cisneros (Desarrollo)
(6, 3, 2),

-- DOC-000007: Asignado a Marius De Paz (Sistemas)
(7, 3, 1),

-- DOC-000008: Asignado a Jonathan Chiclla (Redes)
(8, 3, 4),

-- DOC-000009: Asignado a Oliver Suárez (Sistemas)
(9, 3, 6),

-- DOC-000010: Asignado a Edwin Cisneros (Desarrollo)
(10, 3, 2);

-- Opcionalmente, insertamos hojas de trámite para algunos documentos
INSERT INTO hojas_tramite (numero_ht, ID_documento)
VALUES
('HT-2025-001', 1),
('HT-2025-002', 2),
('HT-2025-003', 3),
('HT-2025-004', 6),
('HT-2025-005', 7);

-- Verificar los datos insertados
SELECT 
    d.codigo,
    d.titulo,
    d.remitente,
    d.estado,
    CONCAT(u.nombre, ' ', u.apellido) AS usuario_asignado,
    a.nombre AS area_asignada,
    d.fecha_ingreso
FROM documentos d
INNER JOIN tramites t ON d.ID_documento = t.ID_documento
INNER JOIN usuarios u ON t.ID_usuario_asignado = u.ID_usuario
LEFT JOIN areas a ON u.ID_area = a.ID_area
ORDER BY d.fecha_ingreso DESC;

-- Mensaje de confirmación
SELECT '✅ Documentos y trámites insertados correctamente' AS mensaje;
SELECT 'Los documentos ahora tienen usuarios asignados' AS info;
SELECT 'Verifica en la bitácora del frontend' AS siguiente_paso;


-- =====================================================
-- MENSAJE DE CONFIRMACIÓN
-- =====================================================
SELECT 'Base de datos creada exitosamente.' AS mensaje;
SELECT '✅ Todos los usuarios tienen la contraseña: 123456' AS info;
SELECT '' AS separador;
SELECT '📋 ÁREAS DE TRABAJO DEL SISTEMA (para usuarios):' AS titulo;
SELECT nombre, sigla FROM areas WHERE tipo = 'AREA_TRABAJO';
SELECT '' AS separador;
SELECT '🏢 DEPARTAMENTOS PNP (para documentos):' AS titulo;
SELECT COUNT(*) as total FROM areas WHERE tipo = 'DEPARTAMENTO_PNP';
SELECT '' AS separador;
SELECT '👥 USUARIOS Y SUS ÁREAS DE TRABAJO:' AS titulo;
SELECT u.username, u.nombre, u.apellido, a.nombre as area, r.nombre as rol
FROM usuarios u
LEFT JOIN areas a ON u.ID_area = a.ID_area
LEFT JOIN usuario_roles ur ON u.ID_usuario = ur.ID_usuario
LEFT JOIN roles r ON ur.ID_rol = r.ID_rol
ORDER BY u.ID_usuario;
