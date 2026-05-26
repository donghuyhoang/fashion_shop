$(document).ready(function() {
    $.ajaxSetup({
        beforeSend: function(xhr) {
            const token = localStorage.getItem("user_token");
            if (token) xhr.setRequestHeader("Authorization", "Bearer " + token);
        }
    });

    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    if (!productId) { window.location.href = "index.html"; return; }

    const API_URL = "/api/";
    let productDetails = [], sizesCache = [], colorsCache = [], baseProductInfo = {};

    // 1. Tải Dữ liệu Ngầm
    $.when(
        $.get(API_URL + "sizes").done(res => sizesCache = res),
        $.get(API_URL + "colors").done(res => colorsCache = res),
        $.get(API_URL + "products").done(res => { baseProductInfo = (res || []).find(p => p.id == productId || p.productId == productId) || {}; })
    ).always(function() {
        renderBaseInfo();
        loadProductImages(); // Gọi riêng API ảnh
        loadProductDetails();
        loadSuggestedProducts();
    });

    // 2. Render Text
    function renderBaseInfo() {
        $('#pdName').text(baseProductInfo.name || `Sản phẩm #${productId}`);
        $('#pdBrand').text(baseProductInfo.brandName || 'SNEAK PEAK');
        $('#pdPrice').text((baseProductInfo.price || 0).toLocaleString('vi-VN') + ' ₫');
        $('#pdDescription').html(baseProductInfo.description || 'Sản phẩm chính hãng với thiết kế hiện đại.');
    }

    // 3. Render Hình Ảnh Từ Database (Đã fix lỗi ít ảnh)
    function loadProductImages() {
		console.log(">>> Đang gọi API lấy ảnh cho sản phẩm ID:", productId);
        $.ajax({
            url: API_URL + "product-images/product/" + productId,
            type: "GET",
            success: function(images) {
				console.log(">>> Dữ liệu ảnh trả về từ Backend:", images);
                if (images && images.length > 0) {
                    images.sort((a, b) => {
                        const orderA = a.sort_order !== undefined ? a.sort_order : (a.sortOrder !== undefined ? a.sortOrder : 0);
                        const orderB = b.sort_order !== undefined ? b.sort_order : (b.sortOrder !== undefined ? b.sortOrder : 0);
                        return orderA - orderB;
                    });
                    
                    // Cập nhật logic lấy URL ảnh, xử lý nhiều dạng trả về của Jackson
                    let imgUrls = images.map(img => {
                        return img.image_url || img.imageUrl || img.image || 'https://via.placeholder.com/600x600?text=Lỗi+Load+Ảnh';
                    }).filter(url => url !== 'https://via.placeholder.com/600x600?text=Lỗi+Load+Ảnh');

                    if (imgUrls.length > 0) {
                        renderGallery(imgUrls);
                    } else {
                        fallbackImages();
                    }
                } else {
                    fallbackImages();
                }
            },
            error: function() { fallbackImages(); }
        });
    }

    // 3.1 Hàm chữa cháy nếu API Ảnh chưa sẵn sàng
    function fallbackImages() {
        let thumbStr = baseProductInfo.thumbnailUrl || baseProductInfo.thumb || baseProductInfo.thumbnailImgUrl;
        let imgUrls = [];
        if (thumbStr) {
            let sep = thumbStr.includes('|||') ? '|||' : ';';
            imgUrls = thumbStr.split(sep).map(u => u.trim()).filter(u => u !== "");
        }
        if(imgUrls.length === 0) imgUrls.push('https://via.placeholder.com/600x600?text=No+Image');
        renderGallery(imgUrls);
    }

    // 3.2 Vẽ HTML cho Ảnh lớn và Ảnh nhỏ
    function renderGallery(imgUrls) {
        let mainHtml = '';
        let thumbHtml = '';
        
        imgUrls.forEach((url, idx) => {
            let active = idx === 0 ? 'active' : '';
            mainHtml += `
                <div class="carousel-item ${active} w-100 h-100">
                    <div class="d-flex justify-content-center align-items-center h-100 p-4">
                        <img src="${url}" class="d-block" style="max-width: 95%; max-height: 95%; object-fit: contain;" alt="Sneaker">
                    </div>
                </div>
            `;
            
            let thumbActive = idx === 0 ? 'active border-success' : 'border-secondary';
            thumbHtml += `
                <img src="${url}" data-bs-target="#productCarousel" data-bs-slide-to="${idx}" 
                     class="img-thumbnail thumb-img ${thumbActive}" 
                     style="width: 80px; height: 80px; object-fit: cover; cursor: pointer; background-color: #1a1d24; border-width: 2px;">
            `;
        });
        
        $('#carouselInner').html(mainHtml);
        $('#carouselThumbnails').html(thumbHtml);
    }

    // Đổi CSS sáng/tối cho viền ảnh khi Slider di chuyển
    $('#productCarousel').on('slide.bs.carousel', function (e) {
        $('.thumb-img').removeClass('active border-success').addClass('border-secondary');
        $(`.thumb-img[data-bs-slide-to="${e.to}"]`).removeClass('border-secondary').addClass('active border-success');
    });
    // Cho phép click vào Thumbnail để đổi ảnh
    $(document).on('click', '.thumb-img', function() {
        const index = $(this).data('bs-slide-to');
        $('#productCarousel').carousel(index);
    });

    // 4. Render Màu và Size
    function loadProductDetails() {
        $.get(API_URL + "product-details/product/" + productId).done(function(details) {
            productDetails = details;
            if (!details || details.length === 0) return $('#pdStockStatus').html('<span class="text-danger">Sản phẩm hiện đang hết hàng.</span>');
            
            const colorMap = new Map();
            const sizeMap = new Map();
            
            details.forEach(d => {
                let cId = d.colorId || d.color_id || (d.color && d.color.id) || 0;
                let cName = d.colorName || d.color_name || d.color || (d.color && d.color.name);
                let sId = d.sizeId || d.size_id || (d.size && d.size.id) || 0;
                let sName = d.sizeName || d.size_name || d.sizeValue || d.size_value || d.size || (d.size && (d.size.name || d.size.value));

                if (!cName && cId !== 0) { const cached = colorsCache.find(c => c.id === cId); if (cached) cName = cached.name || cached.value; }
                if (!sName && sId !== 0) { const cached = sizesCache.find(s => s.id === sId); if (cached) sName = cached.name || cached.value || cached.size; }
                if (!cName) cName = cId === 0 ? "Mặc định" : cId;
                if (!sName) sName = sId === 0 ? "Freesize" : sId;

                if (!colorMap.has(cId)) colorMap.set(cId, { id: cId, name: cName });
                if (!sizeMap.has(sId)) sizeMap.set(sId, { id: sId, name: sName });
            });
            
            let htmlColor = ''; colorMap.forEach(color => htmlColor += `<div class="color-box" data-id="${color.id}">${color.name}</div>`); $('#pdColorOptions').html(htmlColor);
            let htmlSize = ''; sizeMap.forEach(size => htmlSize += `<div class="size-box" data-id="${size.id}">${size.name}</div>`); $('#pdSizeOptions').html(htmlSize);
        });
    }

    // 5. Xử lý Logic Chọn Màu / Size -> Hiện Nút Thêm Giỏ Hàng
    $(document).on('click', '.color-box', function() {
        $('.color-box').removeClass('active'); $(this).addClass('active'); checkStock();
    });
    $(document).on('click', '.size-box', function() {
        $('.size-box').removeClass('active'); $(this).addClass('active'); checkStock();
    });

    function checkStock() {
        const colorId = $('.color-box.active').data('id');
        const sizeId = $('.size-box.active').data('id');
        const btnAdd = $('#pdBtnAddToCart');
        
        if (colorId && sizeId) {
            const detail = productDetails.find(d => {
                let cId = d.colorId || d.color_id || (d.color && d.color.id) || 0;
                let sId = d.sizeId || d.size_id || (d.size && d.size.id) || 0;
                return cId == colorId && sId == sizeId;
            });

            if (detail) {
                const stock = detail.stockQuantity !== undefined ? detail.stockQuantity : detail.stock_quantity;
                if (stock > 0) {
                    $('#pdStockStatus').html(`<span class="text-success"><i class="fas fa-check-circle"></i> Còn ${stock} sản phẩm</span>`);
                    btnAdd.prop('disabled', false).data('detail-id', detail.id || detail.productDetailId || detail.product_detail_id);
                } else {
                    $('#pdStockStatus').html(`<span class="text-danger"><i class="fas fa-times-circle"></i> Đã hết hàng</span>`); btnAdd.prop('disabled', true);
                }
            } else { 
                $('#pdStockStatus').html(`<span class="text-warning"><i class="fas fa-exclamation-triangle"></i> Màu/Size này chưa có hàng</span>`); btnAdd.prop('disabled', true); 
            }
        }
    }

    // 6. Xử lý Gợi ý (Random)
    function loadSuggestedProducts() {
        $.get(API_URL + "products").done(function(products) {
            if (!products) return;
            const suggested = products.filter(p => p.id != productId).sort(() => 0.5 - Math.random()).slice(0, 4);
            if(suggested.length > 0) {
                let suggestHtml = "";
                suggested.forEach(p => {
                    let pImg = p.thumb || p.imageUrl || p.image || p.thumbnailImgUrl;
                    if (pImg && pImg.includes('|||')) pImg = pImg.split('|||')[0].trim();
                    suggestHtml += `
                        <div class="col-md-3 col-6 mb-4">
                            <div class="card h-100 product-card border-0 shadow-sm dark-card" style="background: #1a1d24; border: 1px solid #2a2d34 !important;">
                                <a href="product-detail.html?id=${p.id}" class="product-img text-decoration-none d-block p-3">
                                    <img src="${pImg || 'https://via.placeholder.com/200'}" class="img-fluid rounded" style="object-fit: contain; width: 100%; aspect-ratio: 1/1;">
                                </a>
                                <div class="card-body d-flex flex-column p-3">
                                    <a href="product-detail.html?id=${p.id}" class="text-decoration-none text-light">
                                        <h6 class="product-name text-truncate mb-1">${p.name}</h6>
                                    </a>
                                    <h6 class="mt-auto pt-2 text-warning mb-0">${p.price ? p.price.toLocaleString('vi-VN') : 0} ₫</h6>
                                </div>
                            </div>
                        </div>`;
                });
                $('#suggestedProductsGrid').html(suggestHtml);
            }
        });
    }

    // 7. Thêm Giỏ hàng
    $('#pdBtnAddToCart').click(function() {
        const userId = localStorage.getItem("user_id");
        if (!userId) return alert("Vui lòng đăng nhập để mua hàng!"), window.location.href = "login.html";
        
        const $btn = $(this), originalText = $btn.html();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> ĐANG XỬ LÝ...');
        $.ajax({
            url: API_URL + "cart/add", type: "POST", contentType: "application/json",
            data: JSON.stringify({ userId: parseInt(userId), productDetailId: parseInt($(this).data('detail-id')), quantity: 1 }),
            success: function() { 
                if(window.updateCartBadge) window.updateCartBadge(); 
                if(window.showToastSuccess) window.showToastSuccess($('#pdName').text() + " đã thêm vào giỏ!"); 
                else alert("Thêm giỏ hàng thành công!");
            },
            error: function(xhr) { alert("Lỗi khi thêm: " + xhr.responseText); },
            complete: function() { $btn.prop('disabled', false).html(originalText); }
        });
    });
});