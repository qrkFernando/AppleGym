// AppleGym Frontend JavaScript - Version Corregida y Funcional

console.log('AppleGym Frontend iniciando...');

// API Configuration
const API_BASE_URL = '/api';
const API_ENDPOINTS = {
    auth: {
        register: `${API_BASE_URL}/test-full/register-simple`,
        login: `${API_BASE_URL}/test-full/login-simple`
    },
    test: `${API_BASE_URL}/test-full`
};

// Global Variables
let currentUser = null;
let cart = [];
let products = [];
let currentFilter = 'all';

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado, inicializando aplicacion...');
    initializeApp();
});

// Initialize Application
async function initializeApp() {
    console.log('Inicializando AppleGym...');
    
    // Cargar datos de usuario
    loadUserData();
    
    // Cargar carrito
    loadCart();
    
    // Configurar event listeners
    setupEventListeners();
    
    // Cargar datos del catalogo
    await loadCatalogData();
    
    console.log('AppleGym inicializado correctamente');
}

// Load User Data
function loadUserData() {
    const userData = localStorage.getItem('userData');
    if (userData) {
        try {
            currentUser = JSON.parse(userData);
            updateNavbar();
        } catch (e) {
            localStorage.removeItem('userData');
        }
    }
}

// Load Cart
function loadCart() {
    cart = JSON.parse(localStorage.getItem('cart') || '[]');
    updateCartCount();
}

// Setup Event Listeners
function setupEventListeners() {
    console.log('Configurando event listeners...');
    
    // Mobile navigation toggle
    const navToggle = document.getElementById('nav-toggle');
    const navMenu = document.getElementById('nav-menu');
    
    if (navToggle && navMenu) {
        navToggle.addEventListener('click', () => {
            navMenu.classList.toggle('active');
        });
    }

    // Forms
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }

    // Navegacion suave
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            const href = link.getAttribute('href');
            if (href && href.startsWith('#')) {
                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });

    // Cerrar modals al hacer click fuera
    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            closeModal(e.target.id);
        }
    });
    
    console.log('Event listeners configurados');
}

// Load Catalog Data
async function loadCatalogData() {
    showLoading();
    
    try {
        console.log('Cargando catalogo...');
        
        // Intentar cargar datos reales de la API
        const apiSuccess = await loadRealData();
        
        if (!apiSuccess) {
            console.log('API no disponible, cargando datos de ejemplo...');
            loadSampleData();
        }
        
        renderCatalog();
        console.log('Catalogo cargado exitosamente');
        
    } catch (error) {
        console.error('Error cargando catalogo:', error);
        loadSampleData();
        renderCatalog();
        showNotification('Cargando datos de demostracion', 'info');
    } finally {
        hideLoading();
    }
}

// Load Real Data from API
async function loadRealData() {
    try {
        // Test API connection
        console.log('Probando conexion API...');
        const testResponse = await fetch(`${API_ENDPOINTS.test}/hello`);
        if (!testResponse.ok) {
            return false;
        }
        
        console.log('API conectada correctamente');
        showNotification('API conectada correctamente', 'success');
        
        // Por ahora usar datos de ejemplo hasta que implementes endpoints de productos
        loadSampleData();
        return true;
        
    } catch (error) {
        console.log('API no disponible:', error.message);
        return false;
    }
}

// Load Sample Data
function loadSampleData() {
    products = [
        {
            id: 1,
            nombre: 'Proteina Whey Premium',
            descripcion: 'Proteina de alta calidad para desarrollo muscular optimo',
            precio: 45.99,
            stock: 100,
            tipo: 'productos',
            categoria: 'Suplementos',
            icon: 'fas fa-flask'
        },
        {
            id: 2,
            nombre: 'Mancuernas Ajustables',
            descripcion: 'Set completo de mancuernas profesionales 5-50kg',
            precio: 299.99,
            stock: 25,
            tipo: 'productos',
            categoria: 'Equipamiento',
            icon: 'fas fa-dumbbell'
        },
        {
            id: 3,
            nombre: 'Banca Multifuncional',
            descripcion: 'Banca profesional para entrenamiento completo del cuerpo',
            precio: 459.99,
            stock: 15,
            tipo: 'productos',
            categoria: 'Equipamiento',
            icon: 'fas fa-couch'
        },
        {
            id: 4,
            nombre: 'Creatina Monohidrato',
            descripcion: 'Creatina pura para mayor fuerza y resistencia en entrenamientos',
            precio: 29.99,
            stock: 200,
            tipo: 'productos',
            categoria: 'Suplementos',
            icon: 'fas fa-pills'
        },
        {
            id: 5,
            nombre: 'Entrenamiento Personal',
            descripcion: 'Sesion personalizada de 60 minutos con entrenador certificado',
            precio: 65.00,
            duracion: 60,
            tipo: 'servicios',
            categoria: 'Entrenamiento',
            icon: 'fas fa-user-tie'
        },
        {
            id: 6,
            nombre: 'Clase de Yoga',
            descripcion: 'Clase grupal de yoga relajante para todos los niveles',
            precio: 25.00,
            duracion: 90,
            tipo: 'servicios',
            categoria: 'Clases Grupales',
            icon: 'fas fa-leaf'
        },
        {
            id: 7,
            nombre: 'Asesoria Nutricional',
            descripcion: 'Consulta personalizada con nutricionista especializado',
            precio: 55.00,
            duracion: 45,
            tipo: 'servicios',
            categoria: 'Nutricion',
            icon: 'fas fa-apple-alt'
        },
        {
            id: 8,
            nombre: 'Clase de CrossFit',
            descripcion: 'Entrenamiento funcional de alta intensidad y resistencia',
            precio: 35.00,
            duracion: 60,
            tipo: 'servicios',
            categoria: 'Clases Grupales',
            icon: 'fas fa-fire'
        }
    ];
    
    console.log(`Datos de ejemplo cargados: ${products.length} productos`);
}

// Render Catalog
function renderCatalog() {
    const catalogGrid = document.getElementById('catalog-grid');
    if (!catalogGrid) {
        console.warn('Elemento catalog-grid no encontrado');
        return;
    }
    
    const filteredProducts = currentFilter === 'all' 
        ? products 
        : products.filter(item => item.tipo === currentFilter);
    
    if (filteredProducts.length === 0) {
        catalogGrid.innerHTML = `
            <div class="no-products" style="grid-column: 1/-1; text-align: center; padding: 2rem;">
                <i class="fas fa-search" style="font-size: 3rem; color: #42944C; margin-bottom: 1rem;"></i>
                <h3>No hay productos disponibles</h3>
                <p>Intenta cambiar el filtro o vuelve mas tarde.</p>
            </div>
        `;
        return;
    }
    
    catalogGrid.innerHTML = filteredProducts.map(item => `
        <div class="product-card" onclick="showProductDetail(${item.id})">
            <div class="product-image">
                <i class="${item.icon || 'fas fa-box'}"></i>
            </div>
            <div class="product-info">
                <div class="product-name">${item.nombre}</div>
                <div class="product-description">${item.descripcion}</div>
                <div class="product-price">$${item.precio.toFixed(2)}</div>
                ${item.tipo === 'servicios' && item.duracion ? 
                    `<div class="product-duration" style="color: #666; font-size: 0.9rem; margin: 0.5rem 0;">
                        <i class="fas fa-clock"></i> ${item.duracion} minutos
                    </div>` : ''
                }
                ${item.stock !== undefined ? 
                    `<div class="product-stock" style="color: ${item.stock > 0 ? '#42944C' : '#dc3545'}; font-size: 0.9rem; margin: 0.5rem 0;">
                        <i class="fas fa-box"></i> ${item.stock > 0 ? `Stock: ${item.stock}` : 'Sin stock'}
                    </div>` : ''
                }
                <div class="product-actions">
                    <button class="btn-primary btn-small" onclick="event.stopPropagation(); addToCart(${item.id})" 
                            ${item.stock !== undefined && item.stock === 0 ? 'disabled' : ''}>
                        <i class="fas fa-cart-plus"></i> Agregar
                    </button>
                    <button class="btn-secondary btn-small" onclick="event.stopPropagation(); showProductDetail(${item.id})">
                        <i class="fas fa-eye"></i> Ver Detalles
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    console.log(`Catalogo renderizado: ${filteredProducts.length} productos mostrados`);
}

// Filter Products
function filterProducts(filter) {
    currentFilter = filter;
    
    // Actualizar botones de filtro
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Encontrar el boton clickeado y marcarlo como activo
    if (event && event.target) {
        event.target.classList.add('active');
    }
    
    renderCatalog();
    console.log(`Filtro aplicado: ${filter}`);
}

// Show Product Detail
function showProductDetail(productId) {
    const product = products.find(p => p.id === productId);
    if (!product) return;
    
    const productDetail = document.getElementById('product-detail');
    if (!productDetail) {
        console.warn('Elemento product-detail no encontrado');
        return;
    }
    
    productDetail.innerHTML = `
        <div class="product-detail-content">
            <div class="product-detail-image" style="text-align: center; margin-bottom: 2rem;">
                <i class="${product.icon || 'fas fa-box'}" style="font-size: 6rem; color: #42944C;"></i>
            </div>
            <h2 style="color: #42944C; margin-bottom: 1rem;">${product.nombre}</h2>
            <p style="margin-bottom: 1.5rem; line-height: 1.6; color: #666;">${product.descripcion}</p>
            
            <div class="product-details" style="margin-bottom: 2rem; background: #f8f9fa; padding: 1rem; border-radius: 8px;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                    <span><strong>Precio:</strong></span>
                    <span style="color: #42944C; font-weight: 600; font-size: 1.2rem;">$${product.precio.toFixed(2)}</span>
                </div>
                
                ${product.tipo === 'servicios' && product.duracion ? `
                    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                        <span><strong>Duracion:</strong></span>
                        <span>${product.duracion} minutos</span>
                    </div>
                ` : ''}
                
                ${product.stock !== undefined ? `
                    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                        <span><strong>Stock:</strong></span>
                        <span style="color: ${product.stock > 0 ? '#42944C' : '#dc3545'};">
                            ${product.stock > 0 ? product.stock + ' disponibles' : 'Sin stock'}
                        </span>
                    </div>
                ` : ''}
                
                <div style="display: flex; justify-content: space-between;">
                    <span><strong>Categoria:</strong></span>
                    <span>${product.categoria || 'General'}</span>
                </div>
            </div>
            
            <div class="product-actions" style="display: flex; gap: 1rem;">
                <button class="btn-primary" onclick="addToCart(${product.id}); closeModal('product-modal');" 
                        ${product.stock !== undefined && product.stock === 0 ? 'disabled' : ''}
                        style="flex: 1;">
                    <i class="fas fa-cart-plus"></i> Agregar al Carrito
                </button>
                <button class="btn-secondary" onclick="closeModal('product-modal')" style="flex: 1;">
                    <i class="fas fa-times"></i> Cerrar
                </button>
            </div>
        </div>
    `;
    
    showModal('product-modal');
}

// Authentication Functions
async function handleLogin(e) {
    e.preventDefault();
    showLoading();
    
    const formData = new FormData(e.target);
    const loginData = {
        email: formData.get('email'),
        password: formData.get('password')
    };
    
    try {
        console.log('Intentando login:', loginData.email);
        
        const response = await fetch(API_ENDPOINTS.auth.login, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData)
        });
        
        const result = await response.json();
        console.log('Respuesta de login:', result);
        
        if (response.ok && result.success) {
            currentUser = result.cliente;
            localStorage.setItem('userData', JSON.stringify(result.cliente));
            
            updateNavbar();
            closeModal('login-modal');
            showNotification(`Bienvenido ${result.cliente.nombre || result.cliente.nombreCliente}!`, 'success');
            e.target.reset();
            
        } else {
            showNotification(result.error || 'Credenciales invalidas', 'error');
        }
        
    } catch (error) {
        console.error('Error de login:', error);
        showNotification('Error de conexion. Verifica que el servidor este funcionando.', 'error');
    } finally {
        hideLoading();
    }
}

async function handleRegister(e) {
    e.preventDefault();
    showLoading();
    
    const formData = new FormData(e.target);
    const registerData = {
        nombreCliente: formData.get('nombre'),
        email: formData.get('email'),
        telefono: formData.get('telefono'),
        direccion: formData.get('direccion'),
        password: formData.get('password')
    };
    
    try {
        console.log('Intentando registro:', registerData.email);
        
        const response = await fetch(API_ENDPOINTS.auth.register, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(registerData)
        });
        
        const result = await response.json();
        console.log('Respuesta de registro:', result);
        
        if (response.ok && result.success) {
            closeModal('register-modal');
            showNotification('Registro exitoso! Ya puedes iniciar sesion.', 'success');
            e.target.reset();
            
            setTimeout(() => {
                showLogin();
            }, 1000);
            
        } else {
            showNotification(result.error || 'Error en el registro', 'error');
        }
        
    } catch (error) {
        console.error('Error de registro:', error);
        showNotification('Error de conexion. Verifica que el servidor este funcionando.', 'error');
    } finally {
        hideLoading();
    }
}

function logout() {
    localStorage.removeItem('userData');
    localStorage.removeItem('cart');
    
    currentUser = null;
    cart = [];
    
    updateNavbar();
    updateCartCount();
    
    showNotification('Sesion cerrada correctamente', 'success');
}

// Cart Functions
function addToCart(productId) {
    const product = products.find(p => p.id === productId);
    if (!product) {
        showNotification('Producto no encontrado', 'error');
        return;
    }
    
    if (product.stock !== undefined && product.stock <= 0) {
        showNotification('Producto sin stock', 'error');
        return;
    }
    
    const existingItem = cart.find(item => item.id === productId);
    
    if (existingItem) {
        if (product.stock !== undefined && existingItem.quantity >= product.stock) {
            showNotification('No hay mas stock disponible', 'error');
            return;
        }
        existingItem.quantity += 1;
    } else {
        cart.push({
            id: product.id,
            nombre: product.nombre,
            precio: product.precio,
            tipo: product.tipo,
            quantity: 1,
            icon: product.icon
        });
    }
    
    // Actualizar stock (demo)
    if (product.stock !== undefined) {
        product.stock -= 1;
    }
    
    updateCartCount();
    saveCartToStorage();
    renderCatalog();
    
    showNotification(`${product.nombre} agregado al carrito`, 'success');
    console.log(`Producto agregado:`, product.nombre);
}

function removeFromCart(productId) {
    const itemIndex = cart.findIndex(item => item.id === productId);
    if (itemIndex > -1) {
        const item = cart[itemIndex];
        const product = products.find(p => p.id === productId);
        
        // Restaurar stock (demo)
        if (product && product.stock !== undefined) {
            product.stock += item.quantity;
        }
        
        cart.splice(itemIndex, 1);
        updateCartCount();
        saveCartToStorage();
        renderCart();
        renderCatalog();
        
        showNotification(`${item.nombre} eliminado del carrito`, 'success');
    }
}

function updateCartItemQuantity(productId, change) {
    const item = cart.find(item => item.id === productId);
    const product = products.find(p => p.id === productId);
    
    if (!item) return;
    
    const newQuantity = item.quantity + change;
    
    if (newQuantity <= 0) {
        removeFromCart(productId);
        return;
    }
    
    if (product && product.stock !== undefined) {
        const totalAvailable = product.stock + item.quantity;
        if (newQuantity > totalAvailable) {
            showNotification('No hay suficiente stock', 'error');
            return;
        }
        product.stock = totalAvailable - newQuantity;
    }
    
    item.quantity = newQuantity;
    updateCartCount();
    saveCartToStorage();
    renderCart();
    renderCatalog();
}

function toggleCart() {
    renderCart();
    showModal('cart-modal');
}

function renderCart() {
    const cartItems = document.getElementById('cart-items');
    const cartTotal = document.getElementById('cart-total');
    
    if (!cartItems || !cartTotal) return;
    
    if (cart.length === 0) {
        cartItems.innerHTML = `
            <div class="empty-cart" style="text-align: center; padding: 2rem;">
                <i class="fas fa-shopping-cart" style="font-size: 3rem; color: #ccc; margin-bottom: 1rem;"></i>
                <h3>Tu carrito esta vacio</h3>
                <p>Agrega algunos productos para comenzar</p>
            </div>
        `;
        cartTotal.textContent = '0.00';
        return;
    }
    
    cartItems.innerHTML = cart.map(item => `
        <div class="cart-item" style="display: flex; align-items: center; padding: 1rem; border-bottom: 1px solid #eee;">
            <div style="font-size: 2rem; color: #42944C; margin-right: 1rem;">
                <i class="${item.icon || 'fas fa-box'}"></i>
            </div>
            <div style="flex: 1;">
                <div style="font-weight: 600; margin-bottom: 0.25rem;">${item.nombre}</div>
                <div style="color: #666;">$${item.precio.toFixed(2)} c/u</div>
            </div>
            <div style="display: flex; align-items: center; gap: 0.5rem;">
                <button onclick="updateCartItemQuantity(${item.id}, -1)" 
                        style="background: #42944C; color: white; border: none; width: 30px; height: 30px; border-radius: 4px; cursor: pointer;">
                    <i class="fas fa-minus"></i>
                </button>
                <span style="margin: 0 0.5rem; font-weight: 600; min-width: 30px; text-align: center;">${item.quantity}</span>
                <button onclick="updateCartItemQuantity(${item.id}, 1)" 
                        style="background: #42944C; color: white; border: none; width: 30px; height: 30px; border-radius: 4px; cursor: pointer;">
                    <i class="fas fa-plus"></i>
                </button>
                <button onclick="removeFromCart(${item.id})" 
                        style="background: #dc3545; color: white; border: none; width: 30px; height: 30px; border-radius: 4px; cursor: pointer; margin-left: 0.5rem;">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `).join('');
    
    const total = cart.reduce((sum, item) => sum + (item.precio * item.quantity), 0);
    cartTotal.textContent = total.toFixed(2);
}

function continueShopping() {
    closeModal('cart-modal');
    const catalogSection = document.getElementById('catalog');
    if (catalogSection) {
        catalogSection.scrollIntoView({ behavior: 'smooth' });
    }
}

function proceedToCheckout() {
    if (!currentUser) {
        closeModal('cart-modal');
        showLogin();
        showNotification('Debes iniciar sesion para continuar con la compra', 'error');
        return;
    }
    
    if (cart.length === 0) {
        showNotification('Tu carrito esta vacio', 'error');
        return;
    }
    
    const total = cart.reduce((sum, item) => sum + (item.precio * item.quantity), 0);
    showNotification(`Procesando compra por $${total.toFixed(2)}. Funcionalidad de pago en desarrollo.`, 'info');
}

// Utility Functions
function updateNavbar() {
    const navAuth = document.querySelector('.nav-auth');
    const navUser = document.getElementById('nav-user');
    const userName = document.getElementById('user-name');
    
    if (currentUser) {
        if (navAuth) navAuth.style.display = 'none';
        if (navUser) navUser.style.display = 'flex';
        if (userName) {
            userName.textContent = currentUser.nombre || currentUser.nombreCliente || 'Usuario';
        }
    } else {
        if (navAuth) navAuth.style.display = 'flex';
        if (navUser) navUser.style.display = 'none';
    }
}

function updateCartCount() {
    const cartCount = document.getElementById('cart-count');
    if (cartCount) {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        cartCount.textContent = totalItems;
        cartCount.style.display = totalItems > 0 ? 'flex' : 'none';
    }
}

function saveCartToStorage() {
    localStorage.setItem('cart', JSON.stringify(cart));
}

// Modal Functions
function showModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

function showLogin() {
    showModal('login-modal');
}

function showRegister() {
    showModal('register-modal');
}

// Loading Functions
function showLoading() {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.style.display = 'flex';
    }
}

function hideLoading() {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.style.display = 'none';
    }
}

// Notification Functions
function showNotification(message, type = 'success') {
    const notification = document.getElementById('notification');
    if (notification) {
        notification.textContent = message;
        notification.className = `notification ${type}`;
        notification.style.display = 'block';
        
        setTimeout(() => {
            notification.style.display = 'none';
        }, 4000);
    } else {
        console.log(`Notificacion ${type.toUpperCase()}: ${message}`);
        // Fallback usando alert si no hay elemento notification
        if (type === 'error') {
            alert(`ERROR: ${message}`);
        }
    }
}

// API Test Function
async function testAPI() {
    try {
        const response = await fetch(`${API_ENDPOINTS.test}/hello`);
        const data = await response.json();
        console.log('Test API exitoso:', data);
        showNotification('API conectada correctamente', 'success');
        return true;
    } catch (error) {
        console.log('Test API fallo:', error.message);
        showNotification('API no disponible, usando datos de demostracion', 'warning');
        return false;
    }
}

console.log('AppleGym Frontend cargado correctamente!');