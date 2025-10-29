// Sidebar Navigation Component

function createSidebar() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const currentPage = window.location.pathname.split('/').pop();
    
    const sidebarHTML = `
        <aside class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div class="sidebar-logo">Logo PNP</div>
                <div class="sidebar-title">Mesa de Partes</div>
                <div class="sidebar-subtitle">Policía Nacional del Perú</div>
            </div>
            
            <nav>
                <ul class="sidebar-nav">
                    <li class="sidebar-nav-item">
                        <a href="dashboard.html" class="sidebar-nav-link ${currentPage === 'dashboard.html' ? 'active' : ''}">
                            <span class="sidebar-nav-icon">📊</span>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="registro.html" class="sidebar-nav-link ${currentPage === 'registro.html' ? 'active' : ''}">
                            <span class="sidebar-nav-icon">📝</span>
                            <span>Registrar Documento</span>
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="bitacora.html" class="sidebar-nav-link ${currentPage === 'bitacora.html' ? 'active' : ''}">
                            <span class="sidebar-nav-icon">📋</span>
                            <span>Bitácora</span>
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="gestion-usuarios.html" class="sidebar-nav-link ${currentPage === 'gestion-usuarios.html' ? 'active' : ''}">
                            <span class="sidebar-nav-icon">👥</span>
                            <span>Gestión de Usuarios</span>
                        </a>
                    </li>
                </ul>
            </nav>
            
            <div class="sidebar-user">
                <div class="sidebar-user-info">
                    <div class="sidebar-user-avatar">${user.username ? user.username.substring(0, 2).toUpperCase() : '??'}</div>
                    <div>
                        <div class="sidebar-user-name">${user.username || 'Usuario'}</div>
                    </div>
                </div>
                <button class="sidebar-logout" onclick="handleLogout()">
                    🚪 Cerrar Sesión
                </button>
            </div>
        </aside>
        
        <button class="sidebar-toggle" id="sidebarToggle" onclick="toggleSidebar()">
            ☰
        </button>
    `;
    
    document.body.insertAdjacentHTML('afterbegin', sidebarHTML);
}

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.querySelector('.main-content');
    const toggleBtn = document.getElementById('sidebarToggle');
    
    sidebar.classList.toggle('collapsed');
    mainContent.classList.toggle('expanded');
    toggleBtn.classList.toggle('moved');
}

function handleLogout() {
    if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'login.html';
    }
}

// Verificar autenticación
function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Inicializar sidebar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    if (checkAuth()) {
        createSidebar();
    }
});
