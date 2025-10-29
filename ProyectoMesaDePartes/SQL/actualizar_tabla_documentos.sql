-- Actualizar estructura de tablas para coincidir con el modelo Java

USE mesa_partes_db;

-- Eliminar tablas dependientes primero
DROP TABLE IF EXISTS salidas_documento;
DROP TABLE IF EXISTS tramites;
DROP TABLE IF EXISTS hojas_tramite;
DROP TABLE IF EXISTS documentos;

-- Recrear tabla documentos con estructura correcta
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

-- Recrear tabla hojas_tramite
CREATE TABLE hojas_tramite (
    ID_hoja_tramite INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    numero_ht VARCHAR(50),
    ID_documento INT UNSIGNED,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento)
);

-- Recrear tabla tramites
CREATE TABLE tramites (
    ID_tramite INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_documento INT UNSIGNED,
    ID_usuario_creador INT UNSIGNED,
    ID_usuario_asignado INT UNSIGNED,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento),
    FOREIGN KEY (ID_usuario_creador) REFERENCES usuarios(ID_usuario),
    FOREIGN KEY (ID_usuario_asignado) REFERENCES usuarios(ID_usuario)
);

-- Recrear tabla salidas_documento
CREATE TABLE salidas_documento (
    ID_salida_documento INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_documento INT UNSIGNED,
    fecha_salida DATETIME NOT NULL,
    destino VARCHAR(200) NOT NULL,
    observaciones TEXT,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento)
);

SELECT 'Tablas actualizadas correctamente' AS Resultado;
