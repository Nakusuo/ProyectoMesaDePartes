/**
 * Sistema de Logging Condicional para Producción
 * Autor: Sistema Mesa de Partes PNP
 * Versión: 3.1
 * 
 * USO:
 * import { log, error, warn, info, debug } from './logger.js';
 * 
 * log('Mensaje normal');
 * error('Error crítico', errorObj);
 * warn('Advertencia');
 * info('Información');
 * debug('Depuración detallada');
 */

// ⚙️ CONFIGURACIÓN: Cambiar a false en producción
const DEBUG_MODE = false; // ⚠️ IMPORTANTE: Cambiar a false antes de desplegar a producción

// Niveles de logging
const LogLevel = {
    NONE: 0,    // Sin logs
    ERROR: 1,   // Solo errores críticos
    WARN: 2,    // Errores y advertencias
    INFO: 3,    // Errores, advertencias e información
    DEBUG: 4    // Todo (incluyendo depuración)
};

// Nivel actual (cambiar según necesidad)
const CURRENT_LOG_LEVEL = DEBUG_MODE ? LogLevel.DEBUG : LogLevel.ERROR;

/**
 * Formatea el mensaje con timestamp y nivel
 */
function formatMessage(level, ...args) {
    const timestamp = new Date().toLocaleTimeString('es-PE');
    const levelEmoji = {
        ERROR: '❌',
        WARN: '⚠️',
        INFO: 'ℹ️',
        DEBUG: '🔍',
        LOG: '📝'
    };

    return [`[${timestamp}] ${levelEmoji[level] || ''}`, ...args];
}

/**
 * Logging condicional - Solo se ejecuta si DEBUG_MODE está activado
 */
export const log = (...args) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.DEBUG) {
        console.log(...formatMessage('LOG', ...args));
    }
};

/**
 * Errores - Siempre se muestran (críticos para debugging)
 */
export const error = (...args) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.ERROR) {
        console.error(...formatMessage('ERROR', ...args));
    }
};

/**
 * Advertencias - Se muestran en modo desarrollo
 */
export const warn = (...args) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.WARN) {
        console.warn(...formatMessage('WARN', ...args));
    }
};

/**
 * Información - Se muestra en modo desarrollo
 */
export const info = (...args) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.INFO) {
        console.info(...formatMessage('INFO', ...args));
    }
};

/**
 * Debug detallado - Solo en modo desarrollo
 */
export const debug = (...args) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.DEBUG) {
        console.log(...formatMessage('DEBUG', ...args));
    }
};

/**
 * Agrupa logs relacionados
 */
export const group = (label, callback) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.DEBUG) {
        console.group(label);
        callback();
        console.groupEnd();
    }
};

/**
 * Tabla de datos (útil para arrays de objetos)
 */
export const table = (data) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.DEBUG) {
        console.table(data);
    }
};

/**
 * Timer para medir rendimiento
 */
export const time = (label) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.DEBUG) {
        console.time(label);
    }
};

export const timeEnd = (label) => {
    if (CURRENT_LOG_LEVEL >= LogLevel.DEBUG) {
        console.timeEnd(label);
    }
};

// Exportar estado del modo debug para uso condicional
export const isDebugMode = () => DEBUG_MODE;

// Log inicial del sistema
if (DEBUG_MODE) {
    console.log('%c🔧 MODO DESARROLLO ACTIVADO', 'background: #ff9800; color: white; padding: 5px; font-weight: bold;');
    console.log('%c⚠️ Recuerda: Cambiar DEBUG_MODE a false en producción', 'color: red; font-weight: bold;');
} else {
    console.log('%c✅ Modo Producción - Logs minimizados', 'background: #4caf50; color: white; padding: 5px;');
}

// Exportar por defecto
export default {
    log,
    error,
    warn,
    info,
    debug,
    group,
    table,
    time,
    timeEnd,
    isDebugMode
};
