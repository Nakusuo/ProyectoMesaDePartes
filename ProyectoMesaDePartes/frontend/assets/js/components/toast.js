// Sistema de Notificaciones Toast - Bonito y Animado

class ToastNotification {
    constructor() {
        this.container = null;
        this.init();
    }

    init() {
        // Crear contenedor si no existe
        if (!document.querySelector('.toast-container')) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.querySelector('.toast-container');
        }
    }

    show(options = {}) {
        const {
            type = 'info',
            title = '',
            message = '',
            duration = 4000,
            icon = null,
            compact = false
        } = options;

        // Crear el toast
        const toast = document.createElement('div');
        toast.className = `toast ${type} ${compact ? 'compact' : ''}`;

        // Icono según el tipo
        const icons = {
            success: icon || '✓',
            error: icon || '✕',
            warning: icon || '⚠',
            info: icon || 'ℹ',
            loading: icon || '⟳'
        };

        // Construir HTML
        toast.innerHTML = `
            <div class="toast-icon">${icons[type] || icons.info}</div>
            <div class="toast-content">
                ${title ? `<p class="toast-title">${title}</p>` : ''}
                ${message ? `<p class="toast-message">${message}</p>` : ''}
            </div>
            <button class="toast-close" onclick="this.parentElement.remove()">×</button>
        `;

        // Agregar al contenedor
        this.container.appendChild(toast);

        // Auto-remover después de la duración
        if (duration > 0) {
            setTimeout(() => {
                this.remove(toast);
            }, duration);
        }

        return toast;
    }

    remove(toast) {
        toast.classList.add('removing');
        setTimeout(() => {
            toast.remove();
        }, 400);
    }

    // Métodos de conveniencia
    success(title, message = '', duration = 4000) {
        return this.show({ type: 'success', title, message, duration });
    }

    error(title, message = '', duration = 5000) {
        return this.show({ type: 'error', title, message, duration });
    }

    warning(title, message = '', duration = 4000) {
        return this.show({ type: 'warning', title, message, duration });
    }

    info(title, message = '', duration = 4000) {
        return this.show({ type: 'info', title, message, duration });
    }

    loading(title, message = '') {
        return this.show({ type: 'loading', title, message, duration: 0 });
    }

    // Limpiar todas las notificaciones
    clearAll() {
        const toasts = this.container.querySelectorAll('.toast');
        toasts.forEach(toast => this.remove(toast));
    }
}

// Crear instancia global
window.toast = new ToastNotification();

// Función helper para compatibilidad con alert()
window.showNotification = function(message, type = 'info') {
    const titles = {
        success: '¡Éxito!',
        error: 'Error',
        warning: 'Advertencia',
        info: 'Información'
    };
    
    window.toast.show({
        type: type,
        title: titles[type] || 'Notificación',
        message: message
    });
};

// Sobrescribir alert() con notificaciones bonitas (opcional)
window.alertOriginal = window.alert;
window.alert = function(message) {
    // Detectar tipo por contenido
    let type = 'info';
    if (message.includes('✅') || message.includes('éxito') || message.includes('correcto')) {
        type = 'success';
    } else if (message.includes('❌') || message.includes('error') || message.includes('Error')) {
        type = 'error';
    } else if (message.includes('⚠️') || message.includes('advertencia') || message.includes('Debe')) {
        type = 'warning';
    }
    
    // Limpiar emojis del mensaje
    const cleanMessage = message.replace(/[✅❌⚠️✓✕]/g, '').trim();
    
    window.toast.show({
        type: type,
        title: type === 'success' ? '¡Éxito!' : type === 'error' ? 'Error' : type === 'warning' ? 'Advertencia' : 'Información',
        message: cleanMessage,
        duration: type === 'error' ? 5000 : 4000
    });
};
