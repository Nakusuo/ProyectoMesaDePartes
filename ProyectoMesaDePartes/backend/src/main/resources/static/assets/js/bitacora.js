// Configuración
const API_URL = 'http://localhost:8080/api';

// Cargar documentos al iniciar
document.addEventListener('DOMContentLoaded', () => {
    console.log('🔵 Bitácora cargada');
    cargarDocumentos();
});

// Función para cargar todos los documentos
async function cargarDocumentos() {
    const tableBody = document.getElementById('bitacora-table-body');
    
    try {
        console.log('📡 Obteniendo documentos de:', `${API_URL}/documentos`);
        
        const response = await fetch(`${API_URL}/documentos`);
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
        const documentos = await response.json();
        console.log('✅ Documentos recibidos:', documentos.length);
        
        if (documentos.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #999;">No hay documentos registrados</td></tr>';
            return;
        }
        
        // Ordenar por fecha de ingreso (más recientes primero)
        documentos.sort((a, b) => new Date(b.fechaIngreso) - new Date(a.fechaIngreso));
        
        // Generar filas de la tabla
        tableBody.innerHTML = documentos.map(doc => {
            const fecha = formatearFecha(doc.fechaIngreso);
            const usuario = doc.usuario ? doc.usuario.nombre : 'Sin asignar';
            const tipo = doc.tipoDocumento ? doc.tipoDocumento.nombre : 'N/A';
            const estado = obtenerEstadoBadge(doc.estado);
            
            return `
                <tr>
                    <td>${fecha}</td>
                    <td><strong>${usuario}</strong></td>
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
