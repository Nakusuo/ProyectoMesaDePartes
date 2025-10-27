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
const tituloField = document.getElementById('titulo');
const numeroDocumentoField = document.getElementById('numero-documento');
const numeroHtField = document.getElementById('numero-ht');
const remitenteField = document.getElementById('remitente');
const descripcionField = document.getElementById('descripcion');
const archivoUrlField = document.getElementById('archivo-url');

async function cargarTiposDocumento() {
  try {
    const response = await fetch(`${API_URL}/tipos-documento`, {
      headers: getAuthHeaders()
    });
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
    const response = await fetch(`${API_URL}/usuarios`, {
      headers: getAuthHeaders()
    });
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

async function handleSubmitRegistro(event) {
  event.preventDefault();
  const submitButton = registroForm.querySelector('.btn-submit');
  submitButton.disabled = true;
  submitButton.textContent = 'Registrando...';

  const datosDocumento = {
    titulo: tituloField.value,
    idTipoDocumento: parseInt(tipoDocumentoSelect.value),
    numeroDocumento: numeroDocumentoField.value || null,
    numeroHt: numeroHtField.value || null,
    remitente: remitenteField.value,
    descripcion: descripcionField.value || null,
    idUsuarioAsignado: parseInt(usuarioAsignadoSelect.value),
    archivoUrl: archivoUrlField.value || null
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

    alert(`Documento registrado con éxito.\nCódigo: ${docGuardado.codigo}`);
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

  cargarTiposDocumento();
  cargarUsuariosParaAsignar();

  registroForm.addEventListener('submit', handleSubmitRegistro);
});
