$(document).ready(function() {
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
    }

    function renderChart(data, year) {
        const container = document.querySelector('#revenueChart');
        if (!container) return; // Không tìm thấy chỗ vẽ biểu đồ thì bỏ qua

        // Hủy biểu đồ cũ nếu đã vẽ
        if (revenueChart) {
            revenueChart.destroy();
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
        const $revenueChart = $('#revenueChart');
        // Chỉ tải dữ liệu nếu tìm thấy element và nó chưa được khởi tạo
        if ($revenueChart.length && !$revenueChart.data('chart-initialized')) {
            console.log(">>> [Báo cáo] TÌM THẤY thẻ canvas, đang tiến hành nạp dữ liệu API...");
            loadDashboardStats();
            const currentYear = new Date().getFullYear();
            loadRevenueChart(currentYear);
            $revenueChart.data('chart-initialized', true); // Đánh dấu đã vẽ để không vẽ lại
        } else if ($revenueChart.length === 0) {
             console.warn(">>> [Báo cáo] CẢNH BÁO: Không tìm thấy thẻ <canvas id='revenueChart'>!");
        }
    }

    // ==========================================
    // 4. TẢI DỮ LIỆU ĐỘNG CHO FORM THÊM SẢN PHẨM
    // ==========================================
    function loadFormOptions() {
        // Cấu hình các API tương ứng với các ô Select phổ biến trong form
        const selects = {
            colors: { url: "/api/colors", target: 'select[name="colorId"], select[name="color_id"], #colorSelect, #color, .color-select' },
            sizes: { url: "/api/sizes", target: 'select[name="sizeId"], select[name="size_id"], #sizeSelect, #size, .size-select' },
            brands: { url: "/api/brands", target: 'select[name="brandId"], select[name="brand_id"], #brandSelect, #brand, .brand-select' },
            categories: { url: "/api/categories", target: 'select[name="categoryId"], select[name="category_id"], #categorySelect, #category, .category-select' }
        };

        for (const key in selects) {
            const config = selects[key];
            const $target = $(config.target);
            
            // Chỉ gọi API nếu tìm thấy thẻ select trên giao diện
            if ($target.length > 0) { 
                // Tránh gọi API liên tục nếu đã load rồi
                if ($target.attr('data-loaded') === 'true') continue;

                $.get(config.url).done(function(data) {
                    if (data && data.length > 0) {
                        let defaultText = "-- Chọn lựa --";
                        if (key === 'colors') defaultText = "-- Chọn Màu sắc --";
                        if (key === 'sizes') defaultText = "-- Chọn Kích cỡ --";
                        if (key === 'brands') defaultText = "-- Chọn Thương hiệu --";
                        if (key === 'categories') defaultText = "-- Chọn Danh mục --";

                        let html = `<option value="" disabled selected>${defaultText}</option>`;
                        data.forEach(item => {
                            html += `<option value="${item.id}">${item.name}</option>`;
                        });
                        $target.html(html); // Ghi đè các option code cứng bằng dữ liệu thật từ DB
                        $target.attr('data-loaded', 'true'); // Đánh dấu đã load
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
});