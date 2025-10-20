// AppleGym - Configuration and Global Variables

console.log('AppleGym Config cargando...');

// API Configuration
const API_BASE_URL = '/api';
const API_ENDPOINTS = {
    auth: {
        register: `${API_BASE_URL}/auth/register`,
        login: `${API_BASE_URL}/auth/login`
    },
    test: `${API_BASE_URL}/test-full`,
    catalogo: {
        completo: `${API_BASE_URL}/test-full/catalogo`,
        productos: `${API_BASE_URL}/test-full/productos`,
        servicios: `${API_BASE_URL}/test-full/servicios`,
        categorias: `${API_BASE_URL}/test-full/categorias`,
        producto: `${API_BASE_URL}/test-full/productos`,
        servicio: `${API_BASE_URL}/test-full/servicios`
    }
};

// Global Variables
let currentUser = null;
let cart = [];
let products = [];
let currentFilter = 'all';
let currentCategory = 'Todos';

// Load Real Data from API
async function loadRealData() {
    try {
        console.log('Cargando datos desde la base de datos MySQL...');
        
        // Cargar catálogo completo desde la API
        const response = await fetch(`${API_ENDPOINTS.catalogo.completo}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        if (data.success) {
            products = data.items || [];
            
            console.log(`✅ Datos cargados desde ${data.source}:`);
            console.log(`   📦 Productos: ${data.totalProductos || 0}`);
            console.log(`   🛠️ Servicios: ${data.totalServicios || 0}`);
            console.log(`   📊 Total items: ${products.length}`);
            
            return true;
        } else {
            throw new Error(data.error || 'Respuesta de API inválida');
        }
        
    } catch (error) {
        console.error('❌ Error conectando con la base de datos:', error.message);
        throw error; // Propagar el error en lugar de usar fallback
    }
}

// Load Products from API
async function loadProductsFromAPI() {
    try {
        console.log('Cargando productos desde la base de datos MySQL...');
        
        const response = await fetch(`${API_ENDPOINTS.catalogo.productos}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        if (data.success && data.productos) {
            products = data.productos;
            
            console.log(`✅ Productos cargados desde ${data.source}:`);
            console.log(`   📦 Total: ${products.length} productos`);
            
            return true;
        } else {
            throw new Error(data.error || 'Respuesta de API inválida');
        }
        
    } catch (error) {
        console.error('❌ Error cargando productos:', error.message);
        throw error;
    }
}

// Load Services from API
async function loadServicesFromAPI() {
    try {
        console.log('Cargando servicios desde la base de datos MySQL...');
        
        const response = await fetch(`${API_ENDPOINTS.catalogo.servicios}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        if (data.success && data.servicios) {
            products = data.servicios;
            
            console.log(`✅ Servicios cargados desde ${data.source}:`);
            console.log(`   🛠️ Total: ${products.length} servicios`);
            
            return true;
        } else {
            throw new Error(data.error || 'Respuesta de API inválida');
        }
        
    } catch (error) {
        console.error('❌ Error cargando servicios:', error.message);
        throw error;
    }
}

console.log('AppleGym Config cargado correctamente!');