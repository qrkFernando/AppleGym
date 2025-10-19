// AppleGym - Products Page Script

console.log('AppleGym Products iniciando...');

let searchTerm = '';

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado, inicializando pagina de productos...');
    initializeProductsPage();
});

// Initialize Products Page
async function initializeProductsPage() {
    console.log('Inicializando pagina de productos...');
    
    // Cargar datos de usuario
    loadUserData();
    
    // Cargar carrito
    loadCart();
    
    // Configurar event listeners
    setupEventListeners();
    
    // Cargar datos del catalogo
    await loadProductsData();
    
    console.log('Pagina de productos inicializada correctamente');
}

// Load Products Data
async function loadProductsData() {
    showLoading();
    
    try {
        console.log('Cargando productos...');
        
        // Intentar cargar datos reales de la API
        const apiSuccess = await loadRealData();
        
        if (!apiSuccess) {
            console.log('API no disponible, cargando datos de ejemplo...');
            loadSampleData();
        }
        
        // Establecer filtro inicial para productos
        currentFilter = 'productos';
        currentCategory = 'Todos';
        
        renderProducts();
        console.log('Productos cargados exitosamente');
        
    } catch (error) {
        console.error('Error cargando productos:', error);
        loadSampleData();
        currentFilter = 'productos';
        currentCategory = 'Todos';
        renderProducts();
        showNotification('Cargando datos de demostracion', 'info');
    } finally {
        hideLoading();
    }
}

// Render Products
function renderProducts() {
    const catalogGrid = document.getElementById('catalog-grid');
    const noResults = document.getElementById('no-results');
    
    if (!catalogGrid) {
        console.warn('Elemento catalog-grid no encontrado');
        return;
    }
    
    // Solo mostrar productos (no servicios)
    let filteredProducts = products.filter(item => item.tipo === 'productos');
    
    // Filtro por categoria
    if (currentCategory !== 'Todos') {
        filteredProducts = filteredProducts.filter(item => item.categoria === currentCategory);
    }
    
    // Filtro por búsqueda
    if (searchTerm) {
        filteredProducts = filteredProducts.filter(item => 
            item.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.descripcion.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.categoria.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }
    
    if (filteredProducts.length === 0) {
        catalogGrid.style.display = 'none';
        if (noResults) noResults.style.display = 'block';
        return;
    }
    
    catalogGrid.style.display = 'grid';
    if (noResults) noResults.style.display = 'none';
    
    catalogGrid.innerHTML = filteredProducts.map(item => `
        <div class="product-card" onclick="showProductDetail(${item.id})">
            <div class="product-image">
                <i class="${item.icon || 'fas fa-box'}"></i>
            </div>
            <div class="product-info">
                <div class="product-name">${item.nombre}</div>
                <div class="product-description">${item.descripcion}</div>
                <div class="product-category" style="color: #666; font-size: 0.9rem; margin: 0.5rem 0;">
                    <i class="fas fa-tag"></i> ${item.categoria}
                </div>
                <div class="product-price">$${item.precio.toFixed(2)}</div>
                <div class="product-stock" style="color: ${item.stock > 0 ? '#42944C' : '#dc3545'}; font-size: 0.9rem; margin: 0.5rem 0;">
                    <i class="fas fa-box"></i> ${item.stock > 0 ? `Stock: ${item.stock}` : 'Sin stock'}
                </div>
                <div class="product-actions">
                    <button class="btn-primary btn-small" onclick="event.stopPropagation(); addToCart(${item.id})" 
                            ${item.stock === 0 ? 'disabled' : ''}>
                        <i class="fas fa-cart-plus"></i> Agregar
                    </button>
                    <button class="btn-secondary btn-small" onclick="event.stopPropagation(); showProductDetail(${item.id})">
                        <i class="fas fa-eye"></i> Ver Detalles
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    console.log(`Productos renderizados: ${filteredProducts.length} productos mostrados`);
}

// Filter by Category
function filterByCategory(category) {
    currentCategory = category;
    
    // Actualizar botones de filtro
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Encontrar el boton clickeado y marcarlo como activo
    if (event && event.target) {
        event.target.classList.add('active');
    }
    
    renderProducts();
    console.log(`Filtro de categoria aplicado: ${category}`);
}

// Search Products
function searchProducts() {
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        searchTerm = searchInput.value.trim();
        renderProducts();
        console.log(`Busqueda de productos aplicada: ${searchTerm}`);
    }
}

console.log('AppleGym Products cargado correctamente!');