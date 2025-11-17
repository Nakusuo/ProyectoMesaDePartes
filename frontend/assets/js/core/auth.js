// Este archivo se encargará de revisar la seguridad en TODAS las páginas
// que sean "privadas" (es decir, todas menos el login).

// Función para obtener el token guardado en el navegador
function getToken() {
  return localStorage.getItem('token');
}

// Función para obtener los datos del usuario (guardados en el login)
function getUserData() {
  const data = localStorage.getItem('user');
  if (data) {
    return JSON.parse(data);
  }
  return null;
}

// Función para cerrar sesión
function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  
  // Usar ruta absoluta desde la raíz del servidor
  window.location.replace('/pages/auth/login.html');
}

/**
 * Esta es la función MÁS IMPORTANTE.
 * Revisa si el usuario está logueado y si tiene el rol correcto.
 * @param {string} roleRequerido - El rol que se necesita para ver la página (ej: 'Administrador')
 */
function checkAuth(roleRequerido) {
  const token = getToken();
  const user = getUserData();

  // 1. Si no hay token o no hay datos de usuario, lo mandamos al login.
  if (!token || !user) {
    console.error('Acceso denegado: No hay token o datos de usuario.');
    redirectToLogin();
    return;
  }

  // 2. Si se requiere un rol específico y el usuario no lo tiene
  if (roleRequerido && (!user.roles || !user.roles.includes(roleRequerido))) {
    console.error(`Acceso denegado: Se requiere el rol '${roleRequerido}'.`);
    redirectToLogin();
    return;
  }

  // 3. Si todo está bien, le damos la bienvenida.
  console.log(`Acceso concedido para ${user.username}`);
}

/**
 * Redirige al login desde cualquier ubicación
 */
function redirectToLogin() {
  // Usar ruta absoluta desde la raíz del servidor
  window.location.replace('/pages/auth/login.html');
}