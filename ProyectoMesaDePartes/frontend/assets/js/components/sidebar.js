// Sidebar Management
class SidebarManager {
    constructor() {
        this.sidebar = null;
        this.mainContent = null;
        this.currentPage = window.location.pathname.split('/').pop() || 'index.html';
        this.init();
    }

    async init() {
        await this.loadSidebar();
        this.filterMenuByPermissions();
        this.attachEventListeners();
        this.setActivePage();
        this.loadUserInfo();
    }

    filterMenuByPermissions() {
        // Esperar a que permissionsManager esté disponible
        if (!window.permissionsManager) {
            console.warn('PermissionsManager no disponible');
            return;
        }

        const pm = window.permissionsManager;
        
        // Obtener todos los elementos de menú con data-permission
        const navItems = document.querySelectorAll('.nav-item[data-permission]');
        
        navItems.forEach(item => {
            const permission = item.getAttribute('data-permission');
            if (permission && !pm.hasPermission(permission)) {
                item.style.display = 'none';
            }
        });
        
        // Obtener todos los enlaces de navegación
        const navLinks = document.querySelectorAll('.sidebar-nav a');
        
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            
            // Ocultar enlaces según permisos de páginas específicas
            if (href === 'registro.html' && !pm.hasPermission('VER_REGISTRO')) {
                link.parentElement.style.display = 'none';
            }
            if (href === 'bitacora.html' && !pm.hasPermission('VER_BITACORA')) {
                link.parentElement.style.display = 'none';
            }
            if (href === 'gestion-usuarios.html' && !pm.hasPermission('VER_USUARIOS')) {
                link.parentElement.style.display = 'none';
            }
            if (href === 'salida-documento.html' && !pm.hasPermission('VER_SALIDAS')) {
                link.parentElement.style.display = 'none';
            }
        });
    }

    async loadSidebar() {
        try {
            const response = await fetch('/pages/common/sidebar.html');
            const sidebarHTML = await response.text();
            
            // Insertar el sidebar al inicio del body
            document.body.insertAdjacentHTML('afterbegin', sidebarHTML);
            
            this.sidebar = document.getElementById('sidebar');
            this.mainContent = document.querySelector('.main-content');
        } catch (error) {
            console.error('Error loading sidebar:', error);
        }
    }

    attachEventListeners() {
        // Toggle sidebar
        const toggleBtn = document.getElementById('sidebar-toggle');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => this.toggleSidebar());
        }

        // Mobile overlay
        const overlay = document.getElementById('sidebar-overlay');
        
        if (overlay) {
            overlay.addEventListener('click', () => this.toggleMobileSidebar());
        }

        // Logout
        const logoutBtn = document.getElementById('btn-logout');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.logout();
            });
        }

        // Handle window resize
        window.addEventListener('resize', () => {
            if (window.innerWidth > 768) {
                this.sidebar?.classList.remove('mobile-open');
                document.getElementById('sidebar-overlay')?.classList.remove('active');
            }
        });
    }

    toggleSidebar() {
        this.sidebar?.classList.toggle('collapsed');
        this.mainContent?.classList.toggle('sidebar-collapsed');
        
        // Guardar preferencia
        const isCollapsed = this.sidebar?.classList.contains('collapsed');
        localStorage.setItem('sidebarCollapsed', isCollapsed);
    }

    toggleMobileSidebar() {
        this.sidebar?.classList.toggle('mobile-open');
        document.getElementById('sidebar-overlay')?.classList.toggle('active');
    }

    setActivePage() {
        const navLinks = document.querySelectorAll('.nav-link[data-page]');
        navLinks.forEach(link => {
            const page = link.getAttribute('data-page');
            if (this.currentPage === page || 
                (this.currentPage === '' && page === 'dashboard.html')) {
                link.classList.add('active');
            }
        });
    }

    async loadUserInfo() {
        try {
            const token = localStorage.getItem('token');
            if (!token) return;

            // Intentar obtener info del usuario desde la API
            const API_URL = window.API_URL || 'http://localhost:8080/api';
            
            try {
                const response = await fetch(`${API_URL}/auth/me`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    const userInfo = await response.json();
                    
                    // Usar los IDs correctos del sidebar.html
                    const userName = document.getElementById('user-name');
                    const userRole = document.getElementById('user-role');
                    const userInitials = document.getElementById('user-initials');

                    if (userName) {
                        userName.textContent = `${userInfo.nombre || ''} ${userInfo.apellido || ''}`.trim() || userInfo.username;
                    }

                    if (userRole && userInfo.roles && userInfo.roles.length > 0) {
                        // Manejar tanto objetos {nombre: "Admin"} como strings "Admin"
                        const roleName = typeof userInfo.roles[0] === 'object' ? userInfo.roles[0].nombre : userInfo.roles[0];
                        userRole.textContent = roleName || 'Usuario';
                    }

                    if (userInitials) {
                        const inicial = userInfo.nombre?.charAt(0) || userInfo.username?.charAt(0) || 'U';
                        userInitials.textContent = inicial.toUpperCase();
                    }
                    
                    // Guardar en localStorage para futuras referencias
                    localStorage.setItem('userInfo', JSON.stringify(userInfo));
                    return;
                }
            } catch (apiError) {
                console.error('Error al cargar desde API:', apiError);
            }

            // Fallback: Usar localStorage si la API falla
            const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
            
            const userName = document.getElementById('user-name');
            const userRole = document.getElementById('user-role');
            const userInitials = document.getElementById('user-initials');

            if (userName && (userInfo.nombre || userInfo.username)) {
                userName.textContent = `${userInfo.nombre || ''} ${userInfo.apellido || ''}`.trim() || userInfo.username;
            }

            if (userRole && userInfo.roles) {
                const role = Array.isArray(userInfo.roles) ? userInfo.roles[0] : userInfo.roles;
                const roleName = typeof role === 'object' ? role.nombre : role;
                userRole.textContent = roleName || 'Usuario';
            }

            if (userInitials) {
                const inicial = userInfo.nombre?.charAt(0) || userInfo.username?.charAt(0) || 'U';
                userInitials.textContent = inicial.toUpperCase();
            }
        } catch (error) {
            console.error('No se pudo cargar info del usuario:', error);
            // Valores por defecto
            const userName = document.getElementById('user-name');
            const userRole = document.getElementById('user-role');
            const userInitials = document.getElementById('user-initials');
            
            if (userName) userName.textContent = 'Usuario';
            if (userRole) userRole.textContent = 'Cargando...';
            if (userInitials) userInitials.textContent = 'U';
        }
    }

    logout() {
        if (confirm('¿Está seguro que desea cerrar sesión?')) {
            // Limpiar localStorage
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            
            // Redirigir al login
            window.location.href = 'login.html';
        }
    }

    // Restaurar estado del sidebar
    restoreState() {
        const isCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
        if (isCollapsed) {
            this.sidebar?.classList.add('collapsed');
            this.mainContent?.classList.add('sidebar-collapsed');
        }
    }
}

// Inicializar sidebar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        window.sidebarManager = new SidebarManager();
    });
} else {
    window.sidebarManager = new SidebarManager();
}
