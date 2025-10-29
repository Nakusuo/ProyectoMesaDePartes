// Configuración
const API_URL = 'http://localhost:8080/api';

// Variables globales
let documentosOriginales = [];
let usuariosDisponibles = [];

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

// Función para cargar todos los documentos
async function cargarDocumentos() {
    const tableBody = document.getElementById('bitacora-table-body');
    
    try {
        console.log('📡 Obteniendo documentos de bitácora:', `${API_URL}/documentos/bitacora`);
        
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/documentos/bitacora`, {
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
        console.log('✅ Documentos recibidos:', documentos.length);
        
        if (documentos.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #999;">No hay documentos registrados</td></tr>';
            return;
        }
        
        // Ordenar por fecha de ingreso (más recientes primero)
        documentos.sort((a, b) => new Date(b.documento.fechaIngreso) - new Date(a.documento.fechaIngreso));
        
        mostrarDocumentos(documentos);
        
    } catch (error) {
        console.error('❌ ERROR al cargar documentos:', error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; color: red;">
                    Error al cargar los documentos: ${error.message}
                </td>
            </tr>
        `;
    }
}

// Función para mostrar documentos en la tabla
function mostrarDocumentos(documentos) {
    const tableBody = document.getElementById('bitacora-table-body');
    
    if (documentos.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #999;">No se encontraron documentos</td></tr>';
        return;
    }
    
    tableBody.innerHTML = documentos.map(item => {
        const doc = item.documento;
        const fecha = formatearFecha(doc.fechaIngreso);
        const usuarioAsignado = item.usuarioAsignado || 'Sin asignar';
        const tipo = doc.tipoDocumento ? doc.tipoDocumento.nombre : 'N/A';
        const estado = obtenerEstadoBadge(doc.estado);
        
        return `
            <tr>
                <td>${fecha}</td>
                <td><strong>${usuarioAsignado}</strong></td>
                <td>Registro de documento</td>
                <td>${tipo}</td>
                <td>
                    <strong>Código:</strong> ${doc.codigo}<br>
                    <strong>Título:</strong> ${doc.titulo || 'Sin título'}<br>
                    <strong>Remitente:</strong> ${doc.remitente || 'N/A'}<br>
                    <strong>Estado:</strong> ${estado}
                    ${doc.archivoUrl ? '<br>📎 <a href="http://localhost:8080/' + doc.archivoUrl + '" target="_blank">Ver PDF</a>' : ''}
                </td>
            </tr>
        `;
    }).join('');
}

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
