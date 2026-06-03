$(document).ready(function() {
    console.log("🚀 [DEBUG] Đã load xong file category.js");
    
    const urlParams = new URLSearchParams(window.location.search);
    const categoryId = parseInt(urlParams.get('id'));

    if (!categoryId) {
        console.warn("⚠️ [DEBUG] Không có categoryId trên URL, quay về trang chủ.");
        window.location.href = "index.html";
        return;
    }

    console.log("👉 [DEBUG] Đang ở trang Danh mục có ID =", categoryId);

    // 1. Highlight menu & Đổi Banner
    $('.nav-links a').removeClass('active');
    $(`.nav-links a[href="category.html?id=${categoryId}"]`).addClass('active');

    const categoryData = {
        1: { title: "RUNNING", desc: "Đẩy lùi giới hạn...", bg: "https://images.unsplash.com/photo-1536922246289-88c42f957773?q=80&w=1920", badge: "ENDURANCE & SPEED" },
        2: { title: "FOOTBALL", desc: "Tốc độ, kiểm soát...", bg: "https://images.unsplash.com/photo-1511886929837-354d827aae26?q=80&w=1920", badge: "PITCH PERFECT" },
        3: { title: "BASKETBALL", desc: "Sự kết hợp hoàn hảo...", bg: "https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=1920", badge: "COURT READY" },
        4: { title: "SNEAKERS", desc: "Biểu tượng thời trang...", bg: "https://images.unsplash.com/photo-1552346154-21d32810baa3?q=80&w=1920", badge: "STREETWEAR ICON" }
    };

    const data = categoryData[categoryId] || { title: "COLLECTION", desc: "Khám phá bộ sưu tập.", bg: "", badge: "EXCLUSIVE" };
    $('#categoryTitle').text(data.title);
    $('#categoryDesc').text(data.desc);
    $('#categoryBadge').text(data.badge);

    const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";
    let currentCategoryProducts = [];

    // 2. GỌI API ĐỂ KIỂM TRA DỮ LIỆU TỪ BACKEND
    console.log("⏳ [DEBUG] Bắt đầu gọi API: /api/products");
    $.ajax({
        url: "/api/products",
        type: "GET",
        success: function(products) {
            console.log("✅ [DEBUG - BACKEND TRẢ VỀ] Đã tải được tổng cộng:", products.length, "sản phẩm từ Database.");
            if (products.length > 0) {
                console.log("🔍 [DEBUG - SOI DATA] Cấu trúc của 1 sản phẩm trông như thế này:", products[0]);
            }

            // Lọc ra sản phẩm của danh mục hiện tại
            currentCategoryProducts = products.filter(p => p.categoryId === categoryId || p.category_id === categoryId);
            console.log(`📦 [DEBUG] Số sản phẩm thuộc danh mục ${categoryId} là:`, currentCategoryProducts.length);
            
            applyFiltersAndSort();
        },
        error: function(err) {
            console.error("❌ [DEBUG - LỖI API] Không gọi được API /api/products:", err);
        }
    });

    let currentFilterType = 'all';
    let currentFilterId = null;
    let currentFilterText = 'TẤT CẢ';

    // 3. BẮT SỰ KIỆN NÚT BẤM
    $(document).on('click', '.pill-btn', function(e) {
        e.preventDefault();
        
        $(".pill-btn").removeClass("active");
        $(this).addClass("active");
        
        let text = $(this).text().trim().toUpperCase();
        console.log("-----------------------------------------");
        console.log("🖱️ [DEBUG - CLICK] Bạn vừa bấm nút:", text);
        
        if (text.includes("TẤT CẢ") || text.includes("ALL")) {
            currentFilterType = "all";
            currentFilterId = null;
            currentFilterText = "TẤT CẢ";
        } else {
            currentFilterType = "brand";
            currentFilterText = text;
            
            // Ép cứng ID theo Database
            if (text.includes("NIKE")) currentFilterId = 1;
            else if (text.includes("ADIDAS")) currentFilterId = 2;
            else if (text.includes("MIZUNO")) currentFilterId = 3;
            else if (text.includes("PUMA")) currentFilterId = 4;
            else currentFilterId = parseInt($(this).data("id"));
        }
        
        console.log("🎯 [DEBUG - TIÊU CHÍ LỌC] Loại lọc:", currentFilterType, "| Tìm Brand ID:", currentFilterId);
        applyFiltersAndSort();
    });

    // 4. HÀM LỌC SẢN PHẨM (TRÁI TIM CỦA VẤN ĐỀ)
    function applyFiltersAndSort() {
        console.log("⚙️ [DEBUG] Bắt đầu chạy hàm lọc. Dữ liệu gốc có:", currentCategoryProducts.length, "sản phẩm");
        
        try {
            let filtered = [...currentCategoryProducts];
            
            if (currentFilterType === 'brand') {
                filtered = filtered.filter(p => {
                    const bName = String(p.brandName || p.brand_name || "").toUpperCase();
                    const pName = String(p.name || "").toUpperCase();
                    const bId = p.brandId || p.brand_id;
                    
                    let isMatch = (bId === currentFilterId) || bName.includes(currentFilterText) || pName.includes(currentFilterText);
                    
                    // Chỉ in log vài sản phẩm để đỡ rối mắt
                    if (currentCategoryProducts.indexOf(p) < 3) {
                        console.log(`   + So sánh SP [${pName}]: Có brandId=${bId}, brandName=${bName} --> Khớp? ${isMatch}`);
                    }
                    
                    return isMatch;
                });
                $('#categoryGridTitle').text('SẢN PHẨM ' + currentFilterText);
            } else {
                $('#categoryGridTitle').text('TẤT CẢ SẢN PHẨM');
            }
            
            console.log("✅ [DEBUG] Sau khi lọc, CÒN LẠI:", filtered.length, "sản phẩm.");
            renderCategoryGrid(filtered);
            
        } catch (error) {
            console.error("❌ [DEBUG - LỖI HÀM LỌC]: ", error);
        }
    }

    // 5. HÀM VẼ GIAO DIỆN
    function renderCategoryGrid(productsToRender) {
        if(productsToRender.length > 0) {
            let html = "";
            productsToRender.forEach(p => {
                let imgSrc = p.thumbnailUrl || p.thumb || fallbackImg;
                if (imgSrc.includes('|||')) imgSrc = imgSrc.split('|||')[0].trim();
                
                html += `
                    <div class="col-md-4 col-lg-3 mb-4">
                        <div class="card h-100 product-card border-0 shadow-sm dark-card">
                            <a href="product-detail.html?id=${p.id}" class="product-img d-block">
                                <img src="${imgSrc}" onerror="this.onerror=null; this.src='${fallbackImg}';">
                            </a>
                            <div class="card-body d-flex flex-column">
                                <h6 class="product-name text-truncate text-light">${p.name}</h6>
                                <p class="product-category">${p.brandName || 'N/A'}</p>
                                <h5 class="product-price mt-auto text-light">${p.price ? p.price.toLocaleString('vi-VN') : 0} ₫</h5>
                            </div>
                        </div>
                    </div>`;
            });
            $('#categoryGrid').html(html);
        } else {
            $('#categoryGrid').html('<div class="col-12 text-center mt-5 mb-5"><h5>Không có sản phẩm nào khớp!</h5></div>');
        }
    }
});