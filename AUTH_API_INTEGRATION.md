# Authentication API Integration

## Tổng quan

Đã tích hợp API Authentication (đăng nhập và đăng ký) vào ứng dụng.

- **Base URL**: `https://be-ev-rental-system-production.up.railway.app/`
- **API Documentation**: https://be-ev-rental-system-production.up.railway.app/docs/#/

## Các file đã tạo

### 1. Models (`app/src/main/java/com/example/prm_project/models/`)

#### User.java

Model đại diện cho user data từ API với các fields:

- `_id`: User ID
- `name`: Tên đầy đủ
- `email`: Email
- `phone`: Số điện thoại
- `gender`: Giới tính (male/female)
- `role`: Vai trò (renter/owner)
- `createdAt`, `updatedAt`: Timestamps

#### LoginRequest.java

Request body cho API login:

```json
{
  "email": "admin@example.com",
  "password": "Admin@123"
}
```

#### RegisterRequest.java

Request body cho API register:

```json
{
  "name": "Nguyen Van A",
  "email": "a@example.com",
  "password": "Pass@123",
  "phone": "0909123456",
  "gender": "male",
  "role": "renter"
}
```

#### AuthResponse.java

Response từ API login/register:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "_id": "123456",
      "name": "Nguyen Van A",
      "email": "a@example.com",
      "phone": "0909123456",
      "gender": "male",
      "role": "renter"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 2. API Service (`app/src/main/java/com/example/prm_project/api/`)

#### AuthApiService.java

Interface định nghĩa các API endpoints:

- `login(LoginRequest)`: POST /api/auth/login
- `register(RegisterRequest)`: POST /api/auth/register

### 3. Repository (`app/src/main/java/com/example/prm_project/repository/`)

#### AuthRepository.java

Quản lý các API calls với callback interface:

- `login(email, password, callback)`: Đăng nhập user
- `register(name, email, password, phone, gender, role, callback)`: Đăng ký user mới

### 4. Utils (`app/src/main/java/com/example/prm_project/utils/`)

#### SessionManager.java

Quản lý session và lưu trữ thông tin user:

- `createLoginSession(user, token)`: Lưu session sau khi login/register
- `isLoggedIn()`: Kiểm tra trạng thái đăng nhập
- `getUserDetails()`: Lấy thông tin user
- `getToken()`: Lấy authentication token
- `logout()`: Đăng xuất và xóa session

### 5. Activities đã cập nhật

#### LoginActivity.java

- Tích hợp AuthRepository để gọi API login
- Sử dụng SessionManager để lưu session
- Hiển thị ProgressDialog khi đang đăng nhập
- Tự động chuyển đến MainActivity sau khi login thành công
- Kiểm tra session khi mở app (auto-login nếu đã đăng nhập)

#### RegisterActivity.java

- Tích hợp AuthRepository để gọi API register
- Sử dụng SessionManager để lưu session
- Hiển thị ProgressDialog khi đang đăng ký
- Tự động đăng nhập và chuyển đến MainActivity sau khi đăng ký thành công

## Cách sử dụng

### 1. Đăng nhập

```java
AuthRepository authRepository = new AuthRepository();
SessionManager sessionManager = new SessionManager(context);

authRepository.login("user@example.com", "password123", new AuthRepository.AuthCallback() {
    @Override
    public void onSuccess(User user, String token) {
        // Lưu session
        sessionManager.createLoginSession(user, token);

        // Navigate to main screen
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError(String errorMessage) {
        // Hiển thị lỗi
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }
});
```

### 2. Đăng ký

```java
AuthRepository authRepository = new AuthRepository();
SessionManager sessionManager = new SessionManager(context);

authRepository.register(
    "Nguyen Van A",          // name
    "a@example.com",         // email
    "Pass@123",              // password
    "0909123456",            // phone
    "male",                  // gender
    "renter",                // role
    new AuthRepository.AuthCallback() {
        @Override
        public void onSuccess(User user, String token) {
            // Lưu session
            sessionManager.createLoginSession(user, token);

            // Navigate to main screen
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        @Override
        public void onError(String errorMessage) {
            // Hiển thị lỗi
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
);
```

### 3. Kiểm tra trạng thái đăng nhập

```java
SessionManager sessionManager = new SessionManager(context);

if (sessionManager.isLoggedIn()) {
    // User đã đăng nhập
    User user = sessionManager.getUserDetails();
    String userName = sessionManager.getUserName();
    String token = sessionManager.getToken();
} else {
    // User chưa đăng nhập, chuyển đến LoginActivity
    Intent intent = new Intent(context, LoginActivity.class);
    startActivity(intent);
}
```

### 4. Đăng xuất

```java
SessionManager sessionManager = new SessionManager(context);
sessionManager.logout();

// Chuyển về LoginActivity
Intent intent = new Intent(context, LoginActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(intent);
```

## API Response Examples

### Successful Login

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "_id": "673fc0ca1f9a5ea4d38cde9e",
      "name": "Admin User",
      "email": "admin@example.com",
      "phone": "0909123456",
      "gender": "male",
      "role": "admin"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2NzNmYzBjYTFmOWE1ZWE0ZDM4Y2RlOWUiLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzIyMDQzNDYsImV4cCI6MTczMjI5MDc0Nn0.xyz..."
  }
}
```

### Failed Login

```json
{
  "success": false,
  "error": "Email hoặc mật khẩu không đúng",
  "statusCode": 401
}
```

### Successful Registration

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "_id": "673fc0ca1f9a5ea4d38cde9f",
      "name": "Nguyen Van A",
      "email": "a@example.com",
      "phone": "0909123456",
      "gender": "male",
      "role": "renter"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Failed Registration (Email exists)

```json
{
  "success": false,
  "error": "Email đã được sử dụng",
  "statusCode": 409
}
```

## Testing

### Test Login

1. Mở app
2. Nhập email và password hợp lệ:
   - Email: `admin@example.com`
   - Password: `Admin@123`
3. Click "Sign In"
4. Kiểm tra Logcat với tag "LoginActivity" và "AuthRepository"
5. Sau khi login thành công, app sẽ chuyển đến MainActivity

### Test Register

1. Mở app
2. Click "Sign Up"
3. Nhập thông tin đăng ký:
   - Full Name: Tên đầy đủ
   - Email: Email hợp lệ (chưa được sử dụng)
   - Phone: Số điện thoại
   - Password: Mật khẩu (ít nhất 6 ký tự)
   - Confirm Password: Nhập lại mật khẩu
4. Check "Accept terms"
5. Click "Sign Up"
6. Kiểm tra Logcat với tag "RegisterActivity" và "AuthRepository"
7. Sau khi register thành công, app sẽ tự động đăng nhập và chuyển đến MainActivity

### Test Session

1. Login thành công
2. Close app hoàn toàn
3. Mở app lại
4. App sẽ tự động chuyển đến MainActivity (không cần login lại)

### Test Logout

1. Trong ProfileFragment hoặc SettingsFragment
2. Thêm button logout:

```java
sessionManager.logout();
Intent intent = new Intent(getActivity(), LoginActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(intent);
getActivity().finish();
```

## Error Handling

API sẽ trả về các error codes:

- **400**: Bad Request - Thông tin không hợp lệ
- **401**: Unauthorized - Email hoặc password sai
- **404**: Not Found - Tài khoản không tồn tại
- **409**: Conflict - Email đã được sử dụng
- **500**: Server Error - Lỗi server

Repository đã xử lý các error codes này và trả về message phù hợp bằng tiếng Việt.

## Security Notes

1. **Token Storage**: Token được lưu trong SharedPreferences (MODE_PRIVATE)
2. **HTTPS**: API sử dụng HTTPS để bảo mật connection
3. **Password**: Password không được lưu trữ local, chỉ gửi lên API khi login/register
4. **Session**: Session được lưu local để hỗ trợ auto-login

## Troubleshooting

### Lỗi "Unable to resolve host"

- Kiểm tra internet connection
- Kiểm tra BASE_URL trong ApiClient.java

### Lỗi "Email hoặc mật khẩu không đúng"

- Verify email và password
- Kiểm tra API documentation để xem format yêu cầu

### Lỗi "Email đã được sử dụng"

- Sử dụng email khác
- Hoặc login với email đó

### Lỗi JSON parsing

- Check response structure từ API
- Xem Logcat để debug raw response

## Next Steps

Có thể mở rộng thêm:

1. **Forgot Password**: Thêm chức năng reset password
2. **Social Login**: Tích hợp Google/Facebook login
3. **Profile Update**: Cho phép user cập nhật thông tin
4. **Token Refresh**: Tự động refresh token khi hết hạn
5. **Biometric Auth**: Thêm đăng nhập bằng vân tay/Face ID
6. **Remember Me**: Thêm option "Remember me" cho login
7. **Email Verification**: Xác thực email sau khi đăng ký
