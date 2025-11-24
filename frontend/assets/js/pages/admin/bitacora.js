// Configuración
// API_URL se declara aquí si no existe globalmente
if (typeof API_URL === 'undefined') {
    var API_URL = 'http://localhost:8080/api';
}

// Variables globales
let documentosOriginales = [];
let usuariosDisponibles = [];
let paginaActual = 0;
let tamanioPagina = 10;
let totalPaginas = 0;

// Cargar documentos y usuarios al iniciar
document.addEventListener('DOMContentLoaded', async () => {
    console.log('🔵 Bitácora cargada');
    await cargarUsuarios();
    await cargarDocumentos();
    inicializarEventos();
});

// Función para cargar usuarios disponibles
async function cargarUsuarios() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/usuarios`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            usuariosDisponibles = await response.json();
            const select = document.getElementById('filtro-usuario');
            select.innerHTML = '<option value="">Todos</option>' +
                usuariosDisponibles.map(u =>
                    `<option value="${u.idUsuario}">${u.nombre} ${u.apellido}</option>`
                ).join('');
        }
    } catch (error) {
        console.error('Error al cargar usuarios:', error);
    }
}

// Función para cargar todos los documentos con paginación
async function cargarDocumentos(pagina = 0, tamanio = 10) {
    const tableBody = document.getElementById('bitacora-table-body');

    try {
        console.log('📡 Obteniendo bitácora - Página:', pagina);

        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/bitacora?page=${pagina}&size=${tamanio}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();
        paginaActual = data.currentPage;
        totalPaginas = data.totalPages;
        documentosOriginales = data.content;

        console.log('✅ Registros de bitácora recibidos:', documentosOriginales.length);
        console.log('📊 Página actual:', paginaActual, 'de', totalPaginas);

        if (documentosOriginales.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: #999;">No hay registros en la bitácora</td></tr>';
            actualizarControlesPaginacion(data);
            return;
        }

        mostrarDocumentos(documentosOriginales);
        actualizarControlesPaginacion(data);

    } catch (error) {
        console.error('❌ ERROR al cargar bitácora:', error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; color: red;">
                    Error al cargar la bitácora: ${error.message}
                </td>
            </tr>
        `;
    }
}

// Actualizar controles de paginación
function actualizarControlesPaginacion(data) {
    // Generar botones de página numerados
    let pageButtons = '';
    const maxVisiblePages = 7; // Mostrar máximo 7 botones de página

    if (data.totalPages <= maxVisiblePages) {
        // Si hay pocas páginas, mostrar todas
        for (let i = 0; i < data.totalPages; i++) {
            const isActive = i === data.currentPage ? 'active' : '';
            pageButtons += `
                <button class="page-number ${isActive}" onclick="cambiarPagina(${i})">
                    ${i + 1}
                </button>
            `;
        }
    } else {
        // Lógica de paginación con puntos suspensivos
        const currentPage = data.currentPage;
        const totalPages = data.totalPages;

        // Primera página
        pageButtons += `
            <button class="page-number ${currentPage === 0 ? 'active' : ''}" onclick="cambiarPagina(0)">
                1
            </button>
        `;

        // Puntos suspensivos iniciales
        if (currentPage > 3) {
            pageButtons += `<span class="pagination-ellipsis">...</span>`;
        }

        // Páginas del medio
        let startPage = Math.max(1, currentPage - 1);
        let endPage = Math.min(totalPages - 2, currentPage + 1);

        for (let i = startPage; i <= endPage; i++) {
            const isActive = i === currentPage ? 'active' : '';
            pageButtons += `
                <button class="page-number ${isActive}" onclick="cambiarPagina(${i})">
                    ${i + 1}
                </button>
            `;
        }

        // Puntos suspensivos finales
        if (currentPage < totalPages - 4) {
            pageButtons += `<span class="pagination-ellipsis">...</span>`;
        }

        // Última página
        if (totalPages > 1) {
            pageButtons += `
                <button class="page-number ${currentPage === totalPages - 1 ? 'active' : ''}" onclick="cambiarPagina(${totalPages - 1})">
                    ${totalPages}
                </button>
            `;
        }
    }

    let paginationHtml = `
        <div class="pagination-controls">
            <div class="pagination-info">
                Mostrando ${data.content.length} de ${data.totalItems} documentos 
                (Página ${data.currentPage + 1} de ${data.totalPages})
            </div>
            <div class="pagination-buttons">
                <button onclick="cambiarPagina(${data.currentPage - 1})" 
                        class="page-nav-btn"
                        ${!data.hasPrevious ? 'disabled' : ''}>
                    ⬅️ Anterior
                </button>
                
                ${pageButtons}
                
                <button onclick="cambiarPagina(${data.currentPage + 1})" 
                        class="page-nav-btn"
                        ${!data.hasNext ? 'disabled' : ''}>
                    Siguiente ➡️
                </button>
            </div>
            <div class="page-size-selector">
                <label>Mostrar:</label>
                <select onchange="cambiarTamanioPagina(this.value)" id="sizeSelector">
                    <option value="10" ${tamanioPagina === 10 ? 'selected' : ''}>10</option>
                    <option value="25" ${tamanioPagina === 25 ? 'selected' : ''}>25</option>
                    <option value="50" ${tamanioPagina === 50 ? 'selected' : ''}>50</option>
                    <option value="100" ${tamanioPagina === 100 ? 'selected' : ''}>100</option>
                </select>
                <span>documentos</span>
            </div>
        </div>
    `;

    // Agregar o actualizar controles
    let paginationContainer = document.querySelector('.pagination-controls');
    if (paginationContainer) {
        paginationContainer.outerHTML = paginationHtml;
    } else {
        document.querySelector('.card').insertAdjacentHTML('beforeend', paginationHtml);
    }
}

// Cambiar página
function cambiarPagina(nuevaPagina) {
    cargarDocumentos(nuevaPagina, tamanioPagina);
}

// Cambiar tamaño de página
function cambiarTamanioPagina(nuevoTamanio) {
    tamanioPagina = parseInt(nuevoTamanio);
    cargarDocumentos(0, tamanioPagina); // Volver a página 1
}

// Función para mostrar documentos en la tabla
function mostrarDocumentos(registros) {
    const tableBody = document.getElementById('bitacora-table-body');

    if (registros.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: #999;">No se encontraron registros</td></tr>';
        return;
    }

    tableBody.innerHTML = registros.map(reg => {
        // Determinar fecha más reciente
        const fecha = reg.fechaSalida ? formatearFecha(reg.fechaSalida) : formatearFecha(reg.fechaEntrada);

        // Construir badges de tipo de operación
        let tiposOperacion = '';
        if (reg.tieneEntrada && reg.tieneSalida) {
            tiposOperacion = '<span style="color: #28a745; font-weight: bold;">📥 ENTRADA</span> + <span style="color: #dc3545; font-weight: bold;">📤 SALIDA</span>';
        } else if (reg.tieneEntrada) {
            tiposOperacion = '<span style="color: #28a745; font-weight: bold;">📥 ENTRADA</span><br><small style="color: #999;">Sin salida</small>';
        } else if (reg.tieneSalida) {
            tiposOperacion = '<span style="color: #dc3545; font-weight: bold;">📤 SALIDA</span><br><small style="color: #999;">Sin entrada</small>';
        }

        // Construir información del usuario
        let usuarioInfo = '';
        if (reg.tieneEntrada && reg.tieneSalida) {
            usuarioInfo = `<strong>Entrada:</strong> ${reg.usuarioEntrada || 'N/A'}<br><strong>Salida:</strong> ${reg.usuarioSalida || 'N/A'}`;
        } else if (reg.tieneEntrada) {
            usuarioInfo = reg.usuarioEntrada || 'Sin usuario';
        } else {
            usuarioInfo = reg.usuarioSalida || 'Sin usuario';
        }

        const tipo = reg.tipoDocumento || 'N/A';

        // Construir información de remitente/destinatario
        let infoParticipantes = '';
        if (reg.remitente && reg.destinatario) {
            infoParticipantes = `<strong>Remitente:</strong> ${reg.remitente}<br><strong>Destinatario:</strong> ${reg.destinatario}`;
        } else if (reg.remitente) {
            infoParticipantes = `<strong>Remitente:</strong> ${reg.remitente}`;
        } else if (reg.destinatario) {
            infoParticipantes = `<strong>Destinatario:</strong> ${reg.destinatario}`;
        } else {
            infoParticipantes = '<em style="color: #999;">Sin información</em>';
        }

        // Construir información de números de documento
        let numerosDoc = '';
        if (reg.numeroDocumentoEntrada && reg.numeroDocumentoSalida) {
            numerosDoc = `<strong>N° Entrada:</strong> ${reg.numeroDocumentoEntrada}<br><strong>N° Salida:</strong> ${reg.numeroDocumentoSalida}`;
        } else if (reg.numeroDocumentoEntrada) {
            numerosDoc = `<strong>N° Doc:</strong> ${reg.numeroDocumentoEntrada}`;
        } else if (reg.numeroDocumentoSalida) {
            numerosDoc = `<strong>N° Doc:</strong> ${reg.numeroDocumentoSalida}`;
        } else {
            numerosDoc = '<em style="color: #999;">Sin número</em>';
        }

        // Construir enlaces de archivos
        let archivosLinks = '';
        if (reg.archivoEntradaUrl) {
            archivosLinks += `📎 <a href="http://localhost:8080${reg.archivoEntradaUrl}" target="_blank">Ver archivo entrada</a>`;
        }
        if (reg.archivoSalidaUrl) {
            if (archivosLinks) archivosLinks += '<br>';
            archivosLinks += `📎 <a href="http://localhost:8080${reg.archivoSalidaUrl}" target="_blank">Ver cargo salida</a>`;
        }

        return `
            <tr>
                <td>${fecha}</td>
                <td>${tiposOperacion}</td>
                <td>${usuarioInfo}</td>
                <td>${tipo}</td>
                <td><strong>${reg.codigoDocumento}</strong></td>
                <td>
                    <strong>Título:</strong> ${reg.tituloDocumento}<br>
                    ${infoParticipantes}<br>
                    ${numerosDoc}
                    ${archivosLinks ? '<br>' + archivosLinks : ''}
                </td>
                <td>${reg.observacionesSalida || '-'}</td>
            </tr>
        `;
    }).join('');
}

// Eliminar la función agruparPorDocumento ya no es necesaria

// Función para inicializar eventos de filtros
function inicializarEventos() {
    document.getElementById('btn-aplicar-filtros').addEventListener('click', aplicarFiltros);
    document.getElementById('btn-limpiar-filtros').addEventListener('click', limpiarFiltros);

    // Aplicar filtros al presionar Enter en los inputs
    document.querySelectorAll('#filtro-palabra, #filtro-nro-doc, #filtro-nro-ht').forEach(input => {
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                aplicarFiltros();
            }
        });
    });
}

// Función para aplicar filtros
function aplicarFiltros() {
    const palabra = document.getElementById('filtro-palabra').value.toLowerCase();
    const nroDoc = document.getElementById('filtro-nro-doc').value.toLowerCase();
    const nroHt = document.getElementById('filtro-nro-ht').value.toLowerCase();
    const usuario = document.getElementById('filtro-usuario').value;

    let documentosFiltrados = documentosOriginales;

    // Filtrar por palabra clave (busca en título, descripción, remitente, código)
    if (palabra) {
        documentosFiltrados = documentosFiltrados.filter(item => {
            const doc = item.documento;
            return (doc.titulo && doc.titulo.toLowerCase().includes(palabra)) ||
                (doc.descripcion && doc.descripcion.toLowerCase().includes(palabra)) ||
                (doc.remitente && doc.remitente.toLowerCase().includes(palabra)) ||
                (doc.codigo && doc.codigo.toLowerCase().includes(palabra));
        });
    }

    // Filtrar por número de documento
    if (nroDoc) {
        documentosFiltrados = documentosFiltrados.filter(item => {
            const doc = item.documento;
            return doc.codigo && doc.codigo.toLowerCase().includes(nroDoc);
        });
    }

    // Filtrar por número de HT
    if (nroHt) {
        documentosFiltrados = documentosFiltrados.filter(item => {
            const doc = item.documento;
            return doc.numeroHt && doc.numeroHt.toLowerCase().includes(nroHt);
        });
    }

    // Filtrar por usuario asignado
    if (usuario) {
        documentosFiltrados = documentosFiltrados.filter(item =>
            item.idUsuarioAsignado === parseInt(usuario)
        );
    }

    // Ordenar por fecha (más recientes primero)
    documentosFiltrados.sort((a, b) => new Date(b.documento.fechaIngreso) - new Date(a.documento.fechaIngreso));

    mostrarDocumentos(documentosFiltrados);
}

// Función para limpiar filtros
function limpiarFiltros() {
    document.getElementById('filtro-palabra').value = '';
    document.getElementById('filtro-nro-doc').value = '';
    document.getElementById('filtro-nro-ht').value = '';
    document.getElementById('filtro-usuario').value = '';

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
    const estados = {
        'REGISTRADO': '<span style="background: #28a745; color: white; padding: 2px 8px; border-radius: 12px; font-size: 0.85rem;">Registrado</span>',
        'EN_REVISION': '<span style="background: #ffc107; color: #333; padding: 2px 8px; border-radius: 12px; font-size: 0.85rem;">En Revisión</span>',
        'APROBADO': '<span style="background: #007bff; color: white; padding: 2px 8px; border-radius: 12px; font-size: 0.85rem;">Aprobado</span>',
        'RECHAZADO': '<span style="background: #dc3545; color: white; padding: 2px 8px; border-radius: 12px; font-size: 0.85rem;">Rechazado</span>',
        'ARCHIVADO': '<span style="background: #6c757d; color: white; padding: 2px 8px; border-radius: 12px; font-size: 0.85rem;">Archivado</span>'
    };

    return estados[estado] || `<span style="color: #666;">${estado}</span>`;
}

// ============================================
// FUNCIONES DE EXPORTACIÓN
// ============================================

// Función para exportar bitácora a PDF
async function exportarBitacoraPDF() {
    showToast('Generando PDF...', 'info');

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/reportes/generar`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                tipoReporte: 'BITACORA',
                formato: 'PDF'
            })
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `bitacora_${new Date().toISOString().split('T')[0]}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        showToast('PDF generado exitosamente', 'success');
        document.getElementById('export-menu').style.display = 'none';
    } catch (error) {
        console.error('Error al exportar PDF:', error);
        showToast('Error al generar PDF: ' + error.message, 'error');
    }
}

// Función para exportar bitácora a Excel
async function exportarBitacoraExcel() {
    showToast('Generando Excel...', 'info');

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/reportes/generar`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                tipoReporte: 'BITACORA',
                formato: 'EXCEL'
            })
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `bitacora_${new Date().toISOString().split('T')[0]}.xlsx`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        showToast('Excel generado exitosamente', 'success');
        document.getElementById('export-menu').style.display = 'none';
    } catch (error) {
        console.error('Error al exportar Excel:', error);
        showToast('Error al generar Excel: ' + error.message, 'error');
    }
}
