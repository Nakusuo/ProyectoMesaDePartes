// Sistema de Permisos por Rol
const ROLES = {
    ADMIN: 'Administrador',
    MESA_PARTES: 'Mesa de Partes',
    TRABAJADOR: 'Trabajador',
    JEFATURA: 'Jefatura'
};

const PERMISSIONS = {
    // Navegación
    VER_DASHBOARD: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.TRABAJADOR, ROLES.JEFATURA],
    VER_REGISTRO: [ROLES.ADMIN, ROLES.MESA_PARTES],
    VER_BITACORA: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.JEFATURA],
    VER_USUARIOS: [ROLES.ADMIN],
    VER_SALIDAS: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.JEFATURA],
    
    // Documentos
    VER_TODOS_DOCUMENTOS: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.JEFATURA],
    VER_SOLO_ASIGNADOS: [ROLES.TRABAJADOR],
    REGISTRAR_DOCUMENTO: [ROLES.ADMIN, ROLES.MESA_PARTES],
    MODIFICAR_DOCUMENTO: [ROLES.ADMIN, ROLES.MESA_PARTES],
    ELIMINAR_DOCUMENTO: [ROLES.ADMIN],
    ASIGNAR_DOCUMENTO: [ROLES.ADMIN, ROLES.JEFATURA],
    REGISTRAR_SALIDA: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.JEFATURA],
    
    // Usuarios
    CREAR_USUARIO: [ROLES.ADMIN],
    MODIFICAR_USUARIO: [ROLES.ADMIN],
    ELIMINAR_USUARIO: [ROLES.ADMIN],
    
    // Bitácora
    FILTRAR_BITACORA: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.JEFATURA]
};

class PermissionsManager {
    constructor() {
        this.userRole = null;
        this.userInfo = null;
        this.loadUserRole();
    }

    loadUserRole() {
        try {
            const userInfoStr = localStorage.getItem('userInfo');
            if (userInfoStr) {
                this.userInfo = JSON.parse(userInfoStr);
                if (this.userInfo.roles && this.userInfo.roles.length > 0) {
                    const role = this.userInfo.roles[0];
                    this.userRole = typeof role === 'object' ? role.nombre : role;
                }
            }
        } catch (error) {
            console.error('Error al cargar rol del usuario:', error);
        }
    }

    hasPermission(permission) {
        if (!this.userRole) {
            this.loadUserRole();
        }
        
        const allowedRoles = PERMISSIONS[permission];
        if (!allowedRoles) {
            console.warn(`Permiso desconocido: ${permission}`);
            return false;
        }
        
        return allowedRoles.includes(this.userRole);
    }

    canAccessPage(page) {
        const pagePermissions = {
            'dashboard.html': 'VER_DASHBOARD',
            'registro.html': 'VER_REGISTRO',
            'bitacora.html': 'VER_BITACORA',
            'gestion-usuarios.html': 'VER_USUARIOS',
            'salida-documento.html': 'VER_SALIDAS'
        };

        const permission = pagePermissions[page];
        if (!permission) return true; // Página pública o no controlada

        return this.hasPermission(permission);
    }

    redirectIfUnauthorized(currentPage) {
        if (!this.canAccessPage(currentPage)) {
            alert('No tiene permisos para acceder a esta página');
            window.location.href = 'dashboard.html';
            return true;
        }
        return false;
    }

    shouldFilterDocumentsByUser() {
        return this.hasPermission('VER_SOLO_ASIGNADOS');
    }

    canModifyDocument() {
        return this.hasPermission('MODIFICAR_DOCUMENTO');
    }

    canAssignDocument() {
        return this.hasPermission('ASIGNAR_DOCUMENTO');
    }

    canRegisterDocument() {
        return this.hasPermission('REGISTRAR_DOCUMENTO');
    }

    getUserId() {
        return this.userInfo?.idUsuario || null;
    }

    getUserRole() {
        return this.userRole;
    }
}

// Instancia global
window.permissionsManager = new PermissionsManager();
