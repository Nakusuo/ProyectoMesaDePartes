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
            const nombreCompleto = u => `${u.nombre} ${u.apellido}`;
            select.innerHTML = '<option value="">Todos</option>' +
                usuariosDisponibles.map(u =>
                    `<option value="${nombreCompleto(u)}">${nombreCompleto(u)}</option>`
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

        // Construir información de números de documento y HT
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
        
        // Agregar información de HT
        const htEntrada = reg.numeroHtEntrada || 'S/N';
        const htSalida = reg.numeroHtSalida || (reg.tieneSalida ? 'S/N' : '-');
        let htInfo = `<strong>HT Entrada:</strong> ${htEntrada}`;
        if (reg.tieneSalida) {
            htInfo += `<br><strong>HT Salida:</strong> ${htSalida}`;
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
                    ${numerosDoc}<br>
                    ${htInfo}
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
    let nroDoc = document.getElementById('filtro-nro-doc').value.trim();
    let nroHt = document.getElementById('filtro-nro-ht').value.trim();
    const usuario = document.getElementById('filtro-usuario').value;

    // Formatear automáticamente el número de documento (igual que en salida-documento)
    if (nroDoc) {
        if (/^\d+$/.test(nroDoc)) {
            // Solo números, convertir a formato DOC-000000
            const numero = parseInt(nroDoc);
            nroDoc = `DOC-${numero.toString().padStart(6, '0')}`;
            console.log(`✨ Formato automático Nº Doc: "${document.getElementById('filtro-nro-doc').value}" → "${nroDoc}"`);
        } else if (!/^DOC-\d{6}$/.test(nroDoc)) {
            // Si no es formato DOC-000000 ni solo números, intentar extraer números
            const numeros = nroDoc.match(/\d+/);
            if (numeros) {
                const numero = parseInt(numeros[0]);
                nroDoc = `DOC-${numero.toString().padStart(6, '0')}`;
                console.log(`✨ Números extraídos Nº Doc: "${document.getElementById('filtro-nro-doc').value}" → "${nroDoc}"`);
            }
        }
    }

    // Formatear automáticamente el número de HT (formato HT-0000)
    if (nroHt) {
        if (/^\d+$/.test(nroHt)) {
            // Solo números, convertir a formato HT-0000
            const numero = parseInt(nroHt);
            nroHt = `HT-${numero.toString().padStart(4, '0')}`;
            console.log(`✨ Formato automático Nº HT: "${document.getElementById('filtro-nro-ht').value}" → "${nroHt}"`);
        } else if (!/^HT-\d{4}$/.test(nroHt)) {
            // Si no es formato HT-0000 ni solo números, intentar extraer números
            const numeros = nroHt.match(/\d+/);
            if (numeros) {
                const numero = parseInt(numeros[0]);
                nroHt = `HT-${numero.toString().padStart(4, '0')}`;
                console.log(`✨ Números extraídos Nº HT: "${document.getElementById('filtro-nro-ht').value}" → "${nroHt}"`);
            }
        }
    }

    let documentosFiltrados = documentosOriginales;

    // Filtrar por palabra clave (busca en remitente, destinatario, tipo, título)
    if (palabra) {
        documentosFiltrados = documentosFiltrados.filter(item => {
            return (item.remitente && item.remitente.toLowerCase().includes(palabra)) ||
                (item.destinatario && item.destinatario.toLowerCase().includes(palabra)) ||
                (item.tipoDocumento && item.tipoDocumento.toLowerCase().includes(palabra)) ||
                (item.tituloDocumento && item.tituloDocumento.toLowerCase().includes(palabra)) ||
                (item.codigoDocumento && item.codigoDocumento.toLowerCase().includes(palabra)) ||
                (item.numeroDocumentoEntrada && item.numeroDocumentoEntrada.toLowerCase().includes(palabra)) ||
                (item.numeroDocumentoSalida && item.numeroDocumentoSalida.toLowerCase().includes(palabra));
        });
    }

    // Filtrar por número de documento (busca en código y números de entrada/salida)
    if (nroDoc) {
        documentosFiltrados = documentosFiltrados.filter(item => {
            return (item.codigoDocumento && item.codigoDocumento.toLowerCase().includes(nroDoc.toLowerCase())) ||
                (item.numeroDocumentoEntrada && item.numeroDocumentoEntrada.toLowerCase().includes(nroDoc.toLowerCase())) ||
                (item.numeroDocumentoSalida && item.numeroDocumentoSalida.toLowerCase().includes(nroDoc.toLowerCase()));
        });
    }

    // Filtrar por número de HT
    if (nroHt) {
        documentosFiltrados = documentosFiltrados.filter(item => {
            return (item.numeroDocumentoEntrada && item.numeroDocumentoEntrada.toLowerCase().includes(nroHt.toLowerCase())) ||
                (item.numeroDocumentoSalida && item.numeroDocumentoSalida.toLowerCase().includes(nroHt.toLowerCase()));
        });
    }

    // Filtrar por usuario
    if (usuario) {
        console.log('🔍 Filtrando por usuario:', usuario);
        const usuarioBuscado = usuario.toLowerCase().trim();
        documentosFiltrados = documentosFiltrados.filter(item => {
            const entradaMatch = item.usuarioEntrada && item.usuarioEntrada.toLowerCase().trim().includes(usuarioBuscado);
            const salidaMatch = item.usuarioSalida && item.usuarioSalida.toLowerCase().trim().includes(usuarioBuscado);
            
            if (entradaMatch || salidaMatch) {
                console.log('✅ Coincidencia encontrada:', {
                    codigo: item.codigoDocumento,
                    usuarioEntrada: item.usuarioEntrada,
                    usuarioSalida: item.usuarioSalida,
                    buscado: usuario
                });
            }
            
            return entradaMatch || salidaMatch;
        });
        console.log('📊 Documentos después de filtrar por usuario:', documentosFiltrados.length);
    }

    // Ordenar por fecha (más recientes primero) - usar fechaSalida o fechaEntrada
    documentosFiltrados.sort((a, b) => {
        const fechaA = new Date(a.fechaSalida || a.fechaEntrada || 0);
        const fechaB = new Date(b.fechaSalida || b.fechaEntrada || 0);
        return fechaB - fechaA;
    });

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

// Función para exportar bitácora a PDF (disponible globalmente)
window.exportarBitacoraPDF = async function() {
    console.log('🔵 Exportando PDF...');
    showToast('Generando PDF...', 'info');

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/bitacora/exportar/pdf`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error HTTP: ${response.status} - ${errorText}`);
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
        console.error('❌ Error al exportar PDF:', error);
        showToast('Error al generar PDF: ' + error.message, 'error');
    }
}

// Función para exportar bitácora a Excel (disponible globalmente)
window.exportarBitacoraExcel = async function() {
    console.log('🔵 Exportando Excel...');
    showToast('Generando Excel...', 'info');

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/bitacora/exportar/excel`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error HTTP: ${response.status} - ${errorText}`);
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
        console.error('❌ Error al exportar Excel:', error);
        showToast('Error al generar Excel: ' + error.message, 'error');
    }
}
