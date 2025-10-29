// Dashboard JavaScript
const API_URL = 'http://localhost:8080/api';

// Función para obtener el token
function getToken() {
    return localStorage.getItem('token');
}

// Cargar estadísticas
async function cargarEstadisticas() {
    try {
        const token = getToken();
        
        // Cargar total de documentos
        const docsResponse = await fetch(`${API_URL}/documentos`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const documentos = await docsResponse.json();
        
        // Actualizar estadísticas
        document.getElementById('totalDocumentos').textContent = documentos.length;
        
        // Contar por estado
        const registrados = documentos.filter(d => d.estado === 'Registrado').length;
        const enProceso = documentos.filter(d => d.estado === 'En Proceso').length;
        const finalizados = documentos.filter(d => d.estado === 'Finalizado').length;
        
        document.getElementById('enProceso').textContent = enProceso;
        document.getElementById('finalizados').textContent = finalizados;
        
        // Cargar usuarios activos
        const usuariosResponse = await fetch(`${API_URL}/usuarios`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const usuarios = await usuariosResponse.json();
        const activos = usuarios.filter(u => u.activo).length;
        document.getElementById('usuariosActivos').textContent = activos;
        
        // Mostrar documentos recientes (últimos 10)
        mostrarDocumentosRecientes(documentos.slice(-10).reverse());
        
        // Crear gráficos
        crearGraficoEstados(registrados, enProceso, finalizados);
        crearGraficoMeses(documentos);
        
    } catch (error) {
        console.error('Error al cargar estadísticas:', error);
    }
}

// Mostrar documentos recientes en tabla
function mostrarDocumentosRecientes(documentos) {
    const tbody = document.getElementById('recentDocumentsBody');
    
    if (documentos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">No hay documentos registrados</td></tr>';
        return;
    }
    
    tbody.innerHTML = documentos.map(doc => `
        <tr>
            <td>${doc.numeroRegistro}</td>
            <td>${new Date(doc.fechaDocumento).toLocaleDateString('es-PE')}</td>
            <td>${doc.tipoDocumento?.nombre || 'N/A'}</td>
            <td>${doc.remitente}</td>
            <td><span class="badge badge-${getEstadoClass(doc.estado)}">${doc.estado}</span></td>
        </tr>
    `).join('');
}

function getEstadoClass(estado) {
    const classes = {
        'Registrado': 'info',
        'En Proceso': 'warning',
        'Observado': 'danger',
        'Finalizado': 'success',
        'Salida': 'secondary'
    };
    return classes[estado] || 'secondary';
}

// Crear gráfico de estados
function crearGraficoEstados(registrados, enProceso, finalizados) {
    const ctx = document.getElementById('estadosChart').getContext('2d');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Registrados', 'En Proceso', 'Finalizados'],
            datasets: [{
                data: [registrados, enProceso, finalizados],
                backgroundColor: ['#17a2b8', '#ffc107', '#28a745'],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

// Crear gráfico de documentos por mes
function crearGraficoMeses(documentos) {
    const mesesData = {};
    const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    
    documentos.forEach(doc => {
        const fecha = new Date(doc.fechaDocumento);
        const mes = meses[fecha.getMonth()];
        mesesData[mes] = (mesesData[mes] || 0) + 1;
    });
    
    const ctx = document.getElementById('mesesChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: meses,
            datasets: [{
                label: 'Documentos',
                data: meses.map(m => mesesData[m] || 0),
                borderColor: '#00563B',
                backgroundColor: 'rgba(0, 86, 59, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// Inicializar dashboard
document.addEventListener('DOMContentLoaded', () => {
    cargarEstadisticas();
});