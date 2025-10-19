// AppleGym - Configuration and Global Variables

console.log('AppleGym Config cargando...');

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
let currentCategory = 'Todos';

// Sample Data - Esta función carga los datos de ejemplo
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
    
    console.log(`Datos de ejemplo cargados: ${products.length} productos y servicios`);
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

console.log('AppleGym Config cargado correctamente!');