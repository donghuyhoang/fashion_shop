$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const categoryId = parseInt(urlParams.get('id'));

    if (!categoryId) {
        window.location.href = "index.html";
        return;
    }

    // 1. Highlight menu đang được chọn
    $('.nav-links a').removeClass('active');
    $(`.nav-links a[href="category.html?id=${categoryId}"]`).addClass('active');

    // 2. Data ảo cho từng chuyên mục
    const categoryData = {
        1: { 
            title: "RUNNING", 
            desc: "Đẩy lùi giới hạn với những đôi giày chạy bộ hiệu suất cao, mang lại sự êm ái và tốc độ vượt trội.", 
            bg: "https://images.unsplash.com/photo-1536922246289-88c42f957773?q=80&w=1920&auto=format&fit=crop",
            badge: "ENDURANCE & SPEED"
        },
        2: { 
            title: "FOOTBALL", 
            desc: "Tốc độ, kiểm soát và sức mạnh. Sẵn sàng tỏa sáng trên sân cỏ cùng bộ sưu tập mới nhất.", 
            bg: "https://images.unsplash.com/photo-1511886929837-354d827aae26?q=80&w=1920&auto=format&fit=crop",
            badge: "PITCH PERFECT"
        },
        3: { 
            title: "BASKETBALL", 
            desc: "Sự kết hợp hoàn hảo giữa hiệu năng trên sân bóng và phong cách đường phố đặc trưng.", 
            bg: "https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=1920&auto=format&fit=crop",
            badge: "COURT READY"
        },
        4: { 
            title: "SNEAKERS", 
            desc: "Biểu tượng thời trang đường phố. Thể hiện cá tính riêng với những phiên bản cực chất.", 
            bg: "https://images.unsplash.com/photo-1552346154-21d32810baa3?q=80&w=1920&auto=format&fit=crop",
            badge: "STREETWEAR ICON"
        }
    };

    // Cập nhật giao diện Banner
    const data = categoryData[categoryId] || { title: "COLLECTION", desc: "Khám phá bộ sưu tập cao cấp.", bg: "https://images.unsplash.com/photo-1552346154-21d32810baa3?q=80&w=1920&auto=format&fit=crop", badge: "EXCLUSIVE" };
    $('#categoryTitle').text(data.title);
    $('#categoryDesc').text(data.desc);
    $('#categoryHeroBg').css('background-image', `url('${data.bg}')`);
    $('#categoryBadge').text(data.badge);

    // 3. Gọi API hiển thị sản phẩm
    const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";
    let currentCategoryProducts = [];

    $.ajax({
        url: "/api/products",
        type: "GET",
        success: function(products) {
            currentCategoryProducts = products.filter(p => p.categoryId === categoryId || p.category_id === categoryId);
            applyFiltersAndSort();
        }
    });

    function renderCategoryGrid(productsToRender) {
        if(productsToRender.length > 0) {
                let html = "";
                productsToRender.forEach(p => {
					let imgSrc = p.thumbnailUrl || p.thumb || p.imageUrl || p.image || p.thumbnailImgUrl || p.thumbnail_img_url;

					if (!imgSrc || imgSrc.trim() === "") {
					    imgSrc = fallbackImg;
					} else {
					    // Thêm logic xử lý phân cách ||| giống app.js
					    if (imgSrc.includes('|||')) {
					        imgSrc = imgSrc.split('|||')[0].trim();
					    } else if (imgSrc.includes(';') && !imgSrc.startsWith('data:image')) {
					        imgSrc = imgSrc.split(';')[0].trim();
					    }
					}
                    html += `
                        <div class="col-md-4 col-lg-3 mb-4">
                            <div class="card h-100 product-card border-0 shadow-sm dark-card">
                                <a href="product-detail.html?id=${p.id}" class="product-img text-decoration-none d-block">
                                    <img src="${imgSrc}" alt="${p.name}" onerror="this.onerror=null; this.src='${fallbackImg}';">
                                </a>
                                <div class="card-body d-flex flex-column">
                                    <a href="product-detail.html?id=${p.id}" class="text-decoration-none text-light">
                                        <h6 class="product-name text-truncate">${p.name}</h6>
                                    </a>
                                    <p class="product-category">${p.brandName || 'Sneaker'}</p>
                                    <div class="d-flex justify-content-between align-items-center mt-auto">
                                        <h5 class="product-price mb-0">${p.price ? p.price.toLocaleString('vi-VN') : 0} ₫</h5>
                                        <button class="btn-buy" data-id="${p.id}" title="Thêm vào giỏ"><i class="fas fa-shopping-cart"></i></button>
                                    </div>
                                </div>
                            </div>
                        </div>`;
                });
                $('#categoryGrid').html(html);
            } else {
                $('#categoryGrid').html('<div class="col-12 text-center text-muted mt-5 mb-5"><i class="fas fa-box-open fa-3x mb-3"></i><h5>Chưa có sản phẩm nào trong danh mục này!</h5></div>');
            }
    }

    let currentFilterType = 'all';
    let currentFilterId = null;
    let currentFilterText = '';

    function applyFiltersAndSort() {
        let filtered = [...currentCategoryProducts];
        
        // 1. Áp dụng lọc theo hãng
        if (currentFilterType === 'brand') {
            filtered = filtered.filter(p => {
                const bName = p.brandName || p.brand_name || "";
                return bName.toUpperCase().includes(currentFilterText.toUpperCase()) || p.brandId === currentFilterId || p.brand_id === currentFilterId;
            });
            $('#categoryGridTitle').text('SẢN PHẨM ' + currentFilterText);
        } else {
            $('#categoryGridTitle').text('TẤT CẢ SẢN PHẨM');
        }
        
        // 1.5. Áp dụng lọc theo khoảng giá
        const priceFilter = $('#priceFilterSelect').val();
        if (priceFilter === 'under_1m') {
            filtered = filtered.filter(p => (p.price || 0) < 1000000);
        } else if (priceFilter === '1m_to_3m') {
            filtered = filtered.filter(p => (p.price || 0) >= 1000000 && (p.price || 0) <= 3000000);
        } else if (priceFilter === 'over_3m') {
            filtered = filtered.filter(p => (p.price || 0) > 3000000);
        }

        // 2. Áp dụng sắp xếp theo giá
        const sortVal = $('#sortSelect').val();
        if (sortVal === 'price_asc') {
            filtered.sort((a, b) => (a.price || 0) - (b.price || 0));
        } else if (sortVal === 'price_desc') {
            filtered.sort((a, b) => (b.price || 0) - (a.price || 0));
        }
        
        renderCategoryGrid(filtered);
    }

    // 4. Bắt sự kiện click bộ lọc Brand
    $(".pill-btn").click(function() {
        $(".pill-btn").removeClass("active");
        $(this).addClass("active");
        
        currentFilterType = $(this).data("filter");
        currentFilterId = parseInt($(this).data("id"));
        currentFilterText = $(this).text().trim();
        
        applyFiltersAndSort();
    });

    // 5. Bắt sự kiện thay đổi Sắp xếp & Khoảng giá
    $("#sortSelect, #priceFilterSelect").change(function() {
        applyFiltersAndSort();
    });
});
