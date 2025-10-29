-- Script para actualizar todas las contraseñas de usuarios a "123456"
-- Hash BCrypt para "123456": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

USE mesa_partes_db;

UPDATE usuarios 
SET password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE id_usuario > 0;

SELECT id_usuario, username, email, password_hash 
FROM usuarios;

SELECT 'Contraseñas actualizadas correctamente. Todos los usuarios ahora tienen la contraseña: 123456' AS mensaje;
