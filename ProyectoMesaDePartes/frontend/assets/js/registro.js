const API_REGISTRO_URL = 'http://localhost:8080/api/auth/registro';
const API_AREAS_URL = 'http://localhost:8080/api/areas';

const registroForm = document.getElementById('registroForm');
const errorMessage = document.getElementById('errorMessage');
const successMessage = document.getElementById('successMessage');

// Cargar áreas al cargar la página
async function cargarAreas() {
    try {
        const response = await fetch(API_AREAS_URL);
        if (!response.ok) {
            throw new Error('Error al cargar áreas');
        }
        
        const areas = await response.json();
        const areaSelect = document.getElementById('area');
        
        areaSelect.innerHTML = '<option value="">-- Opcional --</option>';
        areas.forEach(area => {
            areaSelect.innerHTML += `<option value="${area.idArea}">${area.nombre} (${area.sigla})</option>`;
        });
    } catch (error) {
        console.error('Error al cargar áreas:', error);
    }
}

async function handleRegistro(event) {
    event.preventDefault();

    const nombre = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const tipoContrato = document.getElementById('tipoContrato').value;
    const idArea = document.getElementById('area').value;

    // Validaciones básicas
    if (!nombre || !apellido || !username || !email || !password || !tipoContrato) {
        mostrarError('Por favor, complete todos los campos obligatorios.');
        return;
    }

    if (password !== confirmPassword) {
        mostrarError('Las contraseñas no coinciden.');
        return;
    }

    if (password.length < 6) {
        mostrarError('La contraseña debe tener al menos 6 caracteres.');
        return;
    }

    ocultarMensajes();
    const submitButton = registroForm.querySelector('.btn-primary');
    submitButton.disabled = true;
    submitButton.textContent = 'Registrando...';

    try {
        const datosRegistro = {
            nombre,
            apellido,
            username,
            email,
            telefono: telefono || null,
            password,
            tipoContrato,
            idArea: idArea ? parseInt(idArea) : null,
            roles: ['Trabajador'] // Rol por defecto
        };

        const response = await fetch(API_REGISTRO_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(datosRegistro)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || `Error ${response.status}`);
        }

        mostrarExito('¡Registro exitoso! Redirigiendo al login...');
        registroForm.reset();
        
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);

    } catch (error) {
        console.error('Error en el registro:', error);
        mostrarError(error.message || 'Error al registrar usuario.');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Registrarse';
    }
}

function mostrarError(mensaje) {
    errorMessage.textContent = mensaje;
    errorMessage.style.display = 'block';
    successMessage.style.display = 'none';
}

function mostrarExito(mensaje) {
    successMessage.textContent = mensaje;
    successMessage.style.display = 'block';
    errorMessage.style.display = 'none';
}

function ocultarMensajes() {
    errorMessage.style.display = 'none';
    successMessage.style.display = 'none';
}

// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    cargarAreas();
    registroForm.addEventListener('submit', handleRegistro);
});
