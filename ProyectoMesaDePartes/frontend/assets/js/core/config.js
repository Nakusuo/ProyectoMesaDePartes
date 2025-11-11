const APP_CONFIG = {
    API_BASE_URL: 'http://localhost:8080',
    
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
    DEFAULT_DASHBOARD: '/pages/common/dashboard.html'
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
