const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const errorMessage = document.getElementById('errorMessage');

const API_LOGIN_URL = getApiUrl(APP_CONFIG.API_ENDPOINTS.AUTH.LOGIN);

async function handleLogin(event) {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    if (!username || !password) {
        mostrarError('Por favor, ingrese usuario y contraseña.');
        return;
    }

    if (password.length < 6) {
        mostrarError('La contraseña debe tener al menos 6 caracteres.');
        return;
    }

    ocultarError();
    const submitButton = loginForm.querySelector('.login-button');
    submitButton.disabled = true;
    submitButton.textContent = 'Ingresando...';

    try {
        const response = await fetch(API_LOGIN_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || `Error ${response.status}`);
        }

        if (data.idUsuario && data.username && data.roles && data.token) {
            localStorage.setItem(APP_CONFIG.STORAGE_KEYS.TOKEN, data.token);
            
            localStorage.setItem(APP_CONFIG.STORAGE_KEYS.USER, JSON.stringify({
                id: data.idUsuario,
                username: data.username,
                email: data.email,
                roles: data.roles
            }));

            window.location.href = APP_CONFIG.DEFAULT_DASHBOARD;

        } else {
             throw new Error('Respuesta inesperada del servidor.');
        }

    } catch (error) {
        console.error('Error en el login:', error);
        mostrarError(error.message || 'Usuario o contraseña incorrectos.');
        submitButton.disabled = false;
        submitButton.textContent = 'Ingresar';
    }
}

function mostrarError(mensaje) {
    errorMessage.textContent = mensaje;
    errorMessage.style.display = 'block';
}

function ocultarError() {
    errorMessage.textContent = '';
    errorMessage.style.display = 'none';
}

loginForm.addEventListener('submit', handleLogin);