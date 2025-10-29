-- Actualizar todas las contraseñas a "123456"
USE mesa_partes_db;

SET SQL_SAFE_UPDATES = 0;

UPDATE usuarios 
SET password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

SET SQL_SAFE_UPDATES = 1;

SELECT username, 'Contraseña actualizada a: 123456' AS estado 
FROM usuarios 
ORDER BY ID_usuario;
