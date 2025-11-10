// Dashboard.js - Cargar datos reales desde la API
const API_URL = window.APP_CONFIG?.API_BASE_URL || 'http://localhost:8080/api';

console.log('🔗 API_URL configurada:', API_URL);

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
        console.log('🚀 Iniciando carga de dashboard...');
        
        const token = localStorage.getItem('token');
        if (!token) {
            console.warn('❌ No hay token, redirigiendo a login');
            window.location.href = 'login.html';
            return;
        }

        console.log('✅ Token encontrado');

        await cargarMetricas(token);
        await cargarGraficas(token);
        await cargarDocumentosRecientes(token);
        
        console.log('✅ Dashboard cargado completamente');
        
    } catch (error) {
        console.error('❌ Error al cargar dashboard:', error);
        // Mostrar mensaje de error al usuario
        if (window.showToast) {
            showToast('Error al cargar el dashboard', 'error', 'Error');
        }
    }
}

// Cargar métricas principales
async function cargarMetricas(token) {
    try {
        console.log('📊 Cargando métricas...');
        
        // Verificar permisos
        const pm = window.permissionsManager;
        let url = `${API_URL}/documentos`;
        
        // Si es trabajador, filtrar por usuario asignado
        if (pm && pm.shouldFilterDocumentsByUser()) {
            const userId = pm.getUserId();
            if (userId) {
                url = `${API_URL}/documentos/asignados/${userId}`;
                console.log(`👤 Filtrando por usuario ${userId}`);
            }
        }
        
        console.log('🔗 Consultando:', url);
        
        const responseDocumentos = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!responseDocumentos.ok) {
            throw new Error(`Error HTTP: ${responseDocumentos.status}`);
        }

        if (responseDocumentos.ok) {
            const documentos = await responseDocumentos.json();
            
            console.log('📊 Documentos cargados:', documentos.length);
            console.log('📄 Primer documento:', documentos[0]);
            
            const total = documentos.length;
            
            // Filtrar documentos en proceso (estado es un string directo del ENUM)
            const enProceso = documentos.filter(d => {
                const estado = (d.estado || '').toUpperCase().replace('_', ' ');
                return estado === 'EN PROCESO' || estado === 'EN_PROCESO' || 
                       estado === 'ASIGNADO' || estado === 'RECIBIDO';
            }).length;
            
            // Filtrar documentos finalizados
            const finalizados = documentos.filter(d => {
                const estado = (d.estado || '').toUpperCase();
                return estado === 'FINALIZADO' || estado === 'SALIDA';
            }).length;

            console.log(`📈 Métricas - Total: ${total}, En Proceso: ${enProceso}, Finalizados: ${finalizados}`);

            document.getElementById('total-documentos').textContent = total;
            document.getElementById('documentos-proceso').textContent = enProceso;
            document.getElementById('documentos-finalizados').textContent = finalizados;
        }

        console.log('👥 Cargando usuarios...');
        
        const responseUsuarios = await fetch(`${API_URL}/usuarios`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (responseUsuarios.ok) {
            const usuarios = await responseUsuarios.json();
            const usuariosActivos = usuarios.filter(u => u.activo || u.activo === undefined).length;
            console.log(`👥 Usuarios activos: ${usuariosActivos}`);
            document.getElementById('total-usuarios').textContent = usuariosActivos;
        }

        console.log('✅ Métricas cargadas correctamente');

    } catch (error) {
        console.error('❌ Error al cargar métricas:', error);
        // Mostrar valores por defecto
        document.getElementById('total-documentos').textContent = '0';
        document.getElementById('documentos-proceso').textContent = '0';
        document.getElementById('documentos-finalizados').textContent = '0';
        document.getElementById('total-usuarios').textContent = '0';
    }
}

// Cargar datos para las gráficas
async function cargarGraficas(token) {
    try {
        console.log('📊 Cargando datos para gráficas...');
        
        // Verificar permisos
        const pm = window.permissionsManager;
        let url = `${API_URL}/documentos`;
        
        // Si es trabajador, filtrar por usuario asignado
        if (pm && pm.shouldFilterDocumentsByUser()) {
            const userId = pm.getUserId();
            if (userId) {
                url = `${API_URL}/documentos/asignados/${userId}`;
                console.log(`👤 Filtrando gráficas por usuario ${userId}`);
            }
        }
        
        console.log('🔗 Consultando para gráficas:', url);
        
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            console.error('❌ Error al cargar documentos para gráficas:', response.status);
            mostrarMensajesSinDatos();
            return;
        }

        const documentos = await response.json();
        
        console.log(`📊 Documentos para gráficas: ${documentos.length}`);

        if (documentos.length === 0) {
            console.warn('⚠️ No hay documentos para mostrar en gráficas');
            mostrarMensajesSinDatos();
            return;
        }

        // Gráfica por tipo de documento
        crearGraficaPorTipo(documentos);

        // Gráfica por estado
        crearGraficaPorEstado(documentos);

        // Gráfica de documentos en el tiempo
        crearGraficaTiempo(documentos);
        
        console.log('✅ Gráficas creadas exitosamente');

    } catch (error) {
        console.error('❌ Error al cargar gráficas:', error);
        mostrarMensajesSinDatos();
    }
}

// Mostrar mensajes cuando no hay datos
function mostrarMensajesSinDatos() {
    const charts = ['chart-por-tipo', 'chart-por-estado', 'chart-tiempo'];
    
    charts.forEach(chartId => {
        const canvas = document.getElementById(chartId);
        if (canvas) {
            const parent = canvas.parentElement;
            const mensaje = document.createElement('div');
            mensaje.style.cssText = 'text-align: center; padding: 40px; color: var(--text-secondary);';
            mensaje.innerHTML = `
                <div style="font-size: 48px; margin-bottom: 16px;">📊</div>
                <p style="font-weight: 600; margin-bottom: 8px;">No hay datos disponibles</p>
                <p style="font-size: 14px;">No tienes documentos asignados todavía</p>
            `;
            canvas.style.display = 'none';
            parent.appendChild(mensaje);
        }
    });
}

// Crear gráfica de documentos por tipo
function crearGraficaPorTipo(documentos) {
    const tipos = {};
    
    documentos.forEach(doc => {
        // Intentar obtener el nombre del tipo de varias formas
        let tipo = 'Sin Tipo';
        if (doc.tipoDocumento) {
            if (typeof doc.tipoDocumento === 'string') {
                tipo = doc.tipoDocumento;
            } else if (doc.tipoDocumento.nombre) {
                tipo = doc.tipoDocumento.nombre;
            } else if (doc.tipoDocumento.descripcion) {
                tipo = doc.tipoDocumento.descripcion;
            }
        }
        tipos[tipo] = (tipos[tipo] || 0) + 1;
    });

    console.log('📊 Tipos de documento:', tipos);

    const ctx = document.getElementById('chart-por-tipo');
    if (!ctx) {
        console.error('❌ Canvas chart-por-tipo no encontrado');
        return;
    }

    // Verificar si hay datos
    if (Object.keys(tipos).length === 0) {
        console.warn('⚠️ No hay tipos para mostrar en gráfica');
        return;
    }

    if (chartPorTipo) {
        chartPorTipo.destroy();
    }

    try {
        ctx.style.display = 'block';
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
        console.log('✅ Gráfica por tipo creada');
    } catch (error) {
        console.error('❌ Error al crear gráfica por tipo:', error);
    }
}

// Crear gráfica de documentos por estado
function crearGraficaPorEstado(documentos) {
    const estados = {};
    
    documentos.forEach(doc => {
        // Formatear el estado para mostrarlo mejor
        let estado = doc.estado || 'Sin Estado';
        // Reemplazar guiones bajos por espacios
        estado = estado.replace(/_/g, ' ');
        // Capitalizar primera letra de cada palabra
        estado = estado.split(' ').map(word => 
            word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
        ).join(' ');
        
        estados[estado] = (estados[estado] || 0) + 1;
    });

    console.log('📊 Estados de documento:', estados);

    const ctx = document.getElementById('chart-por-estado');
    if (!ctx) {
        console.error('❌ Canvas chart-por-estado no encontrado');
        return;
    }

    // Verificar si hay datos
    if (Object.keys(estados).length === 0) {
        console.warn('⚠️ No hay estados para mostrar en gráfica');
        return;
    }

    if (chartPorEstado) {
        chartPorEstado.destroy();
    }

    try {
        ctx.style.display = 'block';
        
        // Colores por estado
        const colores = Object.keys(estados).map(estado => {
            const estadoUpper = estado.toUpperCase();
            if (estadoUpper.includes('FINALIZADO') || estadoUpper.includes('SALIDA')) {
                return 'rgba(16, 185, 129, 0.8)'; // Verde success
            } else if (estadoUpper.includes('PROCESO') || estadoUpper.includes('RECIBIDO')) {
                return 'rgba(251, 191, 36, 0.8)'; // Amarillo warning
            } else if (estadoUpper.includes('OBSERVADO')) {
                return 'rgba(239, 68, 68, 0.8)'; // Rojo danger
            } else {
                return 'rgba(0, 100, 46, 0.8)'; // Verde PNP
            }
        });

        chartPorEstado = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(estados),
                datasets: [{
                    label: 'Cantidad',
                    data: Object.values(estados),
                    backgroundColor: colores,
                    borderColor: colores.map(c => c.replace('0.8', '1')),
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
        console.log('✅ Gráfica por estado creada');
    } catch (error) {
        console.error('❌ Error al crear gráfica por estado:', error);
    }
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
        // Verificar permisos
        const pm = window.permissionsManager;
        let url = `${API_URL}/documentos`;
        
        // Si es trabajador, filtrar por usuario asignado
        if (pm && pm.shouldFilterDocumentsByUser()) {
            const userId = pm.getUserId();
            if (userId) {
                url = `${API_URL}/documentos/asignados/${userId}`;
            }
        }
        
        const response = await fetch(url, {
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
    
    if (!tbody) {
        console.error('Elemento documentos-recientes-body no encontrado');
        return;
    }
    
    if (documentos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 20px;">No hay documentos registrados</td></tr>';
        return;
    }

    console.log('📋 Mostrando documentos recientes:', documentos.length);

    tbody.innerHTML = documentos.map(doc => {
        const fecha = doc.fechaIngreso ? 
            new Date(doc.fechaIngreso).toLocaleDateString('es-PE', { 
                year: 'numeric', 
                month: '2-digit', 
                day: '2-digit' 
            }) : 
            'Sin fecha';
        
        // Estado es un string directo del ENUM
        let estado = doc.estado || 'Sin estado';
        estado = estado.replace(/_/g, ' ');
        
        let badgeClass = 'badge-info';
        const estadoUpper = estado.toUpperCase();
        
        if (estadoUpper.includes('FINALIZADO') || estadoUpper.includes('SALIDA')) {
            badgeClass = 'badge-success';
        } else if (estadoUpper.includes('PROCESO') || estadoUpper.includes('RECIBIDO')) {
            badgeClass = 'badge-warning';
        } else if (estadoUpper.includes('ASIGNADO')) {
            badgeClass = 'badge-info';
        } else if (estadoUpper.includes('OBSERVADO')) {
            badgeClass = 'badge-danger';
        }

        // Obtener nombre del tipo de documento
        let tipoDocumento = 'Sin tipo';
        if (doc.tipoDocumento) {
            if (typeof doc.tipoDocumento === 'string') {
                tipoDocumento = doc.tipoDocumento;
            } else if (doc.tipoDocumento.nombre) {
                tipoDocumento = doc.tipoDocumento.nombre;
            } else if (doc.tipoDocumento.descripcion) {
                tipoDocumento = doc.tipoDocumento.descripcion;
            }
        }

        return `
            <tr>
                <td>${fecha}</td>
                <td><strong>${doc.codigo || 'S/C'}</strong> - ${doc.asunto || doc.titulo || 'Sin asunto'}</td>
                <td>${tipoDocumento}</td>
                <td>${doc.remitente || 'Sin remitente'}</td>
                <td><span class="badge ${badgeClass}">${estado}</span></td>
            </tr>
        `;
    }).join('');
}
