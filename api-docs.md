# Tài liệu API cho Frontend - eFlow Microservice

Tài liệu này cung cấp danh sách các API chính của hệ thống eFlow, bao gồm các ví dụ về Request và Response để hỗ trợ quá trình phát triển Frontend.

**Base URL:** `/api`

---

## 1. Quản lý Quy trình (Workflow)

Phụ trách khởi tạo và quản lý thông tin chung của các quy trình nghiệp vụ.

### 1.1 Khởi tạo Workflow mới

- **Endpoint:** `POST /workflow/init`
- **Mô tả:** Tạo một bản ghi quy trình mới với trạng thái mặc định là "KhoiTao".
- **Request Body (FlowDTO):**

```json
{
  "flowName": "Quy trình nghỉ phép",
  "department": "Phòng Đào tạo",
  "describe": "Quy trình dành cho cán bộ giảng viên xin nghỉ phép ngắn hạn"
}
```

- **Response (210 Created):**

```json
{
  "id": 1001,
  "flowName": "Quy trình nghỉ phép",
  "department": "Phòng Đào tạo",
  "describe": "Quy trình dành cho cán bộ giảng viên xin nghỉ phép ngắn hạn",
  "status": "KhoiTao",
  "flowStartDate": "2024-04-06T08:16:36Z",
  "ownerName": null,
  "flowEndDate": null
}
```

### 1.2 Lấy thông tin tóm tắt Workflow

- **Endpoint:** `GET /workflow/{flowId}/summary`
- **Mô tả:** Lấy thông tin chi tiết của một Workflow theo ID.
- **Response (200 OK):**

```json
{
  "id": 1001,
  "flowName": "Quy trình nghỉ phép",
  "department": "Phòng Đào tạo",
  "status": "KhoiTao",
  "flowStartDate": "2024-04-06T08:16:36Z"
}
```

### 1.3 Cập nhật trạng thái Workflow

- **Endpoint:** `POST /workflow/{flowId}/status`
- **Mô tả:** Thay đổi trạng thái của quy trình (ví dụ: DangHoatDong, TamDung, KetThuc).
- **Request Body:**

```json
"DangHoatDong"
```

- **Response (200 OK):**

```json
{
  "id": 1001,
  "status": "DangHoatDong"
}
```

---

## 2. Quản lý Tài khoản (Management Account)

Dùng cho các chức năng quản trị tài khoản người dùng trong hệ thống eFlow.

### 2.1 Tìm kiếm thông tin người dùng

- **Endpoint:** `POST /management/account/search`
- **Mô tả:** Tìm kiếm thông tin cơ bản và quyền hạn của người dùng qua Email.
- **Request Body:**

```json
{
  "email": "user@vnu.uet.vn"
}
```

- **Response (200 OK):**

```json
{
  "email": "user@vnu.uet.vn",
  "firstName": "Nguyen Van A",
  "phone": "0912345678",
  "department": "Khoa CNTT",
  "isActive": true,
  "roles": ["ROLE_USER", "ROLE_MANAGER"]
}
```

### 2.2 Kiểm tra khả năng xóa tài khoản

- **Endpoint:** `POST /management/account/check-deletable`
- **Mô tả:** Kiểm tra xem tài khoản có đang tham gia quy trình nào không để cho phép xóa.
- **Request Body:**

```json
{
  "email": "user@vnu.uet.vn"
}
```

- **Response (200 OK):**

```json
{
  "deletable": true
}
```

### 2.3 Xóa tài khoản (Soft Delete)

- **Endpoint:** `POST /management/account/delete`
- **Mô tả:** Thực hiện xóa tài khoản nếu thỏa mãn điều kiện.
- **Request Body:**

```json
{
  "email": "user@vnu.uet.vn"
}
```

- **Response (200 OK):**

```json
{
  "status": "success",
  "message": "Tài khoản đã được xóa thành công"
}
```

---

## 3. Cấu hình Nút (Nodes)

Quản lý các bước (nút) trong một bản thiết kế quy trình.

### 3.1 Tạo Nút mới

- **Endpoint:** `POST /nodes`
- **Mô tả:** Thêm một nút vào Workflow hiện tại.
- **Request Body:**

```json
{
  "nodeType": "审批",
  "flow": {
    "id": 1001
  }
}
```

- **Response (201 Created):**

```json
{
  "id": 501,
  "nodeType": "审批",
  "flow": {
    "id": 1001,
    "flowName": "Quy trình nghỉ phép"
  }
}
```

### 3.2 Lấy danh sách Nút của hệ thống

- **Endpoint:** `GET /nodes?page=0&size=20&sort=id,desc`
- **Mô tả:** Lấy danh sách phân trang của tất cả các nút.
- **Response (200 OK):**

```json
[
  {
    "id": 501,
    "nodeType": "审批",
    "flow": { "id": 1001 }
  }
]
```

---

## 4. Quản lý Phân quyền (Permissions)

Dùng để tra cứu và gán quyền cho người dùng trong hệ thống.

### 4.1 Lấy danh sách Vai trò Hệ thống

- **Endpoint:** `GET /permissions/system-roles`
- **Mô tả:** Trả về danh sách tất cả các Role khả dụng trong hệ thống cùng mô tả.
- **Response (200 OK):**

```json
[
  { "id": 1, "code": "ROLE_USER", "description": "Người dùng cơ bản" },
  { "id": 2, "code": "ROLE_MANAGER", "description": "Quản lý phòng" },
  { "id": 3, "code": "ROLE_ADMIN", "description": "Quản trị viên hệ thống" }
]
```

### 4.2 Tra cứu quyền của một Người dùng

- **Endpoint:** `POST /permissions/search-user-roles`
- **Mô tả:** Trả về danh sách Role hiện tại của một người dùng.
- **Request Body:**

```json
{
  "email": "user@vnu.uet.vn"
}
```

- **Response (200 OK):**

```json
{
  "email": "user@vnu.uet.vn",
  "roles": ["ROLE_USER", "ROLE_MANAGER"]
}
```

### 4.3 Đồng bộ/Gán quyền cho Người dùng

- **Endpoint:** `POST /permissions/sync`
- **Mô tả:** Gán danh sách Role mới cho người dùng (thay thế hoặc cập nhật tùy logic backend).
- **Request Body (RolesRequestDTO):**

```json
{
  "email": "user@vnu.uet.vn",
  "roles": ["ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"]
}
```

- **Response (200 OK):**

```json
{
  "email": "user@vnu.uet.vn",
  "roles": ["ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"],
  "message": "Đã cập nhật danh sách quyền thành công"
}
```

---

## Ghi chú chung

1. **Lỗi 401 Unauthorized:** Token hết hạn hoặc không hợp lệ. Cần login lại.
2. **Lỗi 403 Forbidden:** Tài khoản không có quyền thực hiện hành động này.
3. **Lỗi 404 Not Found:** Không tìm thấy tài nguyên (ID không tồn tại).
4. **Validation:** Các trường có đánh dấu `@NotNull` hoặc `@Size` trong Backend sẽ trả về lỗi 400 nếu vi phạm.
