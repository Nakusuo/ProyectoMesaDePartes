// --- 1. Verificación de Seguridad ---
// Permitimos que 'Administrador' o 'Jefatura' vean el dashboard.
// (Nota: auth.js no soporta roles múltiples, usaremos solo Admin por ahora)
checkAuth('Administrador'); // O 'Jefatura'


// --- 2. Variables Globales y Constantes ---
const API_URL = 'http://localhost:8080/api';
const token = getToken();

// Elementos del DOM
const btnLogout = document.getElementById('btn-logout');
const statIngresados = document.getElementById('stat-ingresados');
const statProceso = document.getElementById('stat-proceso');
const statFinalizados = document.getElementById('stat-finalizados');
const statArchivados = document.getElementById('stat-archivados');

// Contextos de los Gráficos
const ctxArea = document.getElementById('areaChart').getContext('2d');
const ctxEstado = document.getElementById('estadoChart').getContext('2d');

// Variables para guardar las instancias de los gráficos (para destruirlos al recargar)
let areaChartInstance = null;
let estadoChartInstance = null;

// --- 3. Funciones de Carga de Datos ---

// Carga el resumen de estadísticas y datos de gráficos
async function cargarDashboard() {
  try {
    // ¡OJO! Este endpoint /api/dashboard/resumen no lo hemos creado en el backend.
    // Dará error hasta que lo creemos.
    const response = await fetch(`${API_URL}/dashboard/resumen`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (!response.ok) {
      throw new Error('Error al cargar el resumen del dashboard');
    }
    
    const data = await response.json();

    // 1. Actualizar las tarjetas de estadísticas
    statIngresados.textContent = data.ingresados || 0;
    statProceso.textContent = data.enProceso || 0;
    statFinalizados.textContent = data.finalizados || 0;
    statArchivados.textContent = data.archivados || 0;

    // 2. Crear los gráficos
    crearGraficoDeAreas(data.documentosPorArea || []);
    crearGraficoDeEstados(data.tramitesPorEstado || []);

  } catch (error) {
    console.error(error);
    alert('No se pudo cargar la información del dashboard.');
  }
}

// --- 4. Funciones para Dibujar Gráficos (usando Chart.js) ---

/**
 * Crea un gráfico de Dona (Doughnut) para "Documentos por Área"
 * @param {Array} data - Un array de objetos, ej: [{nombre: 'DIRIN', cantidad: 10}, ...]
 */
function crearGraficoDeAreas(data) {
  // Si ya existe un gráfico, lo destruimos antes de crear uno nuevo
  if (areaChartInstance) {
    areaChartInstance.destroy();
  }
  
  // Extraemos las etiquetas (labels) y los datos (cantidades)
  const labels = data.map(item => item.nombre);
  const cantidades = data.map(item => item.cantidad);

  areaChartInstance = new Chart(ctxArea, {
    type: 'doughnut', // Tipo de gráfico
    data: {
      labels: labels,
      datasets: [{
        label: 'Documentos',
        data: cantidades,
        backgroundColor: [
          '#007bff',
          '#28a745',
          '#ffc107',
          '#dc3545',
          '#17a2b8',
          '#6c757d'
        ],
        hoverOffset: 4
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'right', // Mueve las etiquetas a la derecha
        }
      }
    }
  });
}

/**
 * Crea un gráfico de Barras para "Trámites por Estado"
 * @param {Array} data - Un array de objetos, ej: [{estado: 'Registrado', cantidad: 5}, ...]
 */
function crearGraficoDeEstados(data) {
  if (estadoChartInstance) {
    estadoChartInstance.destroy();
  }

  const labels = data.map(item => item.estado);
  const cantidades = data.map(item => item.cantidad);

  estadoChartInstance = new Chart(ctxEstado, {
    type: 'bar', // Tipo de gráfico
    data: {
      labels: labels,
      datasets: [{
        label: 'Cantidad de Trámites',
        data: cantidades,
        backgroundColor: '#28a745',
        borderColor: '#218838',
        borderWidth: 1
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
          beginAtZero: true
        }
      }
    }
  });
}


// --- 5. Event Listeners ---
document.addEventListener('DOMContentLoaded', () => {
  cargarDashboard();
  
  // Mock Data 
  // --------------------------------------------------
  const mockData = {
    ingresados: 120,
    enProceso: 45,
    finalizados: 60,
    archivados: 15,
    documentosPorArea: [
      { nombre: 'DIRIN', cantidad: 10 },
      { nombre: 'DIRCOCOR', cantidad: 25 },
      { nombre: 'DIREED', cantidad: 15 },
      { nombre: 'DIRSAPOL', cantidad: 5 }
    ],
    tramitesPorEstado: [
      { estado: 'Registrado', cantidad: 30 },
      { estado: 'En Proceso', cantidad: 45 },
      { estado: 'Observado', cantidad: 12 },
      { estado: 'Finalizado', cantidad: 60 }
    ]
  };
  statIngresados.textContent = mockData.ingresados;
  statProceso.textContent = mockData.enProceso;
  statFinalizados.textContent = mockData.finalizados;
  statArchivados.textContent = mockData.archivados;
  crearGraficoDeAreas(mockData.documentosPorArea);
  crearGraficoDeEstados(mockData.tramitesPorEstado);
  // --------------------------------------------------
  // FIN DE MOCK DATA

  btnLogout.addEventListener('click', logout);
});