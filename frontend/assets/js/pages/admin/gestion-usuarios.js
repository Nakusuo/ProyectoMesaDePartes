// --- 1. Variables Globales y Constantes ---
const API_URL = 'http://localhost:8080/api'; // URL base de tu backend
const token = getToken(); // Obtenemos el token de auth.js

// Elementos del DOM
const tableBody = document.getElementById('user-table-body');
const modal = document.getElementById('user-modal');
const modalTitle = document.getElementById('modal-title');
const userForm = document.getElementById('user-form');
const btnNuevoUsuario = document.getElementById('btn-nuevo-usuario');
const btnCancelar = document.getElementById('btn-cancelar');

// Elementos del Formulario
const userIdField = document.getElementById('user-id');
const nombreField = document.getElementById('nombre');
const apellidoField = document.getElementById('apellido');
const usernameField = document.getElementById('username');
const emailField = document.getElementById('email');
const telefonoField = document.getElementById('telefono');
const passwordField = document.getElementById('password');
const tipoContratoSelect = document.getElementById('tipo-contrato');
const areaSelect = document.getElementById('area-select');
const rolesSelect = document.getElementById('roles-select');
const activoField = document.getElementById('activo');
const passwordHint = document.getElementById('password-hint');


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

// Carga las Áreas en el <select> del modal (solo áreas de trabajo)
async function cargarAreas() {
  try {
    const response = await fetch(`${API_URL}/areas`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar áreas');
    
    const areas = await response.json();
    // Filtrar solo áreas de trabajo para asignación de usuarios
    const areasTrabajo = areas.filter(area => area.tipo === 'AREA_TRABAJO');
    
    areaSelect.innerHTML = '<option value="">Seleccione un área</option>'; // Limpiar
    areasTrabajo.forEach(area => {
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
    const response = await fetch(`${API_URL}/roles`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar roles');
    
    const roles = await response.json();
    rolesSelect.innerHTML = '<option value="">Seleccione un rol</option>';
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
    // Extraer el área física/departamento (sigla)
    const areaFisica = user.area ? user.area.sigla : 'N/A';
    
    // Convertimos la lista de roles en texto simple (para la columna Área mostraremos el rol principal)
    const rolPrincipal = user.roles && user.roles.length > 0 
      ? user.roles[0].nombre.toUpperCase()
      : 'SIN ROL';
    
    // Para la columna de roles, mostramos todos los roles como badges
    const rolesHtml = user.roles && user.roles.length > 0
      ? user.roles.map(rol => `<span class="badge badge-role">${rol.nombre}</span>`).join(' ')
      : 'Sin roles';

    // Creamos la fila (tr)
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${user.nombre} ${user.apellido}</td>
      <td>${user.username}</td>
      <td>${user.email}</td>
      <td>${areaFisica}</td>
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
    emailField.value = usuario.email || '';
    telefonoField.value = usuario.telefono || '';
    tipoContratoSelect.value = usuario.tipoContrato || 'CAS';
    activoField.checked = usuario.activo;
    
    // Seleccionar el área correcta
    if (usuario.area) {
      areaSelect.value = usuario.area.idArea;
    }
    
    // Seleccionar el rol correcto (asumimos 1 rol por ahora)
    if (usuario.roles && usuario.roles.length > 0) {
      rolesSelect.value = usuario.roles[0].idRol;
    }

    passwordField.placeholder = "Dejar en blanco para no cambiar";
    passwordField.required = false;
    passwordHint.style.display = 'block';

  } else {
    modalTitle.textContent = 'Nuevo Usuario';
    userIdField.value = '';
    passwordField.placeholder = "Ingrese contraseña";
    passwordField.required = true;
    passwordHint.style.display = 'none';
    activoField.checked = true;
  }
  
  modal.style.display = 'flex';
}

// Cierra el modal
function cerrarModal() {
  modal.style.display = 'none';
}


// --- 5. Lógica de Guardado (Formulario) ---

async function guardarUsuario(event) {
  event.preventDefault();

  const id = userIdField.value;
  const esNuevo = id === '';

  // Validar que se haya seleccionado un área y un rol
  if (!areaSelect.value || !rolesSelect.value) {
    if (typeof mostrarNotificacionToast === 'function') {
      mostrarNotificacionToast('⚠️ Datos Incompletos', 'Por favor seleccione un área y un rol', 'warning');
    } else {
      alert('Por favor seleccione un área y un rol');
    }
    return;
  }

  // Validar contraseña para usuario nuevo
  if (esNuevo && !passwordField.value) {
    if (typeof mostrarNotificacionToast === 'function') {
      mostrarNotificacionToast('⚠️ Contraseña Requerida', 'La contraseña es obligatoria para usuarios nuevos', 'warning');
    } else {
      alert('La contraseña es obligatoria para usuarios nuevos');
    }
    return;
  }

  // Validar longitud de contraseña
  if (passwordField.value && passwordField.value.length < 6) {
    if (typeof mostrarNotificacionToast === 'function') {
      mostrarNotificacionToast('⚠️ Contraseña Débil', 'La contraseña debe tener al menos 6 caracteres', 'warning');
    } else {
      alert('La contraseña debe tener al menos 6 caracteres');
    }
    return;
  }

  // Construir objeto de datos
  const datosUsuario = {
    nombre: nombreField.value.trim(),
    apellido: apellidoField.value.trim(),
    username: usernameField.value.trim(),
    email: emailField.value.trim(),
    telefono: telefonoField.value.trim() || null,
    tipoContrato: tipoContratoSelect.value,
    activo: activoField.checked,
    area: { idArea: parseInt(areaSelect.value) },
    roles: [{ idRol: parseInt(rolesSelect.value) }]
  };

  // Solo incluir contraseña si se ingresó
  if (passwordField.value) {
    datosUsuario.passwordHash = passwordField.value;
  }

  const method = esNuevo ? 'POST' : 'PUT';
  const url = esNuevo ? `${API_URL}/usuarios` : `${API_URL}/usuarios/${id}`;

  try {
    const response = await fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(datosUsuario)
    });

    if (!response.ok) {
      const errorText = await response.text();
      let errorMessage = 'Error al guardar usuario';
      try {
        const errorData = JSON.parse(errorText);
        errorMessage = errorData.message || errorMessage;
      } catch (e) {
        errorMessage = errorText || errorMessage;
      }
      throw new Error(errorMessage);
    }

    cerrarModal();
    cargarUsuarios();
    
    if (typeof mostrarNotificacionToast === 'function') {
      mostrarNotificacionToast(
        '✅ Usuario Guardado',
        esNuevo ? 'Usuario creado exitosamente' : 'Usuario actualizado exitosamente',
        'success'
      );
    } else {
      alert(esNuevo ? 'Usuario creado exitosamente' : 'Usuario actualizado exitosamente');
    }

  } catch (error) {
    console.error('Error guardando:', error);
    
    // Determinar tipo de error y mensaje apropiado
    let titulo = '❌ Error al Guardar';
    let mensaje = error.message;
    
    if (error.message.includes('nombre de usuario ya existe')) {
      titulo = '⚠️ Usuario Duplicado';
      mensaje = 'El nombre de usuario ya está registrado. Por favor elija otro.';
    } else if (error.message.includes('email ya está registrado')) {
      titulo = '⚠️ Email Duplicado';
      mensaje = 'El email ya está en uso. Por favor utilice otro correo.';
    } else if (error.message.includes('Duplicate entry') && error.message.includes('telefono')) {
      titulo = '⚠️ Teléfono Duplicado';
      mensaje = 'El número de teléfono ya está registrado. Por favor utilice otro número.';
    } else if (error.message.includes('contraseña')) {
      titulo = '⚠️ Error en Contraseña';
    }
    
    if (typeof mostrarNotificacionToast === 'function') {
      mostrarNotificacionToast(titulo, mensaje, 'error');
    } else {
      alert('Error al guardar: ' + mensaje);
    }
  }
}


// --- 6. Event Listeners (Poner todo en marcha) ---

// Se ejecuta cuando el DOM está listo
document.addEventListener('DOMContentLoaded', () => {
  // Verificación de seguridad - Solo Administradores
  const userInfoStr = localStorage.getItem('userInfo');
  if (!userInfoStr) {
    alert('Sesión no válida. Por favor inicie sesión.');
    window.location.href = 'login.html';
    return;
  }

  const userInfo = JSON.parse(userInfoStr);
  if (!userInfo.roles || !userInfo.roles.includes('Administrador')) {
    alert('Acceso denegado. Solo administradores pueden acceder a esta página.');
    window.location.href = 'dashboard.html';
    return;
  }

  // Verificar que los elementos existen
    if (!btnNuevoUsuario || !btnCancelar || !modal || !userForm || !tableBody) {
    console.error('Error: No se encontraron todos los elementos necesarios del DOM');
    return;
  }  // Carga inicial
  cargarUsuarios();
  cargarAreas();
  cargarRoles();

  // Listeners de los botones
  btnNuevoUsuario.addEventListener('click', () => abrirModal('nuevo'));
  btnCancelar.addEventListener('click', cerrarModal);
  
  // Cerrar modal al hacer click fuera
  modal.addEventListener('click', (event) => {
    if (event.target === modal) {
      cerrarModal();
    }
  });
  
  // Listener del formulario
  userForm.addEventListener('submit', guardarUsuario);

  // Listener para los botones "Editar" (delegación de eventos)
  tableBody.addEventListener('click', async (event) => {
    if (event.target.classList.contains('btn-editar')) {
      const id = event.target.getAttribute('data-id');
      
      try {
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