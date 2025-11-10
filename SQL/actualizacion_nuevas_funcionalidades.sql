-- =====================================================
-- ACTUALIZACIÓN DE BASE DE DATOS - NUEVAS FUNCIONALIDADES
-- Derivaciones, Notificaciones y Trazabilidad
-- Fecha: 10 de noviembre de 2025
-- =====================================================

USE mesa_partes_db;

-- =====================================================
-- TABLA: DERIVACIONES (Para RF2 - Derivar documentos)
-- =====================================================
CREATE TABLE IF NOT EXISTS derivaciones (
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
    FOREIGN KEY (ID_area_origen) REFERENCES areas(ID_area),
    FOREIGN KEY (ID_area_destino) REFERENCES areas(ID_area),
    FOREIGN KEY (ID_usuario_deriva) REFERENCES usuarios(ID_usuario),
    FOREIGN KEY (ID_usuario_recibe) REFERENCES usuarios(ID_usuario),
    INDEX idx_documento (ID_documento),
    INDEX idx_area_destino (ID_area_destino),
    INDEX idx_estado (estado),
    INDEX idx_fecha_derivacion (fecha_derivacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: NOTIFICACIONES (Para RF6 - Sistema de notificaciones)
-- =====================================================
CREATE TABLE IF NOT EXISTS notificaciones (
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
-- ÍNDICES ADICIONALES PARA MEJORAR RENDIMIENTO (RNF1)
-- =====================================================

-- Índices en tabla documentos
ALTER TABLE documentos ADD INDEX idx_documentos_estado (estado);
ALTER TABLE documentos ADD INDEX idx_documentos_fecha_ingreso (fecha_ingreso);
ALTER TABLE documentos ADD INDEX idx_documentos_usuario_registro (ID_usuario_registro);

-- Índices en tabla tramites
ALTER TABLE tramites ADD INDEX idx_tramites_documento (ID_documento);
ALTER TABLE tramites ADD INDEX idx_tramites_usuario_asignado (ID_usuario_asignado);
ALTER TABLE tramites ADD INDEX idx_tramites_usuario_creador (ID_usuario_creador);

-- Índices en tabla usuarios
ALTER TABLE usuarios ADD INDEX idx_usuarios_activo (activo);
ALTER TABLE usuarios ADD INDEX idx_usuarios_area (ID_area);

-- =====================================================
-- DATOS DE PRUEBA PARA ÁREAS (si no existen)
-- =====================================================
INSERT IGNORE INTO areas (nombre, sigla, tipo) VALUES
('OFICINA DE ADMINISTRACIÓN', 'OFAD', 'DEPARTAMENTO_PNP'),
('OFICINA DE RECURSOS HUMANOS', 'RRHH', 'DEPARTAMENTO_PNP'),
('OFICINA DE LOGÍSTICA', 'LOG', 'DEPARTAMENTO_PNP'),
('OFICINA DE TECNOLOGÍA', 'TI', 'DEPARTAMENTO_PNP'),
('OFICINA LEGAL', 'LEG', 'DEPARTAMENTO_PNP'),
('DIRECCIÓN GENERAL', 'DIRGEN', 'DEPARTAMENTO_PNP'),
('SECRETARÍA GENERAL', 'SECGEN', 'DEPARTAMENTO_PNP'),
('MESA DE PARTES', 'MESA', 'AREA_TRABAJO');

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
-- MENSAJE DE FINALIZACIÓN
-- =====================================================
SELECT 'Base de datos actualizada exitosamente con nuevas funcionalidades' AS Resultado;
SELECT COUNT(*) AS Total_Derivaciones FROM derivaciones;
SELECT COUNT(*) AS Total_Notificaciones FROM notificaciones;
SELECT COUNT(*) AS Total_Areas FROM areas;
