DROP DATABASE IF EXISTS mesa_partes_db;
CREATE DATABASE mesa_partes_db;
USE mesa_partes_db;

CREATE TABLE areas (
    ID_area INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    sigla VARCHAR(20) UNIQUE
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

INSERT INTO areas (nombre, sigla) VALUES
('Comandancia General de la PNP', 'COMGEN'),
('Estado Mayor General de la PNP', 'EMG'),
('Inspectoría General de la PNP', 'IGPNP'),
('Comando de Operaciones Policiales de la PNP', 'COOPNP'),
('Secretaria Ejecutiva de la PNP', 'SECEJE'),
('Dirección de Educación y Doctrina Policial de la PNP', 'DIREED'),
('Dirección Nacional de Orden y Seguridad de la PNP', 'DIRNOS'),
('Dirección Nacional de Investigación Criminal de la PNP', 'DIRNIC'),
('Dirección de Operaciones Especiales de la PNP', 'DIROES'),
('Dirección de Asuntos Internacionales de la PNP', 'DIRASINT'),
('Dirección de Aviación Policial de la PNP', 'DIRAVPOL'),
('Dirección de Criminalística de la PNP', 'DIRCRI'),
('Dirección de Inteligencia de la PNP', 'DIRIN'),
('Dirección de Sanidad Policial de la PNP', 'DIRSAPOL'),
('Dirección de Antidrogas de la PNP', 'DIRANDRO'),
('Dirección Contra el Terrorismo de la PNP', 'DIRCOTE'),
('Dirección Contra la Corrupción de la PNP', 'DIRCOCOR'),
('Dirección Contra la Trata de Personas y Tráfico Ilícito de Migrantes de la PNP', 'DIRCTPTIM'),
('Dirección de Investigación de Lavado de Activos de la PNP', 'DIRILA'),
('Dirección de Medio Ambiente de la PNP', 'DIRMEAMB'),
('Dirección de la Policía Fiscal de la PNP', 'DIRPOFIS'),
('Dirección de Seguridad Ciudadana y Policía Comunitaria de la PNP', 'DIRSECIU'),
('Dirección de Seguridad de Estado de la PNP', 'DIRSEEST'),
('Dirección de Seguridad Integral de la PNP', 'DIRSEINT'),
('Dirección de Tránsito, Transporte y Seguridad Vial de la PNP', 'DIRTTSP'),
('Dirección de Turismo de la PNP', 'DIRTUR'),
('Dirección de Bienestar y Apoyo al Policía de la PNP', 'DIRBAP'),
('Dirección de Administración de la PNP', 'DIRADM'),
('Dirección de Planeamiento Institucional de la PNP', 'DIREPI'),
('Dirección de Tecnología de la Información y Comunicación de la PNP', 'DIRTIC'),
('Dirección de Asesoría Jurídica de la PNP', 'DIRASJUR'),
('Dirección de Comunicación e Imagen Institucional de la PNP', 'DIRCIMA'),
('Dirección de Gestión Documental de la PNP', 'DIRGEDO'),
('Dirección de Recursos Humanos de la PNP', 'DIRREHUM');

INSERT INTO roles (nombre) VALUES
('Administrador'),
('Mesa de Partes'),
('Trabajador'),
('Jefatura');

INSERT INTO usuarios (tipo_contrato, nombre, apellido, telefono, username, password_hash, ID_area) VALUES
('LOCADOR','Marius','De Paz Salazar','987654321','mdepaz','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',3),
('LOCADOR','Edwin','Cisneros Buendía','987654322','ecisneros','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2),
('LOCADOR','Anderson','Ccorimanya Huachos','987654323','accori','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',11),
('LOCADOR','Jonathan','Chiclla Melo','987654324','jchiclla','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',5),
('LOCADOR','Gersson','Huamán García','987654325','ghuaman','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',4),
('LOCADOR','Oliver','Suárez Tinoco','987654327','osuarez','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1),
('LOCADOR','Marcela','Rodríguez Munaylla','987654326','nakusu','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',10);

INSERT INTO usuario_roles (ID_usuario, ID_rol) VALUES
(1,3),
(2,3),
(3,2),
(4,3),
(5,4),
(6,3),
(7,1);

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

INSERT INTO documentos (codigo, titulo, descripcion, ID_tipo_documento, numero_documento, remitente, fecha_ingreso, ID_usuario_registro)
VALUES
('DOC001', 'Solicitud de combustible', 'Solicitud de suministro de combustible para operaciones', 1, '256', 'DIRANDRO', '2025-09-01 08:00:00', 1),
('DOC002', 'Resultados de pericia', 'Envío de resultados de pericia criminalística', 2, '123', 'DIRCRI', '2025-09-05 09:30:00', 2),
('DOC003', 'Pedido de información adicional', 'Solicitud de información complementaria para investigación', 3, '456', 'DIRNIC', '2025-09-10 10:15:00', 1),
('DOC004', 'Informe de operaciones aéreas', 'Reporte mensual de operaciones de aviación policial', 4, '789', 'DIRAVPOL', '2025-09-12 14:00:00', 3),
('DOC005', 'Solicitud de materiales educativos', 'Pedido de material didáctico para capacitaciones', 1, '987', 'DIREED', '2025-09-15 11:45:00', 1);

SELECT 'Base de datos creada exitosamente. Todos los usuarios tienen la contraseña: 123456' AS mensaje;
