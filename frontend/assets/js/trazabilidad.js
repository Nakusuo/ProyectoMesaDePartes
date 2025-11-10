// =====================================================
// MÓDULO DE TRAZABILIDAD
// Visualización del historial y seguimiento de documentos
// =====================================================

const TrazabilidadModule = {
    /**
     * Mostrar modal de trazabilidad completa
     */
    mostrarTrazabilidad: async function(idDocumento) {
        try {
            const trazabilidad = await this.obtenerTrazabilidad(idDocumento);
            
            const modal = document.createElement('div');
            modal.className = 'modal-overlay';
            modal.innerHTML = `
                <div class="modal-content modal-xl">
                    <div class="modal-header">
                        <h3>Trazabilidad del Documento ${trazabilidad.codigo}</h3>
                        <button class="btn-close" onclick="this.closest('.modal-overlay').remove()">×</button>
                    </div>
                    <div class="modal-body">
                        ${this.renderizarEncabezadoTrazabilidad(trazabilidad)}
                        ${this.renderizarEstadisticas(trazabilidad.estadisticas)}
                        ${this.renderizarLineaTiempo(trazabilidad.movimientos)}
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="this.closest('.modal-overlay').remove()">
                            Cerrar
                        </button>
                        <button class="btn btn-primary" onclick="ReporteModule.generarReporte('DOCUMENTOS', 'PDF', {idDocumento: ${idDocumento}})">
                            <i class="fas fa-download"></i> Exportar PDF
                        </button>
                    </div>
                </div>
            `;

            document.body.appendChild(modal);
        } catch (error) {
            console.error('Error:', error);
            mostrarToast('Error al cargar trazabilidad', 'error');
        }
    },

    /**
     * Obtener datos de trazabilidad del servidor
     */
    obtenerTrazabilidad: async function(idDocumento) {
        const response = await fetch(`${API_URL}/derivaciones/trazabilidad/${idDocumento}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error('Error al obtener trazabilidad');
        }

        return await response.json();
    },

    /**
     * Renderizar encabezado con información del documento
     */
    renderizarEncabezadoTrazabilidad: function(trazabilidad) {
        return `
            <div class="trazabilidad-header">
                <div class="info-grid">
                    <div class="info-item">
                        <label>Código:</label>
                        <span class="badge badge-primary">${trazabilidad.codigo}</span>
                    </div>
                    <div class="info-item">
                        <label>Título:</label>
                        <span>${trazabilidad.titulo}</span>
                    </div>
                    <div class="info-item">
                        <label>Estado Actual:</label>
                        <span class="badge badge-${this.getEstadoClass(trazabilidad.estadoActual)}">
                            ${trazabilidad.estadoActual}
                        </span>
                    </div>
                    <div class="info-item">
                        <label>Remitente:</label>
                        <span>${trazabilidad.remitente}</span>
                    </div>
                    <div class="info-item">
                        <label>Tipo Documento:</label>
                        <span>${trazabilidad.tipoDocumento}</span>
                    </div>
                    <div class="info-item">
                        <label>Fecha Registro:</label>
                        <span>${this.formatearFecha(trazabilidad.fechaRegistro)}</span>
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * Renderizar estadísticas del documento
     */
    renderizarEstadisticas: function(estadisticas) {
        return `
            <div class="trazabilidad-stats">
                <h4>Resumen Estadístico</h4>
                <div class="stats-grid">
                    <div class="stat-box">
                        <i class="fas fa-clock"></i>
                        <div>
                            <strong>${estadisticas.tiempoTotalHoras}h</strong>
                            <small>Tiempo Total</small>
                        </div>
                    </div>
                    <div class="stat-box">
                        <i class="fas fa-share"></i>
                        <div>
                            <strong>${estadisticas.totalDerivaciones}</strong>
                            <small>Derivaciones</small>
                        </div>
                    </div>
                    <div class="stat-box">
                        <i class="fas fa-building"></i>
                        <div>
                            <strong>${estadisticas.totalAreas}</strong>
                            <small>Áreas Involucradas</small>
                        </div>
                    </div>
                    <div class="stat-box">
                        <i class="fas fa-map-marker-alt"></i>
                        <div>
                            <strong>${estadisticas.areaActual || 'N/A'}</strong>
                            <small>Área Actual</small>
                        </div>
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * Renderizar línea de tiempo de movimientos
     */
    renderizarLineaTiempo: function(movimientos) {
        if (!movimientos || movimientos.length === 0) {
            return '<p class="text-center text-muted">No hay movimientos registrados</p>';
        }

        return `
            <div class="trazabilidad-timeline">
                <h4>Historial de Movimientos</h4>
                <div class="timeline">
                    ${movimientos.map((mov, index) => this.renderizarMovimiento(mov, index === 0)).join('')}
                </div>
            </div>
        `;
    },

    /**
     * Renderizar un movimiento individual
     */
    renderizarMovimiento: function(movimiento, esUltimo) {
        const iconos = {
            'REGISTRO': 'fa-file-alt',
            'DERIVACION': 'fa-share',
            'CAMBIO_ESTADO': 'fa-sync'
        };

        const icono = iconos[movimiento.tipo] || 'fa-circle';
        
        return `
            <div class="timeline-item ${esUltimo ? 'timeline-item-current' : ''}">
                <div class="timeline-marker">
                    <i class="fas ${icono}"></i>
                </div>
                <div class="timeline-content">
                    <div class="timeline-header">
                        <span class="timeline-tipo">${movimiento.tipo}</span>
                        <span class="timeline-fecha">${this.formatearFechaCompleta(movimiento.fecha)}</span>
                    </div>
                    <p class="timeline-descripcion">${movimiento.descripcion}</p>
                    <div class="timeline-details">
                        ${movimiento.usuario ? `
                            <span><i class="fas fa-user"></i> ${movimiento.usuario}</span>
                        ` : ''}
                        ${movimiento.areaOrigen ? `
                            <span><i class="fas fa-building"></i> ${movimiento.areaOrigen}</span>
                        ` : ''}
                        ${movimiento.areaDestino ? `
                            <span><i class="fas fa-arrow-right"></i> ${movimiento.areaDestino}</span>
                        ` : ''}
                        ${movimiento.tiempoEnArea ? `
                            <span><i class="fas fa-hourglass-half"></i> ${movimiento.tiempoEnArea} horas en área anterior</span>
                        ` : ''}
                    </div>
                    ${movimiento.estado ? `
                        <span class="badge badge-${this.getEstadoClass(movimiento.estado)}">
                            ${movimiento.estado}
                        </span>
                    ` : ''}
                </div>
            </div>
        `;
    },

    /**
     * Obtener clase CSS según estado
     */
    getEstadoClass: function(estado) {
        const clases = {
            'Asignado': 'info',
            'Recibido': 'primary',
            'En_Proceso': 'warning',
            'Observado': 'danger',
            'Finalizado': 'success',
            'Salida': 'secondary',
            'PENDIENTE': 'warning',
            'RECIBIDO': 'success'
        };
        return clases[estado] || 'secondary';
    },

    /**
     * Formatear fecha
     */
    formatearFecha: function(fecha) {
        if (!fecha) return 'N/A';
        const date = new Date(fecha);
        return date.toLocaleDateString('es-ES', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    },

    /**
     * Formatear fecha completa con hora
     */
    formatearFechaCompleta: function(fecha) {
        if (!fecha) return 'N/A';
        const date = new Date(fecha);
        return date.toLocaleString('es-ES', {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    /**
     * Agregar botón de trazabilidad a tabla de documentos
     */
    agregarBotonTrazabilidad: function(idDocumento) {
        return `
            <button class="btn btn-sm btn-info" 
                    onclick="TrazabilidadModule.mostrarTrazabilidad(${idDocumento})"
                    title="Ver trazabilidad">
                <i class="fas fa-route"></i>
            </button>
        `;
    },

    /**
     * Widget de trazabilidad resumida para dashboard
     */
    mostrarResumenTrazabilidad: async function(idDocumento, containerId) {
        try {
            const trazabilidad = await this.obtenerTrazabilidad(idDocumento);
            const container = document.getElementById(containerId);
            
            if (!container) return;

            const ultimosMovimientos = trazabilidad.movimientos.slice(0, 3);

            container.innerHTML = `
                <div class="trazabilidad-widget">
                    <div class="widget-header">
                        <h5>Últimos Movimientos</h5>
                        <button class="btn-link" onclick="TrazabilidadModule.mostrarTrazabilidad(${idDocumento})">
                            Ver todo
                        </button>
                    </div>
                    <div class="widget-body">
                        ${ultimosMovimientos.map(mov => `
                            <div class="movimiento-item">
                                <i class="fas ${mov.tipo === 'DERIVACION' ? 'fa-share' : 'fa-circle'}"></i>
                                <div>
                                    <strong>${mov.tipo}</strong>
                                    <small>${this.formatearFecha(mov.fecha)}</small>
                                    <p>${mov.descripcion}</p>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        } catch (error) {
            console.error('Error al mostrar resumen:', error);
        }
    }
};

// Exportar para uso global
window.TrazabilidadModule = TrazabilidadModule;
