$(document).ready(function () {
    const API_URL = "/api/";
        $.ajaxSetup({
        beforeSend: function(xhr) {
            const token = localStorage.getItem("user_token");
            if (token) {
                xhr.setRequestHeader("Authorization", "Bearer " + token);
            }
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
	    // 2. HÀM LẤY DATA VÀ HIỂN THỊ ĐỘNG TRÊN TRANG CHỦ
	    // ==========================================
	    let allProductsCache = []; // <--- THÊM BIẾN NÀY ĐỂ NHỚ DỮ LIỆU

    function loadStorytellingCollections() {
        $.ajax({
            url: API_URL + "products",
            type: "GET",
            success: function (allProducts) {
                if (!allProducts || allProducts.length === 0) return;

                let sortedProducts = [...allProducts].reverse();
                allProductsCache = sortedProducts; // <--- LƯU LẠI VÀO ĐÂY ĐỂ TÍNH SAU LỌC

                const runningShoes = sortedProducts.filter(p => p.categoryId === 2);
                const basketballShoes = sortedProducts.filter(p => p.categoryId === 4);
                const lifestyleShoes = sortedProducts.filter(p => p.categoryId === 1 || p.categoryId === 3);

                renderCardsToSpecificGrid(sortedProducts.slice(0, 12), "#newArrivalsGrid"); 
                renderCardsToSpecificGrid(runningShoes.slice(0, 8), "#runningGrid");        
                renderCardsToSpecificGrid(basketballShoes.slice(0, 8), "#basketballGrid");  
                renderCardsToSpecificGrid(lifestyleShoes.slice(0, 8), "#luxuryGrid");       
            },
            error: function () {
                console.error("Lỗi kết nối Backend. Không lấy được sản phẩm!");
            }
        });
    }

	function renderCardsToSpecificGrid(products, gridId) {
	        const $grid = $(gridId);
	        if (!products || products.length === 0) {
	            $grid.html('<div class="col-12 text-center text-muted py-4">Không có sản phẩm nào.</div>');
	            return;
	        }

	        const localImages = [
	            "images/nike-air-max-90-infrared.jpg", 
	            "images/nike-air-force-1-low-white.jpg",
	            "images/air-jordan-1-high-chicago.jpg", 
	            "images/nike-zoom-tempo-next-pink.jpg"
	        ];

	        let html = "";
	        $.each(products, function (index, product) {
	            
	            let imgSrc = product.thumbnailUrl || product.thumb || product.thumbnailImgUrl || product.image;
	            
	            // Nếu chuỗi chứa nhiều link ảnh ngăn cách bằng |||, bóc tách lấy link đầu tiên làm ảnh đại diện
	            if (imgSrc && imgSrc.includes('|||')) {
	                imgSrc = imgSrc.split('|||')[0].trim();
	            } else if (imgSrc && imgSrc.includes(';')) {
	                imgSrc = imgSrc.split(';')[0].trim();
	            }
	            
	            // Nếu hoàn toàn không có ảnh trong DB thì mới dùng ảnh local dự phòng
	            if (!imgSrc) {
	                imgSrc = localImages[index % localImages.length];
	            }

	            const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";

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
	                            <p class="product-category text-truncate" title="${product.brandName || 'Sneaker'}">${product.brandName || 'Sneaker'}</p>
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
    // 3. XỬ LÝ SỰ KIỆN CLICK CATEGORY PILLS (LỌC SẢN PHẨM TRANG CHỦ)
    // ==========================================
    $(document).on('click', '.category-pills-section .pill-btn', function(e) {
        e.preventDefault();
        
        $(".category-pills-section .pill-btn").removeClass("active");
        $(this).addClass("active");
        
        let text = $(this).text().trim().toUpperCase();
        let currentFilterId = null;
        
        // Gắn ID tự động
        if (text.includes("NIKE")) currentFilterId = 1;
        else if (text.includes("ADIDAS")) currentFilterId = 2;
        else if (text.includes("MIZUNO")) currentFilterId = 3;
        else if (text.includes("PUMA")) currentFilterId = 4;
        else if ($(this).data("id")) currentFilterId = parseInt($(this).data("id"));

        let filtered = [...allProductsCache];

        // Nếu không phải bấm nút "TẤT CẢ" / "NEW ARRIVALS" thì đem đi lọc
        if (!text.includes("NEW ARRIVALS") && !text.includes("ALL")) {
            filtered = filtered.filter(p => {
                const bName = String(p.brandName || p.brand_name || "").toUpperCase();
                const pName = String(p.name || "").toUpperCase();
                const bId = p.brandId || p.brand_id;
                
                return bId === currentFilterId || bName.includes(text) || pName.includes(text);
            });
            $('#sectionTitleText').text('SẢN PHẨM ' + text);
            $('#sectionDescText').text('Kết quả lọc theo thương hiệu ' + text);
        } else {
            $('#sectionTitleText').text('NEW ARRIVALS');
            $('#sectionDescText').text('Khám phá những đôi giày mới cập bến.');
        }

        // Render lại khu vực hiển thị New Arrivals
        renderCardsToSpecificGrid(filtered.slice(0, 12), "#newArrivalsGrid");
        
        // Cuộn nhẹ xuống để người dùng thấy kết quả
        $('html, body').animate({
            scrollTop: $("#newArrivals").offset().top - 100 
        }, 300);
    // ==========================================
    // 2. HÀM LẤY DATA VÀ HIỂN THỊ ĐỘNG TRÊN TRANG CHỦ
    // ==========================================

    let homeProducts = [];
    let homeFilterState = {
        brandId: null,
        minPrice: null,
        maxPrice: null,
        sort: "default"
    };

    function loadStorytellingCollections() {
        // Chỉ chạy phần này ở trang index.html
        if ($("#newArrivalsGrid").length === 0) return;

        $.ajax({
            url: API_URL + "products",
            type: "GET",
            success: function (allProducts) {
                console.log("Danh sách sản phẩm trang chủ:", allProducts);

                if (!allProducts || allProducts.length === 0) {
                    $("#newArrivalsGrid").html(`
                        <div class="col-12 text-center text-muted py-5">
                            Không có sản phẩm nào.
                        </div>
                    `);
                    return;
                }

                // Lưu toàn bộ sản phẩm để lọc lại khi bấm NIKE / ADIDAS / MIZUNO / PUMA
                homeProducts = [...allProducts];

                // Mặc định hiển thị trang chủ
                renderHomeDefault();
            },
            error: function (xhr) {
                console.error("Lỗi lấy sản phẩm trang chủ:", xhr.status, xhr.responseText);
                $("#newArrivalsGrid").html(`
                    <div class="col-12 text-center text-danger py-5">
                        Không thể tải sản phẩm từ hệ thống.
                    </div>
                `);
            }
        });
    }

    function renderHomeDefault() {
        let sortedProducts = [...homeProducts].sort((a, b) => Number(b.id || 0) - Number(a.id || 0));

        // Sửa ID category theo đúng database của bạn:
        // Nếu database của bạn đang là:
        // 1 = RUNNING, 2 = FOOTBALL, 3 = BASKETBALL, 4 = SNEAKERS
        // thì giữ như dưới đây.
        const runningShoes = sortedProducts.filter(p => Number(p.categoryId || p.category_id) === 1);
        const footballShoes = sortedProducts.filter(p => Number(p.categoryId || p.category_id) === 2);
        const basketballShoes = sortedProducts.filter(p => Number(p.categoryId || p.category_id) === 3);
        const sneakersShoes = sortedProducts.filter(p => Number(p.categoryId || p.category_id) === 4);

        $("#sectionTitleText").text("NEW ARRIVALS");
        $("#sectionDescText").text("Khám phá những đôi giày mới cập bến.");

        // Khi bấm NEW ARRIVALS thì hiện lại đầy đủ các section
        $("#runningShoes").show();
        $("#basketballShoes").show();
        $("#lifestyleShoes").show();

        renderCardsToSpecificGrid(sortedProducts.slice(0, 12), "#newArrivalsGrid");
        renderCardsToSpecificGrid(runningShoes.slice(0, 8), "#runningGrid");
        renderCardsToSpecificGrid(basketballShoes.slice(0, 8), "#basketballGrid");
        renderCardsToSpecificGrid(sneakersShoes.slice(0, 8), "#luxuryGrid");
    }

    function applyHomeFilters() {
        let filtered = [...homeProducts];

        if (homeFilterState.brandId) {
            filtered = filtered.filter(p =>
                Number(p.brandId || p.brand_id) === Number(homeFilterState.brandId)
            );

            $("#runningShoes").hide();
            $("#basketballShoes").hide();
            $("#lifestyleShoes").hide();
        }

        if (homeFilterState.minPrice !== null) {
            filtered = filtered.filter(p =>
                Number(p.price || 0) >= Number(homeFilterState.minPrice)
            );
        }

        if (homeFilterState.maxPrice !== null) {
            filtered = filtered.filter(p =>
                Number(p.price || 0) <= Number(homeFilterState.maxPrice)
            );
        }

        if (homeFilterState.sort === "price_asc") {
            filtered.sort((a, b) => Number(a.price || 0) - Number(b.price || 0));
        } else if (homeFilterState.sort === "price_desc") {
            filtered.sort((a, b) => Number(b.price || 0) - Number(a.price || 0));
        } else if (homeFilterState.sort === "newest") {
            filtered.sort((a, b) => Number(b.id || 0) - Number(a.id || 0));
        } else {
            filtered.sort((a, b) => Number(b.id || 0) - Number(a.id || 0));
        }

        renderCardsToSpecificGrid(filtered, "#newArrivalsGrid");
    }

    function renderCardsToSpecificGrid(products, gridId) {
        const $grid = $(gridId);

        if (!$grid.length) return;

        if (!products || products.length === 0) {
            $grid.html(`
                <div class="col-12 text-center text-muted py-5">
                    <i class="fas fa-box-open fa-3x mb-3"></i>
                    <h5>Không tìm thấy sản phẩm phù hợp.</h5>
                    <p>Vui lòng thử bộ lọc khác.</p>
                </div>
            `);
            return;
        }

        const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";

        let html = "";

        products.forEach(function (product) {
            let imgSrc =
                product.thumbnailUrl ||
                product.thumb ||
                product.thumbnailImgUrl ||
                product.thumbnail_img_url ||
                product.imageUrl ||
                product.image ||
                "";

            if (imgSrc && imgSrc.includes("|||")) {
                imgSrc = imgSrc.split("|||")[0].trim();
            }

            if (imgSrc && imgSrc.includes(";")) {
                imgSrc = imgSrc.split(";")[0].trim();
            }

            if (!imgSrc) {
                imgSrc = fallbackImg;
            }

            const price = Number(product.price || 0).toLocaleString("vi-VN");
            const brandName = product.brandName || product.brand_name || "Sneaker";

            html += `
                <div class="col-md-4 col-lg-3 mb-4">
                    <div class="card h-100 product-card border-0 shadow-sm dark-card">
                        <a href="product-detail.html?id=${product.id}" class="product-img text-decoration-none d-block">
                            <img src="${imgSrc}" alt="${product.name}" onerror="this.onerror=null; this.src='${fallbackImg}';">
                        </a>

                        <div class="card-body d-flex flex-column">
                            <a href="product-detail.html?id=${product.id}" class="text-decoration-none text-light">
                                <h6 class="product-name text-truncate" title="${product.name}">
                                    ${product.name}
                                </h6>
                            </a>

                            <p class="product-category text-truncate" title="${brandName}">
                                ${brandName}
                            </p>

                            <div class="d-flex justify-content-between align-items-center mt-auto">
                                <h5 class="product-price mb-0">${price} ₫</h5>
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
    // 3. XỬ LÝ NÚT LỌC Ở TRANG CHỦ: NEW ARRIVALS / NIKE / ADIDAS / ...
    // ==========================================

    $(document).on("click", ".category-pills-section .pill-btn", function () {
        const $btn = $(this);

        $(".category-pills-section .pill-btn").removeClass("active");
        $btn.addClass("active");

        const filterType = $btn.data("filter");
        const brandId = Number($btn.data("id"));
        const brandName = $btn.text().trim();

        if (filterType === "all") {
            homeFilterState.brandId = null;
            homeFilterState.minPrice = null;
            homeFilterState.maxPrice = null;
            homeFilterState.sort = "default";

            $("#homePriceFilter").val("all");
            $("#homeSortSelect").val("default");

            $("#sectionTitleText").text("NEW ARRIVALS");
            $("#sectionDescText").text("Khám phá những đôi giày mới cập bến.");

            renderHomeDefault();
            return;
        }

        if (filterType === "brand") {
            homeFilterState.brandId = brandId;

            $("#sectionTitleText").text("SẢN PHẨM " + brandName.toUpperCase());
            $("#sectionDescText").text("Danh sách sản phẩm thuộc thương hiệu " + brandName.toUpperCase() + ".");

            $("#runningShoes").hide();
            $("#basketballShoes").hide();
            $("#lifestyleShoes").hide();

            applyHomeFilters();
        }
    });

    $("#btnHomeApplyFilter").on("click", function () {
        const priceValue = $("#homePriceFilter").val();
        const sortValue = $("#homeSortSelect").val();

        if (priceValue === "all") {
            homeFilterState.minPrice = null;
            homeFilterState.maxPrice = null;
        } else {
            const parts = priceValue.split("-");
            homeFilterState.minPrice = Number(parts[0]);
            homeFilterState.maxPrice = Number(parts[1]);
        }

        homeFilterState.sort = sortValue;

        $("#runningShoes").hide();
        $("#basketballShoes").hide();
        $("#lifestyleShoes").hide();

        applyHomeFilters();
    });

    $("#btnHomeClearFilter").on("click", function () {
        homeFilterState.brandId = null;
        homeFilterState.minPrice = null;
        homeFilterState.maxPrice = null;
        homeFilterState.sort = "default";

        $(".category-pills-section .pill-btn").removeClass("active");
        $('.category-pills-section .pill-btn[data-filter="all"]').addClass("active");

        $("#homePriceFilter").val("all");
        $("#homeSortSelect").val("default");

        $("#sectionTitleText").text("NEW ARRIVALS");
        $("#sectionDescText").text("Khám phá những đôi giày mới cập bến.");

        renderHomeDefault();
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
        e.preventDefault(); 
        console.log("Đang mở trang hồ sơ...");
        window.location.href = "profile.html"; 
    });

    // ==========================================
    // XỬ LÝ NÚT ĐĂNG XUẤT (Đã fix lỗi bóng ma)
    // ==========================================
    $(document).on("click", "#btnLogout", function(e) {
        e.preventDefault();
        
        // Xóa sạch mọi dấu vết của user trong két sắt
        localStorage.removeItem("user_name"); 
        localStorage.removeItem("user_email"); 
        localStorage.removeItem("user_token"); 
        localStorage.removeItem("staff_token"); 
        localStorage.removeItem("user_id"); 
        localStorage.removeItem("user_cart"); 

        window.location.reload(); 
    });

    // ==========================================
    // 5. XỬ LÝ THÊM VÀO GIỎ HÀNG (LƯU VÀO MYSQL)
    // ==========================================
    $(document).on('click', '.btn-buy', function(e) {
        e.preventDefault();

        // 1. Lấy ID user từ két sắt
        const userId = localStorage.getItem("user_id");
        
        if (!userId || userId === "undefined") {
            alert("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
            window.location.href = "login.html"; 
            return;
        }

        // 2. Lấy ID của đôi giày
        const productId = $(this).data('id');
        const productName = $(this).closest('.dark-card').find('.product-name').text();
        const productPrice = $(this).closest('.dark-card').find('.product-price').text();
        const productImage = $(this).closest('.dark-card').find('img').attr('src');

        // =========================================================
        // LUỒNG MỚI: HIỂN THỊ POPUP QUICK ADD THAY VÌ ADD TRỰC TIẾP
        // =========================================================
        
        // 1. Cập nhật Tiêu đề Modal
        $('#quickAddTitle').text("Tùy chọn sản phẩm");
        
        // 2. Khóa nút Xác nhận, lưu lại tên gốc và dọn dẹp data cũ
        $('#btnConfirmQuickAdd').prop('disabled', true).removeData('detail-id').data('product-name', productName);
        
        // 2.5 Tạo Layout động hiển thị Hình ảnh và Giá ngay trong Modal
        $('#quickAddOptions').html(`
            <div class="d-flex align-items-center mb-4 pb-3 border-bottom border-secondary">
                <img id="quickAddVariantImage" src="${productImage}" alt="${productName}" class="img-thumbnail bg-dark border-secondary" style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px; margin-right: 15px;">
                <div>
                    <h6 class="mb-1 text-light" style="line-height: 1.4;">${productName}</h6>
                    <span id="quickAddVariantPrice" class="text-warning fw-bold fs-5">${productPrice}</span>
                </div>
            </div>
            <div id="quickAddSelectors">
                <p class="text-muted"><i class="fas fa-spinner fa-spin"></i> Đang tải thông tin phân loại...</p>
            </div>
        `);
        
        // 3. Bật Modal hiển thị
        $('#quickAddModal').modal('show');
        
        // 4. Kéo dữ liệu của các phiên bản (Size/Color) từ Backend
        $.ajax({
            url: API_URL + "product-details/product/" + productId,
            type: "GET",
            success: function(details) {
                if (!details || details.length === 0) {
                    $('#quickAddSelectors').html('<p class="text-danger">Sản phẩm này hiện đang hết hàng.</p>');
                    return;
                }
                
                // Lưu mảng details vào modal để dùng lại khi check
                $('#quickAddModal').data('product-details', details);

                const colorMap = new Map();
                const sizeMap = new Map();
                
                details.forEach(detail => {
                    const sId = detail.sizeId !== undefined ? detail.sizeId : detail.size_id;
                    const cId = detail.colorId !== undefined ? detail.colorId : detail.color_id;
                    
                    const sizeObj = sizesCache.find(s => s.id === sId) || { id: sId, name: sId };
                    const colorObj = colorsCache.find(c => c.id === cId) || { id: cId, name: cId };
                    
                    if (!colorMap.has(cId)) colorMap.set(cId, colorObj);
                    if (!sizeMap.has(sId)) sizeMap.set(sId, sizeObj);
                });
                
                let html = `
                    <div class="mb-3">
                        <label class="fw-bold mb-2">Màu sắc:</label>
                        <div class="d-flex flex-wrap gap-2" id="colorOptions">
                `;
                colorMap.forEach(color => {
                    html += `<button type="button" class="btn btn-outline-custom color-btn" data-id="${color.id}">${color.name}</button>`;
                });
                
                html += `
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="fw-bold mb-2">Kích cỡ (Size):</label>
                        <div class="d-flex flex-wrap gap-2" id="sizeOptions">
                `;
                sizeMap.forEach(size => {
                    html += `<button type="button" class="btn btn-outline-custom size-btn" data-id="${size.id}">${size.name}</button>`;
                });
                
                html += `
                        </div>
                    </div>
                    <div id="stockStatus" class="mt-2 text-muted" style="font-size: 0.95rem;">
                        <i class="fas fa-info-circle me-1"></i> Vui lòng chọn Màu sắc và Kích cỡ.
                    </div>
                `;
                $('#quickAddSelectors').html(html);
            },
            error: function() {
                $('#quickAddSelectors').html('<p class="text-danger">Không thể kết nối đến máy chủ. Vui lòng thử lại sau.</p>');
            }
        });
    });

    // ==========================================
    // BẮT SỰ KIỆN KHI ẤN CHỌN MÀU / SIZE
    // ==========================================
    $(document).on('click', '.color-btn', function() {
        if ($(this).hasClass('btn-primary-custom')) {
            $(this).removeClass('btn-primary-custom text-white').addClass('btn-outline-custom');
        } else {
            $('.color-btn').removeClass('btn-primary-custom text-white').addClass('btn-outline-custom');
            $(this).removeClass('btn-outline-custom').addClass('btn-primary-custom text-white');
        }
        checkSelectedVariant();
    });

    $(document).on('click', '.size-btn', function() {
        if ($(this).hasClass('btn-primary-custom')) {
            $(this).removeClass('btn-primary-custom text-white').addClass('btn-outline-custom');
        } else {
            $('.size-btn').removeClass('btn-primary-custom text-white').addClass('btn-outline-custom');
            $(this).removeClass('btn-outline-custom').addClass('btn-primary-custom text-white');
        }
        checkSelectedVariant();
    });
        
    function checkSelectedVariant() {
        const details = $('#quickAddModal').data('product-details');
        const btnConfirm = $('#btnConfirmQuickAdd');
        
        const selectedColorBtn = $('.color-btn.btn-primary-custom');
        const selectedSizeBtn = $('.size-btn.btn-primary-custom');
        
        if (selectedColorBtn.length > 0 && selectedSizeBtn.length > 0) {
            const selectedColorId = selectedColorBtn.data('id');
            const selectedSizeId = selectedSizeBtn.data('id');
            
            const detail = details.find(d => 
                (d.colorId === selectedColorId || d.color_id === selectedColorId) && 
                (d.sizeId === selectedSizeId || d.size_id === selectedSizeId)
            );
            
            if (detail) {
                const stock = detail.stockQuantity !== undefined ? detail.stockQuantity : detail.stock_quantity;
                const dId = detail.id !== undefined ? detail.id : (detail.productDetailId !== undefined ? detail.productDetailId : detail.product_detail_id);
                const price = detail.price !== undefined ? detail.price : null;
                
                const thumb = detail.thumbnailImgUrl || detail.thumbnail_img_url || detail.thumb;
                
                if (price) $('#quickAddVariantPrice').text(price.toLocaleString('vi-VN') + ' ₫');
                if (thumb) $('#quickAddVariantImage').attr('src', thumb);

                if (stock > 0) {
                    $('#stockStatus').html(`<span class="text-success fw-bold"><i class="fas fa-check-circle me-1"></i> Còn ${stock} sản phẩm trong kho</span>`);
                    
                    const colorName = selectedColorBtn.text();
                    const sizeName = selectedSizeBtn.text();
                    const variantName = `${colorName} (Size ${sizeName})`;
                    
                    btnConfirm.data('detail-id', dId).data('variant-name', variantName).prop('disabled', false);
                } else {
                    $('#stockStatus').html(`<span class="text-danger fw-bold"><i class="fas fa-times-circle me-1"></i> Hết hàng cho phân loại này</span>`);
                    btnConfirm.prop('disabled', true).removeData('detail-id');
                }
            } else {
                $('#stockStatus').html(`<span class="text-warning fw-bold"><i class="fas fa-exclamation-triangle me-1"></i> Phân loại này không tồn tại</span>`);
                btnConfirm.prop('disabled', true).removeData('detail-id');
            }
        } else {
            $('#stockStatus').html('<span class="text-muted"><i class="fas fa-info-circle me-1"></i> Vui lòng chọn cả Màu sắc và Kích cỡ.</span>');
            btnConfirm.prop('disabled', true).removeData('detail-id');
        }
    }

    // ==========================================
    // XÁC NHẬN THÊM VÀO GIỎ TỪ BÊN TRONG MODAL
    // ==========================================
    $(document).on('click', '#btnConfirmQuickAdd', function() {
        const detailId = $(this).data('detail-id');
        const variantName = $(this).data('variant-name') || '';
        const userId = localStorage.getItem("user_id");
        const token = localStorage.getItem("user_token"); // <--- THÊM DÒNG NÀY ĐỂ LẤY TOKEN
        const baseProductName = $(this).data('product-name') || 'Sản phẩm';
        const productName = variantName ? `${baseProductName} - ${variantName}` : baseProductName;

        if (!detailId) return;

        const cartRequest = {
            userId: parseInt(userId),
            productDetailId: parseInt(detailId),
            quantity: 1
        };

        $.ajax({
            url: API_URL + "cart/add", 
            type: "POST",
            headers: {
                "Authorization": "Bearer " + token // <--- THÊM DÒNG NÀY ĐỂ ĐÍNH KÈM JWT TOKEN
            },
            contentType: "application/json",
            data: JSON.stringify(cartRequest),
            success: function () {
                $('#quickAddModal').modal('hide'); 
                updateCartBadge();
                showToastSuccess(productName);
            },
            error: function (xhr) {
                console.error("Lỗi khi thêm vào giỏ:", xhr.responseText);
                alert("Hệ thống gặp sự cố khi thêm dữ liệu. Vui lòng thử lại!");
            }
        });
    });

    // ==========================================
    // HÀM LẤY TỔNG SỐ LƯỢNG TỪ MYSQL ĐỂ HIỂN THỊ
    // ==========================================
    function updateCartBadge() {
        const userId = localStorage.getItem("user_id");
        const token = localStorage.getItem("user_token"); // <--- Lấy token
        
        if (!userId || userId === "undefined") {
            $('#cartBadge').hide();
            return;
        }

        $.ajax({
            url: API_URL + "cart/count/" + userId, 
            type: "GET",
            headers: {
                "Authorization": "Bearer " + token // <--- Gửi token lên server
            },
            success: function (totalItems) {
                const $cartIconLink = $('.nav-actions .fa-shopping-cart').closest('a');

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
    // ==========================================
    // HÀM BẬT THÔNG BÁO GÓC MÀN HÌNH 
    // ==========================================
    function showToastSuccess(productName) {
        if ($('#toastContainer').length === 0) {
            $('body').append('<div id="toastContainer" class="toast-container-custom"></div>');
        }

        const toastHtml = `
            <div class="toast-msg show">
                <i class="fas fa-check-circle" style="color: #10b981; font-size: 1.5rem;"></i>
                <div>
                    <h6 class="mb-0 fw-bold text-white">Đã thêm vào giỏ MySQL!</h6>
                    <small style="color: #9ca3af;">${productName}</small>
                </div>
            </div>
        `;

        const $toast = $(toastHtml);
        $('#toastContainer').append($toast);

        setTimeout(() => {
            $toast.css('transform', 'translateX(120%)');
            setTimeout(() => $toast.remove(), 400); 
        }, 3000);
    }

    // ==========================================
    // 6. XỬ LÝ TÌM KIẾM SẢN PHẨM 
    // ==========================================
    
    const urlParams = new URLSearchParams(window.location.search);
    const currentKeyword = urlParams.get('keyword');
    if (currentKeyword) {
        $('#searchInput').val(currentKeyword);
    }

    function performSearch() {
        const keyword = $('#searchInput').val().trim();
        if (keyword !== "") {
            window.location.href = `search.html?keyword=${encodeURIComponent(keyword)}`;
        }
    }

    $(document).on('keypress', '#searchInput', function (e) {
        if (e.which === 13) { 
            e.preventDefault(); 
            performSearch();
        }
    });

    $(document).on('click', '.nav-search i', function () {
        performSearch();
    });
});});