# 🛍️ Fashion Shop - E-commerce Backend System

Một hệ thống quản lý cửa hàng thời trang trực tuyến với phần "lõi" (Backend) được thiết kế nguyên bản và mạnh mẽ bằng Java. Dự án cung cấp toàn bộ nền tảng logic để xử lý các nghiệp vụ phức tạp của một trang thương mại điện tử, từ quản lý luồng sản phẩm, giỏ hàng cho đến hệ thống phân quyền người dùng.

## 🚀 Giới thiệu dự án
**Fashion Shop** không chỉ là một trang web bán hàng cơ bản, mà là một minh chứng cho việc xây dựng kiến trúc Backend vững chắc. Hệ thống được chia lớp (Layered Architecture) rõ ràng để dễ dàng mở rộng và bảo trì. Mặc dù dự án có bao gồm giao diện người dùng (Frontend bằng HTML/JS/CSS), nhưng linh hồn thực sự của dự án nằm ở hệ thống API và các luồng xử lý dữ liệu (Data Processing) ở phía Server.

## 💎 Điểm nổi bật của Backend
Dự án được xây dựng với tư duy ưu tiên hiệu năng và khả năng kiểm soát dữ liệu:
- **Kiến trúc phân tầng (Layered Architecture):** Tách biệt hoàn toàn giữa `API Layer` (tiếp nhận yêu cầu), `Service Layer` (xử lý logic nghiệp vụ) và `Repository Layer` (tương tác trực tiếp với Database).
- **Tối ưu hóa truy vấn Database:** Sử dụng kết nối JDBC thuần (`ConnectionJDBCUtil`) kết hợp với các câu lệnh SQL tối ưu để nắm quyền kiểm soát hoàn toàn hiệu năng truy vấn chi tiết sản phẩm.
- **Hệ sinh thái E-commerce đầy đủ:** Cung cấp sẵn các module quản lý: `Product`, `Category`, `Brand`, `Cart`, `Order`, `Size`, `Color` và hệ thống phân quyền `Role/User`.
- **Triển khai hiện đại (Modern Deployment):** Đóng gói sẵn sàng với `Docker` và `docker-compose`, giúp khởi chạy toàn bộ hệ thống (cả App lẫn Database) chỉ với một câu lệnh.

## 🛠️ Công nghệ sử dụng
- **Backend (Core):** Java (Spring Boot) [1]
- **Tương tác Cơ sở dữ liệu:** JDBC/SQL trực tiếp [2]
- **Quản lý thư viện & Build:** Maven (`pom.xml`) [3]
- **DevOps & Triển khai:** Docker, Docker Compose (`Dockerfile`, `docker-compose.yml`) [3]
- **Frontend (Giao diện):** HTML (44.3%), JavaScript (16.5%), CSS [1]

## ⚙️ Hướng dẫn cài đặt và chạy dự án (Getting Started)

### Yêu cầu môi trường:
- Java JDK 11/17+
- Maven
- Docker & Docker Compose (Khuyên dùng)

### 🐳 Chạy cực nhanh với Docker
Dự án đã được cấu hình sẵn môi trường container hoá. Chỉ cần chạy lệnh sau tại thư mục gốc:
```bash
docker-compose up -d --build

Hệ thống sẽ tự động build file .jar, thiết lập cơ sở dữ liệu và khởi động server.
💻 Chạy ở chế độ Dev (Dành cho lập trình viên)

    Clone dự án về máy:
    Cài đặt các thư viện phụ thuộc:
    Khởi chạy ứng dụng:

📚 Cấu trúc thư mục lõi (Backend)
Hệ thống mã nguồn Java được đặt tại src/main/java/com/javaweb/:

    📁 api/: Nơi định nghĩa các Endpoints giao tiếp với Client (chứa ProductAPI, OrderAPI, UserAPI, CartAPI...).
    📁 service/: Trái tim của hệ thống, chứa toàn bộ logic tính toán kinh doanh (Business Logic).
    📁 repository/: Tầng xử lý giao tiếp cơ sở dữ liệu, thực thi các câu lệnh SQL phức tạp lấy dữ liệu sản phẩm, chi tiết kho.
    📁 builder/: Chứa các mẫu thiết kế (Design Patterns) như Builder để hỗ trợ tìm kiếm với nhiều tiêu chí linh hoạt.

🤝 Tương lai và Đóng góp
Dự án đang trong quá trình liên tục hoàn thiện. Các kế hoạch sắp tới bao gồm:

    Chuẩn hóa toàn bộ hệ thống API theo nguyên tắc RESTful chặt chẽ hơn.
    Cải thiện hệ thống bắt lỗi (Global Exception Handling) và ghi log tập trung.

Mọi Pull Request đóng góp mã nguồn hoặc báo lỗi (Issue) đều được chào đón!


***
