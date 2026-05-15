$(document).ready(function() {
    const API_URL = "/api/";
    const userId = localStorage.getItem("user_id");

    // Kiểm tra đăng nhập
    if (!userId) {
        $('#cartItemsContainer').html(`
            <div class="text-center py-5">
                <i class="fas fa-lock fa-4x text-muted mb-4"></i>
                <h3 class="fw-bold mb-3">VAULT IS LOCKED</h3>
                <p class="text-muted mb-4">Vui lòng đăng nhập để xem giỏ hàng của bạn.</p>
                <a href="login.html" class="btn btn-outline-light px-5 py-2">ĐĂNG NHẬP NGAY</a>
            </div>
        `);
        return;
    }

    // Gọi API lấy danh sách Giỏ hàng
    loadCartItems();

    function loadCartItems() {
        $.ajax({
            url: API_URL + "cart/" + userId,
            type: "GET",
            success: function(cartItems) {
                if (!cartItems || cartItems.length === 0) {
                    $('#cartItemsContainer').html(`
                        <div class="text-center py-5 border border-secondary rounded-4" style="border-style: dashed !important;">
                            <h3 class="fw-bold text-muted mb-3">YOUR VAULT IS EMPTY</h3>
                            <a href="index.html" class="text-decoration-underline" style="color: #818cf8;">Go hunt some sneakers</a>
                        </div>
                    `);
                    $('#totalItemsCount').text(0);
                    $('#subtotalPrice').text('0₫');
                    $('#finalTotalPrice').text('0₫');
                    return;
                }

                let html = "";
                let subtotal = 0;
                let totalQuantity = 0;

                const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";

                cartItems.forEach(item => {
                    let imgSrc = item.thumbnail || item.thumb || item.thumbnailImgUrl || item.image;
                    if (!imgSrc || imgSrc.trim() === "") imgSrc = fallbackImg;
                    else if (imgSrc.includes(';') && !imgSrc.startsWith('data:image')) imgSrc = imgSrc.split(';')[0].trim();

                    const itemPrice = item.price || 0;
                    const itemTotal = itemPrice * item.quantity;
                    
                    subtotal += itemTotal;
                    totalQuantity += item.quantity;

                    html += `
                        <div class="cart-item-card">
                            <div class="cart-img-box">
                                <img src="${imgSrc}" alt="${item.productName}" onerror="this.onerror=null; this.src='${fallbackImg}';">
                            </div>
                            <div class="flex-grow-1 d-flex flex-column justify-content-between">
                                
                                <div class="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h2 class="item-title">${item.productName}</h2>
                                        <div class="item-color">${item.colorName || 'Standard Edition'}</div>
                                    </div>
                                    <div class="item-price">${itemPrice.toLocaleString('vi-VN')}₫</div>
                                </div>

                                <div class="d-flex justify-content-between align-items-end mt-4">
                                    <div class="d-flex gap-5">
                                        <div>
                                            <span class="attr-label">SIZE</span>
                                            <span class="attr-value">EU ${item.sizeName || '40'}</span>
                                        </div>
                                        <div>
                                            <span class="attr-label">QUANTITY</span>
                                            <div class="attr-value">
                                                <button class="qty-btn" onclick="updateQty(${item.productDetailId}, -1, ${item.quantity})"><i class="fas fa-minus"></i></button>
                                                <span style="min-width: 25px; text-align: center;">${item.quantity < 10 ? '0'+item.quantity : item.quantity}</span>
                                                <button class="qty-btn" onclick="updateQty(${item.productDetailId}, 1, ${item.quantity})"><i class="fas fa-plus"></i></button>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <button class="btn-remove" onclick="removeCartItem(${item.productDetailId})">
                                        <i class="fas fa-trash-alt me-1"></i> REMOVE SKU
                                    </button>
                                </div>

                            </div>
                        </div>
                    `;
                });

                $('#cartItemsContainer').html(html);
                $('#totalItemsCount').text(cartItems.length); 
                $('#subtotalPrice').text(subtotal.toLocaleString('vi-VN') + '₫');
                const finalTotal = subtotal + 35000; 
                $('#finalTotalPrice').text(finalTotal.toLocaleString('vi-VN') + '₫');
            },
            error: function() {
                $('#cartItemsContainer').html('<div class="text-danger">Lỗi kết nối đến máy chủ! Không thể tải giỏ hàng.</div>');
            }
        });
    }

    window.removeCartItem = function(productDetailId) {
        if(confirm("Bạn có chắc chắn muốn bỏ sản phẩm này khỏi Vault?")) {
            $.ajax({
                url: API_URL + "cart/remove?userId=" + userId + "&productDetailId=" + productDetailId, 
                type: "DELETE",
                success: function() {
                    loadCartItems(); 
                    if (typeof updateCartBadge === "function") updateCartBadge(); 
                },
                error: function() {
                    alert("Tính năng đang chờ Backend hoàn thiện API (DELETE /api/cart/remove)!");
                }
            });
        }
    }

    window.updateQty = function(productDetailId, change, currentQty) {
        if (currentQty <= 1 && change === -1) {
            removeCartItem(productDetailId);
            return;
        }

        const payload = {
            userId: parseInt(userId),
            productDetailId: parseInt(productDetailId),
            quantity: change
        };

        $.ajax({
            url: API_URL + "cart/add", 
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: function() {
                loadCartItems(); 
                if (typeof updateCartBadge === "function") updateCartBadge(); 
            },
            error: function() {
                alert("Có lỗi xảy ra khi cập nhật số lượng!");
            }
        });
    }

    $(document).on('click', '.btn-checkout', function() {
        $('#checkoutModal').modal('show');
    });

    $(document).on('click', '#btnConfirmCheckout', function() {
        const name = $('#receiverName').val().trim();
        const phone = $('#receiverPhone').val().trim();
        const address = $('#shippingAddress').val().trim();
        const paymentMethod = $('#paymentMethod').val();

        if(!name || !phone || !address) {
            alert("Vui lòng điền đầy đủ thông tin nhận hàng!");
            return;
        }

        const $btn = $(this);
        const originalHtml = $btn.html();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> PROCESSING...');

        const payload = {
            userId: parseInt(userId),
            receiverName: name,
            receiverPhone: phone,
            shippingAddress: address,
            paymentMethod: paymentMethod
        };

        $.ajax({
            url: API_URL + "orders/checkout",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: function(orderId) {
                window.location.href = "success.html?orderId=" + orderId + "&method=" + paymentMethod;
            },
            error: function(xhr) {
                alert("Lỗi đặt hàng: " + xhr.responseText);
                $btn.prop('disabled', false).html(originalHtml);
            }
        });
    });
});