document.addEventListener("DOMContentLoaded", () => {
            
    const loginForm = document.getElementById("loginForm");
    const errorMessage = document.getElementById("errorMessage");

    // Escuchamos el evento "submit" del formulario
    loginForm.addEventListener("submit", async (e) => {
        
        // Prevenimos que la página se recargue
        e.preventDefault(); 
        
        // Ocultamos errores anteriores
        errorMessage.style.display = 'none';
        errorMessage.textContent = '';

        // Obtenemos los valores del formulario
        const username = e.target.username.value;
        const password = e.target.password.value;

        // Esta es la URL de tu backend (Asegúrate que el backend esté corriendo)
        const apiUrl = "http://localhost:8080/api/auth/login";

        try {
            // Hacemos la llamada (fetch) al backend
            const response = await fetch(apiUrl, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            // Convertimos la respuesta a JSON
            const data = await response.json();

            // Si la respuesta NO fue exitosa (ej: 401 No Autorizado)
            if (!response.ok) {
                // Lanzamos un error con el mensaje del backend
                throw new Error(data.message || 'Usuario o contraseña incorrectos');
            }

            // ¡ÉXITO! Guardamos los datos en el navegador
            localStorage.setItem("userToken", data.token);
            localStorage.setItem("username", data.username);
            localStorage.setItem("userRoles", data.roles.join(',')); // Guardamos roles como "Admin,User"

            // Redirigimos al usuario según su rol
            // (Tus roles son: 'Administrador', 'Mesa de Partes', 'Trabajador', 'Jefatura')
            if (data.roles.includes("Administrador")) {
                window.location.href = "admin/dashboard.html";
            } else if (data.roles.includes("Mesa de Partes")) {
                window.location.href = "mesadepartes/bandeja-entrada.html";
            } else if (data.roles.includes("Trabajador")) {
                window.location.href = "trabajador/mis-asignaciones.html";
            } else if (data.roles.includes("Jefatura")) {
                window.location.href = "jefatura/dashboard-area.html";
            } else {
                // Fallback por si no tiene un rol conocido
                throw new Error("Rol de usuario no reconocido.");
            }

        } catch (error) {
            // Si algo falló (red, error 401, etc.), mostramos el error
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        }
    });
});
