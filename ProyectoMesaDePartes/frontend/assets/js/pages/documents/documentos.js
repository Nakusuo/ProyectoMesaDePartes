// Configuración
const API_URL = 'http://localhost:8080/api';

// Variables globales
let documentosOriginales = [];
let documentoActual = null;

// Cargar documentos al iniciar
document.addEventListener('DOMContentLoaded', async () => {
    await cargarDocumentos();
    inicializarEventos();
});

// Función para cargar documentos asignados al usuario
async function cargarDocumentos() {
    const tableBody = document.getElementById('documentos-table-body');
    
    try {
        const token = localStorage.getItem('token');
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        const userId = userInfo.idUsuario;
        
        if (!userId) {
            throw new Error('No se pudo obtener el ID del usuario');
        }
        
        const response = await fetch(`${API_URL}/documentos/asignados/${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
        const documentos = await response.json();
        documentosOriginales = documentos;
        
        if (documentos.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: #999; padding: 40px;">No tienes documentos asignados</td></tr>';
            return;
        }
        
        // Ordenar por fecha de ingreso (más recientes primero)
        documentos.sort((a, b) => new Date(b.fechaIngreso) - new Date(a.fechaIngreso));
        
        mostrarDocumentos(documentos);
        
    } catch (error) {
        console.error('Error al cargar documentos:', error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="8" style="text-align: center; color: red; padding: 40px;">
                    Error al cargar los documentos: ${error.message}
                </td>
            </tr>
        `;
    }
}

// Función para mostrar documentos en la tabla
function mostrarDocumentos(documentos) {
    const tableBody = document.getElementById('documentos-table-body');
    
    if (documentos.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: #999; padding: 40px;">No se encontraron documentos</td></tr>';
        return;
    }
    
    tableBody.innerHTML = documentos.map(doc => {
        const fecha = formatearFecha(doc.fechaIngreso);
        const tipo = doc.tipoDocumento?.nombre || 'N/A';
        const estadoBadge = obtenerEstadoBadge(doc.estado);
        const archivoBtn = doc.archivoUrl 
            ? `<a href="http://localhost:8080${doc.archivoUrl}" target="_blank" class="btn btn-sm btn-secondary">📎 Ver PDF</a>` 
            : '<span style="color: #999;">Sin archivo</span>';
        
        return `
            <tr>
                <td><strong>${doc.codigo}</strong></td>
                <td>${doc.titulo || 'Sin título'}</td>
                <td>${tipo}</td>
                <td>${doc.remitente || 'N/A'}</td>
                <td>${fecha}</td>
                <td>${estadoBadge}</td>
                <td>${archivoBtn}</td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="abrirModalEstado(${doc.idDocumento}, '${doc.codigo}', '${doc.estado}', '${escaparComillas(doc.titulo)}')">
                        ✏️ Actualizar
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

// Función para escapar comillas en strings
function escaparComillas(texto) {
    if (!texto) return '';
    return texto.replace(/'/g, "\\'").replace(/"/g, '\\"');
}

// Función para inicializar eventos de filtros
function inicializarEventos() {
    document.getElementById('btn-aplicar-filtros').addEventListener('click', aplicarFiltros);
    document.getElementById('btn-limpiar-filtros').addEventListener('click', limpiarFiltros);
    
    // Aplicar filtros al presionar Enter
    document.getElementById('filtro-buscar').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            aplicarFiltros();
        }
    });
    
    // Filtrar al cambiar el select de estado
    document.getElementById('filtro-estado').addEventListener('change', aplicarFiltros);
}

// Función para aplicar filtros
function aplicarFiltros() {
    const buscar = document.getElementById('filtro-buscar').value.toLowerCase();
    const estadoFiltro = document.getElementById('filtro-estado').value;
    
    let documentosFiltrados = documentosOriginales;
    
    // Filtrar por búsqueda general
    if (buscar) {
        documentosFiltrados = documentosFiltrados.filter(doc => {
            return (doc.codigo && doc.codigo.toLowerCase().includes(buscar)) ||
                   (doc.titulo && doc.titulo.toLowerCase().includes(buscar)) ||
                   (doc.remitente && doc.remitente.toLowerCase().includes(buscar)) ||
                   (doc.descripcion && doc.descripcion.toLowerCase().includes(buscar));
        });
    }
    
    // Filtrar por estado
    if (estadoFiltro) {
        documentosFiltrados = documentosFiltrados.filter(doc => doc.estado === estadoFiltro);
    }
    
    // Ordenar por fecha (más recientes primero)
    documentosFiltrados.sort((a, b) => new Date(b.fechaIngreso) - new Date(a.fechaIngreso));
    
    mostrarDocumentos(documentosFiltrados);
}

// Función para limpiar filtros
function limpiarFiltros() {
    document.getElementById('filtro-buscar').value = '';
    document.getElementById('filtro-estado').value = '';
    
    mostrarDocumentos(documentosOriginales);
}

// Función para formatear fecha
function formatearFecha(fechaISO) {
    if (!fechaISO) return 'N/A';
    
    const fecha = new Date(fechaISO);
    const dia = String(fecha.getDate()).padStart(2, '0');
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const anio = fecha.getFullYear();
    const horas = String(fecha.getHours()).padStart(2, '0');
    const minutos = String(fecha.getMinutes()).padStart(2, '0');
    
    return `${dia}/${mes}/${anio} ${horas}:${minutos}`;
}

// Función para obtener badge de estado
function obtenerEstadoBadge(estado) {
    // Normalizar el estado (reemplazar guiones bajos por espacios para mostrar)
    const estadoNormalizado = estado.replace(/_/g, ' ');
    
    const estados = {
        'Asignado': '<span style="background: #17a2b8; color: white; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem;">📋 Asignado</span>',
        'Recibido': '<span style="background: #28a745; color: white; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem;">✅ Recibido</span>',
        'En_Proceso': '<span style="background: #ffc107; color: #333; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem;">⚙️ En Proceso</span>',
        'En Proceso': '<span style="background: #ffc107; color: #333; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem;">⚙️ En Proceso</span>',
        'Observado': '<span style="background: #ff9800; color: white; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem;">⚠️ Observado</span>',
        'Finalizado': '<span style="background: #007bff; color: white; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem;">✔️ Finalizado</span>',
        'Salida': '<span style="background: #6c757d; color: white; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem;">📤 Salida</span>'
    };
    
    return estados[estado] || estados[estadoNormalizado] || `<span style="color: #666;">${estadoNormalizado}</span>`;
}

// Función para abrir modal de actualización de estado
window.abrirModalEstado = function(idDocumento, codigo, estadoActual, titulo) {
    documentoActual = { idDocumento, codigo, estadoActual, titulo };
    
    const modal = document.getElementById('modal-estado');
    document.getElementById('modal-documento-codigo').textContent = codigo;
    document.getElementById('modal-documento-titulo').textContent = titulo;
    document.getElementById('select-estado').value = estadoActual;
    document.getElementById('textarea-observaciones').value = '';
    
    // Mostrar/ocultar campo de observaciones según el estado
    toggleObservaciones(estadoActual);
    
    modal.style.display = 'flex';
}

// Función para cerrar modal
window.cerrarModalEstado = function() {
    document.getElementById('modal-estado').style.display = 'none';
    documentoActual = null;
}

// Función para mostrar/ocultar observaciones
window.toggleObservaciones = function(estado) {
    const observacionesDiv = document.getElementById('div-observaciones');
    if (estado === 'Finalizado') {
        observacionesDiv.style.display = 'block';
        document.getElementById('textarea-observaciones').required = true;
    } else {
        observacionesDiv.style.display = 'none';
        document.getElementById('textarea-observaciones').required = false;
    }
}

// Función para guardar cambios de estado
window.guardarCambiosEstado = async function() {
    if (!documentoActual) return;
    
    const nuevoEstado = document.getElementById('select-estado').value;
    const observaciones = document.getElementById('textarea-observaciones').value.trim();
    
    // Validar observaciones si es necesario
    if (nuevoEstado === 'Finalizado' && !observaciones) {
        alert('⚠️ Debe agregar observaciones o informe al finalizar el documento');
        document.getElementById('textarea-observaciones').focus();
        return;
    }
    
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/documentos/${documentoActual.idDocumento}/estado`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                estado: nuevoEstado,
                observaciones: observaciones
            })
        });
        
        if (response.ok) {
            alert('✅ Estado actualizado correctamente');
            cerrarModalEstado();
            await cargarDocumentos(); // Recargar la tabla
        } else {
            const error = await response.json();
            alert('❌ Error al actualizar estado: ' + (error.message || error.error || 'Error desconocido'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Error al actualizar estado: ' + error.message);
    }
}
