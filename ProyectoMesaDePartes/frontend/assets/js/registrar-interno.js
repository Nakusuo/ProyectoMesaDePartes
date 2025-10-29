// Verificar autenticación
function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Función de logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

// Obtener token para las peticiones
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

const API_URL = 'http://localhost:8080/api';

const registroForm = document.getElementById('registro-form');

const tipoDocumentoSelect = document.getElementById('tipo-documento-select');
const usuarioAsignadoSelect = document.getElementById('usuario-asignado-select');
const remitenteSelect = document.getElementById('remitente-select');
const tituloField = document.getElementById('titulo');
const numeroDocumentoField = document.getElementById('numero-documento');
const numeroHtField = document.getElementById('numero-ht');
const descripcionField = document.getElementById('descripcion');
const archivoPdfField = document.getElementById('archivo-pdf');

async function cargarTiposDocumento() {
  try {
    const response = await fetch(`${API_URL}/tipos-documento`);
    if (!response.ok) throw new Error('Error al cargar tipos de documento');

    const tipos = await response.json();
    tipoDocumentoSelect.innerHTML = '<option value="">Seleccione un tipo</option>';
    tipos.forEach(tipo => {
      tipoDocumentoSelect.innerHTML += `<option value="${tipo.idTipoDocumento}">${tipo.nombre}</option>`;
    });

  } catch (error) {
    console.error(error);
    tipoDocumentoSelect.innerHTML = '<option value="">Error al cargar</option>';
  }
}

async function cargarUsuariosParaAsignar() {
  try {
    const response = await fetch(`${API_URL}/usuarios`);
    if (!response.ok) throw new Error('Error al cargar usuarios');

    const usuarios = await response.json();
    usuarioAsignadoSelect.innerHTML = '<option value="">Seleccione un usuario</option>';

    usuarios.forEach(user => {
        const esRolValido = user.roles && user.roles.some(rol => rol.nombre === 'Trabajador' || rol.nombre === 'Jefatura');
        if (user.activo && esRolValido) {
         usuarioAsignadoSelect.innerHTML += `<option value="${user.idUsuario}">${user.nombre} ${user.apellido}</option>`;
        }
    });

  } catch (error) {
    console.error(error);
    usuarioAsignadoSelect.innerHTML = '<option value="">Error al cargar</option>';
  }
}

async function cargarAreas() {
  console.log('=== Iniciando carga de áreas ===');
  console.log('URL:', `${API_URL}/areas`);
  
  try {
    const response = await fetch(`${API_URL}/areas`);
    console.log('Response status:', response.status);
    console.log('Response OK:', response.ok);
    
    if (!response.ok) throw new Error('Error al cargar áreas');

    const areas = await response.json();
    console.log('Áreas recibidas:', areas.length, areas);
    
    remitenteSelect.innerHTML = '<option value="">Seleccione un área</option>';
    
    areas.forEach(area => {
      const textoCompleto = area.sigla ? `${area.sigla} - ${area.nombre}` : area.nombre;
      remitenteSelect.innerHTML += `<option value="${textoCompleto}">${textoCompleto}</option>`;
    });
    
    console.log('✅ Áreas cargadas correctamente');

  } catch (error) {
    console.error('❌ ERROR al cargar áreas:', error);
    remitenteSelect.innerHTML = '<option value="">Error al cargar</option>';
  }
}

async function handleSubmitRegistro(event) {
  event.preventDefault();
  const submitButton = registroForm.querySelector('.btn-submit');
  submitButton.disabled = true;
  submitButton.textContent = 'Registrando...';

  let archivoUrl = null;

  if (archivoPdfField.files.length > 0) {
    const archivo = archivoPdfField.files[0];
    
    if (archivo.type !== 'application/pdf') {
      alert('Solo se permiten archivos PDF.');
      submitButton.disabled = false;
      submitButton.textContent = 'Registrar Documento';
      return;
    }

    if (archivo.size > 10 * 1024 * 1024) {
      alert('El archivo no debe superar los 10 MB.');
      submitButton.disabled = false;
      submitButton.textContent = 'Registrar Documento';
      return;
    }

    submitButton.textContent = 'Subiendo archivo...';
    
    try {
      const formData = new FormData();
      formData.append('file', archivo);

      const uploadResponse = await fetch(`${API_URL}/documentos/upload`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
      });

      if (!uploadResponse.ok) {
        throw new Error('Error al subir el archivo');
      }

      const uploadResult = await uploadResponse.json();
      archivoUrl = uploadResult.url;
      
    } catch (error) {
      console.error('Error al subir archivo:', error);
      alert('Error al subir el archivo: ' + error.message);
      submitButton.disabled = false;
      submitButton.textContent = 'Registrar Documento';
      return;
    }
  }

  submitButton.textContent = 'Guardando documento...';

  const datosDocumento = {
    titulo: tituloField.value,
    idTipoDocumento: parseInt(tipoDocumentoSelect.value),
    numeroDocumento: numeroDocumentoField.value || null,
    numeroHt: numeroHtField.value || null,
    remitente: remitenteSelect.value,
    descripcion: descripcionField.value || null,
    idUsuarioAsignado: parseInt(usuarioAsignadoSelect.value),
    archivoUrl: archivoUrl
  };

  if (!datosDocumento.titulo || !datosDocumento.remitente || !datosDocumento.idTipoDocumento || !datosDocumento.idUsuarioAsignado) {
    alert('Por favor, complete todos los campos obligatorios (*).');
    submitButton.disabled = false;
    submitButton.textContent = 'Registrar Documento';
    return;
  }

  try {
    const response = await fetch(`${API_URL}/documentos/registrar`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(datosDocumento)
    });

    if (!response.ok) {
      let errorMsg = 'Error al registrar el documento.';
      try {
          const errorData = await response.json();
          errorMsg = errorData.message || errorMsg;
      } catch(e) { /* Ignorar si no hay cuerpo JSON */ }
      throw new Error(errorMsg);
    }

    const docGuardado = await response.json();

    alert(`Documento registrado con éxito.\nNúmero de Registro: ${docGuardado.numeroRegistro}`);
    registroForm.reset();

  } catch (error) {
    console.error('Error en el registro:', error);
    alert('Error al registrar: ' + error.message);
  } finally {
      submitButton.disabled = false;
      submitButton.textContent = 'Registrar Documento';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  // Verificar autenticación al cargar
  if (!checkAuth()) return;

  // Mostrar información del usuario en el header
  const userInfo = JSON.parse(localStorage.getItem('user'));
  const userInfoElement = document.getElementById('user-info');
  if (userInfo && userInfoElement) {
    userInfoElement.textContent = `👤 ${userInfo.username}`;
  }

  console.log('🔍 Verificando elementos del DOM:');
  console.log('tipoDocumentoSelect:', tipoDocumentoSelect);
  console.log('usuarioAsignadoSelect:', usuarioAsignadoSelect);
  console.log('remitenteSelect:', remitenteSelect);

  cargarTiposDocumento();
  cargarUsuariosParaAsignar();
  cargarAreas();

  registroForm.addEventListener('submit', handleSubmitRegistro);
});
