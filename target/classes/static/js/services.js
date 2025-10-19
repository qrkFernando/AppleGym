// AppleGym - Services Page Script

console.log('AppleGym Services iniciando...');

let searchTerm = '';

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado, inicializando pagina de servicios...');
    initializeServicesPage();
});

// Initialize Services Page
async function initializeServicesPage() {
    console.log('Inicializando pagina de servicios...');
    
    // Cargar datos de usuario
    loadUserData();
    
    // Cargar carrito
    loadCart();
    
    // Configurar event listeners
    setupEventListeners();
    
    // Cargar datos de servicios
    await loadServicesData();
    
    console.log('Pagina de servicios inicializada correctamente');
}

// Load Services Data
async function loadServicesData() {
    showLoading();
    
    try {
        console.log('Cargando servicios...');
        
        // Intentar cargar datos reales de la API
        const apiSuccess = await loadRealData();
        
        if (!apiSuccess) {
            console.log('API no disponible, cargando datos de ejemplo...');
            loadSampleData();
        }
        
        // Establecer filtro inicial para servicios
        currentFilter = 'servicios';
        currentCategory = 'Todos';
        
        renderServices();
        console.log('Servicios cargados exitosamente');
        
    } catch (error) {
        console.error('Error cargando servicios:', error);
        loadSampleData();
        currentFilter = 'servicios';
        currentCategory = 'Todos';
        renderServices();
        showNotification('Cargando datos de demostracion', 'info');
    } finally {
        hideLoading();
    }
}

// Render Services
function renderServices() {
    const servicesGrid = document.getElementById('services-grid');
    const noResults = document.getElementById('no-results');
    
    if (!servicesGrid) {
        console.warn('Elemento services-grid no encontrado');
        return;
    }
    
    // Solo mostrar servicios (no productos)
    let filteredServices = products.filter(item => item.tipo === 'servicios');
    
    // Filtro por categoria
    if (currentCategory !== 'Todos') {
        filteredServices = filteredServices.filter(item => item.categoria === currentCategory);
    }
    
    // Filtro por búsqueda
    if (searchTerm) {
        filteredServices = filteredServices.filter(item => 
            item.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.descripcion.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.categoria.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }
    
    if (filteredServices.length === 0) {
        servicesGrid.style.display = 'none';
        if (noResults) noResults.style.display = 'block';
        return;
    }
    
    servicesGrid.style.display = 'grid';
    if (noResults) noResults.style.display = 'none';
    
    servicesGrid.innerHTML = filteredServices.map(item => `
        <div class="product-card service-card" onclick="showProductDetail(${item.id})">
            <div class="product-image">
                <i class="${item.icon || 'fas fa-cogs'}"></i>
            </div>
            <div class="product-info">
                <div class="product-name">${item.nombre}</div>
                <div class="product-description">${item.descripcion}</div>
                <div class="product-category" style="color: #666; font-size: 0.9rem; margin: 0.5rem 0;">
                    <i class="fas fa-tag"></i> ${item.categoria}
                </div>
                <div class="product-price">$${item.precio.toFixed(2)}</div>
                <div class="product-duration" style="color: #666; font-size: 0.9rem; margin: 0.5rem 0;">
                    <i class="fas fa-clock"></i> ${item.duracion} minutos
                </div>
                <div class="service-type" style="background: #007bff; color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.8rem; margin: 0.5rem 0; display: inline-block;">
                    Servicio Profesional
                </div>
                <div class="product-actions">
                    <button class="btn-primary btn-small" onclick="event.stopPropagation(); addToCart(${item.id})">
                        <i class="fas fa-calendar-plus"></i> Reservar
                    </button>
                    <button class="btn-secondary btn-small" onclick="event.stopPropagation(); showProductDetail(${item.id})">
                        <i class="fas fa-eye"></i> Ver Detalles
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    console.log(`Servicios renderizados: ${filteredServices.length} servicios mostrados`);
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
    
    renderServices();
    console.log(`Filtro de categoria aplicado: ${category}`);
}

// Search Services
function searchServices() {
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        searchTerm = searchInput.value.trim();
        renderServices();
        console.log(`Busqueda de servicios aplicada: ${searchTerm}`);
    }
}

console.log('AppleGym Services cargado correctamente!');