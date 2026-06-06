$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get('keyword');
    
    let currentSearchProducts = [];

    function getProductImage(product) {
        let imgSrc =
            product.thumbnailUrl ||
            product.thumbnail_url ||
            product.thumbnailImgUrl ||
            product.thumbnail_img_url ||
            product.imageUrl ||
            product.image_url ||
            product.image ||
            product.thumbnail ||
            product.thumb ||
            product.img ||
            "";

        if (!imgSrc && product.images && product.images.length > 0) {
            imgSrc =
                product.images[0].imageUrl ||
                product.images[0].image_url ||
                product.images[0].url ||
                "";
        }

        if (imgSrc && imgSrc.includes("|||")) {
            imgSrc = imgSrc.split("|||")[0].trim();
        }

        if (imgSrc && imgSrc.includes(";")) {
            imgSrc = imgSrc.split(";")[0].trim();
        }

        if (imgSrc && !imgSrc.startsWith("http") && !imgSrc.startsWith("/") && !imgSrc.startsWith("data:")) {
            imgSrc = "/" + imgSrc;
        }

        return imgSrc || "https://placehold.co/400x300/1e2129/9ca3af?text=No+Image";
    }

    if(keyword) {
        $('#searchKeyword').text('"' + keyword + '"');
        
        $.ajax({
            url: "/api/products",
            type: "GET",
            success: function(products) {
                console.log("Kết quả tìm kiếm:", products);
                if (products && products.length > 0) {
                    console.log("Sản phẩm đầu tiên:", products[0]);
                }
                currentSearchProducts = products.filter(p => p.name.toLowerCase().includes(keyword.toLowerCase()));
                applySearchSort();
            }
        });
    }

    function applySearchSort() {
        let filtered = [...currentSearchProducts];
        const sortVal = $('#sortSearchSelect').val();
        
        if (sortVal === 'price_asc') {
            filtered.sort((a, b) => (a.price || 0) - (b.price || 0));
        } else if (sortVal === 'price_desc') {
            filtered.sort((a, b) => (b.price || 0) - (a.price || 0));
        } else {
            filtered.sort((a, b) => b.id - a.id); // Mặc định mới nhất
        }

        if(filtered.length > 0) {
                    let html = "";
                    filtered.forEach(product => {
                        const realId = product.id || product.productId;
                        
                        let imgSrc = getProductImage(product);
                        
                        html += `
                            <div class="col-md-4 col-lg-3 mb-4">
                                <div class="card h-100 product-card border-0 shadow-sm dark-card">
                                    <a href="product-detail.html?id=${realId}" class="product-img text-decoration-none d-block">
                                        <img src="${imgSrc}" alt="${product.name}" onerror="this.onerror=null; this.src='https://placehold.co/400x300/1e2129/9ca3af?text=No+Image';">
                                    </a>
                                    <div class="card-body d-flex flex-column">
                                        <a href="product-detail.html?id=${realId}" class="text-decoration-none text-light">
                                            <h6 class="product-name text-truncate" title="${product.name}">${product.name}</h6>
                                        </a>
                                        <p class="product-category">${product.brandName || 'Sneaker'}</p>
                                        <div class="d-flex justify-content-between align-items-center mt-auto">
                                            <h5 class="product-price mb-0">${product.price ? product.price.toLocaleString('vi-VN') : 0} ₫</h5>
                                            <button class="btn-buy" data-id="${realId}" title="Thêm vào giỏ">
                                                <i class="fas fa-shopping-cart"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `;
                    });
                    $('#searchResultsGrid').html(html);
                } else {
                    $('#searchResultsGrid').html('<div class="col-12 text-center text-muted mt-5"><h5>Không tìm thấy sản phẩm!</h5></div>');
                }
    }

    $("#sortSearchSelect").change(function() {
        applySearchSort();
    });
});