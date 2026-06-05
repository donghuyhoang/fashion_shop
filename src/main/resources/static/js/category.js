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
    let allProducts = [];

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

            allProducts = products;
            applyFilters();
        },
        error: function(err) {
            console.error("❌ [DEBUG - LỖI API] Không gọi được API /api/products:", err);
        }
    });

    let filterState = {
        categoryId: Number(urlParams.get("id")) || null,
        brandId: null,
        sizeId: null,
        colorId: null,
        keyword: "",
        minPrice: null,
        maxPrice: null,
        sort: "default"
    };
    let currentFilterText = 'TẤT CẢ';

    // 3. BẮT SỰ KIỆN NÚT BẤM
    $(document).on('click', '.pill-btn', function(e) {
        e.preventDefault();
        
        const filterType = $(this).data("filter");
        const id = $(this).data("id");

        $(".pill-btn").removeClass("active");
        $(this).addClass("active");
        
        if (filterType === "all") {
            filterState.brandId = null;
            currentFilterText = "TẤT CẢ";
        }

        if (filterType === "brand") {
            filterState.brandId = Number(id);
            currentFilterText = $(this).text().trim().toUpperCase();
        }
        
        applyFilters();
    });

    // 4. HÀM LỌC SẢN PHẨM (TRÁI TIM CỦA VẤN ĐỀ)
    function applyFilters() {
        let filtered = [...allProducts];

        if (filterState.categoryId) {
            filtered = filtered.filter(p => Number(p.categoryId || p.category_id) === Number(filterState.categoryId));
        }

        if (filterState.brandId) {
            filtered = filtered.filter(p => Number(p.brandId || p.brand_id) === Number(filterState.brandId));
        }

        if (filterState.sizeId) {
            filtered = filtered.filter(p => {
                if (!p.details) return false;
                return p.details.some(d => Number(d.sizeId) === Number(filterState.sizeId) || Number(d.size_id) === Number(filterState.sizeId));
            });
        }

        if (filterState.colorId) {
            filtered = filtered.filter(p => {
                if (!p.details) return false;
                return p.details.some(d => Number(d.colorId) === Number(filterState.colorId) || Number(d.color_id) === Number(filterState.colorId));
            });
        }

        if (filterState.keyword && filterState.keyword.trim() !== "") {
            const keyword = filterState.keyword.toLowerCase();

            filtered = filtered.filter(p =>
                String(p.name || "").toLowerCase().includes(keyword) ||
                String(p.brandName || p.brand_name || "").toLowerCase().includes(keyword) ||
                String(p.categoryName || p.category_name || "").toLowerCase().includes(keyword)
            );
        }

        if (filterState.minPrice !== null) {
            filtered = filtered.filter(p => Number(p.price || 0) >= Number(filterState.minPrice));
        }

        if (filterState.maxPrice !== null) {
            filtered = filtered.filter(p => Number(p.price || 0) <= Number(filterState.maxPrice));
        }

        if (filterState.sort === "price_asc") {
            filtered.sort((a, b) => Number(a.price || 0) - Number(b.price || 0));
        } else if (filterState.sort === "price_desc") {
            filtered.sort((a, b) => Number(b.price || 0) - Number(a.price || 0));
        } else if (filterState.sort === "newest") {
            filtered.sort((a, b) => Number(b.id || 0) - Number(a.id || 0));
        }

        renderCategoryGrid(filtered);
        updateFilterResultInfo(filtered.length);
    }

    function updateFilterResultInfo(count) {
        if (filterState.brandId) {
            $('#categoryGridTitle').text('SẢN PHẨM ' + currentFilterText);
        } else {
            $('#categoryGridTitle').text('TẤT CẢ SẢN PHẨM');
        }
        if ($("#filterResultInfo").length) {
            $("#filterResultInfo").text(`Tìm thấy ${count} sản phẩm phù hợp`);
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
            $('#categoryGrid').html(`
                <div class="col-12 text-center py-5">
                    <h4>Không tìm thấy sản phẩm phù hợp</h4>
                    <p>Vui lòng thử bộ lọc khác.</p>
                </div>
            `);
        }
    }

    // 6. SỰ KIỆN NÚT LỌC (GIÁ VÀ SẮP XẾP)
    $("#btnApplyFilter").on("click", function () {
        const priceValue = $("#priceFilter").val();
        const sortValue = $("#sortSelect").val();

        if (priceValue === "all") {
            filterState.minPrice = null;
            filterState.maxPrice = null;
        } else {
            const parts = priceValue.split("-");
            filterState.minPrice = Number(parts[0]);
            filterState.maxPrice = Number(parts[1]);
        }

        filterState.sort = sortValue;

        applyFilters();
    });

    // 7. SỰ KIỆN TÌM KIẾM TRONG DANH MỤC
    $("#categorySearchInput").on("input", function () {
        filterState.keyword = $(this).val();
        applyFilters();
    });

    // 8. SỰ KIỆN XÓA BỘ LỌC
    $("#btnClearFilter").on("click", function () {
        filterState.brandId = null;
        filterState.sizeId = null;
        filterState.colorId = null;
        filterState.keyword = "";
        filterState.minPrice = null;
        filterState.maxPrice = null;
        filterState.sort = "default";
        currentFilterText = "TẤT CẢ";

        $(".pill-btn").removeClass("active");
        $('.pill-btn[data-filter="all"]').addClass("active");

        $("#categorySearchInput").val("");
        $("#priceFilter").val("all");
        $("#sortSelect").val("default");

        applyFilters();
    });
});