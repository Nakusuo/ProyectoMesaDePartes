-- =====================================================
-- ACTUALIZACIÓN DE BITÁCORA: UNIFICAR ENTRADA Y SALIDA
-- =====================================================
-- Este script modifica la tabla bitácora para que un documento
-- tenga un solo registro con información de entrada y salida
-- =====================================================

USE mesa_partes_db;

-- Eliminar triggers antiguos
DROP TRIGGER IF EXISTS trg_bitacora_entrada_documento;
DROP TRIGGER IF EXISTS trg_bitacora_salida_documento;

-- Respaldar datos existentes (opcional pero recomendado)
CREATE TABLE IF NOT EXISTS bitacora_backup_20251119 AS SELECT * FROM bitacora;

-- Eliminar tabla bitácora actual
DROP TABLE IF EXISTS bitacora;

-- Crear nueva tabla bitácora con estructura unificada
CREATE TABLE bitacora (
    ID_bitacora BIGINT PRIMARY KEY AUTO_INCREMENT,
    ID_documento BIGINT NOT NULL UNIQUE,
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
    
    INDEX idx_codigo_documento (codigo_documento),
    INDEX idx_fecha_entrada (fecha_entrada),
    INDEX idx_fecha_salida (fecha_salida),
    FOREIGN KEY (ID_documento) REFERENCES documentos(ID_documento) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TRIGGER: Registrar ENTRADA en bitácora
-- =====================================================
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
-- TRIGGER: Actualizar bitácora con SALIDA
-- =====================================================
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
-- Poblar bitácora con datos existentes (si existen)
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
-- Verificación
-- =====================================================
SELECT 
    COUNT(*) AS total_registros,
    SUM(tiene_entrada) AS con_entrada,
    SUM(tiene_salida) AS con_salida,
    SUM(tiene_entrada AND tiene_salida) AS con_ambos,
    SUM(tiene_entrada AND NOT tiene_salida) AS solo_entrada,
    SUM(NOT tiene_entrada AND tiene_salida) AS solo_salida
FROM bitacora;

SELECT 'Bitácora actualizada correctamente' AS status;
