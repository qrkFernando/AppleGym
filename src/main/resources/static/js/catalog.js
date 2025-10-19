// AppleGym - Catalog Page Script

console.log('AppleGym Catalog iniciando...');

let searchTerm = '';

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado, inicializando pagina de catalogo...');
    initializeCatalogPage();
});

// Initialize Catalog Page
async function initializeCatalogPage() {
    console.log('Inicializando pagina de catalogo...');
    
    // Cargar datos de usuario
    loadUserData();
    
    // Cargar carrito
    loadCart();
    
    // Configurar event listeners
    setupEventListeners();
    
    // Cargar datos del catalogo
    await loadCatalogData();
    
    console.log('Pagina de catalogo inicializada correctamente');
}

// Load Catalog Data
async function loadCatalogData() {
    showLoading();
    
    try {
        console.log('🔄 Conectando con la base de datos MySQL...');
        
        const success = await loadRealData();
        
        if (success) {
            showNotification('✅ Datos cargados desde tu base de datos MySQL', 'success');
        }
        
        renderCatalog();
        console.log('✅ Catálogo cargado exitosamente desde la base de datos');
        
    } catch (error) {
        console.error('❌ Error crítico:', error);
        showNotification('❌ Error: No se puede conectar con la base de datos MySQL. Verifica que el servidor esté ejecutándose.', 'error');
        
        // Mostrar mensaje en la interfaz
        const catalogGrid = document.getElementById('catalog-grid');
        if (catalogGrid) {
            catalogGrid.innerHTML = `
                <div class="error-message" style="grid-column: 1/-1; text-align: center; padding: 3rem; background: #ffebee; border-radius: 8px; margin: 2rem 0;">
                    <i class="fas fa-database" style="font-size: 4rem; color: #f44336; margin-bottom: 1rem;"></i>
                    <h3 style="color: #f44336; margin-bottom: 1rem;">No se puede conectar con la base de datos</h3>
                    <p style="color: #666; margin-bottom: 1rem;">Verifica que:</p>
                    <ul style="text-align: left; max-width: 400px; margin: 0 auto; color: #666;">
                        <li>MySQL esté ejecutándose</li>
                        <li>La base de datos 'applegym' exista</li>
                        <li>Las credenciales sean correctas</li>
                        <li>El servidor Spring Boot esté funcionando</li>
                    </ul>
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

// Render Catalog
function renderCatalog() {
    const catalogGrid = document.getElementById('catalog-grid');
    const noResults = document.getElementById('no-results');
    
    if (!catalogGrid) {
        console.warn('Elemento catalog-grid no encontrado');
        return;
    }
    
    // Aplicar filtros
    let filteredProducts = products;
    
    // Filtro por tipo
    if (currentFilter !== 'all') {
        filteredProducts = filteredProducts.filter(item => item.tipo === currentFilter);
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
                <div class="product-type" style="background: ${item.tipo === 'productos' ? '#42944C' : '#007bff'}; color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.8rem; margin: 0.5rem 0; display: inline-block;">
                    ${item.tipo === 'productos' ? 'Producto' : 'Servicio'}
                </div>
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
    
    console.log(`Catalogo renderizado: ${filteredProducts.length} items mostrados`);
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

// Search Products
function searchProducts() {
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        searchTerm = searchInput.value.trim();
        renderCatalog();
        console.log(`Busqueda aplicada: ${searchTerm}`);
    }
}

console.log('AppleGym Catalog cargado correctamente!');