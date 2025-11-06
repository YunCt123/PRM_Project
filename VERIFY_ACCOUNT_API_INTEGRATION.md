# API Integration - Verify Account (KYC Upload)

## Tổng quan
Đã tích hợp thành công API upload giấy tờ xác minh (KYC) vào `VerifyAccountActivity`.

## API Endpoint
**PATCH** `/api/users/me`
- **URL**: `https://be-ev-rental-system-production.up.railway.app/api/users/me`
- **Method**: PATCH
- **Content-Type**: multipart/form-data
- **Authorization**: Bearer Token

## Request Parameters
- `kyc.idFrontImage` - Ảnh mặt trước CMND/CCCD (file)
- `kyc.idBackImage` - Ảnh mặt sau CMND/CCCD (file)
- `kyc.licenseFrontImage` - Ảnh mặt trước giấy phép lái xe (file)
- `kyc.licenseBackImage` - Ảnh mặt sau giấy phép lái xe (file)
- `name` - Tên người dùng (optional)
- `phone` - Số điện thoại (optional)
- `gender` - Giới tính (optional)

## Files Created/Updated

### 1. UserApiService.java
**Path**: `app/src/main/java/com/example/prm_project/api/UserApiService.java`
- Interface Retrofit cho API user
- Method `updateProfile()` để upload KYC documents

### 2. VerifyAccountActivity.java (Updated)
**Path**: `app/src/main/java/com/example/prm_project/activies/VerifyAccountActivity.java`

**Tính năng mới:**
- ✅ Chọn ảnh từ thư viện (Image Picker)
- ✅ Preview ảnh đã chọn trên UI
- ✅ Upload 4 ảnh giấy tờ lên server qua API
- ✅ Hiển thị trạng thái loading khi upload
- ✅ Xử lý lỗi và thông báo kết quả
- ✅ Sử dụng token từ SharedPreferences để xác thực

**Các method chính:**
- `setupImagePicker()` - Khởi tạo image picker
- `openImagePicker()` - Mở thư viện ảnh
- `uploadKYCDocuments()` - Upload ảnh lên server
- `prepareFilePart()` - Chuyển đổi URI thành MultipartBody.Part

### 3. AndroidManifest.xml (Updated)
**Thêm permissions:**
- `READ_EXTERNAL_STORAGE` - Đọc ảnh từ thư viện (Android 12 trở xuống)
- `READ_MEDIA_IMAGES` - Đọc ảnh (Android 13+)

## Cách sử dụng

### 1. Từ Profile Fragment
```java
// User click vào "Xác minh tài khoản"
Intent intent = new Intent(getActivity(), VerifyAccountActivity.class);
startActivity(intent);
```

### 2. Từ Payment Activity
```java
// User click vào PayOS card
Intent intent = new Intent(this, VerifyAccountActivity.class);
startActivity(intent);
```

## Flow hoạt động

1. **User mở VerifyAccountActivity**
   - Hiển thị 4 ô để upload ảnh

2. **User click vào từng ô**
   - Mở thư viện ảnh
   - User chọn ảnh
   - Hiển thị preview ảnh đã chọn

3. **User click "Gửi xác minh"**
   - Kiểm tra đã chọn đủ 4 ảnh chưa
   - Hiển thị "Đang tải lên..."
   - Upload ảnh lên server qua API
   - Nhận kết quả và hiển thị thông báo

4. **Response từ API**
   - Thành công: Hiển thị "Đã gửi yêu cầu xác minh thành công!" và đóng activity
   - Thất bại: Hiển thị lỗi và cho phép thử lại

## Response Format

### Success (200)
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "_id": "66fab8ad46a4f8a87da8fe9a",
    "role": "renter",
    "name": "string",
    "email": "string",
    "phone": "string",
    "gender": "male",
    "station": "66fab8ad46a4f8a87da8fe9a"
  }
}
```

### Error (400)
```json
{
  "success": false,
  "message": "Bad request"
}
```

### Error (401)
```json
{
  "success": false,
  "message": "Bad request"
}
```

## Lưu ý quan trọng

1. **Token Authorization**
   - Token được lưu trong `SharedPreferences` với key `"auth_token"`
   - Phải có token hợp lệ để upload được
   - Token được gửi qua header: `Authorization: Bearer {token}`

2. **File Size & Format**
   - API hỗ trợ file ảnh (JPEG, PNG)
   - Nên resize ảnh trước khi upload để tối ưu tốc độ
   - Hiện tại chưa có giới hạn size cụ thể

3. **Permissions**
   - App cần xin quyền đọc ảnh lần đầu chạy
   - Android 13+ sử dụng `READ_MEDIA_IMAGES`
   - Android 12 trở xuống sử dụng `READ_EXTERNAL_STORAGE`

4. **ImageView Preview**
   - Layout cần có `ImageView` với id `ivPreview` trong mỗi upload layout
   - Nếu không có, preview sẽ bị bỏ qua nhưng upload vẫn hoạt động

## Testing

### Test Cases
1. ✅ Chọn từng ảnh một và kiểm tra preview
2. ✅ Upload đủ 4 ảnh và kiểm tra response thành công
3. ✅ Upload thiếu ảnh và kiểm tra validation
4. ✅ Upload không có token và kiểm tra redirect login
5. ✅ Test với network error và kiểm tra error handling

## Troubleshooting

### Lỗi: "Vui lòng đăng nhập lại"
- Token không tồn tại hoặc đã hết hạn
- Giải pháp: Đăng xuất và đăng nhập lại

### Lỗi: "Cannot resolve symbol 'ivPreview'"
- Layout chưa có ImageView để preview
- Giải pháp: Thêm ImageView vào layout hoặc bỏ qua phần preview

### Lỗi: Permission denied
- Chưa cấp quyền đọc ảnh
- Giải pháp: Vào Settings > Apps > PRM_Project > Permissions và bật quyền

## Future Improvements

1. ✅ Thêm image compression trước khi upload
2. ✅ Thêm progress bar hiển thị % upload
3. ✅ Cho phép crop/rotate ảnh trước khi upload
4. ✅ Cache ảnh đã chọn khi rotate screen
5. ✅ Thêm camera capture thay vì chỉ chọn từ thư viện

