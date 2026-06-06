$(document).ready(function() {
    // ==========================================
    // 0. KHỞI TẠO VÀ KIỂM TRA QUYỀN TRUY CẬP
    // ==========================================
    const userRole = localStorage.getItem("user_role");
    if (userRole != 1) { // 1 là Role Admin
        alert("Bạn không có quyền truy cập trang Quản trị. Vui lòng đăng nhập!");
        window.location.href = "login.html";
        return;
    }

    const adminName = localStorage.getItem("user_name") || "Admin";
    $("#adminNameDisplay, #adminNameDisplay2, #adminNameDisplay3, #adminNameDisplay4").text(adminName);
    $("#adminAvatar, #adminAvatar2, #adminAvatar3, #adminAvatar4").text(adminName.charAt(0).toUpperCase());

    const API_URL = "/api/";
    const API_ORDERS = "/api/orders/";
    let revenueChart;

    // 1. Tải các con số thống kê tổng quan (Tổng Users, Đơn hàng, Doanh thu)
    function loadDashboardStats() {
        $.ajax({
            url: API_ORDERS + "dashboard/stats",
            type: "GET",
            success: function(stats) {
                $('#totalUsers').text(stats.totalUsers || 0);
                $('#totalOrders').text(stats.totalOrders || 0);
                $('#totalRevenue').text(`${(stats.totalRevenue || 0).toLocaleString('vi-VN')} ₫`);
            },
            error: function(err) {
                console.error("Lỗi lấy thống kê tổng quan:", err);
            }
        });
    }

    // 2. Vẽ biểu đồ doanh thu theo tháng bằng Chart.js
    function loadRevenueChart(year) {
        $.ajax({
            url: API_ORDERS + "dashboard/revenue/" + year,
            type: "GET",
            success: function(data) {
                // Khởi tạo mảng 12 tháng mặc định là 0 đ
                const monthlyData = new Array(12).fill(0);
                
                // Đổ dữ liệu từ API vào đúng vị trí tháng
                if (Array.isArray(data)) {
                    data.forEach(item => {
                        // Nếu tháng hợp lệ từ 1 đến 12 thì nhét dữ liệu vào
                        if (item.month >= 1 && item.month <= 12) {
                            monthlyData[item.month - 1] = item.revenue;
                        }
                    });
                }

                renderChart(monthlyData, year);
            },
            error: function(err) {
                console.error("Lỗi lấy dữ liệu biểu đồ:", err);
            }
        });
    }

    function renderChart(data, year) {
        const container = document.querySelector('#revenueChart');
        if (!container) return; // Không tìm thấy chỗ vẽ biểu đồ thì bỏ qua

        // Hủy biểu đồ cũ nếu đã vẽ
        if (revenueChart) {
            // Cập nhật mượt mà dữ liệu mới thay vì xóa đi vẽ lại (cách chuẩn của ApexCharts)
            revenueChart.updateSeries([{
                name: 'Doanh thu',
                data: data
            }]);
            return;
        }

        // Cấu hình vẽ biểu đồ bằng thư viện ApexCharts (giữ nguyên)
        var options = {
            series: [{
                name: 'Doanh thu',
                data: data
            }],
            chart: {
                type: 'area', // Chuyển sang dạng đường diện tích
                height: 350,
                fontFamily: 'Montserrat, sans-serif',
                toolbar: { show: false } // Ẩn thanh công cụ tải hình ảnh
            },
            colors: ['#3b82f6'], // Màu xanh dương thương hiệu
            fill: {
                type: 'gradient', // Tạo bóng đổ Gradient bên dưới biểu đồ
                gradient: {
                    shadeIntensity: 1,
                    opacityFrom: 0.6,
                    opacityTo: 0.05,
                    stops: [0, 90, 100]
                }
            },
            dataLabels: { enabled: false }, // Ẩn các số ghi cứng trên điểm
            stroke: {
                curve: 'smooth', // Quan trọng: làm mềm đường thẳng
                width: 3
            },
            xaxis: {
                categories: ['Th. 1', 'Th. 2', 'Th. 3', 'Th. 4', 'Th. 5', 'Th. 6', 'Th. 7', 'Th. 8', 'Th. 9', 'Th. 10', 'Th. 11', 'Th. 12'],
                axisBorder: { show: false },
                axisTicks: { show: false }
            },
            yaxis: {
                labels: {
                    formatter: function (value) {
                        // Rút gọn thành đơn vị "Tr" cho dễ đọc
                        if (value >= 1000000) return (value / 1000000) + ' Tr';
                        return value.toLocaleString('vi-VN');
                    }
                }
            },
            tooltip: {
                theme: 'light',
                y: {
                    formatter: function (val) {
                        // Hover chuột vào sẽ thấy full tiền + kí hiệu ₫
                        return val.toLocaleString('vi-VN') + " ₫"
                    }
                }
            }
        };

        revenueChart = new ApexCharts(container, options);
        revenueChart.render();
    }

    // 3. Hàm gộp chung toàn cục để gọi cả 2
    function refreshDashboard() {
        // Luôn tải lại số liệu tổng quan để cập nhật dữ liệu mới nhất
        loadDashboardStats();

        const $revenueChart = $('#revenueChart');
        if ($revenueChart.length) {
            // Tránh vẽ biểu đồ bằng ApexCharts khi thẻ đang bị ẩn (display: none) gây lỗi kích thước 0x0
            if ($revenueChart.is(':hidden')) return;
            
            console.log(">>> [Báo cáo] TÌM THẤY thẻ chứa biểu đồ, đang tiến hành nạp dữ liệu API...");
            const currentYear = new Date().getFullYear();
            loadRevenueChart(currentYear);
        } else if ($revenueChart.length === 0) {
             console.warn(">>> [Báo cáo] CẢNH BÁO: Không tìm thấy thẻ <div id='revenueChart'>!");
        }
    }

    // ==========================================
    // 4. TẢI DỮ LIỆU ĐỘNG CHO FORM THÊM SẢN PHẨM
    // ==========================================
    function loadFormOptions() {
        const selects = {
            colors: { url: "/api/colors", target: '#colorId, select[name="colorId"], select[name="color_id"], #colorSelect, #color, .color-select' },
            sizes: { url: "/api/sizes", target: '#sizeId, select[name="sizeId"], select[name="size_id"], #sizeSelect, #size, .size-select' },
            brands: { url: "/api/brands", target: '#brandId, #searchBrand, select[name="brandId"], select[name="brand_id"], #brandSelect, #brand, .brand-select' },
            categories: { url: "/api/categories", target: '#categoryId, #searchCategory, select[name="categoryId"], select[name="category_id"], #categorySelect, #category, .category-select' }
        };

        for (const key in selects) {
            const config = selects[key];
            const $targets = $(config.target);
            
            if ($targets.length > 0) { 
                $.get(config.url).done(function(data) {
                    if (data && data.length > 0) {
                        $targets.each(function() {
                            const $t = $(this);
                            // Bỏ qua nếu đã tải danh sách (tránh ghi đè khi đang Sửa sản phẩm)
                            if ($t.children('option').length > 1) return;

                            const isSearch = $t.attr('id') && $t.attr('id').startsWith('search');
                            let defaultText = "-- Chọn lựa --";
                            if (key === 'colors') defaultText = "-- Chọn Màu sắc --";
                            if (key === 'sizes') defaultText = "-- Chọn Kích cỡ --";
                            if (key === 'brands') defaultText = "-- Chọn Thương hiệu --";
                            if (key === 'categories') defaultText = "-- Chọn Danh mục --";

                            let html = isSearch ? `<option value="">Tất cả</option>` : `<option value="" disabled selected>${defaultText}</option>`;
                            data.forEach(item => {
                                let itemName = item.name || item.value || item.size || item.color || item.id;
                                html += `<option value="${item.id}">${itemName}</option>`;
                            });
                            $t.html(html);
                        });
                    }
                }).fail(function() { console.warn("Lỗi tải dữ liệu cho " + key); });
            }
        }
    }

    // ==========================================
    // 5. XỬ LÝ SỰ KIỆN VÀ KHỞI CHẠY
    // ==========================================

    // Tự động chạy lần đầu tiên trang load
    console.log(">>> [Báo cáo] File admin_dashboard.js đã được nhúng thành công.");
    refreshDashboard(); // Tải dashboard nếu nó là tab mặc định
    loadFormOptions(); // Tải các option cho form nếu có sẵn
    searchProducts(); // Tải trước danh sách sản phẩm ngay khi vào

    // ==========================================
    // 5.1. XỬ LÝ CHUYỂN HƯỚNG "THÊM SẢN PHẨM" TỪ TRANG CHỦ
    // ==========================================
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('action') === 'add_product') {
        const autoBrand = localStorage.getItem("admin_auto_select_brand");
        const autoCategory = localStorage.getItem("admin_auto_select_category");
        
        setTimeout(() => {
            $("#btnAddNew").click(); // Mở modal thêm sản phẩm
            
            // Đợi một chút để API loadFormOptions() hoàn tất việc nhét các thẻ option vào select
            setTimeout(() => {
                if (autoBrand) $("#brandId").val(autoBrand);
                if (autoCategory) $("#categoryId").val(autoCategory);
                
                localStorage.removeItem("admin_auto_select_brand");
                localStorage.removeItem("admin_auto_select_category");
            }, 600);
        }, 300);
    }

    // Bắt sự kiện khi một tab được hiển thị (đáng tin cậy hơn click)
    $(document).on('shown.bs.tab', 'a[data-bs-toggle="tab"]', function (e) {
        // Nếu tab được chuyển đến là tab chứa dashboard, hãy làm mới nó
        if ($(e.target).attr('href') === '#dashboard' || $(e.target).data('bs-target') === '#dashboard') {
            refreshDashboard();
        }
        loadFormOptions(); // Luôn thử tải các option cho form khi chuyển tab
    });

    // Bắt sự kiện khi một modal được hiển thị (đáng tin cậy hơn setTimeout)
    $(document).on('shown.bs.modal', function () {
        loadFormOptions(); // Tải các option cho form bên trong modal
    });

    // ==========================================
    // 6. ĐIỀU HƯỚNG CHUYỂN TAB (SIDEBAR)
    // ==========================================
    $("#navProducts").click(function(e) {
        e.preventDefault();
        $(".sidebar a").removeClass("active");
        $(this).addClass("active");
        $("#orderManagementSection, #customerManagementSection, #reportManagementSection").hide();
        $("#productManagementSection").show();
        searchProducts();
    });

    $("#navOrders").click(function(e) {
        e.preventDefault();
        $(".sidebar a").removeClass("active");
        $(this).addClass("active");
        $("#productManagementSection, #customerManagementSection, #reportManagementSection").hide();
        $("#orderManagementSection").show();
        loadOrders($("#orderStatusFilter").val());
    });

    $("#navCustomers").click(function(e) {
        e.preventDefault();
        $(".sidebar a").removeClass("active");
        $(this).addClass("active");
        $("#productManagementSection, #orderManagementSection, #reportManagementSection").hide();
        $("#customerManagementSection").show();
        loadCustomers();
    });
    
    $("#navReports").click(function(e) {
        e.preventDefault();
        $(".sidebar a").removeClass("active");
        $(this).addClass("active");
        $("#productManagementSection, #orderManagementSection, #customerManagementSection").hide();
        $("#reportManagementSection").show();
        
        // Đợi 100ms để trình duyệt render hoàn chỉnh thẻ div đang bị ẩn, lúc này biểu đồ mới lấy được kích thước width/height chuẩn
        setTimeout(() => {
            refreshDashboard();
        }, 100);
    });

    // ==========================================
    // 7. QUẢN LÝ SẢN PHẨM (TÌM KIẾM & RENDER)
    // ==========================================
    $("#btnSearch").click(searchProducts);
    
    function searchProducts() {
        const searchName = $("#searchName").val();
        const searchBrand = $("#searchBrand").val();
        const searchCategory = $("#searchCategory").val();
        const searchMinPrice = $("#searchMinPrice").val();
        const searchMaxPrice = $("#searchMaxPrice").val();

        let queryParams = "?";
        if (searchName) queryParams += "name=" + encodeURIComponent(searchName) + "&";
        if (searchBrand) queryParams += "brandId=" + searchBrand + "&";
        if (searchCategory) queryParams += "categoryId=" + searchCategory + "&";
        if (searchMinPrice) queryParams += "minPrice=" + searchMinPrice + "&";
        if (searchMaxPrice) queryParams += "maxPrice=" + searchMaxPrice + "&";
        
        let finalUrl = API_URL + "products";
        if (queryParams !== "?") {
            queryParams = queryParams.slice(0, -1); 
            finalUrl = API_URL + "products/search" + queryParams;
        }

        $("#productTable tbody").html('<tr><td colspan="6" class="text-center py-4"><div class="spinner-border text-primary" role="status"></div></td></tr>');

        $.ajax({
            url: finalUrl,
            type: "GET",
            success: function (data) {
                renderTable(data);
            },
            error: function () {
                $("#productTable tbody").html('<tr><td colspan="6" class="text-center text-danger py-4">Lỗi kết nối Backend! Hãy kiểm tra lại Server.</td></tr>');
            }
        });
    }

    function renderTable(products) {
        const $tbody = $("#productTable tbody");
        if (!products || products.length === 0) {
            $tbody.html('<tr><td colspan="6" class="text-center text-muted py-4">Không tìm thấy sản phẩm nào.</td></tr>');
            return;
        }

        let html = "";
        products.forEach(p => {
            const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";
            let imgSrc = p.thumbnailUrl || p.thumb || p.thumbnailImgUrl || p.thumbnail_img_url || p.image || fallbackImg;
            let separator = imgSrc.includes('|||') ? '|||' : ';';
            if (imgSrc && imgSrc.includes(separator) && !imgSrc.startsWith('data:image')) imgSrc = imgSrc.split(separator)[0].trim();
            const pJson = encodeURIComponent(JSON.stringify(p));
            
            const stockBadge = p.stock > 0 
                ? `<span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 px-3 py-2 rounded-pill"><i class="fas fa-check-circle me-1"></i> Còn ${p.stock}</span>` 
                : `<span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 px-3 py-2 rounded-pill"><i class="fas fa-times-circle me-1"></i> Hết hàng</span>`;

            html += `
                <tr>
                    <td class="ps-4">
                        <div class="d-flex align-items-center gap-3">
                                <img src="${imgSrc}" class="img-thumbnail-table" alt="img" onerror="this.onerror=null; this.src='${fallbackImg}';">
                            <div>
                                <div class="fw-bold text-dark mb-1" style="font-size: 0.95rem;">${p.name}</div>
                                <div class="text-muted small">Mã SP: #${p.id || 'N/A'}</div>
                            </div>
                        </div>
                    </td>
                    <td>
                        <div class="mb-1"><span class="badge bg-light text-secondary border px-2 py-1">${p.brandName || 'Chưa cập nhật'}</span></div>
                        <div class="small text-muted">${p.categoryName || 'Giày thể thao'}</div>
                    </td>
                    <td class="text-primary fw-black fs-6">${p.price ? p.price.toLocaleString('vi-VN') : 0} ₫</td>
                    <td>${stockBadge}</td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-light border text-primary btn-edit me-1 shadow-sm" style="border-radius: 8px;" data-product="${pJson}" title="Sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-light border text-danger btn-delete shadow-sm" style="border-radius: 8px;" data-id="${p.id}" data-name="${p.name}" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        });
        $tbody.html(html);
    }

    // ==========================================
    // 7.1. FORM THÊM / CẬP NHẬT SẢN PHẨM
    // ==========================================
    
    $("#btnAddNew").click(function () {
        $("#modalTitle").text("Thêm sản phẩm mới");
        
        if ($("#productForm").length) {
            $("#productForm")[0].reset(); 
        }
        $("#productId").val(''); 
        $("#imageInputsContainer").html(`
            <div class="input-group mb-2 image-input-row">
                <input type="text" class="form-control bg-dark text-white border-secondary img-url-input" placeholder="Nhập link ảnh...">
                <button type="button" class="btn btn-outline-danger btn-remove-image-input" disabled><i class="fas fa-trash"></i></button>
            </div>
        `);
        if (typeof renderImagePreview === 'function') renderImagePreview();
        
        // Khôi phục giao diện 1 Form (Nhiều Size)
        $("#sizeRowsContainer").html(`
            <div class="row size-row mb-2">
                <div class="col-6">
                    <select class="form-select size-select" id="sizeId">
                        <option value="">-- Chọn Size --</option>
                    </select>
                </div>
                <div class="col-4">
                    <input type="number" class="form-control stock-input" placeholder="SL Tồn" value="0" min="0">
                </div>
                <div class="col-2">
                    <button type="button" class="btn btn-outline-danger w-100 btn-remove-size-row" disabled><i class="fas fa-trash"></i></button>
                </div>
            </div>
        `);
        loadFormOptions();
    });

    $("#btnAddSizeRow").click(function() {
        const firstRow = $(".size-row").first();
        const newRow = firstRow.clone();
        newRow.find("select").val("").removeAttr("id");
        newRow.find("input").val("0");
        newRow.find(".btn-remove-size-row").prop("disabled", false);
        $("#sizeRowsContainer").append(newRow);
    });

    $(document).on("click", ".btn-remove-size-row", function() {
        if ($(".size-row").length > 1) {
            $(this).closest(".size-row").remove();
        }
    });

    // ==========================================
    // HIỂN THỊ ẢNH PREVIEW KHI NHẬP LINK
    // ==========================================
    $(document).on("click", "#btnAddImageInput", function() {
        $("#imageInputsContainer").append(`
            <div class="input-group mb-2 image-input-row">
                <input type="text" class="form-control bg-dark text-white border-secondary img-url-input" placeholder="Nhập link ảnh...">
                <button type="button" class="btn btn-outline-danger btn-remove-image-input"><i class="fas fa-trash"></i></button>
            </div>
        `);
        // Bật lại nút xóa của ô đầu tiên nếu có nhiều hơn 1 ô
        if ($(".image-input-row").length > 1) {
            $(".btn-remove-image-input").prop("disabled", false);
        }
    });

    $(document).on("click", ".btn-remove-image-input", function() {
        if ($(".image-input-row").length > 1) {
            $(this).closest(".image-input-row").remove();
            if ($(".image-input-row").length === 1) {
                $(".btn-remove-image-input").prop("disabled", true);
            }
            renderImagePreview();
        }
    });

    $(document).on("input", ".img-url-input", function() {
        renderImagePreview();
    });

    function renderImagePreview() {
        const lines = [];
        $(".img-url-input").each(function() {
            const val = $(this).val().trim();
            if (val) lines.push(val);
        });
        const previewContainer = $("#imagePreviewContainer");
        if (previewContainer.length === 0) return;
        if (lines.length === 0) { previewContainer.html('<span class="text-muted small w-100 text-center my-1">Ảnh xem trước sẽ hiển thị tại đây</span>'); return; }

        let html = "";
        lines.forEach((url, index) => {
            const badge = index === 0 ? '<span class="position-absolute top-0 start-50 translate-middle badge rounded-pill bg-danger" style="font-size: 0.6rem; z-index: 10;">Ảnh bìa</span>' : '';
            html += `<div class="position-relative shadow-sm rounded" style="width: 80px; height: 80px;">
                        <img src="${url}" class="img-thumbnail bg-dark border-secondary w-100 h-100" style="object-fit: cover; border-radius: 8px;" onerror="this.src='data:image/svg+xml;charset=UTF-8,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2280%22 height=%2280%22 viewBox=%220 0 80 80%22%3E%3Crect fill=%22%23eee%22 width=%2280%22 height=%2280%22/%3E%3Ctext fill=%22%23999%22 font-size=%2212%22 x=%2250%25%22 y=%2250%25%22 text-anchor=%22middle%22 dy=%22.3em%22%3ENo Img%3C/text%3E%3C/svg%3E'">
                        ${badge}
                     </div>`;
        });
        previewContainer.html(html);
    }

    // Gắn sự kiện cho các nút Lưu
    $("#btnSave").click(() => saveProductData(false));
    $("#btnSaveAndContinue").click(() => saveProductData(true));

    function saveProductData(keepModalOpen) {
        const productId = $("#productId").val();
        const isUpdate = productId && productId !== "";
        
        const name = $("#name").val().trim();
        const price = parseInt($("#price").val());
        const brandId = parseInt($("#brandId").val());
        const categoryId = parseInt($("#categoryId").val());
        const description = $("#description").val().trim();
        const colorId = parseInt($("#colorId").val());
        
        if (!name || isNaN(price) || !brandId || !categoryId || isNaN(colorId)) {
            alert("Vui lòng nhập đầy đủ: Tên giày, Giá, Thương hiệu, Danh mục và Màu sắc!");
            return;
        }

        const sizesAndStocks = [];
        $(".size-row").each(function() {
            const sId = $(this).find(".size-select").val() || $(this).find("select").val();
            const sQty = parseInt($(this).find(".stock-input").val()) || parseInt($(this).find("input[type='number']").val()) || 0;
            if (sId) sizesAndStocks.push({ sizeId: parseInt(sId), stock: sQty });
        });

        if (sizesAndStocks.length === 0) {
            alert("Vui lòng thêm ít nhất 1 Size!");
            return;
        }

        // Lấy nhiều link ảnh (từ nhiều ô input)
        const lines = [];
        $(".img-url-input").each(function() {
            const val = $(this).val().trim();
            if (val) lines.push(val);
        });
        if (lines.length === 0) {
            alert("Vui lòng cung cấp ít nhất 1 link ảnh!");
            return;
        }
        const finalThumb = lines.join('|||');

        const payload = {
            id: isUpdate ? parseInt(productId) : null,
            name: name,
            price: price,
            description: description,
            brandId: brandId,
            categoryId: categoryId,
            colorId: colorId,
            thumb: finalThumb
        };

        const $btn = keepModalOpen ? $("#btnSaveAndContinue") : $("#btnSave");
        const $btnOther = keepModalOpen ? $("#btnSave") : $("#btnSaveAndContinue");
        const originalText = $btn.html();
        
        $btn.prop("disabled", true).html('<i class="fas fa-spinner fa-spin"></i> Đang xử lý...');
        if ($btnOther.length) $btnOther.prop("disabled", true);

        if (isUpdate) {
            $.ajax({
                url: API_URL + "products/" + productId,
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify(payload),
                success: function () {
                    let promises = sizesAndStocks.map(ss => {
                        return $.ajax({
                            url: API_URL + "product-details",
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify({
                                productId: parseInt(productId),
                                sizeId: ss.sizeId,
                                colorId: payload.colorId,
                                stockQuantity: ss.stock,
                                price: payload.price,
                                thumb: payload.thumb,
                                thumbnailImgUrl: payload.thumb,
                                thumbnailUrl: payload.thumb
                            })
                        });
                    });
                    
                    Promise.all(promises)
                        .then(() => handleSaveSuccess(keepModalOpen, productId))
                        .catch(err => { alert("Lỗi khi cập nhật kích cỡ: " + (err.responseText || "Unknown Error")); resetButtons(); });
                },
                error: function (xhr) {
                    alert("Lỗi cập nhật sản phẩm: " + (xhr.responseText || "Unknown error"));
                    resetButtons();
                }
            });
        } else {
            // Tạo sản phẩm mới - KHÔNG gắn size vào payload chính
            // Tất cả size đều được tạo qua /api/product-details sau khi có productId (nhất quán với flow Update)
            $.ajax({
                url: API_URL + "products",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(payload),
                success: function (response) {
                    let newId = null;
                    if (typeof response === 'object') newId = response.id || response.productId;
                    else {
                        try { const parsed = JSON.parse(response); newId = parsed.id || parsed.productId; }
                        catch (e) { if (!isNaN(parseInt(response))) newId = parseInt(response); }
                    }

                    if (!newId || isNaN(parseInt(newId))) {
                        // Fallback: lấy sản phẩm vừa tạo từ danh sách
                        $.get(API_URL + "products").done(function(list) {
                            const createdProduct = list.sort((a, b) => b.id - a.id).find(p => p.name === payload.name);
                            if (createdProduct) addAllSizesAndFinish(createdProduct.id);
                            else fallbackSuccess();
                        }).fail(fallbackSuccess);
                    } else {
                        addAllSizesAndFinish(newId);
                    }
                },
                error: function (xhr) {
                    alert("Lỗi tạo sản phẩm: " + (xhr.responseText || "Unknown Error"));
                    resetButtons();
                }
            });
        }

        // Gửi TẤT CẢ size (kể cả size đầu tiên) qua /api/product-details - nhất quán với flow Update
        function addAllSizesAndFinish(savedId) {
            let promises = sizesAndStocks.map(ss => {
                return $.ajax({
                    url: API_URL + "product-details",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify({
                        productId: parseInt(savedId),
                        sizeId: ss.sizeId,
                        colorId: payload.colorId,
                        stockQuantity: ss.stock,
                        price: payload.price,
                        thumb: payload.thumb,
                        thumbnailImgUrl: payload.thumb,
                        thumbnailUrl: payload.thumb
                    })
                });
            });
            Promise.all(promises)
                .then(() => handleSaveSuccess(keepModalOpen, savedId))
                .catch(err => {
                    alert("Đã tạo sản phẩm nhưng lỗi khi thêm một số size: " + (err.responseText || "Unknown"));
                    handleSaveSuccess(keepModalOpen, savedId);
                });
        }

        function fallbackSuccess() {
            alert("Lưu thành công!");
            searchProducts();
            $("#productModal").modal('hide');
            resetButtons();
        }

        function handleSaveSuccess(keepOpen, savedId) {
            searchProducts(); 
            if (keepOpen) {
                if (savedId && !isNaN(parseInt(savedId))) {
                    $("#productId").val(savedId); 
                    $("#colorId").val('');
                    $(".size-row:not(:first)").remove();
                    $(".size-row").first().find("select").val('');
                    $(".size-row").first().find("input").val('0');
                    $("#imageInputsContainer").html(`
                        <div class="input-group mb-2 image-input-row">
                            <input type="text" class="form-control bg-dark text-white border-secondary img-url-input" placeholder="Nhập link ảnh...">
                            <button type="button" class="btn btn-outline-danger btn-remove-image-input" disabled><i class="fas fa-trash"></i></button>
                        </div>
                    `);
                    if (typeof renderImagePreview === 'function') renderImagePreview();
                    
                    if ($("#saveSuccessMsg").length) {
                        $("#saveSuccessMsg").removeClass('d-none').hide().fadeIn();
                        setTimeout(() => $("#saveSuccessMsg").fadeOut(function() { $(this).addClass('d-none'); }), 2500);
                    } else {
                        alert("Thêm kích cỡ/màu sắc mới thành công!");
                    }
                } else {
                    fallbackSuccess();
                }
            } else {
                alert(productId ? "Cập nhật thông tin thành công!" : "Thêm sản phẩm thành công!");
                $("#productModal").modal('hide');
            }
            resetButtons();
        }

        function resetButtons() {
            $btn.prop("disabled", false).html(originalText);
            if ($btnOther.length) $btnOther.prop("disabled", false);
        }
    }

    $(document).on("click", ".btn-edit", function () {
        const product = JSON.parse(decodeURIComponent($(this).attr("data-product")));
        
        $("#modalTitle").text("Cập nhật: " + product.name);
        if ($("#productForm").length) {
            $("#productForm")[0].reset();
        }

        $("#productId").val(product.id);
        $("#name").val(product.name);
        $("#price").val(product.price);
        $("#description").val(product.description);
        $("#brandId").val(product.brandId);
        $("#categoryId").val(product.categoryId);
        
        $(".size-row:not(:first)").remove();

        const thumbStr = product.thumbnailUrl || product.thumb || product.thumbnailImgUrl || product.thumbnail_img_url;
        if (thumbStr) {
            let separator = thumbStr.includes('|||') ? '|||' : ';';
            const imgs = thumbStr.split(separator).map(i => i.trim()).filter(i => i !== "");
            $("#imageInputsContainer").empty();
            if (imgs.length > 0) {
                imgs.forEach((url, index) => {
                    $("#imageInputsContainer").append(`
                        <div class="input-group mb-2 image-input-row">
                            <input type="text" class="form-control bg-dark text-white border-secondary img-url-input" placeholder="Nhập link ảnh..." value="${url}">
                            <button type="button" class="btn btn-outline-danger btn-remove-image-input" ${imgs.length === 1 ? 'disabled' : ''}><i class="fas fa-trash"></i></button>
                        </div>
                    `);
                });
            } else {
                $("#imageInputsContainer").html(`
                    <div class="input-group mb-2 image-input-row">
                        <input type="text" class="form-control bg-dark text-white border-secondary img-url-input" placeholder="Nhập link ảnh...">
                        <button type="button" class="btn btn-outline-danger btn-remove-image-input" disabled><i class="fas fa-trash"></i></button>
                    </div>
                `);
            }
        } else {
            $("#imageInputsContainer").html(`
                <div class="input-group mb-2 image-input-row">
                    <input type="text" class="form-control bg-dark text-white border-secondary img-url-input" placeholder="Nhập link ảnh...">
                    <button type="button" class="btn btn-outline-danger btn-remove-image-input" disabled><i class="fas fa-trash"></i></button>
                </div>
            `);
        }
        if (typeof renderImagePreview === 'function') renderImagePreview();

        const firstRow = $(".size-row").first();
        firstRow.find("select").val("");
        firstRow.find("input").val("0");
        $("#colorId").val("");

        $.ajax({
            url: API_URL + "product-details/product/" + product.id,
            type: "GET",
            success: function(details) {
                const activeDetails = details.filter(d => {
                    const qty = d.stockQuantity !== undefined ? d.stockQuantity : (d.stock_quantity || 0);
                    return qty > 0;
                });

                if (activeDetails && activeDetails.length > 0) {
                    $("#colorId").val(activeDetails[0].colorId || activeDetails[0].color_id || "");
                    firstRow.find("select").val(activeDetails[0].sizeId || activeDetails[0].size_id || "");
                    firstRow.find("input").val(activeDetails[0].stockQuantity !== undefined ? activeDetails[0].stockQuantity : (activeDetails[0].stock_quantity || 0));
                    
                    for(let i = 1; i < activeDetails.length; i++) {
                        const newRow = firstRow.clone();
                        newRow.find("select").val(activeDetails[i].sizeId || activeDetails[i].size_id || "").removeAttr("id");
                        newRow.find("input").val(activeDetails[i].stockQuantity !== undefined ? activeDetails[i].stockQuantity : (activeDetails[i].stock_quantity || 0));
                        newRow.find(".btn-remove-size-row").prop("disabled", false);
                        $("#sizeRowsContainer").append(newRow);
                    }
                }
            },
            complete: function() {
                $("#productModal").modal('show');
            }
        });
        
    });

    $(document).on("click", ".btn-delete", function () {
        const id = $(this).data("id");
        const name = $(this).data("name");

        if (confirm(`Bạn có chắc chắn muốn xóa giày "${name}"?\nHành động này không thể hoàn tác!`)) {
            $.ajax({
                url: API_URL + "products/" + id,
                type: "DELETE",
                success: function () {
                    alert("Đã xóa thành công!");
                    searchProducts(); 
                },
                error: function (xhr) {
                    let errMsg = xhr.responseText || "Đã xảy ra lỗi khi xóa.";
                    try { const j = JSON.parse(errMsg); if (j.message) errMsg = j.message; else if (j.error) errMsg = j.error; } catch(e){}
                    alert("Lỗi: " + errMsg);
                }
            });
        }
    });

    // ==========================================
    // 8. QUẢN LÝ ĐƠN HÀNG
    // ==========================================
    function loadOrders(status) {
        const $tbody = $("#pendingOrdersTable tbody");
        $tbody.html('<tr><td colspan="5" class="text-center text-muted py-5"><i class="fas fa-spinner fa-spin me-2"></i> Đang tải danh sách...</td></tr>');

        $.ajax({
            url: API_URL + "orders/status/" + status,
            type: "GET",
            success: function(orders) {
                if (!orders || orders.length === 0) {
                    $tbody.html('<tr><td colspan="5" class="text-center text-muted py-5"><i class="fas fa-info-circle text-secondary me-2"></i>Không có đơn hàng nào ở trạng thái này.</td></tr>');
                    return;
                }

                let html = "";
                orders.forEach(order => {
                    let actionHtml = "";
                    if (status === "PENDING") {
                        actionHtml = `
                            <button class="btn btn-sm btn-success btn-approve-order" data-id="${order.id}" title="Duyệt đơn">
                                <i class="fas fa-check"></i> Duyệt
                            </button>
                            <button class="btn btn-sm btn-danger btn-cancel-order ms-1" data-id="${order.id}" title="Hủy đơn">
                                <i class="fas fa-times"></i> Hủy
                            </button>
                        `;
                    } else if (status === "SHIPPING") {
                        actionHtml = `<span class="badge bg-primary px-3 py-2"><i class="fas fa-truck me-1"></i> Đang giao hàng</span>`;
                    } else if (status === "CANCELLED") {
                        actionHtml = `<span class="badge bg-danger px-3 py-2"><i class="fas fa-times-circle me-1"></i> Đã hủy</span>`;
                    }

                    html += `
                        <tr>
                            <td class="ps-4 fw-bold">#SP-${order.id}</td>
                            <td>
                                <div class="fw-bold text-dark">${order.receiverName}</div>
                                <div class="small text-muted">${order.receiverPhone}</div>
                            </td>
                            <td>${new Date(order.date).toLocaleDateString('vi-VN')}</td>
                            <td class="text-danger fw-bold">${order.total.toLocaleString('vi-VN')} ₫</td>
                            <td class="text-end pe-4">
                                ${actionHtml}
                            </td>
                        </tr>
                    `;
                });
                $tbody.html(html);
            },
            error: function() {
                $tbody.html('<tr><td colspan="5" class="text-center text-danger py-5">Lỗi khi tải đơn hàng. Vui lòng kiểm tra API Backend.</td></tr>');
            }
        });
    }

    $("#btnRefreshOrders").click(function() { loadOrders($("#orderStatusFilter").val()); });
    $("#orderStatusFilter").change(function() { loadOrders($(this).val()); });

    $(document).on("click", ".btn-approve-order, .btn-cancel-order", function() {
        const orderId = $(this).data("id");
        const isApproving = $(this).hasClass("btn-approve-order");
        const newStatus = isApproving ? "SHIPPING" : "CANCELLED";
        const actionText = isApproving ? "duyệt" : "hủy";

        if (confirm(`Bạn có chắc chắn muốn ${actionText} đơn hàng #${orderId}?`)) {
            $.ajax({
                url: API_URL + "orders/" + orderId + "/status",
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify({ status: newStatus }),
                success: function() {
                    alert(`Đã ${actionText} đơn hàng #${orderId} thành công!`);
                    loadOrders($("#orderStatusFilter").val());
                },
                error: function(xhr) {
                    alert(`Lỗi khi ${actionText} đơn hàng: ${xhr.responseText}`);
                }
            });
        }
    });

    // ==========================================
    // 9. QUẢN LÝ KHÁCH HÀNG
    // ==========================================
    function loadCustomers() {
        const $tbody = $("#customersTable tbody");
        $tbody.html('<tr><td colspan="5" class="text-center text-muted py-5"><i class="fas fa-spinner fa-spin me-2"></i> Đang tải danh sách...</td></tr>');

        $.ajax({
            url: API_URL + "users",
            type: "GET",
            success: function(users) {
                if (!users || users.length === 0) {
                    $tbody.html('<tr><td colspan="5" class="text-center text-muted py-5"><i class="fas fa-info-circle text-secondary me-2"></i>Không có khách hàng nào trên hệ thống.</td></tr>');
                    return;
                }

                let html = "";
                users.forEach(user => {
                    const roleBadge = user.roleId === 1 
                        ? '<span class="badge bg-danger px-3 py-2"><i class="fas fa-user-shield me-1"></i> Admin</span>' 
                        : '<span class="badge bg-success px-3 py-2"><i class="fas fa-user me-1"></i> Khách hàng</span>';

                    const isActive = user.isActive !== undefined ? user.isActive : 1;
                    const statusBadge = isActive === 1
                        ? '<span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 px-2 py-1"><i class="fas fa-check-circle"></i> Hoạt động</span>'
                        : '<span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 px-2 py-1"><i class="fas fa-lock"></i> Bị khóa</span>';

                    const lockBtn = isActive === 1
                        ? `<button class="btn btn-sm btn-outline-danger btn-toggle-status shadow-sm" data-id="${user.id || user.userId}" data-status="0" title="Khóa tài khoản"><i class="fas fa-lock"></i></button>`
                        : `<button class="btn btn-sm btn-outline-success btn-toggle-status shadow-sm" data-id="${user.id || user.userId}" data-status="1" title="Mở khóa tài khoản"><i class="fas fa-unlock"></i></button>`;

                    html += `
                        <tr>
                            <td class="ps-4 fw-bold">#USER-${user.id || user.userId}</td>
                            <td><div class="fw-bold text-dark">${user.fullName || user.fullname || 'N/A'}</div></td>
                            <td>${user.email || 'N/A'}</td>
                            <td>${user.phoneNumber || user.phone || 'Chưa cập nhật'}</td>
                            <td>${roleBadge}</td>
                            <td>${statusBadge}</td>
                            <td class="text-end pe-4">
                                ${(user.roleId !== 1) ? lockBtn : '<span class="text-muted small">Không thể khóa Admin</span>'}
                            </td>
                        </tr>
                    `;
                });
                $tbody.html(html);
            },
            error: function() {
                $tbody.html('<tr><td colspan="5" class="text-center text-danger py-5">Lỗi khi tải danh sách khách hàng. Vui lòng kiểm tra lại Backend API (/api/users).</td></tr>');
            }
        });
    }

    $("#btnRefreshCustomers").click(loadCustomers);

    $(document).on("click", ".btn-toggle-status", function() {
        const userId = $(this).data("id");
        const newStatus = $(this).data("status");
        const actionText = newStatus === 1 ? "mở khóa" : "khóa";

        if (confirm(`Bạn có chắc chắn muốn ${actionText} tài khoản #USER-${userId}?`)) {
            $.ajax({
                url: API_URL + "users/" + userId + "/status",
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify({ isActive: newStatus }),
                success: function() {
                    alert(`Đã ${actionText} tài khoản thành công!`);
                    loadCustomers(); 
                },
                error: function(xhr) {
                    alert(`Lỗi khi ${actionText} tài khoản: ${xhr.responseText}`);
                }
            });
        }
    });

    // ==========================================
    // CẤU HÌNH GỬI TOKEN JWT KÈM MỌI REQUEST AJAX
    // ==========================================
    $.ajaxSetup({
        beforeSend: function(xhr) {
            const token = localStorage.getItem("user_token");
            if (token) {
                xhr.setRequestHeader("Authorization", "Bearer " + token);
            }
        }
    });

    // ==========================================
    // 10. ĐĂNG XUẤT
    // ==========================================
    $("#btnLogout").click(function (e) {
        e.preventDefault();
        localStorage.removeItem("user_name"); 
        localStorage.removeItem("user_email"); 
        localStorage.removeItem("user_role"); 
        localStorage.removeItem("user_id"); 
        localStorage.removeItem("user_token"); 
        window.location.href = "login.html"; 
    });
});