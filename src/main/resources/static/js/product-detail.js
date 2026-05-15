$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    if (!productId) { window.location.href = "index.html"; return; }

    const API_URL = "/api/";
    let productDetails = [], sizesCache = [], colorsCache = [], baseProductInfo = {};

    // Tải ngầm mọi dữ liệu
    $.when(
        $.get(API_URL + "sizes").done(res => sizesCache = res),
        $.get(API_URL + "colors").done(res => colorsCache = res),
        $.get(API_URL + "products").done(res => { baseProductInfo = (res || []).find(p => p.id == productId) || {}; })
    ).always(function() {
        renderBaseInfo();
        loadProductDetails();
        loadSuggestedProducts();
    });

    function renderBaseInfo() {
        $('#pdName').text(baseProductInfo.name || `Sản phẩm #${productId}`);
        $('#pdBrand').text(baseProductInfo.brandName || 'SNEAK PEAK');
        $('#pdPrice').text((baseProductInfo.price || 0).toLocaleString('vi-VN') + ' ₫');
        $('#pdDescription').html(baseProductInfo.description || 'Sản phẩm chính hãng với thiết kế hiện đại.');
        const thumbStr = baseProductInfo.thumbnailUrl || baseProductInfo.thumb || baseProductInfo.thumbnailImgUrl || baseProductInfo.thumbnail_img_url;
        if(thumbStr) {
            let separator = thumbStr.includes('|||') ? '|||' : ';';
            let firstImg = thumbStr;
            if (firstImg.includes(separator) && !firstImg.startsWith('data:image')) firstImg = firstImg.split(separator)[0].trim();
            $('#pdMainImage').attr('src', firstImg);
        }
    }

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

                if (!cName && cId !== 0) {
                    const cached = colorsCache.find(c => c.id === cId);
                    if (cached) cName = cached.name || cached.value;
                }
                if (!sName && sId !== 0) {
                    const cached = sizesCache.find(s => s.id === sId);
                    if (cached) sName = cached.name || cached.value || cached.size;
                }

                if (!cName) cName = cId === 0 ? "Mặc định" : cId;
                if (!sName) sName = sId === 0 ? "Freesize" : sId;

                const finalColorKey = cId !== 0 ? cId : cName;
                const finalSizeKey = sId !== 0 ? sId : sName;

                if (!colorMap.has(finalColorKey)) colorMap.set(finalColorKey, { id: finalColorKey, name: cName });
                if (!sizeMap.has(finalSizeKey)) sizeMap.set(finalSizeKey, { id: finalSizeKey, name: sName });
            });
            
            let htmlColor = ''; colorMap.forEach(color => htmlColor += `<button class="variant-btn color-btn" data-id="${color.id}">${color.name}</button>`); $('#pdColorOptions').html(htmlColor);
            let htmlSize = ''; sizeMap.forEach(size => htmlSize += `<button class="variant-btn size-btn" data-id="${size.id}">${size.name}</button>`); $('#pdSizeOptions').html(htmlSize);

            // Tự động tạo Carousel Slider từ chuỗi ảnh
            const thumbStr = baseProductInfo.thumbnailUrl || baseProductInfo.thumb || baseProductInfo.thumbnailImgUrl || baseProductInfo.thumbnail_img_url;
            const mainThumb = thumbStr || fallbackImg;
            let addedImgs = [];
            
            let mainThumbsArray = [mainThumb];
            if (mainThumb && !mainThumb.startsWith('data:image')) {
                let separator = mainThumb.includes('|||') ? '|||' : ';';
                if (mainThumb.includes(separator)) mainThumbsArray = mainThumb.split(separator).map(u => u.trim()).filter(u => u !== "");
            }
            
            mainThumbsArray.forEach(img => {
                if (!addedImgs.includes(img)) addedImgs.push(img);
            });
            
            details.forEach(d => {
                let imgStr = d.thumbnailUrl || d.thumbnailImgUrl || d.thumbnail_img_url || d.thumb;
                if (imgStr && imgStr.trim() !== "") {
                    let imgUrls = [imgStr];
                    if (!imgStr.startsWith('data:image')) {
                        let separator = imgStr.includes('|||') ? '|||' : ';';
                        if (imgStr.includes(separator)) imgUrls = imgStr.split(separator).map(u => u.trim()).filter(u => u !== "");
                    }
                    imgUrls.forEach(img => {
                        if (!addedImgs.includes(img)) addedImgs.push(img);
                    });
                }
            });
            
            let carouselInnerHtml = '';
            let carouselThumbsHtml = '';
            addedImgs.forEach((img, index) => {
                let activeClass = index === 0 ? "active" : "";
                carouselInnerHtml += `
                    <div class="carousel-item ${activeClass} w-100 h-100 d-flex justify-content-center align-items-center p-4">
                        <img src="${img}" class="d-block" style="max-width: 90%; max-height: 90%; object-fit: contain; filter: drop-shadow(0 20px 30px rgba(0,0,0,0.6)); transition: transform 0.3s;" alt="Product Image" onerror="this.onerror=null; this.src='${fallbackImg}';">
                    </div>
                `;
                carouselThumbsHtml += `
                    <img src="${img}" data-bs-target="#productCarousel" data-bs-slide-to="${index}" class="img-thumbnail bg-dark border-secondary cursor-pointer ${activeClass}" style="width: 80px; height: 80px; object-fit: cover; cursor: pointer; opacity: ${index === 0 ? '1' : '0.5'}; transition: 0.3s;" onclick="$(this).siblings().css('opacity', '0.5'); $(this).css('opacity', '1');" onerror="this.onerror=null; this.src='${fallbackImg}';">
                `;
            });
            
            if(addedImgs.length > 0) {
                $('#carouselInner').html(carouselInnerHtml);
                $('#carouselThumbnails').html(carouselThumbsHtml);
            }
            
            $('#productCarousel').on('slide.bs.carousel', function (e) {
                $('#carouselThumbnails img').css('opacity', '0.5');
                $(`#carouselThumbnails img[data-bs-slide-to="${e.to}"]`).css('opacity', '1');
            });
        });
    }

    // Hàm xử lý tải Sản phẩm gợi ý ngẫu nhiên
    const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";

    function loadSuggestedProducts() {
        $.get(API_URL + "products").done(function(products) {
            if (!products || products.length === 0) {
                $('#suggestedProductsGrid').html('<div class="col-12 text-muted">Không có sản phẩm gợi ý nào.</div>');
                return;
            }
            // Lấy 4 sản phẩm ngẫu nhiên khác với sản phẩm hiện tại
            const suggested = products.filter(p => p.id != productId).sort(() => 0.5 - Math.random()).slice(0, 4);
            if(suggested.length > 0) {
                let suggestHtml = "";
                suggested.forEach(p => {
                    let pImg = p.thumb || p.imageUrl || p.image || p.thumbnailImgUrl || p.thumbnail_img_url;
                    if (!pImg || pImg.trim() === "") pImg = fallbackImg;
                    let pSep = pImg.includes('|||') ? '|||' : ';';
                    if (pImg.includes(pSep) && !pImg.startsWith('data:image')) pImg = pImg.split(pSep)[0].trim();

                    suggestHtml += `
                        <div class="col-md-3 col-6 mb-4">
                            <div class="card h-100 product-card border-0 shadow-sm dark-card" style="background: #1a1d24; border: 1px solid #2a2d34 !important;">
                                <a href="product-detail.html?id=${p.id}" class="product-img text-decoration-none d-block p-3">
                                    <img src="${pImg}" alt="${p.name}" class="img-fluid rounded" style="object-fit: contain; width: 100%; aspect-ratio: 1/1;" onerror="this.onerror=null; this.src='${fallbackImg}';">
                                </a>
                                <div class="card-body d-flex flex-column p-3">
                                    <a href="product-detail.html?id=${p.id}" class="text-decoration-none text-light">
                                        <h6 class="product-name text-truncate mb-1" style="font-size: 0.9rem;">${p.name}</h6>
                                    </a>
                                    <h6 class="mt-auto pt-2 text-warning mb-0" style="font-size: 1rem;">${p.price ? p.price.toLocaleString('vi-VN') : 0} ₫</h6>
                                </div>
                            </div>
                        </div>`;
                });
                $('#suggestedProductsGrid').html(suggestHtml);
            }
        });
    }

    $(document).on('click', '.color-btn, .size-btn', function() {
        $(this).hasClass('color-btn') ? $('.color-btn').removeClass('active') : $('.size-btn').removeClass('active');
        $(this).addClass('active');
        
        const colorId = $('.color-btn.active').data('id');
        const sizeId = $('.size-btn.active').data('id');
        const btnAdd = $('#pdBtnAddToCart');
        
        if (colorId && sizeId) {
            const detail = productDetails.find(d => {
                let cId = d.colorId || d.color_id || (d.color && d.color.id) || 0;
                let cName = d.colorName || d.color_name || d.color || (d.color && d.color.name);
                let finalColorKey = cId !== 0 ? cId : (cName || "Mặc định");

                let sId = d.sizeId || d.size_id || (d.size && d.size.id) || 0;
                let sName = d.sizeName || d.size_name || d.sizeValue || d.size_value || d.size || (d.size && (d.size.name || d.size.value));
                let finalSizeKey = sId !== 0 ? sId : (sName || "Freesize");

                return finalColorKey == colorId && finalSizeKey == sizeId;
            });
            if (detail) {
                const stock = detail.stockQuantity !== undefined ? detail.stockQuantity : detail.stock_quantity;
                if (detail.price) $('#pdPrice').text(detail.price.toLocaleString('vi-VN') + ' ₫');
                
                // Cập nhật ảnh chính và Highlight ảnh dưới Gallery
                const varImgStr = detail.thumbnailUrl || detail.thumb || detail.thumbnailImgUrl || detail.thumbnail_img_url;
                if (varImgStr && varImgStr.trim() !== "") {
                    let separator = varImgStr.includes('|||') ? '|||' : ';';
                    let firstImg = varImgStr;
                    if (firstImg.includes(separator) && !firstImg.startsWith('data:image')) firstImg = firstImg.split(separator)[0].trim();
                    
                    const targetIndex = $('#carouselThumbnails img').filter(function() { return $(this).attr('src') === firstImg; }).data('bs-slide-to');
                    if (targetIndex !== undefined) {
                        $('#productCarousel').carousel(targetIndex);
                        $('#carouselThumbnails img').css('opacity', '0.5');
                        $(`#carouselThumbnails img[data-bs-slide-to="${targetIndex}"]`).css('opacity', '1');
                    }
                }

                if (stock > 0) { $('#pdStockStatus').html(`<span class="text-success"><i class="fas fa-check-circle"></i> Còn ${stock} sản phẩm</span>`); btnAdd.prop('disabled', false).data('detail-id', detail.id || detail.productDetailId || detail.product_detail_id); }
                else { $('#pdStockStatus').html(`<span class="text-danger"><i class="fas fa-times-circle"></i> Đã hết hàng</span>`); btnAdd.prop('disabled', true); }
            } else { $('#pdStockStatus').html(`<span class="text-warning"><i class="fas fa-exclamation-triangle"></i> Phân loại không tồn tại</span>`); btnAdd.prop('disabled', true); }
        }
    });

    $('#pdBtnAddToCart').click(function() {
        const userId = localStorage.getItem("user_id");
        if (!userId) return alert("Vui lòng đăng nhập để mua hàng!"), window.location.href = "login.html";
        const $btn = $(this), originalText = $btn.html();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> ĐANG XỬ LÝ...');
        $.ajax({
            url: API_URL + "cart/add", type: "POST", contentType: "application/json",
            data: JSON.stringify({ userId: parseInt(userId), productDetailId: parseInt($(this).data('detail-id')), quantity: 1 }),
            success: function() { if(window.updateCartBadge) window.updateCartBadge(); if(window.showToastSuccess) window.showToastSuccess($('#pdName').text() + ` (${$('.color-btn.active').text()} - ${$('.size-btn.active').text()})`); },
            error: function(xhr) { alert("Lỗi khi thêm: " + xhr.responseText); },
            complete: function() { $btn.prop('disabled', false).html(originalText); }
        });
    });
});