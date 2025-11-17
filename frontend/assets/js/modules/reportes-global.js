// =====================================================
// REPORTES - FUNCIONES GLOBALES
// =====================================================

// URL base del API (solo si no está definida)
if (typeof API_URL === 'undefined') {
    var API_URL = window.API_URL || 'http://localhost:8080/api';
}

// Función para generar reporte PDF
async function generarReportePDF() {
    try {
        showToast('Generando reporte PDF...', 'loading');
        
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/reportes/pdf`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Error al generar el reporte');
        }

        // Obtener el blob del PDF
        const blob = await response.blob();
        
        // Crear URL temporal y descargar
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `reporte_documentos_${new Date().toISOString().split('T')[0]}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        
        showToast('✅ Reporte PDF generado exitosamente', 'success');

    } catch (error) {
        console.error('Error al generar reporte:', error);
        showToast('Error al generar el reporte: ' + error.message, 'error');
    }
}

// Función para ver PDF de documento
function verPDF(url) {
    if (!url) {
        showToast('No hay archivo PDF disponible', 'warning');
        return;
    }
    
    const token = localStorage.getItem('token');
    const pdfUrl = `${API_URL}/documentos/ver-pdf?url=${encodeURIComponent(url)}`;
    
    // Abrir en nueva pestaña con el token en la URL o usar fetch para mostrar
    window.open(pdfUrl, '_blank');
}

// Función global para botones de reportes
window.generarReportePDF = generarReportePDF;
window.verPDF = verPDF;
