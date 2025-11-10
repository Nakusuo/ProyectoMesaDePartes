// =====================================================
// MÓDULO DE REPORTES
// Generación de reportes en PDF y Excel
// =====================================================

const ReporteModule = {
    /**
     * Generar reporte según parámetros
     */
    generarReporte: async function(tipoReporte, formato, filtros = {}) {
        try {
            const reporteDTO = {
                tipoReporte: tipoReporte, // DOCUMENTOS, TIEMPOS, AREAS
                formato: formato, // PDF, EXCEL
                fechaInicio: filtros.fechaInicio || null,
                fechaFin: filtros.fechaFin || null,
                idArea: filtros.idArea || null,
                estado: filtros.estado || null
            };

            const response = await fetch(`${API_URL}/reportes/generar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(reporteDTO)
            });

            if (!response.ok) {
                throw new Error('Error al generar reporte');
            }

            // Descargar archivo
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `reporte_${tipoReporte}_${Date.now()}.${formato.toLowerCase() === 'excel' ? 'xlsx' : 'pdf'}`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();

            mostrarToast('Reporte generado exitosamente', 'success');
        } catch (error) {
            console.error('Error:', error);
            mostrarToast('Error al generar reporte', 'error');
            throw error;
        }
    },

    /**
     * Obtener estadísticas generales
     */
    obtenerEstadisticas: async function() {
        try {
            const response = await fetch(`${API_URL}/reportes/estadisticas`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                throw new Error('Error al obtener estadísticas');
            }

            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    },

    /**
     * Mostrar modal de generación de reportes
     */
    mostrarModalReportes: function() {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content modal-lg">
                <div class="modal-header">
                    <h3>Generar Reporte</h3>
                    <button class="btn-close" onclick="this.closest('.modal-overlay').remove()">×</button>
                </div>
                <div class="modal-body">
                    <form id="formReporte">
                        <div class="row">
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="tipoReporte">Tipo de Reporte *</label>
                                    <select id="tipoReporte" class="form-control" required>
                                        <option value="DOCUMENTOS">Reporte de Documentos</option>
                                        <option value="TIEMPOS">Reporte de Tiempos de Atención</option>
                                        <option value="AREAS">Reporte por Áreas</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="formato">Formato *</label>
                                    <select id="formato" class="form-control" required>
                                        <option value="PDF">PDF</option>
                                        <option value="EXCEL">Excel</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="fechaInicio">Fecha Inicio</label>
                                    <input type="date" id="fechaInicio" class="form-control">
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="fechaFin">Fecha Fin</label>
                                    <input type="date" id="fechaFin" class="form-control">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="estadoFiltro">Estado</label>
                                    <select id="estadoFiltro" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="Asignado">Asignado</option>
                                        <option value="Recibido">Recibido</option>
                                        <option value="En_Proceso">En Proceso</option>
                                        <option value="Observado">Observado</option>
                                        <option value="Finalizado">Finalizado</option>
                                        <option value="Salida">Salida</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="areaFiltro">Área</label>
                                    <select id="areaFiltro" class="form-control">
                                        <option value="">Todas</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="modal-actions">
                            <button type="button" class="btn btn-secondary" 
                                    onclick="this.closest('.modal-overlay').remove()">Cancelar</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-download"></i> Generar Reporte
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        // Cargar áreas
        this.cargarAreasSelect('areaFiltro');

        // Establecer fecha actual como fecha fin por defecto
        document.getElementById('fechaFin').valueAsDate = new Date();

        // Manejar envío del formulario
        document.getElementById('formReporte').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const tipoReporte = document.getElementById('tipoReporte').value;
            const formato = document.getElementById('formato').value;
            
            const filtros = {
                fechaInicio: document.getElementById('fechaInicio').value || null,
                fechaFin: document.getElementById('fechaFin').value || null,
                estado: document.getElementById('estadoFiltro').value || null,
                idArea: document.getElementById('areaFiltro').value || null
            };

            // Convertir fechas a formato ISO si existen
            if (filtros.fechaInicio) {
                filtros.fechaInicio = new Date(filtros.fechaInicio).toISOString();
            }
            if (filtros.fechaFin) {
                filtros.fechaFin = new Date(filtros.fechaFin + 'T23:59:59').toISOString();
            }

            try {
                await this.generarReporte(tipoReporte, formato, filtros);
                modal.remove();
            } catch (error) {
                console.error('Error al generar reporte:', error);
            }
        });
    },

    /**
     * Cargar áreas en select
     */
    cargarAreasSelect: async function(selectId) {
        try {
            const response = await fetch(`${API_URL}/areas`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                const areas = await response.json();
                const select = document.getElementById(selectId);
                
                areas.forEach(area => {
                    const option = document.createElement('option');
                    option.value = area.idArea;
                    option.textContent = `${area.nombre} (${area.sigla})`;
                    select.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error al cargar áreas:', error);
        }
    },

    /**
     * Mostrar dashboard de estadísticas
     */
    mostrarDashboardEstadisticas: async function(containerId) {
        try {
            const estadisticas = await this.obtenerEstadisticas();
            const container = document.getElementById(containerId);
            
            if (!container) return;

            container.innerHTML = `
                <div class="estadisticas-grid">
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-file-alt"></i>
                        </div>
                        <div class="stat-info">
                            <h3>${estadisticas.totalDocumentos}</h3>
                            <p>Total Documentos</p>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-share"></i>
                        </div>
                        <div class="stat-info">
                            <h3>${estadisticas.totalDerivaciones}</h3>
                            <p>Total Derivaciones</p>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-clock"></i>
                        </div>
                        <div class="stat-info">
                            <h3>${Math.round(estadisticas.tiempoPromedioHoras)}h</h3>
                            <p>Tiempo Promedio</p>
                        </div>
                    </div>

                    <div class="stat-card-full">
                        <h4>Documentos por Estado</h4>
                        <div class="estado-grid">
                            ${Object.entries(estadisticas.documentosPorEstado || {})
                                .map(([estado, cantidad]) => `
                                    <div class="estado-item">
                                        <span class="estado-nombre">${estado}</span>
                                        <span class="estado-cantidad">${cantidad}</span>
                                    </div>
                                `).join('')}
                        </div>
                    </div>
                </div>
            `;
        } catch (error) {
            console.error('Error al mostrar estadísticas:', error);
        }
    }
};

// Exportar para uso global
window.ReporteModule = ReporteModule;
