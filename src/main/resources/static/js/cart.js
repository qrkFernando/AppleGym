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
    if (!currentUser) {
        closeModal('cart-modal');
        showLogin();
        showNotification('Debes iniciar sesion para continuar con la compra', 'error');
        return;
    }
    
    if (cart.length === 0) {
        showNotification('Tu carrito esta vacio', 'error');
        return;
    }
    
    const total = cart.reduce((sum, item) => sum + (item.precio * item.quantity), 0);
    showNotification(`Procesando compra por $${total.toFixed(2)}. Funcionalidad de pago en desarrollo.`, 'info');
}

console.log('AppleGym Cart cargado correctamente!');