/**
 * Sistema de logging profesional para producción
 * Los logs solo se muestran si DEBUG está activado
 */
const Logger = {
    // Activar/desactivar según entorno
    enabled: APP_CONFIG?.DEBUG || false,

    /**
     * Log general (solo en desarrollo)
     */
    log: function(...args) {
        if (this.enabled) {
            console.log('[LOG]', ...args);
        }
    },

    /**
     * Log de información (solo en desarrollo)
     */
    info: function(...args) {
        if (this.enabled) {
            console.info('[INFO]', ...args);
        }
    },

    /**
     * Log de advertencia (solo en desarrollo)
     */
    warn: function(...args) {
        if (this.enabled) {
            console.warn('[WARN]', ...args);
        }
    },

    /**
     * Log de error (SIEMPRE se muestra)
     */
    error: function(...args) {
        console.error('[ERROR]', ...args);
    },

    /**
     * Log de debug detallado (solo en desarrollo)
     */
    debug: function(...args) {
        if (this.enabled) {
            console.debug('[DEBUG]', ...args);
        }
    },

    /**
     * Log de tabla (solo en desarrollo)
     */
    table: function(data) {
        if (this.enabled) {
            console.table(data);
        }
    }
};

// Exportar para uso global
window.Logger = Logger;
