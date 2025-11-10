// =====================================================
// MÓDULO DE DERIVACIONES
// Gestión de derivación de documentos a áreas internas
// =====================================================

const DerivacionModule = {
    /**
     * Derivar un documento a un área específica
     */
    derivarDocumento: async function(idDocumento, idAreaDestino, idUsuarioRecibe, observaciones, prioridad = 'NORMAL') {
        try {
            const usuarioActual = JSON.parse(localStorage.getItem('usuario'));
            if (!usuarioActual) {
                throw new Error('Usuario no autenticado');
            }

            const response = await fetch(`${API_URL}/derivaciones/derivar?idUsuarioDeriva=${usuarioActual.idUsuario}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({
                    idDocumento,
                    idAreaDestino,
                    idUsuarioRecibe,
                    observaciones,
                    prioridad
                })
            });

            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || 'Error al derivar documento');
            }

            mostrarToast('Documento derivado exitosamente', 'success');
            return data;
        } catch (error) {
            console.error('Error:', error);
            mostrarToast(error.message, 'error');
            throw error;
        }
    },

    /**
     * Recibir una derivación pendiente
     */
    recibirDerivacion: async function(idDerivacion) {
        try {
            const usuarioActual = JSON.parse(localStorage.getItem('usuario'));
            if (!usuarioActual) {
                throw new Error('Usuario no autenticado');
            }

            const response = await fetch(
                `${API_URL}/derivaciones/recibir/${idDerivacion}?idUsuarioRecibe=${usuarioActual.idUsuario}`,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                }
            );

            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || 'Error al recibir derivación');
            }

            mostrarToast('Derivación recibida exitosamente', 'success');
            return data;
        } catch (error) {
            console.error('Error:', error);
            mostrarToast(error.message, 'error');
            throw error;
        }
    },

    /**
     * Obtener historial de derivaciones de un documento
     */
    obtenerDerivacionesDocumento: async function(idDocumento) {
        try {
            const response = await fetch(`${API_URL}/derivaciones/documento/${idDocumento}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                throw new Error('Error al obtener derivaciones');
            }

            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            mostrarToast('Error al cargar historial de derivaciones', 'error');
            throw error;
        }
    },

    /**
     * Obtener derivaciones de un área específica
     */
    obtenerDerivacionesArea: async function(idArea) {
        try {
            const response = await fetch(`${API_URL}/derivaciones/area/${idArea}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                throw new Error('Error al obtener derivaciones del área');
            }

            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            mostrarToast('Error al cargar derivaciones del área', 'error');
            throw error;
        }
    },

    /**
     * Obtener trazabilidad completa de un documento
     */
    obtenerTrazabilidad: async function(idDocumento) {
        try {
            const response = await fetch(`${API_URL}/derivaciones/trazabilidad/${idDocumento}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                throw new Error('Error al obtener trazabilidad');
            }

            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            mostrarToast('Error al cargar trazabilidad', 'error');
            throw error;
        }
    },

    /**
     * Renderizar modal para derivar documento
     */
    mostrarModalDerivar: function(idDocumento, codigoDocumento) {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Derivar Documento ${codigoDocumento}</h3>
                    <button class="btn-close" onclick="this.closest('.modal-overlay').remove()">×</button>
                </div>
                <div class="modal-body">
                    <form id="formDerivar">
                        <div class="form-group">
                            <label for="areaDestino">Área Destino *</label>
                            <select id="areaDestino" class="form-control" required>
                                <option value="">Seleccione un área...</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="usuarioRecibe">Usuario Receptor</label>
                            <select id="usuarioRecibe" class="form-control">
                                <option value="">Sin asignar</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="prioridad">Prioridad</label>
                            <select id="prioridad" class="form-control">
                                <option value="BAJA">Baja</option>
                                <option value="NORMAL" selected>Normal</option>
                                <option value="ALTA">Alta</option>
                                <option value="URGENTE">Urgente</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="observaciones">Observaciones</label>
                            <textarea id="observaciones" class="form-control" rows="3" 
                                      placeholder="Instrucciones o comentarios sobre la derivación..."></textarea>
                        </div>

                        <div class="modal-actions">
                            <button type="button" class="btn btn-secondary" 
                                    onclick="this.closest('.modal-overlay').remove()">Cancelar</button>
                            <button type="submit" class="btn btn-primary">Derivar Documento</button>
                        </div>
                    </form>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        // Cargar áreas
        this.cargarAreasSelect('areaDestino');

        // Manejar cambio de área para cargar usuarios
        document.getElementById('areaDestino').addEventListener('change', (e) => {
            if (e.target.value) {
                this.cargarUsuariosArea(e.target.value, 'usuarioRecibe');
            }
        });

        // Manejar envío del formulario
        document.getElementById('formDerivar').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const idAreaDestino = document.getElementById('areaDestino').value;
            const idUsuarioRecibe = document.getElementById('usuarioRecibe').value || null;
            const prioridad = document.getElementById('prioridad').value;
            const observaciones = document.getElementById('observaciones').value;

            try {
                await this.derivarDocumento(idDocumento, idAreaDestino, idUsuarioRecibe, observaciones, prioridad);
                modal.remove();
                
                // Recargar la página o actualizar la tabla
                if (typeof cargarDocumentos === 'function') {
                    cargarDocumentos();
                }
            } catch (error) {
                console.error('Error al derivar:', error);
            }
        });
    },

    /**
     * Cargar áreas en un select
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
     * Cargar usuarios de un área
     */
    cargarUsuariosArea: async function(idArea, selectId) {
        try {
            const response = await fetch(`${API_URL}/usuarios/area/${idArea}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                const usuarios = await response.json();
                const select = document.getElementById(selectId);
                
                // Limpiar opciones excepto la primera
                select.innerHTML = '<option value="">Sin asignar</option>';
                
                usuarios.forEach(usuario => {
                    const option = document.createElement('option');
                    option.value = usuario.idUsuario;
                    option.textContent = `${usuario.nombre} ${usuario.apellido}`;
                    select.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error al cargar usuarios:', error);
        }
    }
};

// Exportar para uso global
window.DerivacionModule = DerivacionModule;
