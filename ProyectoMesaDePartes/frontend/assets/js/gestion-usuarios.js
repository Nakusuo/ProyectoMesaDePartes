// --- 1. Verificación de Seguridad ---
// Llamamos a checkAuth() del archivo auth.js
// Solo los 'Administrador' pueden ver esta página.
checkAuth('Administrador'); 


// --- 2. Variables Globales y Constantes ---
const API_URL = 'http://localhost:8080/api'; // URL base de tu backend
const token = getToken(); // Obtenemos el token de auth.js

// Elementos del DOM
const tableBody = document.getElementById('user-table-body');
const modal = document.getElementById('user-modal');
const modalTitle = document.getElementById('modal-title');
const userForm = document.getElementById('user-form');
const btnNuevoUsuario = document.getElementById('btn-nuevo-usuario');
const btnCancelar = document.getElementById('btn-cancelar');
const btnLogout = document.getElementById('btn-logout');

// Elementos del Formulario
const userIdField = document.getElementById('user-id');
const nombreField = document.getElementById('nombre');
const apellidoField = document.getElementById('apellido');
const usernameField = document.getElementById('username');
const emailField = document.getElementById('email');
const passwordField = document.getElementById('password');
const areaSelect = document.getElementById('area-select');
const rolesSelect = document.getElementById('roles-select');
const activoField = document.getElementById('activo');


// --- 3. Funciones de Carga de Datos (Fetch) ---

// Carga todos los usuarios y los pinta en la tabla
async function cargarUsuarios() {
  try {
    const response = await fetch(`${API_URL}/usuarios`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar usuarios');
    
    const usuarios = await response.json();
    pintarUsuariosEnTabla(usuarios);

  } catch (error) {
    console.error(error);
    tableBody.innerHTML = `<tr><td colspan="7">Error al cargar usuarios.</td></tr>`;
  }
}

// Carga las Áreas en el <select> del modal
async function cargarAreas() {
  try {
    const response = await fetch(`${API_URL}/areas`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar áreas');
    
    const areas = await response.json();
    areaSelect.innerHTML = '<option value="">Seleccione un área</option>'; // Limpiar
    areas.forEach(area => {
      areaSelect.innerHTML += `<option value="${area.idArea}">${area.nombre} (${area.sigla})</option>`;
    });

  } catch (error) {
    console.error(error);
    areaSelect.innerHTML = '<option value="">Error al cargar áreas</option>';
  }
}

// Carga los Roles en el <select> del modal
async function cargarRoles() {
  try {
    // ¡OJO! Este endpoint /api/roles aún no lo hemos creado en el backend.
    // Dará error hasta que lo creemos.
    const response = await fetch(`${API_URL}/roles`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar roles');
    
    const roles = await response.json();
    rolesSelect.innerHTML = '<option value="">Seleccione un rol</option>'; // Limpiar
    roles.forEach(rol => {
      rolesSelect.innerHTML += `<option value="${rol.idRol}">${rol.nombre}</option>`;
    });

  } catch (error) {
    console.error(error);
    rolesSelect.innerHTML = '<option value="">Error al cargar roles</option>';
  }
}

// --- 4. Funciones de Manipulación del DOM ---

// Pinta la fila de un usuario en la tabla
function pintarUsuariosEnTabla(usuarios) {
  tableBody.innerHTML = ''; // Limpiar la tabla
  
  if (usuarios.length === 0) {
    tableBody.innerHTML = '<tr><td colspan="7">No se encontraron usuarios.</td></tr>';
    return;
  }

  usuarios.forEach(user => {
    // Convertimos la lista de roles en "badges"
    const rolesHtml = user.roles && user.roles.length > 0
      ? user.roles.map(rol => `<span class="badge badge-role">${rol.nombre}</span>`).join(' ')
      : 'Sin roles';

    // Creamos la fila (tr)
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${user.nombre} ${user.apellido}</td>
      <td>${user.username}</td>
      <td>${user.email}</td>
      <td>${user.area ? user.area.sigla : 'N/A'}</td>
      <td>${rolesHtml}</td>
      <td>
        <span class="badge ${user.activo ? 'badge-success' : 'badge-danger'}">
          ${user.activo ? 'Activo' : 'Inactivo'}
        </span>
      </td>
      <td>
        <button class="btn btn-sm btn-secondary btn-editar" data-id="${user.idUsuario}">Editar</button>
      </td>
    `;
    tableBody.appendChild(tr);
  });
}

// Abre el modal (ya sea para "Nuevo" o "Editar")
function abrirModal(modo, usuario = null) {
  userForm.reset(); // Limpia el formulario
  
  if (modo === 'editar' && usuario) {
    modalTitle.textContent = 'Editar Usuario';
    userIdField.value = usuario.idUsuario;
    nombreField.value = usuario.nombre;
    apellidoField.value = usuario.apellido;
    usernameField.value = usuario.username;
    emailField.value = usuario.email;
    activoField.checked = usuario.activo;
    
    // Seleccionar el área correcta
    areaSelect.value = usuario.area ? usuario.area.idArea : '';
    
    // Seleccionar el rol correcto (asumimos 1 rol por ahora)
    rolesSelect.value = (usuario.roles && usuario.roles.length > 0) ? usuario.roles[0].idRol : '';

    passwordField.placeholder = "Dejar en blanco para no cambiar";

  } else {
    modalTitle.textContent = 'Nuevo Usuario';
    userIdField.value = ''; // ID vacío para "Nuevo"
    passwordField.placeholder = "Ingrese contraseña";
  }
  
  modal.style.display = 'flex';
}

// Cierra el modal
function cerrarModal() {
  modal.style.display = 'none';
}


// --- 5. Lógica de Guardado (Formulario) ---

async function guardarUsuario(event) {
  event.preventDefault(); // Evita que la página se recargue

  const id = userIdField.value;
  const esNuevo = id === '';

  // 1. Obtenemos los datos del formulario
  // (¡OJO! El backend espera los OBJETOS completos de Area y Rol)
  const datosUsuario = {
    nombre: nombreField.value,
    apellido: apellidoField.value,
    username: usernameField.value,
    email: emailField.value,
    passwordHash: passwordField.value, // El backend se encargará de encriptarlo
    activo: activoField.checked,
    tipoContrato: 'CAS', // Valor por defecto, puedes añadir un select si quieres
    
    // Obtenemos los IDs de los selects
    area: { idArea: parseInt(areaSelect.value) },
    roles: [ { idRol: parseInt(rolesSelect.value) } ] // Enviamos una lista de roles
  };

  // 2. Si es una edición y no se puso contraseña, la quitamos
  if (!esNuevo && datosUsuario.passwordHash === '') {
    delete datosUsuario.passwordHash;
  }

  // 3. Definimos el método (POST o PUT) y la URL
  const method = esNuevo ? 'POST' : 'PUT';
  const url = esNuevo ? `${API_URL}/usuarios` : `${API_URL}/usuarios/${id}`;

  try {
    // ¡OJO! Estos endpoints (POST y PUT /api/usuarios) no los hemos creado en el backend.
    // Dará error 404 o 405 hasta que los creemos.
    const response = await fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(datosUsuario)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Error al guardar usuario');
    }

    // 4. Si todo salió bien
    cerrarModal();
    cargarUsuarios(); // Recargamos la tabla
    alert('Usuario guardado con éxito');

  } catch (error) {
    console.error('Error guardando:', error);
    alert('Error al guardar: ' + error.message);
  }
}


// --- 6. Event Listeners (Poner todo en marcha) ---

// Se ejecuta cuando el DOM está listo
document.addEventListener('DOMContentLoaded', () => {
  // Carga inicial
  cargarUsuarios();
  cargarAreas();
  cargarRoles();

  // Listeners de los botones
  btnLogout.addEventListener('click', logout);
  btnNuevoUsuario.addEventListener('click', () => abrirModal('nuevo'));
  btnCancelar.addEventListener('click', cerrarModal);
  
  // Listener del formulario
  userForm.addEventListener('submit', guardarUsuario);

  // Listener para los botones "Editar" (delegación de eventos)
  tableBody.addEventListener('click', async (event) => {
    if (event.target.classList.contains('btn-editar')) {
      const id = event.target.getAttribute('data-id');
      
      // Necesitamos cargar el usuario individual para tener todos sus datos
      try {
        // ¡OJO! Este endpoint (GET /api/usuarios/{id}) tampoco lo hemos creado.
        const response = await fetch(`${API_URL}/usuarios/${id}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Error al cargar datos del usuario');
        
        const usuario = await response.json();
        abrirModal('editar', usuario);

      } catch (error) {
        console.error(error);
        alert('No se pudieron cargar los datos para editar.');
      }
    }
  });
});