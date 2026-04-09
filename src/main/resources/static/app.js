$(document).ready(function () {
    // Cấu hình URL gọi tới Spring Boot Backend
    const API_URL = "http://localhost:8081/api/";

    // 1. KHỞI TẠO DỮ LIỆU CHO CÁC DROPDOWN CHỌN
    loadSelectData();

    searchProducts();
    // 2. TÌM KIẾM SẢN PHẨM
    // Xử lý khi nhấn nút "Tìm kiếm"
    $("#btnSearch").click(function () {
        searchProducts();
    });

    // Mẹo UX: Nhấn nút Enter ở ô nhập Tên cũng tự kích hoạt tìm kiếm
    $("#searchName").keypress(function(e) {
        if(e.which == 13) {
            e.preventDefault();
            searchProducts();
        }
    });

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

        // ĐÃ SỬA: Hiện loading vào đúng khu vực lưới Card (#productGrid)
        $("#productGrid").html('<div class="col-12 text-center my-5"><div class="spinner-border text-warning" role="status"></div><p>Đang tải dữ liệu...</p></div>');

        $.ajax({
            url: API_URL + "products/" + queryParams,
            type: "GET",
            dataType: "json",
            success: function (data) {
                // ĐÃ SỬA: Gọi đúng tên hàm renderCards thay vì renderTable
                renderCards(data);
            },
            error: function (xhr, status, error) {
                console.error("Lỗi:", error);
                // ĐÃ SỬA: Hiện lỗi vào khu vực lưới Card
                $("#productGrid").html('<div class="col-12 text-center text-danger my-5">Lỗi kết nối Backend. Hãy chắc chắn Spring Boot đang chạy!</div>');
            }
        });
    }
    // HÀM IN DỮ LIỆU RA BẢNG
    // Thay đổi tên hàm gọi ở bên trong success của ajax từ renderTable thành renderCards
    function renderCards(products) {
        const $grid = $("#productGrid");
        
        if (!products || products.length === 0) {
            $grid.html('<div class="col-12 text-center text-muted my-5"><h5>Không tìm thấy sản phẩm nào phù hợp.</h5></div>');
            return;
        }

        let html = "";
        $.each(products, function (index, product) {
            // Ảnh placeholder nếu không có link ảnh
            const imgSrc = product.thumb || 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=500&auto=format&fit=crop';
            
            html += `
                <div class="col-md-4 col-lg-3 mb-4">
                    <div class="card h-100 product-card border-0 shadow-sm">
                        <img src="${imgSrc}" class="card-img-top product-img" alt="${product.name}">
                        <div class="card-body d-flex flex-column">
                            <h6 class="card-title text-truncate" title="${product.name}">${product.name}</h6>
                            <p class="text-muted small mb-2">${product.brandName || 'Chưa phân loại'}</p>
                            <h5 class="text-danger fw-bold mt-auto">${product.price ? product.price.toLocaleString('vi-VN') : 0} ₫</h5>
                            
                            <button class="btn btn-dark w-100 mt-3 btn-buy" data-id="${product.id}">
                                <i class="fas fa-shopping-cart"></i> Mua Ngay
                            </button>
                        </div>
                    </div>
                </div>
            `;
        });
        $grid.html(html);
    }

    // Thêm sự kiện khi bấm nút Mua Ngay
    $(document).on('click', '.btn-buy', function() {
        const token = localStorage.getItem("user_token");
        if (!token) {
            // Chưa đăng nhập -> Đá về trang đăng nhập
            window.location.href = "login.html";
        } else {
            // Đã đăng nhập -> Xử lý thêm vào giỏ hàng
            const productId = $(this).data("id");
            alert("Sản phẩm ID " + productId + " đã được thêm vào giỏ!");
            // Viết code gọi API AddToCart ở đây sau này
        }
    });

    // 4. MỞ MODAL ĐỂ "THÊM MỚI"
    $("#btnAddNew").click(function () {
        $("#modalTitle").text("Thêm sản phẩm mới");
        $("#productForm")[0].reset(); // Xóa sạch dữ liệu cũ trong form
        $("#productId").val(''); // Reset ID về rỗng (báo hiệu đây là tạo mới)
    });

    // 5. MỞ MODAL ĐỂ "SỬA" (CẬP NHẬT)
    $(document).on("click", ".btn-edit", function () {
        const product = $(this).data("product");
        
        $("#modalTitle").text("Cập nhật sản phẩm: " + product.name);
        $("#productForm")[0].reset(); 

        // Đổ dữ liệu hiện tại vào các ô input
        $("#productId").val(product.id);
        $("#name").val(product.name);
        $("#price").val(product.price);
        $("#description").val(product.description);
        $("#thumbnailUrl").val(product.thumb);
        $("#stockQuantity").val(product.stock);
        
        // (Lưu ý: Nếu API của bạn có trả về brand_id, category_id... thì điền vào đây)
        // Ví dụ: $("#brandId").val(product.brandId); 
        
        $("#productModal").modal('show');
    });

    // 6. XỬ LÝ NÚT "LƯU HỆ THỐNG" (CREATE & UPDATE)
    $("#btnSave").click(function () {
        // Thu thập dữ liệu từ Form
        const payload = {
            id: $("#productId").val() || null,
            name: $("#name").val(),
            price: parseInt($("#price").val()),
            description: $("#description").val(),
            brandId: $("#brandId").val() ? parseInt($("#brandId").val()) : null,
            categoryId: $("#categoryId").val() ? parseInt($("#categoryId").val()) : null,
            sizeId: $("#sizeId").val() ? parseInt($("#sizeId").val()) : null,
            colorId: $("#colorId").val() ? parseInt($("#colorId").val()) : null,
            stock: parseInt($("#stockQuantity").val()),
            thumb: $("#thumbnailUrl").val()
        };

        // Validate cơ bản bắt buộc nhập tên và giá
        if (!payload.name || isNaN(payload.price)) {
            alert("Vui lòng nhập Tên giày và Giá niêm yết hợp lệ!");
            return;
        }

        // Nếu có ID thì là Cập nhật (PUT), nếu ID rỗng thì là Thêm mới (POST)
        const isUpdate = payload.id !== null;
        const method = isUpdate ? "PUT" : "POST";
        const url = isUpdate ? API_URL + "products/" + payload.id : API_URL + "products/";

        // Đổi trạng thái nút thành "Đang lưu..." để chống click đúp
        const $btn = $(this);
        const originalText = $btn.text();
        $btn.prop("disabled", true).html('<span class="spinner-border spinner-border-sm"></span> Đang xử lý...');

        $.ajax({
            url: url,
            type: method,
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: function (response) {
                alert(isUpdate ? "Cập nhật thành công!" : "Thêm sản phẩm thành công!");
                $("#productModal").modal('hide');
                searchProducts(); // Load lại kết quả mới nhất
            },
            error: function (xhr, status, error) {
                console.error("Lỗi khi lưu:", error);
                alert("Bạn chưa viết hàm xử lý " + method + " bên trong ProductAPI.java. Hãy bổ sung nhé!");
            },
            complete: function () {
                $btn.prop("disabled", false).text(originalText); // Khôi phục lại nút
            }
        });
    });

    // 7. XÓA SẢN PHẨM
    $(document).on("click", ".btn-delete", function () {
        const id = $(this).data("id");
        const name = $(this).data("name");

        if (confirm(`Bạn có chắc chắn muốn xóa giày "${name}" không?\nHành động này sẽ xóa dữ liệu trên hệ thống!`)) {
            $.ajax({
                url: API_URL + "products/" + id,
                type: "DELETE",
                success: function () {
                    alert("Đã xóa sản phẩm thành công!");
                    searchProducts(); // Load lại kết quả mới nhất
                },
                error: function (xhr, status, error) {
                    console.error("Lỗi khi xóa:", error);
                    alert("Bạn chưa viết hàm xử lý DELETE bên trong ProductAPI.java.");
                }
            });
        }
    });

    // 8. HÀM GIẢ LẬP DỮ LIỆU ĐỔ VÀO CÁC DROPDOWN
    function loadSelectData() {
        // Xóa sạch các option cũ (chỉ giữ lại option Mặc định)
        $("#searchBrand").html('<option value="">Tất cả</option>');
        $("#brandId").html('<option value="">-- Chọn thương hiệu --</option>');
        
        $("#searchCategory").html('<option value="">Tất cả</option>');
        $("#categoryId").html('<option value="">-- Chọn danh mục --</option>');

        // Lấy Brands từ DB
        $.get(API_URL + "brands", function (data) {
            data.forEach(b => {
                $("#brandId").append(`<option value="${b.id}">${b.name}</option>`);
                $("#searchBrand").append(`<option value="${b.id}">${b.name}</option>`);
            });
        });

        // Lấy Categories từ DB
        $.get(API_URL + "categories", function (data) {
            data.forEach(c => {
                $("#categoryId").append(`<option value="${c.id}">${c.name}</option>`);
                $("#searchCategory").append(`<option value="${c.id}">${c.name}</option>`);
            });
        });

        // Tạm thời Hardcode Size và Color (Sau này bạn có thể viết thêm API getSizes, getColors tương tự)
        const sizes = [{ id: 1, value: "39" }, { id: 2, value: "40" }, { id: 3, value: "41" }, { id: 4, value: "42" }];
        sizes.forEach(s => $("#sizeId").append(`<option value="${s.id}">Size ${s.value}</option>`));

        const colors = [{ id: 1, name: "Trắng" }, { id: 2, name: "Đen" }, { id: 3, name: "Đỏ" }];
        colors.forEach(c => $("#colorId").append(`<option value="${c.id}">${c.name}</option>`));
    }
    // 9. NÚT ĐĂNG XUẤT CHO NHÂN VIÊN
    $("#btnLogout").click(function (e) {
        e.preventDefault();
        // Xóa thông tin đã đăng nhập
        localStorage.removeItem("staff_token"); 
        window.location.href = "login.html"; // Trả về trang login
    });
});