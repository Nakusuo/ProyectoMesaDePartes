const APP_CONFIG = {
    // Detectar automáticamente el protocolo (HTTP/HTTPS)
    API_BASE_URL: (() => {
        const protocol = window.location.protocol; // 'http:' o 'https:'
        const hostname = window.location.hostname; // 'localhost' o dominio
        
        // Desarrollo local
        if (hostname === 'localhost' || hostname === '127.0.0.1') {
            return protocol === 'https:' 
                ? 'https://localhost:8443'  // Puerto HTTPS
                : 'http://localhost:8080';  // Puerto HTTP
        }
        
        // Producción: usar el mismo protocolo que el frontend
        const port = protocol === 'https:' ? '' : ':8080'; // HTTPS en 443 (por defecto)
        return `${protocol}//${hostname}${port}`;
    })(),
    
    API_ENDPOINTS: {
        AUTH: {
            LOGIN: '/api/auth/login',
            REGISTRO: '/api/auth/registro',
            LOGOUT: '/api/auth/logout'
        },
        USUARIOS: '/api/usuarios',
        AREAS: '/api/areas',
        TIPOS_DOCUMENTO: '/api/tipos-documento',
        DOCUMENTOS: '/api/documentos'
    },
    
    STORAGE_KEYS: {
        TOKEN: 'token',
        USER: 'user'
    },
    
    REQUEST_TIMEOUT: 30000,
    PUBLIC_PAGES: ['login.html', 'index.html'],
    DEFAULT_DASHBOARD: '../../common/dashboard.html'
};

function getApiUrl(endpoint) {
    return `${APP_CONFIG.API_BASE_URL}${endpoint}`;
}

function getAuthToken() {
    return localStorage.getItem(APP_CONFIG.STORAGE_KEYS.TOKEN);
}

function getAuthHeaders() {
    const token = getAuthToken();
    const headers = {
        'Content-Type': 'application/json'
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    return headers;
}

// Hacer disponible globalmente para compatibilidad con código legacy
window.APP_CONFIG = APP_CONFIG;
window.API_URL = APP_CONFIG.API_BASE_URL + '/api'; // Agregar /api al final
window.getApiUrl = getApiUrl;
window.getAuthToken = getAuthToken;
window.getAuthHeaders = getAuthHeaders;

console.log('✅ Config cargado. API_URL:', window.API_URL);
