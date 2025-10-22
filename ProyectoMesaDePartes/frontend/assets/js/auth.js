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
  // Redirigimos al login. El '../' es para salir de carpetas como /admin
  window.location.href = '../login.html'; 
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
    // Usamos '../' para asegurarnos de que encuentre el login.html desde /admin
    window.location.href = '../login.html'; 
    return;
  }

  // 2. Si se requiere un rol específico (ej: 'Administrador')
  if (roleRequerido) {
    // Verificamos si la lista de roles del usuario NO incluye el rol requerido
    if (!user.roles || !user.roles.includes(roleRequerido)) {
      console.error(`Acceso denegado: Se requiere el rol '${roleRequerido}'.`);
      // Si no tiene el rol, lo mandamos a una página de "no autorizado" o al login.
      window.location.href = '../login.html'; 
      return;
    }
  }

  // 3. Si todo está bien, le damos la bienvenida.
  console.log(`Acceso concedido para ${user.username}`);
}