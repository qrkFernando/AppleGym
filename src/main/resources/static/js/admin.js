// AppleGym - Admin Dashboard JavaScript

console.log('Admin Dashboard cargando...');

let salesChart, productsChart, servicesChart;
let topProducts = [];
let topServices = [];
let ventasPorFecha = [];

// Inicializar Dashboard
function initializeAdmin() {
    console.log('Inicializando admin dashboard...');
    
    // Mostrar nombre del admin
    if (currentUser && currentUser.nombreCliente) {
        document.getElementById('admin-name').textContent = currentUser.nombreCliente;
    }
    
    // Establecer fechas por defecto (últimos 30 días)
    const today = new Date();
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);
    
    document.getElementById('fecha-inicio').value = formatDate(thirtyDaysAgo);
    document.getElementById('fecha-fin').value = formatDate(today);
    
    // Cargar datos
    loadDashboardData();
    
    // Actualizar timestamp
    updateTimestamp();
    setInterval(updateTimestamp, 60000); // Actualizar cada minuto
}

// Cargar datos del dashboard
async function loadDashboardData() {
    showLoading();
    
    try {
        // Cargar resumen
        await loadResumen();
        
        // Cargar datos para gráficos
        await loadChartData();
        
        // Cargar top productos
        await loadTopProducts();
        
        // Cargar top servicios
        await loadTopServices();
        
        console.log('Dashboard cargado exitosamente');
        
    } catch (error) {
        console.error('Error cargando dashboard:', error);
        showNotification('Error cargando datos del dashboard', 'error');
    } finally {
        hideLoading();
    }
}

// Cargar resumen general
async function loadResumen() {
    try {
        const response = await fetch(`${API_BASE_URL}/reportes/resumen`, {
            headers: {
                'Authorization': `Bearer ${currentUser.token}`
            }
        });
        
        if (!response.ok) throw new Error('Error cargando resumen');
        
        const data = await response.json();
        
        // Actualizar cards
        document.getElementById('total-sales').textContent = `$${data.ventasTotales.toFixed(2)}`;
        document.getElementById('total-orders').textContent = data.totalVentas;
        document.getElementById('total-clients').textContent = data.totalClientes;
        document.getElementById('total-products').textContent = data.totalProductos;
        
        console.log('Resumen cargado:', data);
        
    } catch (error) {
        console.error('Error cargando resumen:', error);
    }
}

// Cargar datos para gráficos
async function loadChartData() {
    const fechaInicio = document.getElementById('fecha-inicio').value;
    const fechaFin = document.getElementById('fecha-fin').value;
    
    if (!fechaInicio || !fechaFin) {
        showNotification('Por favor selecciona ambas fechas', 'warning');
        return;
    }
    
    try {
        const response = await fetch(
            `${API_BASE_URL}/reportes/ventas-por-fecha?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`,
            {
                headers: {
                    'Authorization': `Bearer ${currentUser.token}`
                }
            }
        );
        
        if (!response.ok) throw new Error('Error cargando ventas por fecha');
        
        ventasPorFecha = await response.json();
        
        // Actualizar gráfico
        updateSalesChart(ventasPorFecha);
        
        // Actualizar tabla de reportes
        updateReportsTable(ventasPorFecha);
        
        console.log('Ventas por fecha cargadas:', ventasPorFecha.length);
        
    } catch (error) {
        console.error('Error cargando datos de gráficos:', error);
    }
}

// Cargar top productos
async function loadTopProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/reportes/productos-top?limit=10`, {
            headers: {
                'Authorization': `Bearer ${currentUser.token}`
            }
        });
        
        if (!response.ok) throw new Error('Error cargando top productos');
        
        topProducts = await response.json();
        
        // Actualizar gráfico
        updateProductsChart(topProducts);
        
        // Actualizar tabla
        updateProductsTable(topProducts);
        
        console.log('Top productos cargados:', topProducts.length);
        
    } catch (error) {
        console.error('Error cargando top productos:', error);
    }
}

// Cargar top servicios
async function loadTopServices() {
    try {
        const response = await fetch(`${API_BASE_URL}/reportes/servicios-top?limit=10`, {
            headers: {
                'Authorization': `Bearer ${currentUser.token}`
            }
        });
        
        if (!response.ok) throw new Error('Error cargando top servicios');
        
        topServices = await response.json();
        
        // Actualizar gráfico
        updateServicesChart(topServices);
        
        // Actualizar tabla
        updateServicesTable(topServices);
        
        console.log('Top servicios cargados:', topServices.length);
        
    } catch (error) {
        console.error('Error cargando top servicios:', error);
    }
}

// Actualizar gráfico de ventas
function updateSalesChart(data) {
    const ctx = document.getElementById('sales-chart').getContext('2d');
    
    if (salesChart) {
        salesChart.destroy();
    }
    
    salesChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map(d => d.fecha),
            datasets: [{
                label: 'Ventas ($)',
                data: data.map(d => d.totalVentas),
                borderColor: '#42944C',
                backgroundColor: 'rgba(66, 148, 76, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return '$' + context.parsed.y.toFixed(2);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value;
                        }
                    }
                }
            }
        }
    });
}

// Actualizar gráfico de productos
function updateProductsChart(data) {
    const ctx = document.getElementById('products-chart').getContext('2d');
    
    if (productsChart) {
        productsChart.destroy();
    }
    
    productsChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.map(d => d.nombre),
            datasets: [{
                label: 'Cantidad Vendida',
                data: data.map(d => d.cantidadVendida),
                backgroundColor: '#42944C',
                borderRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// Actualizar gráfico de servicios
function updateServicesChart(data) {
    const ctx = document.getElementById('services-chart').getContext('2d');
    
    if (servicesChart) {
        servicesChart.destroy();
    }
    
    servicesChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.map(d => d.nombre),
            datasets: [{
                label: 'Cantidad Solicitada',
                data: data.map(d => d.cantidadVendida),
                backgroundColor: '#2196F3',
                borderRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// Actualizar tabla de productos
function updateProductsTable(data) {
    const tbody = document.querySelector('#products-table tbody');
    tbody.innerHTML = '';
    
    data.forEach((item, index) => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${item.nombre}</td>
            <td>${item.cantidadVendida}</td>
            <td>$${item.totalVentas.toFixed(2)}</td>
        `;
    });
}

// Actualizar tabla de servicios
function updateServicesTable(data) {
    const tbody = document.querySelector('#services-table tbody');
    tbody.innerHTML = '';
    
    data.forEach((item, index) => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${item.nombre}</td>
            <td>${item.cantidadVendida}</td>
            <td>$${item.totalVentas.toFixed(2)}</td>
        `;
    });
}

// Actualizar tabla de reportes
function updateReportsTable(data) {
    const tbody = document.querySelector('#reports-table tbody');
    tbody.innerHTML = '';
    
    data.forEach(item => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${item.fecha}</td>
            <td>${item.cantidadVentas}</td>
            <td>$${item.totalVentas.toFixed(2)}</td>
        `;
    });
}

// Mostrar sección
function showSection(section) {
    // Ocultar todas las secciones
    document.getElementById('dashboard-section').style.display = 'none';
    document.getElementById('products-section').style.display = 'none';
    document.getElementById('services-section').style.display = 'none';
    document.getElementById('reports-section').style.display = 'none';
    
    // Remover clase active de todos los items
    document.querySelectorAll('.menu-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Mostrar sección seleccionada
    if (section === 'dashboard') {
        document.getElementById('dashboard-section').style.display = 'block';
        document.querySelectorAll('.menu-item')[0].classList.add('active');
    } else if (section === 'products') {
        document.getElementById('products-section').style.display = 'block';
        document.querySelectorAll('.menu-item')[1].classList.add('active');
    } else if (section === 'services') {
        document.getElementById('services-section').style.display = 'block';
        document.querySelectorAll('.menu-item')[2].classList.add('active');
    } else if (section === 'reports') {
        document.getElementById('reports-section').style.display = 'block';
        document.querySelectorAll('.menu-item')[3].classList.add('active');
    }
}

// Exportar a Excel
async function exportExcel() {
    const fechaInicio = document.getElementById('fecha-inicio').value;
    const fechaFin = document.getElementById('fecha-fin').value;
    
    if (!fechaInicio || !fechaFin) {
        showNotification('Por favor selecciona ambas fechas', 'warning');
        return;
    }
    
    try {
        showLoading();
        
        const response = await fetch(
            `${API_BASE_URL}/reportes/export/excel?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`,
            {
                headers: {
                    'Authorization': `Bearer ${currentUser.token}`
                }
            }
        );
        
        if (!response.ok) throw new Error('Error exportando a Excel');
        
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Reporte_AppleGym_${fechaInicio}_${fechaFin}.xlsx`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        
        showNotification('Reporte Excel descargado exitosamente', 'success');
        
    } catch (error) {
        console.error('Error exportando a Excel:', error);
        showNotification('Error exportando a Excel: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

// Refrescar datos
function refreshData() {
    loadDashboardData();
    showNotification('Datos actualizados', 'success');
}

// Actualizar timestamp
function updateTimestamp() {
    const now = new Date();
    const time = now.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
    document.getElementById('last-update').textContent = time;
}

// Formatear fecha para input
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

console.log('Admin Dashboard cargado correctamente!');


