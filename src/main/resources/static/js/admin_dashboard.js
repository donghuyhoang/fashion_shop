$(document).ready(function() {
    const API_ORDERS = "/api/orders/";

    // 1. Tải các con số thống kê tổng quan (Tổng Users, Đơn hàng, Doanh thu)
    window.loadDashboardStats = function() {
        $.ajax({
            url: API_ORDERS + "dashboard/stats",
            type: "GET",
            success: function(stats) {
                $('#totalUsers').text(stats.totalUsers || 0);
                $('#totalOrders').text(stats.totalOrders || 0);
                $('#totalRevenue').text((stats.totalRevenue || 0).toLocaleString('vi-VN') + ' ₫');
            },
            error: function(err) {
                console.error("Lỗi lấy thống kê tổng quan:", err);
            }
        });
    };

    // 2. Vẽ biểu đồ doanh thu theo tháng bằng Chart.js
    let revenueChart;
    window.loadRevenueChart = function(year) {
        $.ajax({
            url: API_ORDERS + "dashboard/revenue/" + year,
            type: "GET",
            success: function(data) {
                // Khởi tạo mảng 12 tháng mặc định là 0 đ
                const monthlyData = new Array(12).fill(0);
                
                // Đổ dữ liệu từ API vào đúng vị trí tháng
                data.forEach(item => {
                    // Nếu tháng hợp lệ từ 1 đến 12 thì nhét dữ liệu vào
                    if (item.month >= 1 && item.month <= 12) {
                        monthlyData[item.month - 1] = item.revenue;
                    }
                });

                renderChart(monthlyData, year);
            },
            error: function(err) {
                console.error("Lỗi lấy dữ liệu biểu đồ:", err);
            }
        });
    };

    window.renderChart = function(data, year) {
        const ctx = document.getElementById('revenueChart');
        if (!ctx) return; // Không tìm thấy chỗ vẽ biểu đồ thì bỏ qua

        // Hủy biểu đồ cũ nếu đã vẽ
        if (revenueChart) {
            revenueChart.destroy();
        }

        // Tạo hiệu ứng màu Gradient từ trên xuống dưới cho cột
        const gradient = ctx.getContext('2d').createLinearGradient(0, 0, 0, 350);
        gradient.addColorStop(0, 'rgba(99, 102, 241, 0.8)'); // Màu xanh chàm (Indigo) ở trên
        gradient.addColorStop(1, 'rgba(99, 102, 241, 0.1)'); // Mờ dần xuống dưới

        revenueChart = new Chart(ctx, {
            type: 'bar', 
            data: {
                labels: ['Th. 1', 'Th. 2', 'Th. 3', 'Th. 4', 'Th. 5', 'Th. 6', 'Th. 7', 'Th. 8', 'Th. 9', 'Th. 10', 'Th. 11', 'Th. 12'],
                datasets: [{
                    label: 'Doanh thu năm ' + year + ' (VNĐ)',
                    data: data,
                    backgroundColor: gradient, 
                    borderColor: '#6366f1',
                    borderWidth: 2,
                    borderRadius: 6, // Bo tròn góc các cột
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }, 
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return 'Doanh thu: ' + context.parsed.y.toLocaleString('vi-VN') + ' ₫';
                            }
                        }
                    }
                },
                scales: {
                    y: { 
                        beginAtZero: true,
                        grid: { color: 'rgba(0,0,0,0.05)' }, 
                        ticks: { 
                            color: '#6b7280',
                            callback: function(value) {
                                // Rút gọn số tiền (vd: 1000000 -> 1 Tr)
                                if (value >= 1000000) return (value / 1000000) + ' Tr';
                                return value;
                            }
                        } 
                    },
                    x: {
                        grid: { display: false }, 
                        ticks: { color: '#6b7280' }
                    }
                }
            }
        });
    };

    // 3. Hàm gộp chung toàn cục để gọi cả 2
    window.refreshDashboard = function() {
        console.log(">>> [Báo cáo] Hàm refreshDashboard() ĐÃ ĐƯỢC GỌI!");
        setTimeout(function() {
            if ($('#revenueChart').length) {
                console.log(">>> [Báo cáo] TÌM THẤY thẻ canvas, đang tiến hành nạp dữ liệu API...");
                loadDashboardStats();
                loadRevenueChart(2026); // Mình đã cố định gọi năm 2026 cho khớp với DB của bạn
            } else {
                console.warn(">>> [Báo cáo] CẢNH BÁO: Không tìm thấy thẻ <canvas id='revenueChart'>!");
            }
        }, 200); 
    };

    // Tự động chạy lần đầu tiên trang load
    console.log(">>> [Báo cáo] File admin_dashboard.js đã được nhúng thành công.");
    refreshDashboard();

    // Bắt sự kiện tự động
    $(document).on('shown.bs.tab', 'a[data-bs-toggle="tab"], button[data-bs-toggle="tab"], [data-toggle="tab"]', function (e) {
        refreshDashboard();
    });

    $(document).on('click', '.nav-link, .menu-item, a[href="#dashboard"], a:contains("Báo cáo"), a:contains("Doanh thu")', function() {
        refreshDashboard();
    });
});