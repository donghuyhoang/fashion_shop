$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get('keyword');
    
    const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";

    let currentSearchProducts = [];

    if(keyword) {
        $('#searchKeyword').text('"' + keyword + '"');
        
        $.ajax({
            url: "/api/products",
            type: "GET",
            success: function(products) {
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
                        
						let imgSrc = product.thumbnailUrl || product.thumb || product.imageUrl || product.image || product.thumbnailImgUrl || product.thumbnail_img_url;
						if (!imgSrc || imgSrc.trim() === "") {
						    imgSrc = fallbackImg;
						} else if (imgSrc.includes('|||') && !imgSrc.startsWith('data:image')) {
						    imgSrc = imgSrc.split('|||')[0].trim(); // Bổ sung bóc tách |||
						} else if (imgSrc.includes(';') && !imgSrc.startsWith('data:image')) {
						    imgSrc = imgSrc.split(';')[0].trim();
						}
                        html += `
                            <div class="col-md-4 col-lg-3 mb-4">
                                <div class="card h-100 product-card border-0 shadow-sm dark-card">
                                    <a href="product-detail.html?id=${realId}" class="product-img text-decoration-none d-block">
                                        <img src="${imgSrc}" alt="${product.name}" onerror="this.onerror=null; this.src='${fallbackImg}';">
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