$(document).ready(function() {
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

    const API_URL = "/api/orders/user/";
    const userId = localStorage.getItem("user_id");

    if (!userId || userId === "undefined") {
        alert("Vui lòng đăng nhập để xem đơn hàng!");
        window.location.href = "login.html";
        return;
    }

    // Tải dữ liệu lần đầu tiên
    loadOrders("ALL");

    // Xử lý sự kiện khi bấm chuyển Tab
    $('.nav-link').click(function() {
        if ($(this).hasClass('active')) return; // Bỏ qua nếu click lại tab đang mở
        
        $('.nav-link').removeClass('active');
        $(this).addClass('active');
        
        const status = $(this).data('status');
        
        // Giao việc mượt mà lại hoàn toàn cho CSS lo (bỏ jQuery fade)
        loadOrders(status);
    });

    function loadOrders(statusFilter) {
        $('#ordersList').html('<div class="text-center py-5 empty-state"><i class="fas fa-spinner fa-spin fa-2x text-muted mb-3"></i><p>Đang tải...</p></div>');

        $.ajax({
            url: API_URL + userId,
            type: "GET",
            success: function(orders) {
                renderOrders(orders, statusFilter);
            },
            error: function() {
                // DỮ LIỆU MẪU (MOCK DATA) NẾU API CHƯA SẴN SÀNG
                console.warn("API Lấy đơn hàng chưa hoạt động, sử dụng dữ liệu mẫu.");
                const mockOrders = [
                    { id: 1004, date: "24/10/2026", status: "PENDING", itemsCount: 2, total: 4500000, methodName: "Thanh toán VNPay" },
                    { id: 1003, date: "20/10/2026", status: "SHIPPING", itemsCount: 1, total: 3200000, methodName: "Thanh toán COD" },
                    { id: 1002, date: "15/10/2026", status: "COMPLETED", itemsCount: 3, total: 8500000, methodName: "Thanh toán VNPay" },
                    { id: 1001, date: "10/10/2026", status: "CANCELLED", itemsCount: 1, total: 2100000, methodName: "Thanh toán COD" }
                ];
                renderOrders(mockOrders, statusFilter);
            }
        });
    }

    function renderOrders(orders, statusFilter) {
        // Lọc đơn hàng theo Tab
        let filteredOrders = orders;
        if (statusFilter !== "ALL") {
            filteredOrders = orders.filter(o => o.status === statusFilter);
        }

        // Nếu mảng rỗng
        if (!filteredOrders || filteredOrders.length === 0) {
            $('#ordersList').html(`
                <div class="text-center py-5 mt-4 empty-state" style="background: #1a1d24; border-radius: 16px; border: 1px dashed #3a3f4a;">
                    <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                    <h5 class="fw-bold text-light">Chưa có đơn hàng nào</h5>
                    <p class="text-muted">Bạn chưa có đơn hàng nào trong trạng thái này.</p>
                    <a href="index.html" class="btn btn-primary-custom mt-2 px-4">Mua sắm ngay</a>
                </div>
            `);
            return;
        }

        // Vẽ giao diện cho từng đơn
        let html = "";
        filteredOrders.forEach(order => {
            let statusText = "Không xác định";
            let icon = "fa-circle";
            
            if (order.status === "PENDING") { statusText = "Chờ xác nhận"; icon = "fa-clock"; }
            else if (order.status === "SHIPPING") { statusText = "Đang vận chuyển"; icon = "fa-truck-fast"; }
            else if (order.status === "COMPLETED") { statusText = "Đã giao"; icon = "fa-check-circle"; }
            else if (order.status === "CANCELLED") { statusText = "Đã hủy"; icon = "fa-times-circle"; }

            html += `
                <div class="order-card shadow-sm">
                    <div class="order-header">
                        <div>
                            <span class="order-id text-light">#SP-${order.id}</span>
                            <span class="order-date ms-3"><i class="far fa-calendar-alt me-1"></i> Ngày đặt: ${order.date}</span>
                        </div>
                        <div class="order-status status-${order.status}">
                            <i class="fas ${icon} me-1"></i> ${statusText}
                        </div>
                    </div>
                    <div class="order-body flex-column flex-md-row gap-3">
                        <div class="order-summary">
                            <p class="mb-1"><i class="fas fa-shopping-bag me-2 text-secondary"></i> Số lượng sản phẩm: <strong class="text-light">${order.itemsCount}</strong></p>
                            <p class="mb-0"><i class="fas fa-credit-card me-2 text-secondary"></i> Phương thức: <strong class="text-light">${order.methodName || 'COD'}</strong></p>
                        </div>
                        <div class="text-md-end text-start mt-3 mt-md-0 d-flex flex-row flex-md-column justify-content-between align-items-center align-items-md-end w-100 w-md-auto">
                            <div>
                                <span class="text-muted small d-block mb-1">Tổng tiền:</span>
                                <span class="order-total">${order.total.toLocaleString('vi-VN')} ₫</span>
                            </div>
                            <div class="d-flex gap-2 mt-md-2">
                                <button class="btn btn-outline-custom btn-sm btn-view-details" data-id="${order.id}">
                                    Xem chi tiết
                                </button>
                                ${order.status === 'PENDING' ? `
                                <button class="btn btn-outline-danger btn-sm btn-cancel-order" data-id="${order.id}">
                                    Hủy đơn
                                </button>` : ''}
                                ${order.status === 'SHIPPING' ? `
                                <button class="btn btn-success btn-sm btn-complete-order" data-id="${order.id}">
                                    Đã nhận hàng
                                </button>` : ''}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });

        $('#ordersList').html(html);
    }

    // Xử lý sự kiện bấm nút "Xem chi tiết" để hiển thị Modal Lịch sử / Chi tiết đơn hàng
    $(document).on('click', '.btn-view-details', function() {
        const orderId = $(this).data('id');
        $('#orderDetailsModal').modal('show');
        $('#orderDetailsContent').html('<div class="text-center py-4"><i class="fas fa-spinner fa-spin fa-2x text-muted mb-3"></i><p class="text-muted">Đang tải thông tin chi tiết...</p></div>');

        // Gọi API lấy chi tiết đơn hàng từ Backend
        $.ajax({
            url: "/api/orders/" + orderId + "/details",
            type: "GET",
            success: function(details) {
                if (!details || details.length === 0) {
                    $('#orderDetailsContent').html('<div class="text-center py-4 text-warning">Không tìm thấy thông tin chi tiết cho đơn hàng này.</div>');
                    return;
                }

                let itemsHtml = "";
                let totalAmount = 0;

                const fallbackImg = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22200%22%20height%3D%22200%22%20viewBox%3D%220%200%20200%20200%22%3E%3Crect%20fill%3D%22%23eee%22%20width%3D%22200%22%20height%3D%22200%22%2F%3E%3Ctext%20fill%3D%22%23999%22%20font-family%3D%22sans-serif%22%20font-size%3D%2216%22%20dy%3D%2210.5%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2250%25%22%20text-anchor%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E";

                details.forEach(item => {
                    const price = item.price || item.unitPrice || 0;
                    const qty = item.quantity || 1;
                    const itemTotal = price * qty;
                    totalAmount += itemTotal;
                    
                    let imgSrc = item.thumbnail || item.thumb || item.thumbnailImgUrl || item.image;
                    if (!imgSrc || imgSrc.trim() === "") imgSrc = fallbackImg;
                    else if (imgSrc.includes(';') && !imgSrc.startsWith('data:image')) imgSrc = imgSrc.split(';')[0].trim();

                    itemsHtml += `
                        <tr>
                            <td>
                                <div class="d-flex align-items-center">
                                    <div class="bg-secondary rounded me-3" style="width: 50px; height: 50px; display: flex; align-items: center; justify-content: center; overflow: hidden;">
                                        <img src="${imgSrc}" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.onerror=null; this.src='${fallbackImg}';">
                                    </div>
                                    <div>
                                        <div class="fw-bold text-light">${item.productName || 'Sản phẩm'}</div>
                                        <div class="small text-muted">Size: ${item.sizeName || 'N/A'} | Màu: ${item.colorName || 'N/A'}</div>
                                    </div>
                                </div>
                            </td>
                            <td class="text-center text-light fw-bold">${qty}</td>
                            <td class="text-end text-success fw-bold">${itemTotal.toLocaleString('vi-VN')} ₫</td>
                        </tr>
                    `;
                });

                $('#orderDetailsContent').html(`
                    <div class="mb-3">
                        <h6 class="text-light fw-bold mb-3"><i class="fas fa-hashtag text-primary me-2"></i>Mã đơn hàng: SP-${orderId}</h6>
                    </div>
                    <div class="table-responsive mt-4">
                        <table class="table table-dark table-hover align-middle mb-0" style="--bs-table-bg: transparent;">
                            <thead>
                                <tr style="border-bottom: 2px solid #3a3f4a;">
                                    <th class="text-muted small text-uppercase">Sản phẩm</th>
                                    <th class="text-muted small text-uppercase text-center">Số lượng</th>
                                    <th class="text-muted small text-uppercase text-end">Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${itemsHtml}
                            </tbody>
                            <tfoot>
                                <tr style="border-top: 2px solid #3a3f4a;">
                                    <td colspan="2" class="text-end text-muted fw-bold">Tổng cộng:</td>
                                    <td class="text-end text-warning fw-bold fs-5">${totalAmount.toLocaleString('vi-VN')} ₫</td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                `);
            },
            error: function() {
                $('#orderDetailsContent').html('<div class="text-center py-4 text-danger"><i class="fas fa-exclamation-circle fa-2x mb-3 d-block"></i> Lỗi tải dữ liệu. Vui lòng kiểm tra lại kết nối hoặc API.</div>');
            }
        });
    });

    // Xử lý sự kiện hủy đơn hàng
    $(document).on('click', '.btn-cancel-order', function() {
        const orderId = $(this).data('id');
        if (confirm(`Bạn có chắc chắn muốn HỦY đơn hàng #SP-${orderId} không?\nHành động này không thể hoàn tác!`)) {
            $.ajax({
                url: `/api/orders/${orderId}/status`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({ status: "CANCELLED" }),
                success: function() {
                    alert("Đã hủy đơn hàng thành công!");
                    loadOrders($('.nav-link.active').data('status')); // Tải lại danh sách
                },
                error: function(xhr) {
                    alert("Không thể hủy đơn hàng: " + xhr.responseText);
                }
            });
        }
    });

    // Xử lý sự kiện xác nhận đã nhận hàng
    $(document).on('click', '.btn-complete-order', function() {
        const orderId = $(this).data('id');
        if (confirm(`Xác nhận bạn đã nhận được đơn hàng #SP-${orderId} và sản phẩm không có vấn đề gì?`)) {
            $.ajax({
                url: `/api/orders/${orderId}/status`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({ status: "COMPLETED" }),
                success: function() {
                    alert("Cảm ơn bạn đã mua sắm tại SNEAK PEAK! Đơn hàng đã hoàn thành.");
                    loadOrders($('.nav-link.active').data('status')); // Tải lại danh sách
                },
                error: function(xhr) {
                    alert("Không thể cập nhật trạng thái đơn hàng: " + xhr.responseText);
                }
            });
        }
    });
});