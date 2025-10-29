// Dashboard.js - Cargar datos reales desde la API
const API_URL = window.API_URL || 'http://localhost:8080/api';

// Variables para las gráficas
let chartPorTipo = null;
let chartPorEstado = null;
let chartTiempo = null;

// Inicializar dashboard
document.addEventListener('DOMContentLoaded', function() {
    mostrarFechaActual();
    cargarDashboard();
});

// Mostrar fecha actual
function mostrarFechaActual() {
    const fecha = new Date();
    const opciones = { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
    };
    document.getElementById('fecha-actual').textContent = 
        fecha.toLocaleDateString('es-PE', opciones);
}

// Cargar todos los datos del dashboard
async function cargarDashboard() {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = 'login.html';
            return;
        }

        await cargarMetricas(token);
        await cargarGraficas(token);
        await cargarDocumentosRecientes(token);
        
    } catch (error) {
        console.error('Error al cargar dashboard:', error);
    }
}

// Cargar métricas principales
async function cargarMetricas(token) {
    try {
        const responseDocumentos = await fetch(`${API_URL}/documentos`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (responseDocumentos.ok) {
            const documentos = await responseDocumentos.json();
            
            const total = documentos.length;
            const enProceso = documentos.filter(d => 
                d.estadoDocumento?.nombre?.toUpperCase() === 'EN_PROCESO' || 
                d.estadoDocumento?.nombre?.toUpperCase() === 'PENDIENTE'
            ).length;
            const finalizados = documentos.filter(d => 
                d.estadoDocumento?.nombre?.toUpperCase() === 'FINALIZADO' ||
                d.estadoDocumento?.nombre?.toUpperCase() === 'ATENDIDO'
            ).length;

            document.getElementById('total-documentos').textContent = total;
            document.getElementById('documentos-proceso').textContent = enProceso;
            document.getElementById('documentos-finalizados').textContent = finalizados;
        }

        const responseUsuarios = await fetch(`${API_URL}/usuarios`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (responseUsuarios.ok) {
            const usuarios = await responseUsuarios.json();
            const usuariosActivos = usuarios.filter(u => u.activo).length;
            document.getElementById('total-usuarios').textContent = usuariosActivos;
        }

    } catch (error) {
        console.error('Error al cargar métricas:', error);
    }
}

// Cargar datos para las gráficas
async function cargarGraficas(token) {
    try {
        const response = await fetch(`${API_URL}/documentos`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            console.error('Error al cargar documentos para gráficas');
            return;
        }

        const documentos = await response.json();

        // Gráfica por tipo de documento
        crearGraficaPorTipo(documentos);

        // Gráfica por estado
        crearGraficaPorEstado(documentos);

        // Gráfica de documentos en el tiempo
        crearGraficaTiempo(documentos);

    } catch (error) {
        console.error('Error al cargar gráficas:', error);
    }
}

// Crear gráfica de documentos por tipo
function crearGraficaPorTipo(documentos) {
    const tipos = {};
    
    documentos.forEach(doc => {
        const tipo = doc.tipoDocumento?.nombre || 'Sin Tipo';
        tipos[tipo] = (tipos[tipo] || 0) + 1;
    });

    const ctx = document.getElementById('chart-por-tipo');
    if (!ctx) {
        console.error('Canvas chart-por-tipo no encontrado');
        return;
    }

    if (chartPorTipo) {
        chartPorTipo.destroy();
    }

    chartPorTipo = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(tipos),
            datasets: [{
                data: Object.values(tipos),
                backgroundColor: [
                    'rgba(0, 100, 46, 0.8)',
                    'rgba(251, 191, 36, 0.8)',
                    'rgba(16, 185, 129, 0.8)',
                    'rgba(245, 158, 11, 0.8)',
                    'rgba(0, 140, 64, 0.8)',
                    'rgba(252, 211, 77, 0.8)'
                ],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        font: { size: 12 }
                    }
                }
            }
        }
    });
}

// Crear gráfica de documentos por estado
function crearGraficaPorEstado(documentos) {
    const estados = {};
    
    documentos.forEach(doc => {
        const estado = doc.estado || 'Sin Estado';
        estados[estado] = (estados[estado] || 0) + 1;
    });

    const ctx = document.getElementById('chart-por-estado');
    if (!ctx) {
        console.error('Canvas chart-por-estado no encontrado');
        return;
    }

    if (chartPorEstado) {
        chartPorEstado.destroy();
    }

    chartPorEstado = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(estados),
            datasets: [{
                label: 'Cantidad',
                data: Object.values(estados),
                backgroundColor: 'rgba(0, 100, 46, 0.8)',
                borderColor: 'rgba(0, 100, 46, 1)',
                borderWidth: 2
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

// Crear gráfica de documentos en el tiempo
function crearGraficaTiempo(documentos) {
    const porMes = {};
    
    documentos.forEach(doc => {
        if (doc.fechaIngreso) {
            const fecha = new Date(doc.fechaIngreso);
            const mes = `${fecha.getFullYear()}-${String(fecha.getMonth() + 1).padStart(2, '0')}`;
            porMes[mes] = (porMes[mes] || 0) + 1;
        }
    });

    const mesesOrdenados = Object.keys(porMes).sort();
    const valores = mesesOrdenados.map(mes => porMes[mes]);

    const ctx = document.getElementById('chart-tiempo');
    if (!ctx) {
        console.error('Canvas chart-tiempo no encontrado');
        return;
    }

    if (chartTiempo) {
        chartTiempo.destroy();
    }

    chartTiempo = new Chart(ctx, {
        type: 'line',
        data: {
            labels: mesesOrdenados.map(mes => {
                const [year, month] = mes.split('-');
                const fecha = new Date(year, month - 1);
                return fecha.toLocaleDateString('es-PE', { month: 'short', year: 'numeric' });
            }),
            datasets: [{
                label: 'Documentos Registrados',
                data: valores,
                borderColor: 'rgba(0, 100, 46, 1)',
                backgroundColor: 'rgba(0, 100, 46, 0.1)',
                tension: 0.4,
                fill: true,
                borderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
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

// Cargar documentos recientes
async function cargarDocumentosRecientes(token) {
    try {
        const response = await fetch(`${API_URL}/documentos`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Error al cargar documentos');
        }

        const documentos = await response.json();
        
        const recientes = documentos
            .sort((a, b) => new Date(b.fechaIngreso) - new Date(a.fechaIngreso))
            .slice(0, 10);

        mostrarDocumentosRecientes(recientes);

    } catch (error) {
        console.error('Error al cargar documentos recientes:', error);
        document.getElementById('documentos-recientes-body').innerHTML = 
            '<tr><td colspan="5" style="text-align: center; padding: 20px;">Error al cargar documentos</td></tr>';
    }
}

// Mostrar documentos recientes en la tabla
function mostrarDocumentosRecientes(documentos) {
    const tbody = document.getElementById('documentos-recientes-body');
    
    if (documentos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 20px;">No hay documentos registrados</td></tr>';
        return;
    }

    tbody.innerHTML = documentos.map(doc => {
        const fecha = doc.fechaIngreso ? 
            new Date(doc.fechaIngreso).toLocaleDateString('es-PE') : 
            'Sin fecha';
        
        const estado = doc.estado || 'Sin estado';
        let badgeClass = 'badge-info';
        
        if (estado.toUpperCase().includes('FINALIZADO')) {
            badgeClass = 'badge-success';
        } else if (estado.toUpperCase().includes('PROCESO')) {
            badgeClass = 'badge-warning';
        } else if (estado.toUpperCase().includes('REGISTRADO')) {
            badgeClass = 'badge-info';
        }

        return `
            <tr>
                <td>${fecha}</td>
                <td>${doc.titulo || 'Sin título'}</td>
                <td>${doc.tipoDocumento?.nombre || 'Sin tipo'}</td>
                <td>${doc.remitente || 'Sin remitente'}</td>
                <td><span class="badge ${badgeClass}">${estado}</span></td>
            </tr>
        `;
    }).join('');
}
