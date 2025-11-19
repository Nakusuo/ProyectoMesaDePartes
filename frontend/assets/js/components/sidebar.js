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

        // Ocultar secciones vacías (sin ítems visibles)
        const navSections = document.querySelectorAll('.nav-section');
        navSections.forEach(section => {
            const visibleItems = section.querySelectorAll('.nav-item:not([style*="display: none"])');
            if (visibleItems.length === 0) {
                section.style.display = 'none';
            }
        });
    }

    async loadSidebar() {
        try {
            // Detectar la ubicación actual y ajustar la ruta
            const currentPath = window.location.pathname;
            let sidebarPath = '/frontend/pages/common/sidebar.html';
            
            // Si estamos en Live Server o servidor local, ajustar la ruta
            if (currentPath.includes('/pages/')) {
                const depth = (currentPath.match(/\/pages\//g) || []).length;
                if (depth === 1) {
                    // Estamos en /pages/auth/ o /pages/common/
                    sidebarPath = '../common/sidebar.html';
                } else if (depth === 2) {
                    // Estamos en /pages/documents/ o /pages/admin/
                    sidebarPath = '../common/sidebar.html';
                }
            }
            
            const response = await fetch(sidebarPath);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const sidebarHTML = await response.text();
            
            // Insertar el sidebar al inicio del body
            document.body.insertAdjacentHTML('afterbegin', sidebarHTML);
            
            this.sidebar = document.getElementById('sidebar');
            this.mainContent = document.querySelector('.main-content');
        } catch (error) {
            console.error('Error loading sidebar:', error);
            // Crear un sidebar mínimo en caso de error
            this.createFallbackSidebar();
        }
    }
    
    createFallbackSidebar() {
        const fallbackHTML = `
            <aside id="sidebar" class="sidebar">
                <div class="sidebar-header">
                    <h3>Mesa de Partes PNP</h3>
                </div>
                <nav class="sidebar-nav">
                    <a href="../common/dashboard.html" class="nav-link">Dashboard</a>
                    <a href="../documents/documentos.html" class="nav-link">Documentos</a>
                </nav>
                <div class="sidebar-footer">
                    <button id="btn-logout" class="btn btn-secondary">Cerrar Sesión</button>
                </div>
            </aside>
        `;
        document.body.insertAdjacentHTML('afterbegin', fallbackHTML);
        this.sidebar = document.getElementById('sidebar');
        this.mainContent = document.querySelector('.main-content');
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
            localStorage.removeItem('user');
            
            // Detectar ruta relativa al login
            const currentPath = window.location.pathname;
            let loginPath = '../auth/login.html';
            
            if (currentPath.includes('/auth/')) {
                loginPath = 'login.html';
            }
            
            // Redirigir al login
            window.location.href = loginPath;
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
