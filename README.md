# Hướng dẫn chạy dự án TaskFlow (Todo List)

## Yêu cầu chuẩn bị
- **Java JDK 17** hoặc cao hơn.
- **Node.js** (Phiên bản v18 trở lên) và **npm**.
- **Docker & Docker Compose**.

---

## Các bước khởi chạy dự án

### Bước 1: Khởi chạy Cơ sở dữ liệu (MySQL)
Mở terminal tại thư mục gốc của dự án (`todo/`) và chạy lệnh sau để khởi động MySQL bằng Docker:
```bash
docker-compose up -d --build
```
*MySQL sẽ chạy tại cổng `3307` của localhost.*

### Bước 2: Chạy Backend (Spring Boot)
Chạy ứng dụng Spring Boot bằng Maven Wrapper tại thư mục gốc của dự án:
- **Windows**:
  ```cmd
  .\mvnw.cmd spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  chmod +x mvnw
  ./mvnw spring-boot:run
  ```
Backend sẽ được khởi chạy tại địa chỉ: `http://localhost:8080`

### Bước 3: Chạy Frontend (Vue 3)
1. Di chuyển vào thư mục `todo-fe`:
   ```bash
   cd todo-fe
   ```
2. Cài đặt các gói phụ thuộc:
   ```bash
   npm install
   ```
3. Khởi động môi trường phát triển:
   ```bash
   npm run dev
   ```
Frontend sẽ chạy tại địa chỉ: `http://localhost:5173`. Mở trình duyệt và truy cập để sử dụng ứng dụng.
