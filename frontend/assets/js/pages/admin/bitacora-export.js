// Función para exportar bitácora a PDF
async function exportarBitacoraPDF() {
    showToast('Generando PDF...', 'info');

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/reportes/bitacora/pdf`, {
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
        const response = await fetch(`${API_URL}/reportes/bitacora/excel`, {
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
