# 🛍️ Fashion Shop RESTful API

Một hệ thống quản lý cửa hàng thời trang (Fashion Shop) với kiến trúc Backend cung cấp các RESTful API cho việc quản lý Sản phẩm, Đơn hàng, Giỏ hàng và Người dùng.

## 🚀 Giới thiệu
Dự án **fashion_shop** là một ứng dụng web phục vụ việc kinh doanh thời trang. Hệ thống chú trọng vào việc tuân thủ các chuẩn thiết kế RESTful API (hỗ trợ tìm kiếm an toàn với JSON Body) và quản lý ngoại lệ (Exception Handling) chặt chẽ để đảm bảo hệ thống vận hành ổn định và trả về log lỗi chính xác.

## 🛠️ Công nghệ sử dụng
Dự án được xây dựng dựa trên các ngôn ngữ và công nghệ:
- **Backend:** Java (Spring Boot)
- **Frontend:** HTML, JavaScript, CSS
- **Quản lý thư viện:** Maven (`pom.xml`)
- **Triển khai (Deployment):** Docker & Docker Compose (`Dockerfile`, `docker-compose.yml`)

## ✨ Các chức năng chính (Dự kiến)
- **Product Management:** Thêm, Sửa, Xóa và Tìm kiếm sản phẩm (hỗ trợ các bộ lọc phức tạp).
- **Brand & Category Management:** Phân loại sản phẩm theo nhãn hiệu và danh mục.
- **Cart & Order System:** Quản lý giỏ hàng và xử lý thanh toán đơn hàng.
- **User API:** Quản lý thông tin và phân quyền người dùng.

## ⚙️ Hướng dẫn cài đặt và chạy dự án (Getting Started)

### Yêu cầu hệ thống:
- Java JDK 11/17 (hoặc phiên bản bạn đang dùng)
- Maven
- Docker & Docker Compose (nếu chạy qua container)

### Cách 1: Chạy trực tiếp bằng Maven
1. Clone dự án về máy:
   ```bash
   git clone https://github.com/donghuyhoang/fashion_shop.git

    Di chuyển vào thư mục dự án và tải các dependency:
    Khởi chạy ứng dụng:

Cách 2: Chạy bằng Docker
Hệ thống đã được tích hợp sẵn cấu hình Docker.

    Build và khởi chạy các container (Bao gồm cả Database nếu có cấu hình):

📚 Cấu trúc thư mục mã nguồn chính

    src/main/java/com/javaweb/api: Chứa các REST Controller xử lý HTTP Request (ProductAPI, OrderAPI, v.v...).
    src/main/java/com/javaweb/service: Chứa các Business Logic nghiệp vụ.
    src/main/java/com/javaweb/repository: Tầng giao tiếp với cơ sở dữ liệu (đã được cấu hình xử lý log và exception).
    Dockerfile & docker-compose.yml: Cấu hình môi trường triển khai.

🤝 Đóng góp (Contributing)
Mọi đóng góp (Pull Request) hay báo lỗi (Issue) đều được hoan nghênh.


***

