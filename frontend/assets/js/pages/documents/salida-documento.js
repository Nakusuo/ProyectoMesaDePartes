// =====================================================
// SALIDA DE DOCUMENTOS - MESA DE PARTES PNP
// =====================================================

// Usar API_URL del config global
const API_URL = window.API_URL;

let documentoSeleccionado = null;
let archivoCargoUrl = null;

// =====================================================
// INICIALIZACIÓN
// =====================================================

// Función para verificar autenticación
function verificarAutenticacion() {
    const token = getToken();
    const user = getUserData();
    
    if (!token || !user) {
        console.error('❌ No hay autenticación. Redirigiendo al login...');
        window.location.href = '../auth/login.html';
        return false;
    }
    
    console.log('✅ Usuario autenticado:', user.username || user.nombre);
    return true;
}

document.addEventListener('DOMContentLoaded', () => {
    console.log('🔍 DOMContentLoaded: Iniciando salida-documento.js');
    verificarAutenticacion();
    console.log('✅ Autenticación verificada');
    cargarTiposDocumento();
    console.log('✅ cargarTiposDocumento() llamada');
    cargarDestinatarios();
    console.log('✅ cargarDestinatarios() llamada');
    configurarEventos();
    console.log('✅ Eventos configurados');
    cargarSalidasRecientes();
    console.log('✅ Salidas recientes cargadas');
});

// =====================================================
// CONFIGURAR EVENTOS
// =====================================================
function configurarEventos() {
    document.getElementById('buscarBtn').addEventListener('click', buscarDocumento);
    document.getElementById('salidaForm').addEventListener('submit', registrarSalida);
    document.getElementById('limpiarBtn').addEventListener('click', limpiarFormulario);
    // verHistorialBtn removido del HTML - redirección eliminada
    
    // Archivo de cargo
    const inputArchivo = document.getElementById('archivoCargo');
    inputArchivo.addEventListener('change', function() {
        const label = this.nextElementSibling;
        const fileName = this.files[0]?.name || 'Seleccionar archivo PDF...';
        label.querySelector('.file-input-text').textContent = fileName;
        
        if (this.files[0]) {
            subirArchivoCargo(this.files[0]);
        }
    });
}

// =====================================================
// BUSCAR DOCUMENTO
// =====================================================
async function buscarDocumento() {
    const input = document.getElementById('codigoDocumento').value.trim();
    
    if (!input) {
        showToast('Por favor ingrese un código o número de documento', 'warning');
        return;
    }

    let loadingToast = null;
    
    try {
        // Guardar referencia del toast de loading para poder eliminarlo después
        loadingToast = showToast('Buscando documento...', 'loading');
        
        console.log('🔍 Entrada del usuario:', input);
        
        // Formatear automáticamente el código
        // Si es solo números, convertir a formato DOC-000000 (6 dígitos)
        let codigoBusqueda = input;
        if (/^\d+$/.test(input)) {
            // Solo números, formatear a DOC-000000
            const numero = parseInt(input);
            codigoBusqueda = `DOC-${numero.toString().padStart(6, '0')}`;
            console.log(`✨ Formato automático: "${input}" → "${codigoBusqueda}"`);
        } else if (!/^DOC-\d{6}$/.test(input)) {
            // Si no es formato DOC-000000 ni solo números, intentar extraer números
            const numeros = input.match(/\d+/);
            if (numeros) {
                const numero = parseInt(numeros[0]);
                codigoBusqueda = `DOC-${numero.toString().padStart(6, '0')}`;
                console.log(`✨ Números extraídos: "${input}" → "${codigoBusqueda}"`);
            }
        }
        
        console.log('🔎 Código final de búsqueda:', codigoBusqueda);
        
        const response = await fetch(`${API_URL}/documentos/buscar/${codigoBusqueda}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        console.log('📡 Response status:', response.status, 'OK:', response.ok);
        
        if (!response.ok) {
            throw new Error('Documento no encontrado. Status: ' + response.status);
        }

        const data = await response.json();
        console.log('📦 Datos recibidos:', data);
        
        // El endpoint devuelve el documento directamente, no envuelto
        documentoSeleccionado = data.documento || data;
        
        console.log('✅ Documento seleccionado:', documentoSeleccionado);
        
        // Mostrar información del documento
        document.getElementById('infoTitulo').textContent = documentoSeleccionado.titulo;
        document.getElementById('infoRemitente').textContent = documentoSeleccionado.remitente;
        document.getElementById('infoTipo').textContent = documentoSeleccionado.tipoDocumento?.nombre || 'N/A';
        document.getElementById('infoEstado').textContent = documentoSeleccionado.estado.replace('_', ' ');
        
        document.getElementById('documentoInfo').style.display = 'block';
        
        // Obtener HT si existe
        await cargarHojaTramite(documentoSeleccionado.idDocumento);
        
        // Eliminar el toast de loading antes de mostrar el de éxito
        if (loadingToast) loadingToast.remove();
        showToast('Documento encontrado exitosamente', 'success');
        
    } catch (error) {
        console.error('❌ Error al buscar documento:', error);
        // Eliminar el toast de loading antes de mostrar el de error
        if (loadingToast) loadingToast.remove();
        showToast('Error: ' + error.message, 'error');
        document.getElementById('documentoInfo').style.display = 'none';
        documentoSeleccionado = null;
    }
}

// =====================================================
// CARGAR HOJA DE TRÁMITE
// =====================================================
async function cargarHojaTramite(idDocumento) {
    try {
        const response = await fetch(`${API_URL}/hojas-tramite/documento/${idDocumento}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const hojasTramite = await response.json();
            if (hojasTramite && hojasTramite.length > 0) {
                document.getElementById('numeroHT').value = hojasTramite[0].numeroHt;
            }
        } else if (response.status === 404) {
            // No tiene hoja de trámite, dejar vacío
            console.log('ℹ️ Documento sin hoja de trámite');
        }
    } catch (error) {
        console.error('Error al cargar HT:', error);
        // No mostrar error al usuario, solo dejar el campo vacío
    }
}

// =====================================================
// SUBIR ARCHIVO DE CARGO
// =====================================================
async function subirArchivoCargo(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const statusDiv = document.getElementById('archivoCargoStatus');
    statusDiv.innerHTML = '<span style="color: #fbbf24;">⏳ Subiendo archivo...</span>';

    try {
        const response = await fetch(`${API_URL}/salidas/upload-cargo`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            },
            body: formData
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Error al subir archivo');
        }

        archivoCargoUrl = data.url;
        statusDiv.innerHTML = `<span style="color: #10b981;">✅ Archivo subido: ${file.name}</span>`;
        showToast('Archivo de cargo subido exitosamente', 'success');

    } catch (error) {
        statusDiv.innerHTML = `<span style="color: #ef4444;">❌ Error: ${error.message}</span>`;
        showToast('Error al subir archivo: ' + error.message, 'error');
        archivoCargoUrl = null;
    }
}

// =====================================================
// REGISTRAR SALIDA
// =====================================================
async function registrarSalida(e) {
    e.preventDefault();

    if (!documentoSeleccionado) {
        showToast('Debe buscar y seleccionar un documento primero', 'warning');
        return;
    }

    const usuario = getUserData();
    
    const salidaData = {
        idDocumento: documentoSeleccionado.idDocumento,
        idTipoDocumento: document.getElementById('tipoDocumentoSalida').value || null,
        numeroDocumentoSalida: document.getElementById('numeroDocumentoSalida').value,
        destinatarioSalida: document.getElementById('destinatarioSalida').value,
        observacion: document.getElementById('observacionSalida').value,
        archivoCargoUrl: archivoCargoUrl,
        idUsuarioSalida: usuario.idUsuario
    };

    try {
        showToast('Registrando salida...', 'loading');
        
        const response = await fetch(`${API_URL}/salidas/registrar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            },
            body: JSON.stringify(salidaData)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Error al registrar salida');
        }

        showToast('✅ Salida de documento registrada exitosamente', 'success');
        
        // Limpiar formulario y recargar tabla
        setTimeout(() => {
            limpiarFormulario();
            cargarSalidasRecientes();
        }, 1500);

    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

// =====================================================
// LIMPIAR FORMULARIO
// =====================================================
function limpiarFormulario() {
    document.getElementById('salidaForm').reset();
    document.getElementById('documentoInfo').style.display = 'none';
    document.getElementById('archivoCargoStatus').innerHTML = '';
    document.querySelector('.file-input-text').textContent = 'Seleccionar archivo PDF...';
    documentoSeleccionado = null;
    archivoCargoUrl = null;
}

// =====================================================
// CARGAR TIPOS DE DOCUMENTO
// =====================================================
async function cargarTiposDocumento() {
    try {
        const token = localStorage.getItem('token');
        console.log('📡 Llamando a: ' + `${API_URL}/tipos-documento`);
        const response = await fetch(`${API_URL}/tipos-documento`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        console.log('📡 Response status:', response.status, 'OK:', response.ok);
        
        if (!response.ok) {
            throw new Error('Error al cargar tipos de documento. Status: ' + response.status);
        }

        const tipos = await response.json();
        console.log('✅ Tipos recibidos:', tipos.length, tipos);
        const select = document.getElementById('tipoDocumentoSalida');
        
        if (!select) {
            console.error('❌ Elemento tipoDocumentoSalida no encontrado en el DOM');
            return;
        }
        
        select.innerHTML = '<option value="">Seleccione...</option>';
        tipos.forEach(tipo => {
            const option = document.createElement('option');
            option.value = tipo.idTipoDocumento;
            option.textContent = tipo.nombre;
            select.appendChild(option);
        });
        console.log('✅ Dropdown de tipos poblado con ' + tipos.length + ' opciones');

    } catch (error) {
        console.error('❌ Error al cargar tipos de documento:', error);
        const select = document.getElementById('tipoDocumentoSalida');
        if (select) {
            select.innerHTML = '<option value="">Error al cargar tipos</option>';
        }
    }
}

// =====================================================
// CARGAR DESTINATARIOS (Departamentos PNP)
// =====================================================
async function cargarDestinatarios() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/areas`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error('Error al cargar destinatarios');
        }

        const areas = await response.json();
        // Filtrar solo departamentos PNP (mismo criterio que en entrada de documentos)
        const departamentosPNP = areas.filter(area => area.tipo === 'DEPARTAMENTO_PNP');
        
        const select = document.getElementById('destinatarioSalida');
        select.innerHTML = '<option value="">Seleccione un destinatario...</option>';
        
        departamentosPNP.forEach(area => {
            const option = document.createElement('option');
            const textoCompleto = area.sigla ? `${area.sigla} - ${area.nombre}` : area.nombre;
            option.value = textoCompleto;
            option.textContent = textoCompleto;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error al cargar destinatarios:', error);
        const select = document.getElementById('destinatarioSalida');
        select.innerHTML = '<option value="">Error al cargar destinatarios</option>';
    }
}

// =====================================================
// CARGAR SALIDAS RECIENTES
// =====================================================
async function cargarSalidasRecientes() {
    try {
        const response = await fetch(`${API_URL}/salidas`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const salidas = await response.json();
        
        // Validar que sea un array
        if (Array.isArray(salidas)) {
            mostrarTablaSalidas(salidas);
        } else {
            console.warn('⚠️ La respuesta no es un array:', salidas);
            document.getElementById('tablaSalidas').innerHTML = 
                '<p style="text-align: center; color: #999;">No hay salidas registradas</p>';
        }

    } catch (error) {
        console.error('Error al cargar salidas:', error);
        document.getElementById('tablaSalidas').innerHTML = 
            '<p style="text-align: center; color: #999;">No hay salidas registradas aún</p>';
    }
}

// =====================================================
// MOSTRAR TABLA DE SALIDAS
// =====================================================
function mostrarTablaSalidas(salidas) {
    const container = document.getElementById('tablaSalidas');
    
    if (!salidas || salidas.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999;">No hay salidas registradas</p>';
        return;
    }

    // Ordenar por fecha más reciente
    salidas.sort((a, b) => new Date(b.fechaSalida) - new Date(a.fechaSalida));
    
    // Mostrar solo las últimas 10
    const salidasRecientes = salidas.slice(0, 10);

    let html = `
        <table class="documentos-table">
            <thead>
                <tr>
                    <th>Código Doc.</th>
                    <th>Destinatario</th>
                    <th>Nro. Doc. Salida</th>
                    <th>Fecha Salida</th>
                    <th>Usuario</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
    `;

    salidasRecientes.forEach(salida => {
        const fecha = new Date(salida.fechaSalida).toLocaleDateString('es-PE', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });

        const usuario = salida.usuarioSalida ? 
            `${salida.usuarioSalida.nombre} ${salida.usuarioSalida.apellido}` : 
            'N/A';

        html += `
            <tr>
                <td><strong>${salida.documento.codigo}</strong></td>
                <td>${salida.destinatarioSalida}</td>
                <td>${salida.numeroDocumentoSalida || 'N/A'}</td>
                <td>${fecha}</td>
                <td>${usuario}</td>
                <td>
                    ${salida.archivoCargoUrl ? 
                        `<button class="btn-small" onclick="verCargo('${salida.archivoCargoUrl}')">
                            📄 Ver Cargo
                        </button>` : 
                        '<span style="color: #999;">Sin cargo</span>'}
                </td>
            </tr>
        `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;
}

// =====================================================
// VER CARGO
// =====================================================
function verCargo(url) {
    window.open(`${API_URL}/documentos/ver-pdf?url=${encodeURIComponent(url)}`, '_blank');
}
