#!/bin/bash
# ============================================
# Script de Backup Automático - Linux/Mac
# Sistema Mesa de Partes Digital PNP
# ============================================

# Configuración
BACKUP_DIR="/backup/mesa_partes"
DB_NAME="mesa_partes_db"
DB_USER="root"
DB_PASS="root"
UPLOADS_DIR="/path/to/ProyectoMesaDePartes/backend/uploads"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
DATE=$(date +"%Y-%m-%d %H:%M:%S")

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Crear directorio de backup si no existe
if [ ! -d "$BACKUP_DIR" ]; then
    mkdir -p "$BACKUP_DIR"
    echo -e "${GREEN}[$DATE] Directorio de backup creado: $BACKUP_DIR${NC}"
else
    echo -e "${GREEN}[$DATE] Usando directorio de backup existente: $BACKUP_DIR${NC}"
fi

echo ""
echo "============================================"
echo "  BACKUP MESA DE PARTES DIGITAL - PNP"
echo "============================================"
echo "Fecha: $(date)"
echo "Base de datos: $DB_NAME"
echo "Directorio destino: $BACKUP_DIR"
echo "============================================"
echo ""

# Backup de la base de datos
echo -e "${YELLOW}[1/3] Realizando backup de base de datos...${NC}"
mysqldump -u "$DB_USER" -p"$DB_PASS" \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    "$DB_NAME" > "$BACKUP_DIR/db_$TIMESTAMP.sql" 2>"$BACKUP_DIR/error_db.log"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}[OK] Backup de base de datos completado: db_$TIMESTAMP.sql${NC}"
    
    # Comprimir el SQL
    echo -e "${YELLOW}[2/3] Comprimiendo backup de base de datos...${NC}"
    gzip "$BACKUP_DIR/db_$TIMESTAMP.sql"
    
    if [ -f "$BACKUP_DIR/db_$TIMESTAMP.sql.gz" ]; then
        echo -e "${GREEN}[OK] Archivo comprimido: db_$TIMESTAMP.sql.gz${NC}"
        
        # Calcular tamaño
        SIZE=$(du -h "$BACKUP_DIR/db_$TIMESTAMP.sql.gz" | cut -f1)
        echo -e "${GREEN}[INFO] Tamaño del backup: $SIZE${NC}"
    fi
else
    echo -e "${RED}[ERROR] Fallo al realizar backup de base de datos${NC}"
    echo -e "${RED}Ver detalles en: $BACKUP_DIR/error_db.log${NC}"
    cat "$BACKUP_DIR/error_db.log"
fi

# Backup de archivos uploads
if [ -d "$UPLOADS_DIR" ]; then
    echo -e "${YELLOW}[3/3] Realizando backup de archivos uploads...${NC}"
    tar -czf "$BACKUP_DIR/uploads_$TIMESTAMP.tar.gz" -C "$(dirname "$UPLOADS_DIR")" "$(basename "$UPLOADS_DIR")" 2>"$BACKUP_DIR/error_uploads.log"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}[OK] Backup de uploads completado: uploads_$TIMESTAMP.tar.gz${NC}"
        
        # Calcular tamaño
        SIZE=$(du -h "$BACKUP_DIR/uploads_$TIMESTAMP.tar.gz" | cut -f1)
        echo -e "${GREEN}[INFO] Tamaño del backup: $SIZE${NC}"
    else
        echo -e "${RED}[ERROR] Fallo al realizar backup de uploads${NC}"
        echo -e "${RED}Ver detalles en: $BACKUP_DIR/error_uploads.log${NC}"
    fi
else
    echo -e "${YELLOW}[AVISO] Directorio uploads no encontrado: $UPLOADS_DIR${NC}"
fi

# Limpiar backups antiguos (mayores a 30 días)
echo ""
echo -e "${YELLOW}Limpiando backups antiguos (mayores a 30 días)...${NC}"
DELETED=$(find "$BACKUP_DIR" -name "*.gz" -type f -mtime +30 -delete -print | wc -l)
if [ $DELETED -gt 0 ]; then
    echo -e "${GREEN}[OK] $DELETED backups antiguos eliminados${NC}"
else
    echo -e "${GREEN}[INFO] No hay backups antiguos para eliminar${NC}"
fi

# Resumen
echo ""
echo "============================================"
echo "  RESUMEN DEL BACKUP"
echo "============================================"
ls -lh "$BACKUP_DIR"/*$TIMESTAMP* 2>/dev/null || echo "No se encontraron archivos"
echo "============================================"
echo -e "${GREEN}[$DATE] Backup completado exitosamente${NC}"
echo "============================================"
echo ""

# Registrar en log
echo "[$DATE] Backup completado - db_$TIMESTAMP.sql.gz, uploads_$TIMESTAMP.tar.gz" >> "$BACKUP_DIR/backup_history.log"

# Enviar notificación (opcional - descomentar si se desea)
# echo "Backup de Mesa de Partes completado exitosamente" | mail -s "Backup OK - $(date)" admin@ejemplo.com

exit 0
