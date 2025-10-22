// AppleGym - Authentication Functions

console.log("AppleGym Auth cargando...");

// Handle Login Form Submission
async function handleLogin(e) {
  e.preventDefault();
  showLoading();

  const formData = new FormData(e.target);
  const loginData = {
    email: formData.get("email"),
    password: formData.get("password"),
  };

  try {
    console.log("Intentando login:", loginData.email);

    const response = await fetch(API_ENDPOINTS.auth.login, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(loginData),
    });

    const result = await response.json();
    console.log("Respuesta de login:", result);

    if (response.ok && result.success) {
      currentUser = result.cliente;
      localStorage.setItem("userData", JSON.stringify(result.cliente));

      console.log('Usuario logueado:', result.cliente);
      console.log('Rol del usuario:', result.cliente.rol);
      
      // Redirigir según el rol ANTES de cualquier otra acción
      if (result.cliente.rol === 'ADMIN') {
        console.log('✅ ADMIN detectado - Redirigiendo a dashboard...');
        hideLoading();
        window.location.href = 'admin-dashboard.html';
        return; // Importante: salir aquí
      }
      
      // Solo si NO es admin, hacer las demás acciones
      updateNavbar();
      closeModal("login-modal");
      showNotification(
        `Bienvenido ${result.cliente.nombre || result.cliente.nombreCliente}!`,
        "success"
      );
      e.target.reset();
    } else {
      showNotification(result.error || "Credenciales invalidas", "error");
    }
  } catch (error) {
    console.error("Error de login:", error);
    showNotification(
      "Error de conexion. Verifica que el servidor este funcionando.",
      "error"
    );
  } finally {
    hideLoading();
  }
}

// Handle Register Form Submission
async function handleRegister(e) {
  e.preventDefault();
  showLoading();

  const formData = new FormData(e.target);
  const registerData = {
    nombreCliente: formData.get("nombre"),
    email: formData.get("email"),
    telefono: formData.get("telefono"),
    direccion: formData.get("direccion"),
    password: formData.get("password"),
  };

  try {
    console.log("Intentando registro:", registerData.email);

    const response = await fetch(API_ENDPOINTS.auth.register, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(registerData),
    });

    const result = await response.json();
    console.log("Respuesta de registro:", result);

    if (response.ok && result.success) {
      closeModal("register-modal");
      showNotification(
        "Registro exitoso! Ya puedes iniciar sesion.",
        "success"
      );
      e.target.reset();

      setTimeout(() => {
        showLogin();
      }, 1000);
    } else {
      showNotification(result.error || "Error en el registro", "error");
    }
  } catch (error) {
    console.error("Error de registro:", error);
    showNotification(
      "Error de conexion. Verifica que el servidor este funcionando.",
      "error"
    );
  } finally {
    hideLoading();
  }
}

// Logout Function
function logout() {
  localStorage.removeItem("userData");
  localStorage.removeItem("cart");

  currentUser = null;
  cart = [];

  updateNavbar();
  updateCartCount();

  showNotification("Sesion cerrada correctamente", "success");
}

console.log("AppleGym Auth cargado correctamente!");
