// reportes-global.js - Funciones para generar y descargar reportes PDF

/**
 * Genera y descarga el reporte PDF de documentos
 */
async function generarReportePDF() {
    try {
        showToast('Generando reporte PDF...', 'info');
        
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/api/reportes/pdf`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error al generar PDF: ${response.status}`);
        }

        // Obtener el blob del PDF
        const blob = await response.blob();
        
        // Crear URL temporal para el blob
        const url = window.URL.createObjectURL(blob);
        
        // Crear elemento de enlace temporal
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = `Reporte_Documentos_${new Date().getTime()}.pdf`;
        
        // Agregar al DOM, hacer clic y remover
        document.body.appendChild(a);
        a.click();
        
        // Limpiar
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        showToast('Reporte PDF generado correctamente', 'success');
        
    } catch (error) {
        console.error('Error al generar PDF:', error);
        showToast('Error al generar el reporte PDF: ' + error.message, 'error');
    }
}

/**
 * Abre el PDF de un documento en una nueva pestaña
 * @param {string} archivoUrl - URL del archivo PDF
 */
async function verPDF(archivoUrl) {
    if (!archivoUrl) {
        showToast('No hay archivo PDF disponible para este documento', 'warning');
        return;
    }

    try {
        showToast('Cargando PDF...', 'info');
        
        const token = sessionStorage.getItem('token');
        
        // Construir URL completa con encoding correcto
        const url = `${API_BASE_URL}/api/documentos/ver-pdf?archivo=${encodeURIComponent(archivoUrl)}`;
        
        // Hacer fetch con autorización
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error al cargar PDF: ${response.status}`);
        }

        // Obtener el blob del PDF
        const blob = await response.blob();
        
        // Crear URL temporal para el blob
        const blobUrl = window.URL.createObjectURL(blob);
        
        // Abrir en nueva pestaña
        const newWindow = window.open(blobUrl, '_blank');
        
        if (!newWindow) {
            showToast('Por favor permite las ventanas emergentes para ver el PDF', 'warning');
        } else {
            // Limpiar la URL del blob después de un tiempo
            setTimeout(() => {
                window.URL.revokeObjectURL(blobUrl);
            }, 1000);
        }
        
    } catch (error) {
        console.error('Error al abrir PDF:', error);
        showToast('Error al abrir el archivo PDF: ' + error.message, 'error');
    }
}

/**
 * Descarga el archivo PDF de un documento
 * @param {string} archivoUrl - URL del archivo PDF
 * @param {string} nombreDocumento - Nombre del documento para el archivo descargado
 */
async function descargarPDF(archivoUrl, nombreDocumento) {
    if (!archivoUrl) {
        showToast('No hay archivo disponible para descargar', 'warning');
        return;
    }

    try {
        showToast('Descargando archivo...', 'info');
        
        const token = sessionStorage.getItem('token');
        
        // Construir URL completa
        let url;
        if (archivoUrl.startsWith('http')) {
            url = archivoUrl;
        } else {
            url = `${API_BASE_URL}/api/documentos/ver-pdf?archivo=${encodeURIComponent(archivoUrl)}`;
        }
        
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error al descargar: ${response.status}`);
        }

        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = downloadUrl;
        a.download = nombreDocumento ? `${nombreDocumento}.pdf` : 'documento.pdf';
        
        document.body.appendChild(a);
        a.click();
        
        window.URL.revokeObjectURL(downloadUrl);
        document.body.removeChild(a);
        
        showToast('Archivo descargado correctamente', 'success');
        
    } catch (error) {
        console.error('Error al descargar PDF:', error);
        showToast('Error al descargar el archivo: ' + error.message, 'error');
    }
}

/**
 * Genera y descarga reporte Excel de documentos
 */
async function generarReporteExcel() {
    try {
        showToast('Generando reporte Excel...', 'info');
        
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/api/reportes/excel`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error al generar Excel: ${response.status}`);
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = `Reporte_Documentos_${new Date().getTime()}.xlsx`;
        
        document.body.appendChild(a);
        a.click();
        
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        showToast('Reporte Excel generado correctamente', 'success');
        
    } catch (error) {
        console.error('Error al generar Excel:', error);
        showToast('Error al generar el reporte Excel: ' + error.message, 'error');
    }
}
