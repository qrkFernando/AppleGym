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
        console.log('🔄 Cargando productos desde la base de datos MySQL...');
        
        const success = await loadProductsFromAPI();
        
        if (success) {
            showNotification('✅ Productos cargados desde tu base de datos MySQL', 'success');
        }
        
        // Establecer filtro inicial para productos
        currentFilter = 'productos';
        currentCategory = 'Todos';
        
        renderProducts();
        console.log('✅ Productos cargados exitosamente desde la base de datos');
        
    } catch (error) {
        console.error('❌ Error crítico:', error);
        showNotification('❌ Error: No se puede conectar con la base de datos MySQL', 'error');
        
        // Establecer array vacío y mostrar mensaje de error
        products = [];
        currentFilter = 'productos';
        currentCategory = 'Todos';
        
        renderProducts();
        
        // Mostrar mensaje específico en la grid
        const productsGrid = document.getElementById('products-grid');
        if (productsGrid) {
            productsGrid.innerHTML = `
                <div class="error-message" style="grid-column: 1/-1; text-align: center; padding: 3rem; background: #ffebee; border-radius: 8px; margin: 2rem 0;">
                    <i class="fas fa-box" style="font-size: 4rem; color: #f44336; margin-bottom: 1rem;"></i>
                    <h3 style="color: #f44336; margin-bottom: 1rem;">No se pueden cargar los productos</h3>
                    <p style="color: #666;">Revisa la conexión con la base de datos MySQL</p>
                    <div style="margin-top: 1rem;">
                        <button onclick="location.reload()" class="btn-primary">
                            <i class="fas fa-redo"></i> Reintentar
                        </button>
                    </div>
                </div>
            `;
        }
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