-- =====================================================
-- ACTUALIZACIÓN: Agregar tabla de bitácora y triggers
-- Fecha: 17 de Noviembre de 2025
-- =====================================================

USE mesa_partes_db;

-- =====================================================
-- TABLA: BITÁCORA (Para auditoría de entradas y salidas)
-- =====================================================
CREATE TABLE IF NOT EXISTS bitacora (
    ID_bitacora INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion ENUM('ENTRADA','SALIDA') NOT NULL COMMENT 'ENTRADA: Registro de documento | SALIDA: Salida de documento',
    ID_documento INT UNSIGNED NOT NULL,
    codigo_documento VARCHAR(50) NOT NULL,
    titulo_documento VARCHAR(200) NOT NULL,
    tipo_documento VARCHAR(100),
    remitente VARCHAR(200),
    destinatario VARCHAR(200),
    fecha_operacion DATETIME NOT NULL,
    ID_usuario_operacion INT UNSIGNED,
    usuario_nombre VARCHAR(200),
    numero_documento VARCHAR(100),
    observaciones TEXT,
    archivo_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE CASCADE,
    FOREIGN KEY (ID_usuario_operacion) REFERENCES usuarios(ID_usuario) ON DELETE SET NULL,
    INDEX idx_tipo_operacion (tipo_operacion),
    INDEX idx_documento (ID_documento),
    INDEX idx_fecha_operacion (fecha_operacion),
    INDEX idx_codigo_documento (codigo_documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TRIGGER: Registrar ENTRADA en bitácora al insertar documento
-- =====================================================
DROP TRIGGER IF EXISTS trg_bitacora_entrada_documento;

DELIMITER //
CREATE TRIGGER trg_bitacora_entrada_documento
AFTER INSERT ON documentos
FOR EACH ROW
BEGIN
    INSERT INTO bitacora (
        tipo_operacion,
        ID_documento,
        codigo_documento,
        titulo_documento,
        tipo_documento,
        remitente,
        destinatario,
        fecha_operacion,
        ID_usuario_operacion,
        usuario_nombre,
        numero_documento,
        archivo_url
    )
    SELECT 
        'ENTRADA',
        NEW.ID_documento,
        NEW.codigo,
        NEW.titulo,
        td.nombre,
        NEW.remitente,
        NEW.destinatario,
        NEW.fecha_ingreso,
        NEW.ID_usuario_registro,
        CONCAT(u.nombre, ' ', u.apellido),
        NEW.numero_documento,
        NEW.archivo_url
    FROM tipos_documento td
    LEFT JOIN usuarios u ON NEW.ID_usuario_registro = u.ID_usuario
    WHERE td.ID_tipo_documento = NEW.ID_tipo_documento;
END //
DELIMITER ;

-- =====================================================
-- TRIGGER: Registrar SALIDA en bitácora al insertar salida
-- =====================================================
DROP TRIGGER IF EXISTS trg_bitacora_salida_documento;

DELIMITER //
CREATE TRIGGER trg_bitacora_salida_documento
AFTER INSERT ON salidas_documento
FOR EACH ROW
BEGIN
    INSERT INTO bitacora (
        tipo_operacion,
        ID_documento,
        codigo_documento,
        titulo_documento,
        tipo_documento,
        remitente,
        destinatario,
        fecha_operacion,
        ID_usuario_operacion,
        usuario_nombre,
        numero_documento,
        observaciones,
        archivo_url
    )
    SELECT 
        'SALIDA',
        d.ID_documento,
        d.codigo,
        d.titulo,
        td.nombre,
        d.remitente,
        NEW.destinatario_salida,
        NEW.fecha_salida,
        NEW.ID_usuario_salida,
        CONCAT(u.nombre, ' ', u.apellido),
        NEW.numero_documento_salida,
        NEW.observacion,
        NEW.archivo_cargo_url
    FROM documentos d
    LEFT JOIN tipos_documento td ON d.ID_tipo_documento = td.ID_tipo_documento
    LEFT JOIN usuarios u ON NEW.ID_usuario_salida = u.ID_usuario
    WHERE d.ID_documento = NEW.ID_documento;
END //
DELIMITER ;

-- =====================================================
-- INSERTAR DATOS HISTÓRICOS EN BITÁCORA
-- (Registros de documentos ya existentes)
-- =====================================================
INSERT INTO bitacora (
    tipo_operacion,
    ID_documento,
    codigo_documento,
    titulo_documento,
    tipo_documento,
    remitente,
    destinatario,
    fecha_operacion,
    ID_usuario_operacion,
    usuario_nombre,
    numero_documento,
    archivo_url
)
SELECT 
    'ENTRADA',
    d.ID_documento,
    d.codigo,
    d.titulo,
    td.nombre,
    d.remitente,
    d.destinatario,
    d.fecha_ingreso,
    d.ID_usuario_registro,
    CONCAT(u.nombre, ' ', u.apellido),
    d.numero_documento,
    d.archivo_url
FROM documentos d
LEFT JOIN tipos_documento td ON d.ID_tipo_documento = td.ID_tipo_documento
LEFT JOIN usuarios u ON d.ID_usuario_registro = u.ID_usuario
WHERE NOT EXISTS (
    SELECT 1 FROM bitacora b 
    WHERE b.ID_documento = d.ID_documento 
    AND b.tipo_operacion = 'ENTRADA'
);

SELECT '✅ Tabla bitácora creada exitosamente' AS mensaje;
SELECT '✅ Triggers configurados correctamente' AS mensaje;
SELECT CONCAT('📊 ', COUNT(*), ' registros históricos insertados') AS mensaje FROM bitacora;
