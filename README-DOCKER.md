# Hướng dẫn Triển khai eFlow bằng Docker (Dành cho máy khác)

Tài liệu này hướng dẫn cách đóng gói ứng dụng `eFlow` thành Docker Image và chạy nó trên một máy tính khác mà không cần cài đặt môi trường Java/Maven.

## 1. Đóng gói Ứng dụng (Trên máy hiện tại)

Trước tiên, bạn cần tạo ra file Image của ứng dụng. Chạy lệnh sau tại thư mục gốc của dự án:

```powershell
./mvnw -Pprod verify jib:dockerBuild -DskipTests
```

Lệnh này sẽ tạo ra một Docker Image có tên là `eflow:latest` trong Docker local của bạn.

## 2. Xuất Image ra file (Để mang sang máy khác)

Để mang Image này sang máy khác mà không cần dùng Docker Hub, bạn hãy xuất nó ra file nén `.tar`:

```powershell
docker save eflow:latest > eflow-image.tar
```

## 3. Di chuyển sang máy mới

Hãy copy các thành phần sau sang máy tính mới:

1.  File `eflow-image.tar` vừa tạo.
2.  Thư mục `src/main/docker/` (Chứa các file cấu hình compose).

## 4. Triển khai trên máy mới

Tại máy tính mới (đã cài sẵn Docker), thực hiện các bước sau:

### Bước 4.1: Nạp Image vào Docker

Mở Terminal tại thư mục chứa file `.tar` và chạy:

```powershell
docker load < eflow-image.tar
```

### Bước 4.2: Khởi chạy hệ thống

Di chuyển vào thư mục chứa các file docker (thường là `src/main/docker`) và chạy lệnh:

```powershell
docker compose -f app.yml up -d
```

## 5. Danh sách các Cổng (Ports) truy cập

Sau khi khởi chạy thành công, bạn có thể truy cập các dịch vụ tại:

- **eFlow App**: `http://localhost:8050`
- **MySQL DB**: `localhost:3307` (User: `root`, Password: [Trống], DB: `eflow`)
- **JHipster Registry**: `http://localhost:8761` (User: `admin`, Pass: `admin`)

---

**Lưu ý**: Đảm bảo máy tính mới đã cài đặt **Docker Desktop** hoặc **Docker Engine**.
