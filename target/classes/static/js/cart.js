// AppleGym - Shopping Cart Functions

console.log('AppleGym Cart cargando...');

// Add product to cart
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
            idProducto: product.tipo === 'producto' ? product.id : null,
            idServicio: product.tipo === 'servicio' ? product.id : null,
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
    
    // Actualizar displays si están visibles
    if (typeof renderCatalog === 'function') renderCatalog();
    if (typeof renderProducts === 'function') renderProducts();
    if (typeof renderServices === 'function') renderServices();
    
    showNotification(`${product.nombre} agregado al carrito`, 'success');
    console.log(`Producto agregado:`, product.nombre);
}

// Remove product from cart
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
        
        // Actualizar displays si están visibles
        if (typeof renderCatalog === 'function') renderCatalog();
        if (typeof renderProducts === 'function') renderProducts();
        if (typeof renderServices === 'function') renderServices();
        
        showNotification(`${item.nombre} eliminado del carrito`, 'success');
    }
}

// Update cart item quantity
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
    
    // Actualizar displays si están visibles
    if (typeof renderCatalog === 'function') renderCatalog();
    if (typeof renderProducts === 'function') renderProducts();
    if (typeof renderServices === 'function') renderServices();
}

// Toggle cart modal
function toggleCart() {
    renderCart();
    showModal('cart-modal');
}

// Render cart contents
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

// Continue shopping
function continueShopping() {
    closeModal('cart-modal');
    // Redirect to appropriate page based on current page
    const currentPage = window.location.pathname;
    
    if (currentPage.includes('index')) {
        const catalogSection = document.getElementById('catalog');
        if (catalogSection) {
            catalogSection.scrollIntoView({ behavior: 'smooth' });
        } else {
            window.location.href = 'catalogo.html';
        }
    } else if (!currentPage.includes('catalogo') && !currentPage.includes('productos') && !currentPage.includes('servicios')) {
        window.location.href = 'catalogo.html';
    }
}

// Proceed to checkout
function proceedToCheckout() {
    // Validación crítica: el usuario DEBE estar logueado
    if (!currentUser) {
        closeModal('cart-modal');
        showNotification('Debes iniciar sesión para proceder con la compra', 'error');
        setTimeout(() => {
            showLogin();
        }, 500);
        return;
    }
    
    if (cart.length === 0) {
        showNotification('Tu carrito está vacío', 'error');
        return;
    }
    
    // Mostrar modal de selección de método de pago
    closeModal('cart-modal');
    showPaymentModal();
}

// Mostrar modal de pago
function showPaymentModal() {
    const modal = document.createElement('div');
    modal.id = 'payment-modal';
    modal.className = 'modal';
    modal.style.display = 'block';
    
    const total = cart.reduce((sum, item) => sum + (item.precio * item.quantity), 0);
    
    modal.innerHTML = `
        <div class="modal-content" style="max-width: 500px;">
            <span class="close" onclick="closePaymentModal()">&times;</span>
            <h2>Proceder al Pago</h2>
            <div style="margin: 1.5rem 0;">
                <h3 style="margin-bottom: 1rem;">Total a Pagar: $${total.toFixed(2)}</h3>
                <label style="display: block; margin-bottom: 0.5rem; font-weight: 600;">Selecciona el método de pago:</label>
                <select id="payment-method" onchange="toggleCardFields()" style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 4px; font-size: 1rem;">
                    <option value="">-- Seleccionar --</option>
                    <option value="TARJETA_CREDITO">💳 Tarjeta de Crédito</option>
                    <option value="TARJETA_DEBITO">💳 Tarjeta de Débito</option>
                </select>
            </div>
            
            <div id="card-details" style="display: none; margin: 1.5rem 0;">
                <div style="margin-bottom: 1rem;">
                    <label style="display: block; margin-bottom: 0.5rem;">Nombre del Titular: *</label>
                    <input type="text" id="card-holder" placeholder="Ej: Juan Pérez" 
                           style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                
                <div style="margin-bottom: 1rem;">
                    <label style="display: block; margin-bottom: 0.5rem;">Número de Tarjeta: *</label>
                    <input type="text" id="card-number" placeholder="1234 5678 9012 3456" maxlength="19"
                           oninput="formatCardNumber(this)"
                           style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                
                <div style="display: flex; gap: 1rem;">
                    <div style="flex: 1;">
                        <label style="display: block; margin-bottom: 0.5rem;">Fecha Venc. (MM/AA): *</label>
                        <input type="text" id="card-expiry" placeholder="MM/AA" maxlength="5"
                               oninput="formatExpiry(this)"
                               style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 4px;">
                    </div>
                    <div style="flex: 1;">
                        <label style="display: block; margin-bottom: 0.5rem;">CVV: *</label>
                        <input type="text" id="card-cvv" placeholder="123" maxlength="4"
                               oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                               style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 4px;">
                    </div>
                </div>
            </div>
            
            <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                <button class="btn-secondary" onclick="closePaymentModal()" style="flex: 1;">Cancelar</button>
                <button class="btn-primary" onclick="confirmPayment()" style="flex: 1;">Confirmar Pago</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
}

// Cerrar modal de pago
function closePaymentModal() {
    const modal = document.getElementById('payment-modal');
    if (modal) {
        modal.remove();
    }
}

// Mostrar/ocultar campos de tarjeta
function toggleCardFields() {
    const paymentMethod = document.getElementById('payment-method').value;
    const cardDetails = document.getElementById('card-details');
    
    if (paymentMethod === 'TARJETA_CREDITO' || paymentMethod === 'TARJETA_DEBITO') {
        cardDetails.style.display = 'block';
    } else {
        cardDetails.style.display = 'none';
    }
}

// Formatear número de tarjeta
function formatCardNumber(input) {
    let value = input.value.replace(/\s/g, '').replace(/[^0-9]/g, '');
    let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
    input.value = formattedValue;
}

// Formatear fecha de vencimiento
function formatExpiry(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.slice(0, 2) + '/' + value.slice(2, 4);
    }
    input.value = value;
}

// Validar datos de tarjeta
function validateCardData() {
    const holder = document.getElementById('card-holder')?.value.trim();
    const number = document.getElementById('card-number')?.value.replace(/\s/g, '');
    const expiry = document.getElementById('card-expiry')?.value;
    const cvv = document.getElementById('card-cvv')?.value;
    
    if (!holder || holder.length < 3) {
        showNotification('Por favor ingresa el nombre del titular', 'error');
        return false;
    }
    
    if (!number || number.length < 13) {
        showNotification('Por favor ingresa un número de tarjeta válido', 'error');
        return false;
    }
    
    if (!expiry || expiry.length !== 5) {
        showNotification('Por favor ingresa una fecha de vencimiento válida (MM/AA)', 'error');
        return false;
    }
    
    // Validar formato de fecha
    const [month, year] = expiry.split('/');
    const monthNum = parseInt(month);
    const yearNum = parseInt('20' + year);
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth() + 1;
    
    if (monthNum < 1 || monthNum > 12) {
        showNotification('Mes inválido. Debe ser entre 01 y 12', 'error');
        return false;
    }
    
    if (yearNum < currentYear || (yearNum === currentYear && monthNum < currentMonth)) {
        showNotification('La tarjeta está vencida', 'error');
        return false;
    }
    
    if (!cvv || cvv.length < 3) {
        showNotification('Por favor ingresa un CVV válido', 'error');
        return false;
    }
    
    return true;
}

// Confirmar pago y procesar venta
async function confirmPayment() {
    const metodoPago = document.getElementById('payment-method').value;
    
    if (!metodoPago) {
        showNotification('Por favor selecciona un método de pago', 'error');
        return;
    }
    
    // Validar datos de tarjeta si los campos están visibles
    const cardDetails = document.getElementById('card-details');
    if (cardDetails && cardDetails.style.display !== 'none') {
        if (!validateCardData()) {
            return;
        }
    }
    
    // Verificar autenticación
    if (!currentUser || !currentUser.token) {
        showNotification('Error de autenticación. Por favor inicia sesión nuevamente.', 'error');
        closePaymentModal();
        showLogin();
        return;
    }
    
    // Verificar si el token está expirado
    if (isTokenExpired(currentUser.token)) {
        console.log('Token expirado detectado');
        closePaymentModal();
        logout();
        showNotification('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'error');
        showLogin();
        return;
    }
    
    console.log('Procesando pago con usuario:', currentUser);
    console.log('Token disponible:', currentUser.token ? 'Sí' : 'No');
    
    try {
        showLoading();
        
        // PASO 1: Primero sincronizar el carrito con el backend
        console.log('Paso 1: Sincronizando carrito...');
        await sincronizarCarritoConBackend();
        
        // PASO 2: Procesar la venta
        console.log('Paso 2: Procesando venta...');
        const response = await fetch(`${API_BASE_URL}/ventas/procesar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            },
            body: JSON.stringify({
                metodoPago: metodoPago
            })
        });
        
        const data = await response.json();
        console.log('Respuesta del servidor:', data);
        
        if (!response.ok) {
            // Si el error es por token expirado, cerrar sesión
            if (data.error && data.error.includes('JWT expired')) {
                hideLoading();
                closePaymentModal();
                logout();
                showNotification('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'error');
                showLogin();
                return;
            }
            throw new Error(data.error || 'Error al procesar la venta');
        }
        
        hideLoading();
        closePaymentModal();
        
        // Limpiar carrito
        cart = [];
        updateCartCount();
        saveCartToStorage();
        
        showNotification('¡Compra realizada exitosamente!', 'success');
        
        // Descargar comprobante PDF automáticamente
        await descargarComprobantePDF(data.venta.idVenta);
        
        // Mostrar modal de confirmación
        mostrarConfirmacionCompra(data.venta);
        
    } catch (error) {
        hideLoading();
        console.error('Error procesando pago:', error);
        showNotification('Error: ' + error.message, 'error');
    }
}

// Sincronizar carrito local con backend
async function sincronizarCarritoConBackend() {
    if (!currentUser || !currentUser.token) {
        throw new Error('Usuario no autenticado');
    }
    
    // Verificar si el token está expirado
    if (isTokenExpired(currentUser.token)) {
        console.log('Token expirado, cerrando sesión...');
        logout();
        showNotification('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'error');
        showLogin();
        throw new Error('Token expirado');
    }
    
    if (cart.length === 0) {
        throw new Error('El carrito está vacío');
    }
    
    console.log('Sincronizando', cart.length, 'items del carrito...');
    
    // Agregar cada item del carrito local al backend
    for (const item of cart) {
        try {
            // Determinar el tipo y el ID correcto
            let tipo = item.tipo || (item.idProducto ? 'producto' : 'servicio');
            
            // Normalizar el tipo (convertir plural a singular para el backend)
            if (tipo === 'productos') tipo = 'producto';
            if (tipo === 'servicios') tipo = 'servicio';
            
            const itemId = item.idProducto || item.idServicio || item.id;
            
            console.log('Enviando item al backend:', {
                item: item,
                tipoOriginal: item.tipo,
                tipoNormalizado: tipo,
                itemId: itemId,
                cantidad: item.quantity
            });
            
            const requestData = {
                productoId: itemId,
                tipo: tipo,
                cantidad: item.quantity
            };
            
            console.log('Request data:', JSON.stringify(requestData));
            
            const response = await fetch(`${API_BASE_URL}/carrito/agregar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${currentUser.token}`
                },
                body: JSON.stringify(requestData)
            });
            
            if (!response.ok) {
                const error = await response.json();
                console.error('Error agregando item al carrito:', error);
                
                // Si el error es por token expirado, cerrar sesión
                if (error.error && error.error.includes('JWT expired')) {
                    logout();
                    showNotification('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'error');
                    showLogin();
                    throw new Error('Token expirado');
                }
            } else {
                console.log('Item agregado al carrito backend:', item.nombre);
            }
        } catch (error) {
            console.error('Error en sincronización de item:', error);
            throw error;
        }
    }
    
    console.log('Carrito sincronizado con backend');
}

// Descargar comprobante PDF
async function descargarComprobantePDF(ventaId) {
    try {
        const response = await fetch(`${API_BASE_URL}/ventas/comprobante/${ventaId}/pdf`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${currentUser.token}`
            }
        });
        
        if (!response.ok) {
            throw new Error('Error al descargar el comprobante');
        }
        
        // Crear blob y descargar
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Comprobante_Venta_${ventaId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
        
        showNotification('Comprobante descargado exitosamente', 'success');
        
    } catch (error) {
        console.error('Error descargando PDF:', error);
        showNotification('Advertencia: No se pudo descargar el comprobante automáticamente', 'warning');
    }
}

// Mostrar confirmación de compra
function mostrarConfirmacionCompra(venta) {
    const modal = document.createElement('div');
    modal.id = 'confirmation-modal';
    modal.className = 'modal';
    modal.style.display = 'block';
    
    modal.innerHTML = `
        <div class="modal-content" style="max-width: 600px; text-align: center;">
            <div style="color: #42944C; font-size: 4rem; margin-bottom: 1rem;">
                <i class="fas fa-check-circle"></i>
            </div>
            <h2>¡Compra Realizada con Éxito!</h2>
            <p style="margin: 1rem 0;">Tu pedido ha sido procesado correctamente.</p>
            <div style="background: #f5f5f5; padding: 1rem; border-radius: 8px; margin: 1.5rem 0;">
                <p><strong>Número de Venta:</strong> ${venta.idVenta || venta.numeroVenta || 'N/A'}</p>
                <p><strong>Total:</strong> $${(venta.total || 0).toFixed(2)}</p>
                <p><strong>Estado:</strong> ${venta.estado || 'PROCESADO'}</p>
            </div>
            <p style="font-size: 0.9rem; color: #666;">
                El comprobante se ha descargado automáticamente.<br>
                Si no se descargó, puedes descargarlo nuevamente.
            </p>
            <div style="display: flex; gap: 1rem; margin-top: 2rem; justify-content: center;">
                <button class="btn-secondary" onclick="descargarComprobantePDF(${venta.idVenta})">
                    <i class="fas fa-download"></i> Descargar Comprobante
                </button>
                <button class="btn-primary" onclick="closeConfirmationModal()">
                    Continuar
                </button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
}

// Cerrar modal de confirmación
function closeConfirmationModal() {
    const modal = document.getElementById('confirmation-modal');
    if (modal) {
        modal.remove();
    }
    // Redirigir a la página principal o catálogo
    window.location.href = 'catalogo.html';
}

// Función para decodificar el token JWT y verificar expiración
function isTokenExpired(token) {
    if (!token) return true;
    
    try {
        // Decodificar el payload del JWT (parte del medio)
        const payload = JSON.parse(atob(token.split('.')[1]));
        
        // Verificar si tiene fecha de expiración
        if (!payload.exp) return false;
        
        // Comparar con la fecha actual (exp está en segundos, Date.now() en milisegundos)
        const now = Date.now() / 1000;
        return payload.exp < now;
    } catch (error) {
        console.error('Error verificando expiración del token:', error);
        return true;
    }
}

// Verificar el token al cargar la página
function checkTokenOnLoad() {
    if (currentUser && currentUser.token) {
        if (isTokenExpired(currentUser.token)) {
            console.log('Token expirado detectado al cargar la página');
            logout();
            showNotification('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'warning');
        }
    }
}

// Verificar token al cargar
if (typeof currentUser !== 'undefined') {
    checkTokenOnLoad();
}

console.log('AppleGym Cart cargado correctamente!');