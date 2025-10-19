// AppleGym - Utility Functions

console.log('AppleGym Utils cargando...');

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

// Update Navbar based on user state
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

// Update Cart Count Display
function updateCartCount() {
    const cartCount = document.getElementById('cart-count');
    if (cartCount) {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        cartCount.textContent = totalItems;
        cartCount.style.display = totalItems > 0 ? 'flex' : 'none';
    }
}

// Save cart to localStorage
function saveCartToStorage() {
    localStorage.setItem('cart', JSON.stringify(cart));
}

// Load user data from localStorage
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

// Load cart from localStorage
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

console.log('AppleGym Utils cargado correctamente!');