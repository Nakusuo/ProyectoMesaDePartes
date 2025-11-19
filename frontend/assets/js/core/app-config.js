/**
 * Configuración global de la aplicación
 */
const APP_CONFIG = {
    // Modo debug (false en producción)
    DEBUG: false, // Cambiar a true solo en desarrollo
    
    // URLs de API
    API: {
        BASE_URL: 'http://localhost:8080/api',
        TIMEOUT: 30000 // 30 segundos
    },
    
    // Endpoints de la API
    API_ENDPOINTS: {
        AUTH: {
            LOGIN: '/auth/login',
            REGISTRO: '/auth/registro',
            LOGOUT: '/auth/logout'
        },
        DOCUMENTOS: {
            BASE: '/documentos',
            REGISTRAR: '/documentos/registrar',
            UPLOAD: '/documentos/upload',
            BITACORA: '/documentos/bitacora'
        },
        DERIVACIONES: {
            BASE: '/derivaciones',
            DERIVAR: '/derivaciones/derivar',
            TRAZABILIDAD: '/derivaciones/trazabilidad'
        },
        USUARIOS: {
            BASE: '/usuarios',
            PERFIL: '/usuarios/perfil'
        },
        REPORTES: {
            BASE: '/reportes',
            GENERAR: '/reportes/generar'
        }
    },
    
    // Configuración de archivos
    FILES: {
        MAX_SIZE: 10485760, // 10 MB
        ALLOWED_TYPES: ['application/pdf'],
        ALLOWED_EXTENSIONS: ['.pdf']
    },
    
    // Configuración de paginación
    PAGINATION: {
        DEFAULT_PAGE: 0,
        DEFAULT_SIZE: 10,
        SIZES: [5, 10, 20, 50, 100]
    },
    
    // Timeouts
    TIMEOUTS: {
        TOAST: 3000,
        REDIRECT: 2000,
        LOADING: 500
    },
    
    // Rutas de navegación
    ROUTES: {
        LOGIN: '../../auth/login.html',
        DASHBOARD: '../../common/dashboard.html',
        DOCUMENTOS: '../../documents/documentos.html',
        SALIDA_DOCUMENTO: '../../documents/salida-documento.html',
        GESTION_USUARIOS: '../../admin/gestion-usuarios.html',
        BITACORA: '../../admin/bitacora.html'
    },
    
    // Ruta por defecto después del login
    DEFAULT_DASHBOARD: '/frontend/pages/common/dashboard.html',
    
    // Claves de almacenamiento local
    STORAGE_KEYS: {
        TOKEN: 'token',
        USER: 'user',
        USER_INFO: 'userInfo'
    }
};

// Hacer disponible globalmente
window.APP_CONFIG = APP_CONFIG;
