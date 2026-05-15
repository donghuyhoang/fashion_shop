$(document).ready(function () {
    const API_URL = "/api/";

    // ==========================================
    // NGĂN TRÌNH DUYỆT TỰ ĐỘNG CUỘN TRANG (BACK/FORWARD)
    // ==========================================
    if ('scrollRestoration' in history) {
        history.scrollRestoration = 'manual';
    }
    window.scrollTo(0, 0);

    // ==========================================
    // SỬA LỖI ACCESSIBILITY (aria-hidden) CHO TẤT CẢ CÁC MODAL
    // ==========================================
    $(document).on('hide.bs.modal', '.modal', function () {
        if (document.activeElement) {
            document.activeElement.blur();
        }
    });

    // ==========================================
    // 0. CACHE DỮ LIỆU SIZE VÀ MÀU SẮC TỪ API 
    // ==========================================
    let sizesCache = [];
    let colorsCache = [];
    
    // Tải ngầm danh sách Size và Màu khi vừa mở trang (Bắt lỗi an toàn nếu API chưa có)
    $.get(API_URL + "sizes").done(function(data) { 
        if(data) sizesCache = data; 
    }).fail(function() { console.warn("Chưa có API /api/sizes hoặc lỗi kết nối"); });
    
    $.get(API_URL + "colors").done(function(data) { 
        if(data) colorsCache = data; 
    }).fail(function() { console.warn("Chưa có API /api/colors hoặc lỗi kết nối"); });

    // ==========================================
    // 1. KHỞI CHẠY KHI MỞ TRANG
    // ==========================================
    loadStorytellingCollections();
    updateHeaderAfterLogin();
    updateCartBadge(); // Cập nhật số lượng giỏ hàng ngay khi mở trang

    // ==========================================
    // 1.0 THEME TOGGLE (CHẾ ĐỘ SÁNG / TỐI)
    // ==========================================
    const currentTheme = localStorage.getItem('theme');
    if (currentTheme === 'light') {
        $('body').addClass('light-theme');
        $('#themeToggle i').removeClass('fa-moon').addClass('fa-sun');
    }

    $(document).on('click', '#themeToggle', function(e) {
        e.preventDefault();
        $('body').toggleClass('light-theme');
        
        const isLight = $('body').hasClass('light-theme');
        $(this).find('i').removeClass('fa-moon fa-sun').addClass(isLight ? 'fa-sun' : 'fa-moon');
        
        localStorage.setItem('theme', isLight ? 'light' : 'dark');
    });

    // ==========================================
    // 1.1 HIỆU ỨNG CHUYỂN TRANG MƯỢT MÀ (GLOBAL)
    // ==========================================
    // Hàm chuyển trang có hiệu ứng dùng chung
    window.smoothNavigate = function(url) {
        $('body').addClass('page-transitioning');
        setTimeout(function() {
            window.location.href = url;
        }, 400); // Khớp với thời gian transition 0.4s trong CSS
    };

    // Bắt sự kiện click vào các thẻ <a> nội bộ
    $(document).on('click', 'a', function(e) {
        const target = $(this).attr('href');
        const targetAttr = $(this).attr('target');
        const hasOnClick = $(this).attr('onclick');

        // Bỏ qua các link rỗng, neo (#), js thực thi, link ngoài, tab mới hoặc nút gọi hàm
        if (!target || target === '#' || target.startsWith('#') || target.startsWith('javascript:') || target.startsWith('http') || target.startsWith('mailto') || targetAttr === '_blank' || hasOnClick) {
            return;
        }

        e.preventDefault();
        window.smoothNavigate(target);
    });

    // ==========================================
    // 2. HÀM LẤY DATA VÀ HIỂN THỊ 24 ĐÔI GIÀY
    // ==========================================
    let allProductsData = [];

    function loadStorytellingCollections() {
        $.ajax({
            url: API_URL + "products",
            type: "GET",
            success: function (allProducts) {
                if (!allProducts) return;
                
                allProductsData = allProducts; // Lưu lại để dùng cho bộ lọc

                // Gọi hàm lọc và sắp xếp lần đầu
                applyIndexFiltersAndSort();
            },
            error: function () {
                console.error("Lỗi kết nối Backend. Không lấy được sản phẩm!");
            }
        });
    }

    function renderCarouselCards(products, gridId) {
        const $grid = $(gridId);
        if (!products || products.length === 0) {
            $grid.html(`
                <div class="col-12 text-center text-muted py-5 mt-4 dark-card" style="border-style: dashed !important;">
                    <i class="fas fa-box-open fa-3x mb-3 text-secondary"></i>
                    <h5 class="fw-bold product-name mt-2" style="color: inherit !important; text-shadow: none;">Chưa có sản phẩm</h5>
                    <p class="mb-0">Đang cập nhật thêm sản phẩm cho bộ sưu tập này.</p>
                </div>
            `);
            return;
        }


        let html = "";
        $.each(products, function (index, product) {
            let imgSrc = product.thumbnailUrl || product.thumb || product.thumbnail_img_url || product.image || product.thumbnailImgUrl;
            const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";
            if (!imgSrc || imgSrc.trim() === "") imgSrc = fallbackImg;
            else if (imgSrc.includes(';') && !imgSrc.startsWith('data:image')) imgSrc = imgSrc.split(';')[0].trim();

            html += `
                <div class="col-md-4 col-lg-3 mb-4">
                    <div class="card h-100 product-card border-0 shadow-sm dark-card">
                        <a href="product-detail.html?id=${product.id}" class="product-img text-decoration-none d-block">
                            <img src="${imgSrc}" alt="${product.name}" onerror="this.onerror=null; this.src='${fallbackImg}';">
                        </a>
                        <div class="card-body d-flex flex-column">
                            <a href="product-detail.html?id=${product.id}" class="text-decoration-none text-light">
                                <h6 class="product-name text-truncate" title="${product.name}">${product.name}</h6>
                            </a>
                        <p class="product-category text-truncate" title="${product.brandName || product.brand_name || 'Sneaker'}">${product.brandName || product.brand_name || 'Sneaker'}</p>
                            <div class="d-flex justify-content-between align-items-center mt-auto">
                                <h5 class="product-price mb-0">${product.price ? product.price.toLocaleString('vi-VN') : 0} ₫</h5>
                                <button class="btn-buy" data-id="${product.id}" title="Thêm vào giỏ">
                                    <i class="fas fa-shopping-cart"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });
        $grid.html(html);
    }

    // ==========================================
    // 3. XỬ LÝ SỰ KIỆN CLICK CATEGORY PILLS (LỌC SẢN PHẨM & CUỘN TRANG)
    // ==========================================
    let currentIndexFilterType = 'all';
    let currentIndexFilterId = null;
    let currentIndexFilterText = 'NEW ARRIVALS';

    function applyIndexFiltersAndSort() {
        let filteredProducts = [...allProductsData];
        
        // 1. Áp dụng bộ lọc
        if (currentIndexFilterType === 'all') {
            $('#sectionTitleText').text('NEW ARRIVALS');
            $('#sectionDescText').text('Khám phá những đôi giày mới cập bến.');
            $('.admin-add-product-btn').data('brand', '').data('category', '');
        } else if (currentIndexFilterType === 'brand') {
            $('#sectionTitleText').text(currentIndexFilterText);
            $('#sectionDescText').text('Khám phá các bộ sưu tập mang đậm dấu ấn từ thương hiệu ' + currentIndexFilterText + '.');
            $('.admin-add-product-btn').data('brand', currentIndexFilterId).data('category', '');
            filteredProducts = filteredProducts.filter(p => {
                const bName = p.brandName || p.brand_name || "";
                return bName.toUpperCase().includes(currentIndexFilterText.toUpperCase()) || p.brandId === currentIndexFilterId || p.brand_id === currentIndexFilterId;
            });
        }

        // 1.5. Áp dụng khoảng giá
        const priceFilter = $('#priceFilterIndexSelect').val();
        if (priceFilter === 'under_1m') {
            filteredProducts = filteredProducts.filter(p => (p.price || 0) < 1000000);
        } else if (priceFilter === '1m_to_3m') {
            filteredProducts = filteredProducts.filter(p => (p.price || 0) >= 1000000 && (p.price || 0) <= 3000000);
        } else if (priceFilter === 'over_3m') {
            filteredProducts = filteredProducts.filter(p => (p.price || 0) > 3000000);
        }

        // 2. Áp dụng sắp xếp
        const sortVal = $('#sortIndexSelect').val();
        if (sortVal === 'price_asc') {
            filteredProducts.sort((a, b) => (a.price || 0) - (b.price || 0));
        } else if (sortVal === 'price_desc') {
            filteredProducts.sort((a, b) => (b.price || 0) - (a.price || 0));
        } else {
            filteredProducts.sort((a, b) => b.id - a.id); // Mặc định là sản phẩm mới nhất
        }

        // Chỉ lấy 10 đôi ở chế độ NEW ARRIVALS
        if (currentIndexFilterType === 'all') {
            filteredProducts = filteredProducts.slice(0, 10);
        }

        renderCarouselCards(filteredProducts, "#newArrivalsGrid");
    }

    $(".pill-btn").click(function() {
        $(".pill-btn").removeClass("active");
        $(this).addClass("active");
        
        currentIndexFilterType = $(this).data("filter");
        currentIndexFilterId = parseInt($(this).data("id"));
        currentIndexFilterText = $(this).text().trim();
        
        applyIndexFiltersAndSort();

        const targetOffset = $('#newArrivals').offset().top;
        const headerHeight = $('.main-navbar').outerHeight();
        $('html, body').animate({
            scrollTop: targetOffset - headerHeight - 15 
        }, 400);
    });

    $("#sortIndexSelect, #priceFilterIndexSelect").change(function() {
        applyIndexFiltersAndSort();
    });

    // ==========================================
    // 4. XỬ LÝ GIAO DIỆN ĐĂNG NHẬP (HEADER)
    // ==========================================
    function updateHeaderAfterLogin() {
        const userName = localStorage.getItem("user_name"); 

        if (userName) {
            $("#userArea").html(`
                <div class="user-menu-container" style="position: relative;">
                    <div class="user-profile-toggle" style="cursor: pointer; display: flex; align-items: center; gap: 8px; font-weight: 600; color: white;">
                        <i class="fas fa-user-circle text-success" style="font-size: 1.2rem;"></i> 
                        <span class="text-truncate" style="max-width: 120px;">${userName}</span>
                        <i class="fas fa-chevron-down" style="font-size: 0.8rem; color: #9ca3af;"></i>
                    </div>
                    
                    <div class="user-message-box">
                        
                        <div class="px-3 py-2 mb-2" style="border-bottom: 1px dashed #3a3f4a;">
                            <small class="text-muted d-block" style="font-size: 0.75rem;">Tài khoản của bạn:</small>
                            <strong class="text-light text-truncate d-block" style="max-width: 150px;">${localStorage.getItem('user_email') || ''}</strong>
                        </div>
                        
                        <a href="#" id="btnProfile" class="dropdown-custom-item">
                            <i class="fas fa-id-badge fa-fw me-3 text-primary"></i> Hồ sơ của tôi
                        </a>
                        
                        <a href="orders.html" class="dropdown-custom-item">
                            <i class="fas fa-box-open fa-fw me-3 text-warning"></i> Đơn mua
                        </a>
                        
                        <div style="height: 1px; background-color: #3a3f4a; margin: 8px 0;"></div>
                        
                        <a href="#" id="btnLogout" class="dropdown-custom-item text-danger fw-bold">
                            <i class="fas fa-sign-out-alt fa-fw me-3"></i> Đăng xuất
                        </a>
                    </div>
                </div>
            `);
        }
    }

    // ==========================================
    // JS XỬ LÝ SỰ KIỆN CHO MENU (Đóng/Mở & Click)
    // ==========================================

    // 1. Hiệu ứng Bấm vào tên để xổ Menu xuống
    $(document).on("click", ".user-profile-toggle", function(e) {
        e.stopPropagation(); // Ngăn sự kiện click lan ra ngoài
        $(".user-message-box").fadeToggle(200); // Mở/đóng menu với hiệu ứng mờ
    });

    // 2. Bấm ra ngoài khoảng trống thì tự động đóng Menu lại
    $(document).on("click", function(e) {
        if (!$(e.target).closest(".user-menu-container").length) {
            $(".user-message-box").fadeOut(200);
        }
    });

    // Ngăn việc click vào chính Message Box làm nó bị đóng
    $(document).on("click", ".user-message-box", function(e) {
        e.stopPropagation();
    });

    // 3. Xử lý sự kiện bấm nút Hồ Sơ (Không bị hiện dấu #)
    $(document).on("click", "#btnProfile", function(e) {
        e.preventDefault(); // <-- Đây chính là "Thần chú" chặn dấu # trên URL
        
        // Bạn có thể viết code kiểm tra/xử lý gì đó ở đây trước khi chuyển trang
        console.log("Đang mở trang hồ sơ...");
        
        // Chuyển sang trang profile
        window.smoothNavigate("profile.html"); 
    });

    // ==========================================
    // XỬ LÝ NÚT ĐĂNG XUẤT (Đã fix lỗi bóng ma)
    // ==========================================
    $(document).on("click", "#btnLogout", function(e) {
        e.preventDefault();
        
        // Xóa sạch mọi dấu vết của user trong két sắt
        localStorage.removeItem("user_name"); 
        localStorage.removeItem("user_email"); 
        
        // 🔥 ĐÂY CHÍNH LÀ DÒNG QUAN TRỌNG NHẤT ĐỂ SỬA LỖI 🔥
        localStorage.removeItem("user_id"); 
        
        // (Tùy chọn) Xóa luôn giỏ hàng tạm nếu bạn còn dùng
        localStorage.removeItem("user_cart"); 

        // Load lại trang
        $('body').addClass('page-transitioning');
        setTimeout(function() {
            window.location.reload();
        }, 400);
    });

    // ==========================================
    // 5. XỬ LÝ CLICK GIỎ HÀNG THẺ SẢN PHẨM (HIỂN THỊ MODAL TẠI TRANG CHỦ)
    // ==========================================
    $(document).on('click', '.btn-buy', function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 1. Lấy ID user từ két sắt
        const userId = localStorage.getItem("user_id");
        
        if (!userId || userId === "undefined") {
            alert("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
            if (typeof window.smoothNavigate === 'function') {
                window.smoothNavigate("login.html");
            } else {
                window.location.href = "login.html";
            }
            return;
        }

        // 2. Lấy ID của đôi giày
        const productId = $(this).data('id');
        
        // 3. Tìm thông tin SP
        const productName = $(this).closest('.dark-card').find('.product-name').text() || $(this).data('name') || "Sản phẩm";
        const productPrice = $(this).closest('.dark-card').find('.product-price').text() || $(this).data('price') || "";
        const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";
        const productImage = $(this).closest('.dark-card').find('img').attr('src') || $(this).data('image') || fallbackImg;

        // 4. Tạo Modal HTML và nhúng thẳng vào cuối <body> nếu chưa tồn tại
        if ($('#quickBuyModal').length === 0) {
            $('body').append(`
                <div id="quickBuyModal" class="custom-modal-overlay">
                    <div class="custom-modal-content">
                        <span class="close-modal-btn">&times;</span>
                        
                        <div class="modal-flex-container">
                            <div class="modal-img-wrapper">
                                <img id="modal-img" src="" alt="Sản phẩm">
                            </div>

                            <div class="modal-info-wrapper">
                                <h2 id="modal-name">Tên sản phẩm</h2>
                                <p class="modal-price" id="modal-price">0 VNĐ</p>
                                <div class="mb-3 text-muted" style="font-size: 0.9rem;">Kho: <span id="modal-stock">-</span></div>

                                <div id="quickBuySelectors">
                                    <p class="text-muted"><i class="fas fa-spinner fa-spin"></i> Đang tải thông tin phân loại...</p>
                                </div>

                                <button class="btn-buy-now" id="btnConfirmQuickBuy" disabled>VUI LÒNG CHỌN PHÂN LOẠI</button>
                            </div>
                        </div>
                    </div>
                </div>
            `);

            // Bắt sự kiện tắt khi bấm nút X hoặc bấm ra vùng đen bên ngoài
            $(document).on('click', '.close-modal-btn, .custom-modal-overlay', function(e) {
                if (e.target === this) {
                    $('#quickBuyModal').hide();
                    $('body').css('overflow', ''); // Mở khóa cuộn trang
                }
            });
        }
        
        // 5. Đổ dữ liệu tĩnh ngay khi vừa mở
        $('#modal-name').text(productName);
        $('#modal-price').text(productPrice);
        $('#modal-img').attr('src', productImage).attr('alt', productName);
        $('#modal-stock').html('<span class="text-muted">-</span>');
        $('#btnConfirmQuickBuy').prop('disabled', true).text('VUI LÒNG CHỌN PHÂN LOẠI').removeData('detail-id').data('product-name', productName);
        $('#quickBuySelectors').html('<p class="text-muted"><i class="fas fa-spinner fa-spin"></i> Đang tải thông tin phân loại...</p>');
        
        // 6. Mở bảng chọn (Dùng flex để căn giữa hoàn hảo)
        $('#quickBuyModal').css('display', 'flex');
        $('body').css('overflow', 'hidden'); // Khóa cuộn trang nền để Modal luôn cố định trước mắt
        
        // 7. Kéo dữ liệu của các phiên bản (Size/Color) từ Backend
        $.ajax({
            url: API_URL + "product-details/product/" + productId,
            type: "GET",
            success: function(details) {
                if (!details || details.length === 0) {
                    $('#quickBuySelectors').html('<p class="text-danger">Sản phẩm này hiện đang hết hàng.</p>');
                    return;
                }
                
                $('#quickBuyModal').data('product-details', details);
                const colorMap = new Map();
                const sizeMap = new Map();
                
                details.forEach(detail => {
                    let cId = detail.colorId || detail.color_id || (detail.color && detail.color.id) || 0;
                    let cName = detail.colorName || detail.color_name || detail.color || (detail.color && detail.color.name);
                    let sId = detail.sizeId || detail.size_id || (detail.size && detail.size.id) || 0;
                    let sName = detail.sizeName || detail.size_name || detail.sizeValue || detail.size_value || detail.size || (detail.size && (detail.size.name || detail.size.value));

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
                
                let html = `
                    <div class="option-group">
                        <label>Màu sắc:</label>
                        <div class="color-list">
                `;
                colorMap.forEach(color => {
                    const cName = color.name || color.value || color.color || color.id;
                    html += `<div class="size-box color-item" data-id="${color.id}">${cName}</div>`;
                });
                
                html += `
                        </div>
                    </div>
                    <div class="option-group">
                        <label>Kích thước (Size):</label>
                        <div class="size-list">
                `;
                sizeMap.forEach(size => {
                    const sName = size.name || size.value || size.size || size.id;
                    html += `<div class="size-box size-item" data-id="${size.id}">${sName}</div>`;
                });
                
                html += `
                        </div>
                    </div>
                `;
                $('#quickBuySelectors').html(html);
                checkSelectedVariantCustom();
            },
            error: function() {
                $('#quickBuySelectors').html('<p class="text-danger">Không thể kết nối đến máy chủ. Vui lòng thử lại sau.</p>');
            }
        });
    });

    // ==========================================
    // BẮT SỰ KIỆN KHI ẤN CHỌN MÀU / SIZE CHO CUSTOM MODAL
    // ==========================================
    $(document).on('click', '#quickBuyModal .color-item', function() {
        $('#quickBuyModal .color-item').removeClass('active');
        $(this).addClass('active');
        checkSelectedVariantCustom();
    });

    $(document).on('click', '#quickBuyModal .size-item', function() {
        $('#quickBuyModal .size-item').removeClass('active');
        $(this).addClass('active');
        checkSelectedVariantCustom();
    });
        
    function checkSelectedVariantCustom() {
        const details = $('#quickBuyModal').data('product-details');
        if (!details) return; 
        
        const btnConfirm = $('#btnConfirmQuickBuy');
        const selectedColorBtn = $('#quickBuyModal .color-item.active');
        const selectedSizeBtn = $('#quickBuyModal .size-item.active');
        
        if (selectedColorBtn.length > 0 && selectedSizeBtn.length > 0) {
            const selectedColorId = selectedColorBtn.data('id');
            const selectedSizeId = selectedSizeBtn.data('id');
            
            const detail = details.find(d => {
                let cId = d.colorId || d.color_id || (d.color && d.color.id) || 0;
                let cName = d.colorName || d.color_name || d.color || (d.color && d.color.name);
                let finalColorKey = cId !== 0 ? cId : (cName || "Mặc định");

                let sId = d.sizeId || d.size_id || (d.size && d.size.id) || 0;
                let sName = d.sizeName || d.size_name || d.sizeValue || d.size_value || d.size || (d.size && (d.size.name || d.size.value));
                let finalSizeKey = sId !== 0 ? sId : (sName || "Freesize");

                return finalColorKey == selectedColorId && finalSizeKey == selectedSizeId;
            });
            
            if (detail) {
                const stock = detail.stockQuantity !== undefined ? detail.stockQuantity : detail.stock_quantity;
                const dId = detail.id !== undefined ? detail.id : (detail.productDetailId !== undefined ? detail.productDetailId : detail.product_detail_id);
                const price = detail.price !== undefined ? detail.price : null;
                const thumb = detail.thumbnailImgUrl || detail.thumbnail_img_url || detail.thumb;
                
                if (price) $('#modal-price').text(price.toLocaleString('vi-VN') + ' VNĐ');
                if (thumb) $('#modal-img').attr('src', thumb);

                if (stock > 0) {
                    $('#modal-stock').html(`<span class="text-success fw-bold">${stock}</span>`);
                    const variantName = `${selectedColorBtn.text()} (Size ${selectedSizeBtn.text()})`;
                    btnConfirm.text('THÊM VÀO GIỎ HÀNG').prop('disabled', false).data('detail-id', dId).data('variant-name', variantName);
                } else {
                    $('#modal-stock').html(`<span class="text-danger fw-bold">Hết hàng</span>`);
                    btnConfirm.text('SẢN PHẨM ĐÃ HẾT HÀNG').prop('disabled', true).removeData('detail-id');
                }
            } else {
                $('#modal-stock').html(`<span class="text-muted">0</span>`);
                btnConfirm.text('PHÂN LOẠI KHÔNG TỒN TẠI').prop('disabled', true).removeData('detail-id');
            }
        } else {
            $('#modal-stock').html(`<span class="text-muted">-</span>`);
            btnConfirm.text('VUI LÒNG CHỌN PHÂN LOẠI').prop('disabled', true).removeData('detail-id');
        }
    }

    // ==========================================
    // XÁC NHẬN THÊM VÀO GIỎ TỪ CUSTOM MODAL
    // ==========================================
    $(document).on('click', '#btnConfirmQuickBuy', function() {
        const detailId = $(this).data('detail-id');
        const variantName = $(this).data('variant-name') || '';
        const userId = localStorage.getItem("user_id");
        const baseProductName = $(this).data('product-name') || 'Sản phẩm';
        const productName = variantName ? `${baseProductName} - ${variantName}` : baseProductName;

        if (!detailId) return;

        const cartRequest = {
            userId: parseInt(userId),
            productDetailId: parseInt(detailId),
            quantity: 1
        };

        const $btn = $(this);
        const originalText = $btn.text();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> ĐANG XỬ LÝ...');

        $.ajax({
            url: API_URL + "cart/add", 
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(cartRequest),
            success: function () {
                $('#quickBuyModal').hide();
                $('body').css('overflow', ''); // Mở khóa cuộn trang
                updateCartBadge();
                showToastSuccess(productName);
            },
            error: function (xhr) {
                console.error("Lỗi khi thêm vào giỏ:", xhr.responseText);
                alert("Hệ thống gặp sự cố khi thêm dữ liệu. Vui lòng thử lại!");
            },
            complete: function() {
                $btn.prop('disabled', false).text(originalText);
            }
        });
    });

    // ==========================================
    // HÀM LẤY TỔNG SỐ LƯỢNG TỪ MYSQL ĐỂ HIỂN THỊ
    // ==========================================
    function updateCartBadge() {
        const userId = localStorage.getItem("user_id");
        
        if (!userId || userId === "undefined") {
            $('#cartBadge').hide();
            return;
        }

        // Đổi API đếm số lượng: truyền userId thay vì email
        $.ajax({
            url: API_URL + "cart/count/" + userId, 
            type: "GET",
            success: function (totalItems) {
                const $cartIconLink = $('.nav-actions .fa-cart-shopping').closest('a');

                if ($('#cartBadge').length === 0) {
                    $cartIconLink.addClass('position-relative');
                    $cartIconLink.append(`
                        <span id="cartBadge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" 
                              style="font-size: 0.65rem; padding: 0.35em 0.5em;">
                            ${totalItems}
                        </span>
                    `);
                } else {
                    $('#cartBadge').text(totalItems);
                }

                if (totalItems > 0) {
                    $('#cartBadge').show();
                } else {
                    $('#cartBadge').hide();
                }
            },
            error: function(xhr) {
                console.error("Lỗi đếm giỏ hàng từ MySQL:", xhr.responseText);
            }
        });
    }
    
    window.updateCartBadge = updateCartBadge;

    // ==========================================
    // HÀM BẬT THÔNG BÁO GÓC MÀN HÌNH (Giữ nguyên)
    // ==========================================
    function showToastSuccess(productName) {
        $('#shopeeToast').remove(); // Xóa toast cũ nếu đang hiện

        const toastHtml = `
            <div id="shopeeToast" class="shopee-toast-container show">
                <i class="fas fa-check-circle shopee-toast-icon"></i>
                <div class="shopee-toast-text">Đã thêm vào Giỏ hàng</div>
            </div>
        `;

        $('body').append(toastHtml);

        setTimeout(() => {
            $('#shopeeToast').removeClass('show');
            setTimeout(() => $('#shopeeToast').remove(), 300); 
        }, 2000);
    }
    
    window.showToastSuccess = showToastSuccess;

    // ==========================================
    // 1.5. HIỂN THỊ NÚT THÊM SẢN PHẨM TRỰC TIẾP CHO ADMIN
    // ==========================================
    const userRole = localStorage.getItem("user_role");
    if (userRole === "1") {
        $('.admin-add-product-btn').removeClass('d-none');
    }

    $(document).on('click', '.admin-add-product-btn', function(e) {
        e.preventDefault();
        const category = $(this).data('category') || "";
        const brand = $(this).data('brand') || "";
        
        localStorage.setItem("admin_auto_select_category", category);
        localStorage.setItem("admin_auto_select_brand", brand);
        window.location.href = "adminpage.html?action=add_product";
    });

    // ==========================================
    // 6. XỬ LÝ TÌM KIẾM SẢN PHẨM (ENTER & CLICK KÍNH LÚP)
    // ==========================================
    
    // Tự động điền lại từ khóa cũ vào ô tìm kiếm nếu đang ở trang search
    const urlParams = new URLSearchParams(window.location.search);
    const currentKeyword = urlParams.get('keyword');
    if (currentKeyword) {
        $('#searchInput').val(currentKeyword);
    }

    // Hàm xử lý logic chuyển trang tìm kiếm
    function performSearch() {
        const keyword = $('#searchInput').val().trim();
        if (keyword !== "") {
            window.smoothNavigate(`search.html?keyword=${encodeURIComponent(keyword)}`);
        }
    }

    // Bắt sự kiện ấn phím Enter trong ô input
    $(document).on('keypress', '#searchInput', function (e) {
        if (e.which === 13) { 
            e.preventDefault(); 
            performSearch();
        }
    });

    // Bắt sự kiện click vào icon kính lúp
    $(document).on('click', '.nav-search i', function () {
        performSearch();
    });


}); // <-- Đóng hàm an toàn