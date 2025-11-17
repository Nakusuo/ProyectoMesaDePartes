// =====================================================
// MÓDULO DE NOTIFICACIONES
// Sistema de notificaciones en tiempo real
// =====================================================

const NotificacionModule = {
    intervaloActualizacion: null,
    tiempoActualizacion: 30000, // 30 segundos

    /**
     * Inicializar sistema de notificaciones
     */
    inicializar: function() {
        const usuario = JSON.parse(localStorage.getItem('usuario'));
        if (!usuario) return;

        // Cargar notificaciones iniciales
        this.cargarNotificaciones();
        
        // Actualizar periódicamente
        this.intervaloActualizacion = setInterval(() => {
            this.cargarNotificaciones();
        }, this.tiempoActualizacion);

        // Crear contenedor de notificaciones si no existe
        if (!document.getElementById('notification-container')) {
            this.crearContenedorNotificaciones();
        }
    },

    /**
     * Detener actualizaciones automáticas
     */
    detener: function() {
        if (this.intervaloActualizacion) {
            clearInterval(this.intervaloActualizacion);
        }
    },

    /**
     * Crear contenedor de notificaciones en el header
     */
    crearContenedorNotificaciones: function() {
        const container = document.createElement('div');
        container.id = 'notification-container';
        container.className = 'notification-bell-container';
        container.innerHTML = `
            <button class="notification-bell" onclick="NotificacionModule.togglePanel()">
                <i class="fas fa-bell"></i>
                <span class="notification-badge" id="notification-count" style="display: none;">0</span>
            </button>
            <div class="notification-panel" id="notification-panel" style="display: none;">
                <div class="notification-header">
                    <h4>Notificaciones</h4>
                    <button class="btn-small" onclick="NotificacionModule.marcarTodasLeidas()">
                        Marcar todas como leídas
                    </button>
                </div>
                <div class="notification-list" id="notification-list">
                    <p class="text-center">Cargando notificaciones...</p>
                </div>
            </div>
        `;

        // Agregar al header
        const header = document.querySelector('.header') || document.querySelector('header');
        if (header) {
            header.appendChild(container);
        }
    },

    /**
     * Alternar panel de notificaciones
     */
    togglePanel: function() {
        const panel = document.getElementById('notification-panel');
        if (panel) {
            panel.style.display = panel.style.display === 'none' ? 'block' : 'none';
        }
    },

    /**
     * Cargar notificaciones del usuario
     */
    cargarNotificaciones: async function() {
        try {
            const usuario = JSON.parse(localStorage.getItem('usuario'));
            if (!usuario) return;

            const response = await fetch(`${API_URL}/notificaciones/ultimas/${usuario.idUsuario}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                const notificaciones = await response.json();
                this.actualizarContador(notificaciones);
                this.renderizarNotificaciones(notificaciones);
            }
        } catch (error) {
            console.error('Error al cargar notificaciones:', error);
        }
    },

    /**
     * Actualizar contador de notificaciones no leídas
     */
    actualizarContador: function(notificaciones) {
        const noLeidas = notificaciones.filter(n => !n.leida).length;
        const badge = document.getElementById('notification-count');
        
        if (badge) {
            if (noLeidas > 0) {
                badge.textContent = noLeidas > 9 ? '9+' : noLeidas;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        }
    },

    /**
     * Renderizar lista de notificaciones
     */
    renderizarNotificaciones: function(notificaciones) {
        const list = document.getElementById('notification-list');
        if (!list) return;

        if (notificaciones.length === 0) {
            list.innerHTML = '<p class="text-center text-muted">No hay notificaciones</p>';
            return;
        }

        list.innerHTML = notificaciones.map(notif => `
            <div class="notification-item ${notif.leida ? 'read' : 'unread'}" 
                 onclick="NotificacionModule.marcarLeida(${notif.idNotificacion})">
                <div class="notification-icon">
                    <i class="${this.getIconoPorTipo(notif.tipo)}"></i>
                </div>
                <div class="notification-content">
                    <h5>${notif.titulo}</h5>
                    <p>${notif.mensaje}</p>
                    <small>${this.formatearFecha(notif.fechaCreacion)}</small>
                </div>
            </div>
        `).join('');
    },

    /**
     * Obtener icono según tipo de notificación
     */
    getIconoPorTipo: function(tipo) {
        const iconos = {
            'REGISTRO': 'fas fa-file-alt',
            'DERIVACION': 'fas fa-share',
            'CAMBIO_ESTADO': 'fas fa-sync',
            'ASIGNACION': 'fas fa-user-check'
        };
        return iconos[tipo] || 'fas fa-bell';
    },

    /**
     * Marcar notificación como leída
     */
    marcarLeida: async function(idNotificacion) {
        try {
            const response = await fetch(`${API_URL}/notificaciones/marcar-leida/${idNotificacion}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                this.cargarNotificaciones();
            }
        } catch (error) {
            console.error('Error al marcar notificación:', error);
        }
    },

    /**
     * Marcar todas las notificaciones como leídas
     */
    marcarTodasLeidas: async function() {
        try {
            const usuario = JSON.parse(localStorage.getItem('usuario'));
            if (!usuario) return;

            const response = await fetch(`${API_URL}/notificaciones/marcar-todas-leidas/${usuario.idUsuario}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                mostrarToast('Todas las notificaciones marcadas como leídas', 'success');
                this.cargarNotificaciones();
            }
        } catch (error) {
            console.error('Error al marcar todas:', error);
            mostrarToast('Error al marcar notificaciones', 'error');
        }
    },

    /**
     * Formatear fecha de notificación
     */
    formatearFecha: function(fecha) {
        const ahora = new Date();
        const fechaNotif = new Date(fecha);
        const diferencia = ahora - fechaNotif;
        
        const minutos = Math.floor(diferencia / 60000);
        const horas = Math.floor(diferencia / 3600000);
        const dias = Math.floor(diferencia / 86400000);

        if (minutos < 1) return 'Hace un momento';
        if (minutos < 60) return `Hace ${minutos} minuto${minutos > 1 ? 's' : ''}`;
        if (horas < 24) return `Hace ${horas} hora${horas > 1 ? 's' : ''}`;
        if (dias < 7) return `Hace ${dias} día${dias > 1 ? 's' : ''}`;
        
        return fechaNotif.toLocaleDateString('es-ES', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        });
    },

    /**
     * Mostrar notificación toast personalizada
     */
    mostrarNotificacionToast: function(titulo, mensaje, tipo = 'info') {
        const toast = document.createElement('div');
        toast.className = `notification-toast notification-${tipo}`;
        toast.innerHTML = `
            <div class="toast-icon">
                <i class="${this.getIconoPorTipo(tipo)}"></i>
            </div>
            <div class="toast-content">
                <strong>${titulo}</strong>
                <p>${mensaje}</p>
            </div>
            <button class="toast-close" onclick="this.parentElement.remove()">×</button>
        `;

        document.body.appendChild(toast);

        // Auto-remover después de 5 segundos
        setTimeout(() => {
            toast.classList.add('fade-out');
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    }
};

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    if (localStorage.getItem('token')) {
        NotificacionModule.inicializar();
    }
});

// Limpiar al cerrar sesión
window.addEventListener('beforeunload', () => {
    NotificacionModule.detener();
});

// Exportar para uso global
window.NotificacionModule = NotificacionModule;
