// AppleGym - Main Application Script for Index Page

console.log('AppleGym Main iniciando...');

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado, inicializando aplicacion principal...');
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
    
    // Cargar datos si hay una seccion de catalogo (para compatibilidad)
    const catalogSection = document.getElementById('catalog-grid');
    if (catalogSection) {
        await loadCatalogData();
    }
    
    console.log('AppleGym inicializado correctamente');
}

// Load Catalog Data (solo si existe la seccion en la pagina)
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

// Render Catalog (funcionalidad basica para index)
function renderCatalog() {
    const catalogGrid = document.getElementById('catalog-grid');
    if (!catalogGrid) {
        return; // No hay seccion de catalogo en esta pagina
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

// Filter Products (funcionalidad basica para index)
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

console.log('AppleGym Main cargado correctamente!');