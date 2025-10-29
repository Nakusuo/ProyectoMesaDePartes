-- Script para actualizar las contraseñas de usuarios de prueba a BCrypt
-- La contraseña para todos será: "12345"
-- Hash BCrypt de "12345": $2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi

USE mesa_partes_db;

-- Actualizar contraseñas a BCrypt (todas serán "12345")
UPDATE usuarios SET password_hash = '$2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi' WHERE username = 'mdepaz';
UPDATE usuarios SET password_hash = '$2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi' WHERE username = 'ecisneros';
UPDATE usuarios SET password_hash = '$2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi' WHERE username = 'accori';
UPDATE usuarios SET password_hash = '$2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi' WHERE username = 'jchiclla';
UPDATE usuarios SET password_hash = '$2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi' WHERE username = 'ghuaman';
UPDATE usuarios SET password_hash = '$2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi' WHERE username = 'osuarez';
UPDATE usuarios SET password_hash = '$2a$10$X5wFuQoXxJqhVlqVqXqZ5.8qS5qp3HZKJqQQzJ3YzZl1qU5qJ5qJi' WHERE username = 'nakusu';

-- Verificar la actualización
SELECT username, LEFT(password_hash, 20) as password_preview FROM usuarios;
