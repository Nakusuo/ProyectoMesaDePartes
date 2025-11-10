// salida-documento.js - Gestión de salida de documentos
const API_URL = API_BASE_URL;

let documentoActual = null;
let documentosFinalizados = []; // Solo documentos con estado FINALIZADO
let documentosFiltrados = [];
let paginaActualDocumentos = 1;
const documentosPorPagina = 10;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    verificarPermisos();
    cargarAreas();
    inicializarFormulario();
    cargarDocumentosFinalizados(); // Cargar documentos FINALIZADOS para salida
    
    // Event listener para el filtro de búsqueda
    document.getElementById('filtroDocumentos')?.addEventListener('input', function(e) {
        filtrarDocumentos(e.target.value);
    });
});

function verificarPermisos() {
    const pm = window.permissionsManager;
    if (!pm.hasPermission('VER_SALIDAS')) {
        window.location.href = 'dashboard.html';
        return;
    }
}

// ============= DOCUMENTOS FINALIZADOS PARA SALIDA =============

// Cargar áreas desde el backend
async function cargarAreas() {
    try {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_URL}/api/areas`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const areas = await response.json();
            const select = document.getElementById('areaSalida');
            
            areas.forEach(area => {
                const option = document.createElement('option');
                option.value = area.id;
                option.textContent = area.nombre;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error al cargar áreas:', error);
        showToast('Error al cargar las áreas', 'error');
    }
}

// Inicializar el formulario con la fecha actual
function inicializarFormulario() {
    const ahora = new Date();
    const year = ahora.getFullYear();
    const month = String(ahora.getMonth() + 1).padStart(2, '0');
    const day = String(ahora.getDate()).padStart(2, '0');
    const hours = String(ahora.getHours()).padStart(2, '0');
    const minutes = String(ahora.getMinutes()).padStart(2, '0');
    
    document.getElementById('fechaSalida').value = `${year}-${month}-${day}T${hours}:${minutes}`;

    // Manejar el submit del formulario
    document.getElementById('formSalida').addEventListener('submit', registrarSalida);
}

// Buscar documento por número de registro
async function buscarDocumento() {
    const numeroRegistro = document.getElementById('numeroRegistro').value.trim();
    
    if (!numeroRegistro) {
        showToast('Por favor ingrese un número de registro', 'warning');
        return;
    }

    try {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_URL}/api/documentos/buscar?numeroRegistro=${encodeURIComponent(numeroRegistro)}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const documento = await response.json();
            documentoActual = documento;
            mostrarInformacionDocumento(documento);
            await cargarHistorialSalidas(documento.idDocumento);
        } else if (response.status === 404) {
            showToast('Documento no encontrado', 'error');
            ocultarSecciones();
        } else {
            throw new Error('Error al buscar el documento');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al buscar el documento', 'error');
        ocultarSecciones();
    }
}

// Mostrar información del documento
function mostrarInformacionDocumento(documento) {
    document.getElementById('documentoInfo').style.display = 'block';
    document.getElementById('salidaForm').style.display = 'block';
    document.getElementById('historialSection').style.display = 'block';

    document.getElementById('documentoId').value = documento.idDocumento;
    document.getElementById('infoNumero').textContent = documento.codigo || documento.numeroDocumento || '-';
    document.getElementById('infoTipo').textContent = documento.tipoDocumento?.nombre || '-';
    document.getElementById('infoAsunto').textContent = documento.titulo || documento.descripcion || '-';
    document.getElementById('infoRemitente').textContent = documento.remitente;
    document.getElementById('infoFechaRegistro').textContent = formatearFecha(documento.fechaIngreso);
    
    const estadoBadge = document.getElementById('infoEstado');
    estadoBadge.textContent = documento.estado;
    estadoBadge.className = 'badge badge-' + obtenerClaseEstado(documento.estado);
    
    document.getElementById('infoAreaDestino').textContent = documento.area?.nombre || '-';
    document.getElementById('infoFolios').textContent = documento.numeroFolios || '-';

    // Limpiar formulario de salida
    document.getElementById('formSalida').reset();
    inicializarFormulario();
}

// Obtener clase CSS para el estado
function obtenerClaseEstado(estado) {
    const estados = {
        'REGISTRADO': 'info',
        'EN_TRAMITE': 'warning',
        'DERIVADO': 'primary',
        'ATENDIDO': 'success',
        'ARCHIVADO': 'secondary'
    };
    return estados[estado] || 'secondary';
}

// Cargar historial de salidas del documento
async function cargarHistorialSalidas(documentoId) {
    try {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_URL}/api/salidas/documento/${documentoId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const salidas = await response.json();
            mostrarHistorial(salidas);
        } else {
            document.getElementById('historialBody').innerHTML = 
                '<tr><td colspan="6" class="text-center">No hay salidas registradas</td></tr>';
        }
    } catch (error) {
        console.error('Error al cargar historial:', error);
        document.getElementById('historialBody').innerHTML = 
            '<tr><td colspan="6" class="text-center">Error al cargar el historial</td></tr>';
    }
}

// Mostrar historial en la tabla
function mostrarHistorial(salidas) {
    const tbody = document.getElementById('historialBody');
    
    if (salidas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay salidas registradas</td></tr>';
        return;
    }

    tbody.innerHTML = salidas.map(salida => `
        <tr>
            <td>${formatearFecha(salida.fechaSalida)}</td>
            <td>${salida.destinatario}</td>
            <td>${salida.cargoDestinatario}</td>
            <td>${salida.area?.nombre || '-'}</td>
            <td>${salida.usuario?.nombreCompleto || '-'}</td>
            <td>${salida.observaciones || '-'}</td>
        </tr>
    `).join('');
}

// Registrar salida del documento
async function registrarSalida(event) {
    event.preventDefault();

    if (!documentoActual) {
        showToast('No hay documento seleccionado', 'error');
        return;
    }

    const pm = window.permissionsManager;
    if (!pm.hasPermission('REGISTRAR_SALIDA')) {
        showToast('No tiene permisos para registrar salidas', 'error');
        return;
    }

    const salidaData = {
        documentoId: parseInt(document.getElementById('documentoId').value),
        destinatario: document.getElementById('destinatario').value.trim(),
        cargoDestinatario: document.getElementById('cargoDestinatario').value.trim(),
        areaId: parseInt(document.getElementById('areaSalida').value),
        fechaSalida: document.getElementById('fechaSalida').value,
        observaciones: document.getElementById('observaciones').value.trim() || null
    };

    try {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_URL}/api/salidas`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(salidaData)
        });

        if (response.ok) {
            showToast('Salida registrada exitosamente', 'success');
            
            // Recargar historial
            await cargarHistorialSalidas(documentoActual.idDocumento);
            
            // Limpiar formulario
            document.getElementById('formSalida').reset();
            inicializarFormulario();
        } else {
            const error = await response.text();
            showToast(`Error al registrar la salida: ${error}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al registrar la salida', 'error');
    }
}

// Cancelar registro de salida
function cancelarSalida() {
    document.getElementById('formSalida').reset();
    inicializarFormulario();
    document.getElementById('numeroRegistro').value = '';
    ocultarSecciones();
}

// Ocultar secciones
function ocultarSecciones() {
    document.getElementById('documentoInfo').style.display = 'none';
    document.getElementById('salidaForm').style.display = 'none';
    document.getElementById('historialSection').style.display = 'none';
    documentoActual = null;
}

// Formatear fecha
function formatearFecha(fechaStr) {
    if (!fechaStr) return '-';
    
    const fecha = new Date(fechaStr);
    const opciones = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    };
    
    return fecha.toLocaleDateString('es-PE', opciones);
}

// Listener para Enter en el campo de búsqueda
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('numeroRegistro')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            buscarDocumento();
        }
    });
});

// Cargar documentos finalizados
async function cargarDocumentosFinalizados() {
    try {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_URL}/api/documentos`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const todosDocumentos = await response.json();
            console.log('Total de documentos recibidos:', todosDocumentos.length);
            console.log('Primer documento (ejemplo):', todosDocumentos[0]);
            
            // Contar documentos por estado
            const estadosContador = {};
            todosDocumentos.forEach(doc => {
                const estado = doc.estado || 'Sin estado';
                estadosContador[estado] = (estadosContador[estado] || 0) + 1;
            });
            console.log('Documentos por estado:', estadosContador);
            
            // Filtrar documentos finalizados (probar diferentes variantes)
            documentosFinalizados = todosDocumentos.filter(doc => {
                const estadoOriginal = doc.estado || '';
                const estadoUpper = estadoOriginal.toUpperCase();
                return estadoOriginal === 'Finalizado' || // Capitalizado (como en BD)
                       estadoUpper === 'FINALIZADO' ||      // Mayúsculas
                       estadoUpper === 'FINALIZADOS' ||
                       estadoOriginal === 'Salida' ||       // También Salida capitalizado
                       estadoUpper === 'SALIDA' ||
                       estadoUpper === 'ARCHIVADO' ||
                       estadoUpper === 'ATENDIDO';
            });
            
            console.log('Documentos finalizados encontrados:', documentosFinalizados.length);
            
            // Si no hay finalizados, mostrar todos para debug
            if (documentosFinalizados.length === 0) {
                console.warn('No se encontraron documentos finalizados. Mostrando todos los documentos...');
                documentosFinalizados = todosDocumentos;
            }
            
            documentosFiltrados = [...documentosFinalizados];
            mostrarDocumentosFinalizados();
        } else {
            console.error('Error en la respuesta:', response.status);
            document.getElementById('documentosFinalizadosBody').innerHTML = 
                `<tr><td colspan="6" class="text-center">Error al cargar documentos (Código: ${response.status})</td></tr>`;
        }
    } catch (error) {
        console.error('Error al cargar documentos finalizados:', error);
        document.getElementById('documentosFinalizadosBody').innerHTML = 
            '<tr><td colspan="6" class="text-center">Error al cargar documentos: ' + error.message + '</td></tr>';
    }
}

// Filtrar documentos por texto
function filtrarDocumentos(texto) {
    const filtro = texto.toLowerCase().trim();
    
    if (!filtro) {
        documentosFiltrados = [...documentosFinalizados];
    } else {
        documentosFiltrados = documentosFinalizados.filter(doc => {
            const codigo = (doc.codigo || '').toLowerCase();
            const asunto = (doc.asunto || '').toLowerCase();
            const titulo = (doc.titulo || '').toLowerCase();
            const descripcion = (doc.descripcion || '').toLowerCase();
            const remitente = (doc.remitente || '').toLowerCase();
            
            return codigo.includes(filtro) || 
                   asunto.includes(filtro) ||
                   titulo.includes(filtro) || 
                   descripcion.includes(filtro) || 
                   remitente.includes(filtro);
        });
    }
    
    paginaActualDocumentos = 1;
    mostrarDocumentosFinalizados();
}

// Mostrar documentos finalizados con paginación
function mostrarDocumentosFinalizados() {
    const tbody = document.getElementById('documentosFinalizadosBody');
    
    if (documentosFiltrados.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay documentos finalizados disponibles</td></tr>';
        document.getElementById('paginationDocumentos').style.display = 'none';
        return;
    }

    const inicio = (paginaActualDocumentos - 1) * documentosPorPagina;
    const fin = inicio + documentosPorPagina;
    const documentosPagina = documentosFiltrados.slice(inicio, fin);

    tbody.innerHTML = documentosPagina.map(doc => `
        <tr>
            <td><strong>${doc.codigo || '-'}</strong></td>
            <td>${doc.tipoDocumento?.nombre || '-'}</td>
            <td>${doc.asunto || doc.titulo || (doc.descripcion ? doc.descripcion.substring(0, 60) + '...' : '-')}</td>
            <td>${doc.remitente || '-'}</td>
            <td>${formatearFecha(doc.fechaIngreso)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick='seleccionarDocumentoParaSalida(${JSON.stringify(doc).replace(/'/g, "&apos;")})'>
                    📤 Seleccionar
                </button>
            </td>
        </tr>
    `).join('');

    generarPaginacionDocumentos();
}

// Generar paginación para documentos
function generarPaginacionDocumentos() {
    const totalPaginas = Math.ceil(documentosFiltrados.length / documentosPorPagina);
    
    if (totalPaginas <= 1) {
        document.getElementById('paginationDocumentos').style.display = 'none';
        return;
    }

    const container = document.getElementById('paginationDocumentos');
    container.style.display = 'flex';

    let html = '<div class="pagination-info">';
    html += `Mostrando ${((paginaActualDocumentos - 1) * documentosPorPagina) + 1} - `;
    html += `${Math.min(paginaActualDocumentos * documentosPorPagina, documentosFiltrados.length)} `;
    html += `de ${documentosFiltrados.length} documentos</div>`;
    html += '<div class="pagination-buttons">';

    // Botón Anterior
    if (paginaActualDocumentos > 1) {
        html += `<button class="btn-pagination" onclick="cambiarPaginaDocumentos(${paginaActualDocumentos - 1})">Anterior</button>`;
    }

    // Números de página
    const maxBotones = 5;
    let inicio = Math.max(1, paginaActualDocumentos - Math.floor(maxBotones / 2));
    let fin = Math.min(totalPaginas, inicio + maxBotones - 1);

    if (fin - inicio < maxBotones - 1) {
        inicio = Math.max(1, fin - maxBotones + 1);
    }

    if (inicio > 1) {
        html += `<button class="btn-pagination" onclick="cambiarPaginaDocumentos(1)">1</button>`;
        if (inicio > 2) html += '<span class="pagination-ellipsis">...</span>';
    }

    for (let i = inicio; i <= fin; i++) {
        html += `<button class="btn-pagination ${i === paginaActualDocumentos ? 'active' : ''}" 
                 onclick="cambiarPaginaDocumentos(${i})">${i}</button>`;
    }

    if (fin < totalPaginas) {
        if (fin < totalPaginas - 1) html += '<span class="pagination-ellipsis">...</span>';
        html += `<button class="btn-pagination" onclick="cambiarPaginaDocumentos(${totalPaginas})">${totalPaginas}</button>`;
    }

    // Botón Siguiente
    if (paginaActualDocumentos < totalPaginas) {
        html += `<button class="btn-pagination" onclick="cambiarPaginaDocumentos(${paginaActualDocumentos + 1})">Siguiente</button>`;
    }

    html += '</div>';
    container.innerHTML = html;
}

// Cambiar página de documentos
function cambiarPaginaDocumentos(pagina) {
    paginaActualDocumentos = pagina;
    mostrarDocumentosFinalizados();
    // Scroll suave a la tabla
    document.querySelector('.documentos-finalizados-section').scrollIntoView({ behavior: 'smooth' });
}

// Seleccionar documento para registrar salida (NUEVO FLUJO)
function seleccionarDocumentoParaSalida(documento) {
    documentoActual = documento;
    mostrarInformacionDocumento(documento);
    cargarHistorialSalidas(documento.idDocumento);
    
    // Scroll suave a la información del documento
    setTimeout(() => {
        document.getElementById('documentoInfo')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
}

// Buscar documento por número de registro (mantener por si acaso)
async function buscarDocumento() {
    const numeroRegistro = document.getElementById('numeroRegistro')?.value.trim();
    
    if (!numeroRegistro) {
        showToast('Por favor ingrese un número de registro', 'warning');
        return;
    }

    try {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_URL}/api/documentos/buscar?numeroRegistro=${encodeURIComponent(numeroRegistro)}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const documento = await response.json();
            seleccionarDocumentoParaSalida(documento);
        } else if (response.status === 404) {
            showToast('Documento no encontrado', 'error');
            ocultarSecciones();
        } else {
            throw new Error('Error al buscar el documento');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al buscar el documento', 'error');
        ocultarSecciones();
    }
}
