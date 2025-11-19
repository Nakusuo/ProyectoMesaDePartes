-- =====================================================
-- FIX: Corregir trigger de bitácora para salidas
-- Problema: El trigger intenta insertar cuando ya existe entrada
-- Solución: Usar UPDATE con verificación correcta
-- =====================================================

USE mesa_partes_db;

-- Eliminar trigger antiguo
DROP TRIGGER IF EXISTS trg_bitacora_salida_documento;

-- Crear trigger corregido
DELIMITER //
CREATE TRIGGER trg_bitacora_salida_documento
AFTER INSERT ON salidas_documento
FOR EACH ROW
BEGIN
    DECLARE existe_registro INT;
    
    -- Verificar si ya existe un registro para este documento
    SELECT COUNT(*) INTO existe_registro
    FROM bitacora
    WHERE ID_documento = NEW.ID_documento;
    
    IF existe_registro > 0 THEN
        -- Actualizar registro existente con datos de salida
        UPDATE bitacora b
        LEFT JOIN usuarios u ON NEW.ID_usuario_salida = u.ID_usuario
        SET 
            b.tiene_salida = TRUE,
            b.destinatario = NEW.destinatario_salida,
            b.fecha_salida = NEW.fecha_salida,
            b.usuario_salida = CONCAT(IFNULL(u.nombre, ''), ' ', IFNULL(u.apellido, '')),
            b.numero_documento_salida = NEW.numero_documento_salida,
            b.observaciones_salida = NEW.observacion,
            b.archivo_salida_url = NEW.archivo_cargo_url
        WHERE b.ID_documento = NEW.ID_documento;
    ELSE
        -- Crear nuevo registro solo con datos de salida
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

SELECT '✅ Trigger corregido exitosamente' AS mensaje;
