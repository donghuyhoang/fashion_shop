$(document).ready(function () {
    const API_URL = "/api/";

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
    // 2. HÀM LẤY DATA VÀ HIỂN THỊ 24 ĐÔI GIÀY
    // ==========================================
    function loadStorytellingCollections() {
        $.ajax({
            url: API_URL + "products",
            type: "GET",
            success: function (allProducts) {
                let productsForStorytelling = [...allProducts];
                const extraImages = [
                    "images/air-jordan-1-high-chicago.jpg", "images/nike-air-force-1-low-white.jpg",
                    "images/nike-air-max-90-infrared.jpg", "images/nike-dunk-low-panda.jpg",
                    "images/nike-zoom-tempo-next-pink.jpg", "images/nike-air-max-97-silver-bullet.jpg",
                    "images/nike-blazer-mid-77-white-black.jpg", "images/nike-cortez-forrest-gump.jpg",
                    "images/nike-react-vision-gravity-purple.jpg", "images/nike-sb-dunk-low-blue-lobster.jpg",
                    "images/nike-x-sacai-ldwaffle-green-orange.jpg", "images/nike-metcon-black-gum.jpg",
                    "images/nike-air-max-1-university-red.jpg", "images/nike-air-presto-triple-black.jpg",
                    "images/nike-air-huarache-scream-green.jpg", "images/nike-air-max-90-black-orange.jpg"
                ];

                let i = 0;
                while (productsForStorytelling.length < 24) {
                    productsForStorytelling.push({
                        id: 9990 + i, 
                        name: "IMPECCABLE Sneaker #" + (i + 1), 
                        brandName: "Sneaker Peak",
                        price: 3500000 + (i * 150000), 
                        thumb: extraImages[i % extraImages.length] 
                    });
                    i++;
                }

                renderCardsToSpecificGrid(productsForStorytelling.slice(0, 4), "#newArrivalsGrid");
                renderCardsToSpecificGrid(productsForStorytelling.slice(4, 8), "#runningGrid");
                renderCardsToSpecificGrid(productsForStorytelling.slice(8, 12), "#basketballGrid");
                renderCardsToSpecificGrid(productsForStorytelling.slice(12, 16), "#luxuryGrid");
                renderCardsToSpecificGrid(productsForStorytelling.slice(16, 20), "#collabGrid");
                renderCardsToSpecificGrid(productsForStorytelling.slice(20, 24), "#ecoGrid");
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
            const imgSrc = product.thumb ? product.thumb : localImages[index % localImages.length];

            html += `
                <div class="col-md-4 col-lg-3 mb-4">
                    <div class="card h-100 product-card border-0 shadow-sm dark-card">
                        <a href="product-detail.html?id=${product.id}" class="product-img text-decoration-none d-block">
                            <img src="${imgSrc}" alt="${product.name}">
                        </a>
                        <div class="card-body d-flex flex-column">
                            <a href="product-detail.html?id=${product.id}" class="text-decoration-none text-light">
                                <h6 class="product-name text-truncate" title="${product.name}">${product.name}</h6>
                            </a>
                            <p class="product-category">${product.brandName || 'Sneaker'}</p>
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
    // 3. XỬ LÝ SỰ KIỆN CLICK CATEGORY PILLS (CUỘN TRANG)
    // ==========================================
    $(".pill-btn").click(function() {
        $(".pill-btn").removeClass("active");
        $(this).addClass("active");
        const targetId = $(this).attr("data-target");

        if (targetId && $(targetId).length) {
            const headerHeight = $('.main-navbar').outerHeight();
            $('html, body').animate({
                scrollTop: $(targetId).offset().top - headerHeight - 15 
            }, 600); 
        }
    });

    // ==========================================
    // 4. XỬ LÝ GIAO DIỆN ĐĂNG NHẬP (HEADER)
    // ==========================================
    function updateHeaderAfterLogin() {
        const userName = localStorage.getItem("user_name"); 

        if (userName) {
            $("#userArea").html(`
                <div class="dropdown">
                    <a href="#" class="dropdown-toggle text-white text-decoration-none" data-bs-toggle="dropdown" style="font-weight: 600; display: flex; align-items: center; gap: 8px;">
                        <i class="fas fa-user-circle text-success" style="font-size: 1.2rem;"></i> 
                        ${userName}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end mt-3" style="background-color: #1e2129; border: 1px solid #3a3f4a; border-radius: 12px;">
                        <li><a class="dropdown-item text-light" href="#">Hồ sơ của tôi</a></li>
                        <li><a class="dropdown-item text-light" href="#">Đơn mua</a></li>
                        <li><hr class="dropdown-divider" style="border-color: #3a3f4a;"></li>
                        <li><a class="dropdown-item text-danger fw-bold" href="#" id="btnLogout"><i class="fas fa-sign-out-alt me-2"></i> Đăng xuất</a></li>
                    </ul>
                </div>
            `);
        }
    }

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
        
        // 🔥 ĐÂY CHÍNH LÀ DÒNG QUAN TRỌNG NHẤT ĐỂ SỬA LỖI 🔥
        localStorage.removeItem("user_id"); 
        
        // (Tùy chọn) Xóa luôn giỏ hàng tạm nếu bạn còn dùng
        localStorage.removeItem("user_cart"); 

        // Load lại trang
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

        // =========================================================
        // LUỒNG MỚI: HIỂN THỊ POPUP QUICK ADD THAY VÌ ADD TRỰC TIẾP
        // =========================================================
        
        // 1. Cập nhật tên sản phẩm lên Modal
        $('#quickAddTitle').text(productName);
        
        // 2. Khóa nút Xác nhận và dọn dẹp data cũ
        $('#btnConfirmQuickAdd').prop('disabled', true).removeData('detail-id');
        $('#quickAddOptions').html('<p class="text-muted"><i class="fas fa-spinner fa-spin"></i> Đang tải thông tin phân loại...</p>');
        
        // 3. Bật Modal hiển thị
        $('#quickAddModal').modal('show');
        
        // CHẶN GỌI API NẾU LÀ SẢN PHẨM DEMO (ID >= 9990)
        if (productId >= 9990) {
            $('#quickAddOptions').html('<div class="text-warning text-center mt-3"><i class="fas fa-exclamation-triangle mb-2 fa-2x"></i><p>Đây là sản phẩm Demo dùng để trang trí giao diện.<br>Bạn hãy đăng nhập Admin và thêm sản phẩm thật để trải nghiệm mua hàng nhé!</p></div>');
            return;
        }

        // 4. Kéo dữ liệu của các phiên bản (Size/Color) từ Backend
        $.ajax({
            url: API_URL + "product-details/product/" + productId,
            type: "GET",
            success: function(details) {
                if (!details || details.length === 0) {
                    $('#quickAddOptions').html('<p class="text-danger">Sản phẩm này hiện đang hết hàng.</p>');
                    return;
                }
                
                let html = '<div class="d-flex flex-wrap gap-2">';
                details.forEach(detail => {
                    // Xử lý đọc tên trường phòng trường hợp API trả về camelCase (sizeId) hoặc snake_case (size_id)
                    const sId = detail.sizeId !== undefined ? detail.sizeId : detail.size_id;
                    const cId = detail.colorId !== undefined ? detail.colorId : detail.color_id;
                    const dId = detail.id !== undefined ? detail.id : (detail.productDetailId !== undefined ? detail.productDetailId : detail.product_detail_id);
                    const stock = detail.stockQuantity !== undefined ? detail.stockQuantity : detail.stock_quantity;
                    
                    // Mapping ID ra Tên gọi hiển thị
                    const sizeObj = sizesCache.find(s => s.id === sId);
                    const colorObj = colorsCache.find(c => c.id === cId);
                    
                    const sName = sizeObj ? sizeObj.name : sId;
                    const cName = colorObj ? colorObj.name : cId;
                    
                    // Xử lý giao diện cho nút nếu hết hàng
                    const disabledState = stock <= 0 ? 'disabled' : '';
                    const stockBadge = stock <= 0 ? '<small class="text-danger">(Hết)</small>' : `<small class="text-muted">(Còn ${stock})</small>`;
                    
                    html += `
                        <button type="button" class="btn btn-outline-custom variant-btn ${disabledState}" data-detail-id="${dId}" ${disabledState}>
                            Size ${sName} - ${cName} ${stockBadge}
                        </button>
                    `;
                });
                html += '</div>';
                $('#quickAddOptions').html(html);
            },
            error: function() {
                $('#quickAddOptions').html('<p class="text-danger">Không thể kết nối đến máy chủ. Vui lòng thử lại sau.</p>');
            }
        });
    });

    // ==========================================
    // BẮT SỰ KIỆN KHI ẤN CHỌN PHIÊN BẢN
    // ==========================================
    $(document).on('click', '.variant-btn:not(.disabled)', function() {
        $('.variant-btn').removeClass('btn-primary-custom text-white').addClass('btn-outline-custom');
        $(this).removeClass('btn-outline-custom').addClass('btn-primary-custom text-white');
        
        // Nạp ID của phiên bản vào bụng của Nút "Thêm vào giỏ"
        const detailId = $(this).data('detail-id');
        $('#btnConfirmQuickAdd').data('detail-id', detailId).prop('disabled', false);
    });

    // ==========================================
    // XÁC NHẬN THÊM VÀO GIỎ TỪ BÊN TRONG MODAL
    // ==========================================
    $(document).on('click', '#btnConfirmQuickAdd', function() {
        const detailId = $(this).data('detail-id');
        const userId = localStorage.getItem("user_id");
        const productName = $('#quickAddTitle').text();

        if (!detailId) return;

        const cartRequest = {
            userId: parseInt(userId),
            productDetailId: parseInt(detailId),
            quantity: 1
        };

        $.ajax({
            url: API_URL + "cart/add", 
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(cartRequest),
            success: function () {
                $('#quickAddModal').modal('hide'); // Tắt Modal đi
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
        
        if (!userId || userId === "undefined") {
            $('#cartBadge').hide();
            return;
        }

        // Đổi API đếm số lượng: truyền userId thay vì email
        $.ajax({
            url: API_URL + "cart/count/" + userId, 
            type: "GET",
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
    // HÀM BẬT THÔNG BÁO GÓC MÀN HÌNH (Giữ nguyên)
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
            window.location.href = `search.html?keyword=${encodeURIComponent(keyword)}`;
        }
    }

    // Bắt sự kiện ấn phím Enter trong ô input
    $(document).on('keypress', '#searchInput', function (e) {
        if (e.which === 13) { 
            e.preventDefault(); 
        }
            performSearch();
    });

    // Bắt sự kiện click vào icon kính lúp
    $(document).on('click', '.nav-search i', function () {
        performSearch();
    });

}); // <-- Đóng hàm an toàn