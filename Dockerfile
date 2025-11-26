# =====================================================
# DOCKERFILE PARA RAILWAY - Mesa de Partes PNP
# Optimizado para Spring Boot + Java 21
# =====================================================

# ============ ETAPA 1: BUILD ============
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copiar archivos de Maven
COPY backend/pom.xml ./
COPY backend/mvnw ./
COPY backend/mvnw.cmd ./
COPY backend/.mvn ./.mvn

# Descargar dependencias (esta capa se cachea)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copiar código fuente
COPY backend/src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests -B

# ============ ETAPA 2: RUNTIME ============
FROM eclipse-temurin:21-jre-alpine

# Metadatos
LABEL maintainer="Mesa de Partes PNP"
LABEL version="1.0"
LABEL description="Sistema de Mesa de Partes Digital - Backend para Railway"

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /build/target/*.jar app.jar

# Crear directorios necesarios
RUN mkdir -p /app/uploads/documentos /app/uploads/cargos /app/logs && \
    chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Puerto de la aplicación (Railway asigna automáticamente)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
