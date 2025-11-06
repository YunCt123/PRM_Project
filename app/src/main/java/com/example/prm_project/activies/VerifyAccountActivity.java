package com.example.prm_project.activies;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prm_project.R;
import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.UserApiService;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.User;
import com.example.prm_project.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAccountActivity extends AppCompatActivity {

    private static final String TAG = "VerifyAccount";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private ImageButton btnBack;
    private FrameLayout layoutIdFront, layoutIdBack, layoutLicenseFront, layoutLicenseBack;
    private ImageView ivIdFront, ivIdBack, ivLicenseFront, ivLicenseBack;
    private View placeholderIdFront, placeholderIdBack, placeholderLicenseFront, placeholderLicenseBack;
    private MaterialButton btnSubmit;
    private TextView tvKycStatus;
    private EditText etIdNumber, etLicenseNumber;

    private Uri idFrontUri, idBackUri, licenseFrontUri, licenseBackUri;
    private int currentImageType = 0; // 1: ID Front, 2: ID Back, 3: License Front, 4: License Back

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private UserApiService userApiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        initViews();

        // Initialize session manager and API
        sessionManager = new SessionManager(this);
        userApiService = ApiClient.getClient().create(UserApiService.class);

        // Request runtime permission early so user can pick images
        checkAndRequestStoragePermission();

        setupImagePicker();
        setupListeners();

        // Load existing KYC data if available
        loadExistingKycData();

        // Update UI according to current verification status
        updateStatusFromSession();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        layoutIdFront = findViewById(R.id.layoutIdFront);
        layoutIdBack = findViewById(R.id.layoutIdBack);
        layoutLicenseFront = findViewById(R.id.layoutLicenseFront);
        layoutLicenseBack = findViewById(R.id.layoutLicenseBack);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvKycStatus = findViewById(R.id.tvKycStatus);
        etIdNumber = findViewById(R.id.etIdNumber);
        etLicenseNumber = findViewById(R.id.etLicenseNumber);

        // ImageViews for preview
        ivIdFront = findViewById(R.id.ivIdFront);
        ivIdBack = findViewById(R.id.ivIdBack);
        ivLicenseFront = findViewById(R.id.ivLicenseFront);
        ivLicenseBack = findViewById(R.id.ivLicenseBack);

        // Placeholders
        placeholderIdFront = findViewById(R.id.layoutIdFrontPlaceholder);
        placeholderIdBack = findViewById(R.id.layoutIdBackPlaceholder);
        placeholderLicenseFront = findViewById(R.id.layoutLicenseFrontPlaceholder);
        placeholderLicenseBack = findViewById(R.id.layoutLicenseBackPlaceholder);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "Image picker result code: " + result.getResultCode());
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    Log.d(TAG, "Selected image URI: " + selectedImageUri);

                    if (selectedImageUri == null) {
                        Toast.makeText(this, "Không thể lấy ảnh đã chọn", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    switch (currentImageType) {
                        case 1:
                            idFrontUri = selectedImageUri;
                            showImagePreview(ivIdFront, placeholderIdFront, selectedImageUri);
                            Toast.makeText(this, "Đã chọn ảnh CMND/CCCD mặt trước", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            idBackUri = selectedImageUri;
                            showImagePreview(ivIdBack, placeholderIdBack, selectedImageUri);
                            Toast.makeText(this, "Đã chọn ảnh CMND/CCCD mặt sau", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            licenseFrontUri = selectedImageUri;
                            showImagePreview(ivLicenseFront, placeholderLicenseFront, selectedImageUri);
                            Toast.makeText(this, "Đã chọn ảnh giấy phép lái xe mặt trước", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            licenseBackUri = selectedImageUri;
                            showImagePreview(ivLicenseBack, placeholderLicenseBack, selectedImageUri);
                            Toast.makeText(this, "Đã chọn ảnh giấy phép lái xe mặt sau", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    checkAllUploaded();
                }
            }
        );
    }

    private void showImagePreview(ImageView imageView, View placeholder, Uri imageUri) {
        // Set image URI and show image view
        imageView.setImageURI(imageUri);
        imageView.setVisibility(View.VISIBLE);
        placeholder.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Text watchers for EditTexts
        android.text.TextWatcher textWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllUploaded();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };

        etIdNumber.addTextChangedListener(textWatcher);
        etLicenseNumber.addTextChangedListener(textWatcher);

        // Upload listeners - open image picker
        layoutIdFront.setOnClickListener(v -> {
            Log.d(TAG, "ID Front clicked");
            currentImageType = 1;
            openImagePicker();
        });

        layoutIdBack.setOnClickListener(v -> {
            Log.d(TAG, "ID Back clicked");
            currentImageType = 2;
            openImagePicker();
        });

        layoutLicenseFront.setOnClickListener(v -> {
            Log.d(TAG, "License Front clicked");
            currentImageType = 3;
            openImagePicker();
        });

        layoutLicenseBack.setOnClickListener(v -> {
            Log.d(TAG, "License Back clicked");
            currentImageType = 4;
            openImagePicker();
        });

        btnSubmit.setOnClickListener(v -> {
            // Validate images
            if (idFrontUri == null || idBackUri == null || licenseFrontUri == null || licenseBackUri == null) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ảnh giấy tờ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate ID number
            String idNumber = etIdNumber.getText() != null ? etIdNumber.getText().toString().trim() : "";
            if (idNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số CMND/CCCD", Toast.LENGTH_SHORT).show();
                etIdNumber.requestFocus();
                return;
            }

            // Validate License number
            String licenseNumber = etLicenseNumber.getText() != null ? etLicenseNumber.getText().toString().trim() : "";
            if (licenseNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số giấy phép lái xe", Toast.LENGTH_SHORT).show();
                etLicenseNumber.requestFocus();
                return;
            }

            uploadKYCDocuments();
        });
    }

    private void openImagePicker() {
        Log.d(TAG, "Opening image picker for type: " + currentImageType);
        
        // Check permission before opening picker
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }

        Log.d(TAG, "Has permission: " + hasPermission);

        if (!hasPermission) {
            Toast.makeText(this, "Vui lòng cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
            checkAndRequestStoragePermission();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
            Log.d(TAG, "Image picker launched successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error launching image picker", e);
            Toast.makeText(this, "Lỗi mở trình chọn ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAllUploaded() {
        String idNumber = etIdNumber.getText() != null ? etIdNumber.getText().toString().trim() : "";
        String licenseNumber = etLicenseNumber.getText() != null ? etLicenseNumber.getText().toString().trim() : "";
        
        if (idFrontUri != null && idBackUri != null && licenseFrontUri != null && licenseBackUri != null 
            && !idNumber.isEmpty() && !licenseNumber.isEmpty()) {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.primary_green, null));
        } else {
            btnSubmit.setEnabled(false);
            btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.gray_300, null));
        }
    }

    private void uploadKYCDocuments() {
        // Show loading
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang chuẩn bị...");

        // Get token from SessionManager
        String token = sessionManager.getToken();
        Log.d(TAG, "Token: " + token);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Move heavy image processing to background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Convert URIs to MultipartBody.Part in background thread
                MultipartBody.Part idFrontPart = prepareFilePart("kyc.idFrontImage", idFrontUri);
                MultipartBody.Part idBackPart = prepareFilePart("kyc.idBackImage", idBackUri);
                MultipartBody.Part licenseFrontPart = prepareFilePart("kyc.licenseFrontImage", licenseFrontUri);
                MultipartBody.Part licenseBackPart = prepareFilePart("kyc.licenseBackImage", licenseBackUri);

                Log.d(TAG, "Files prepared: " + (idFrontPart != null) + ", " + (idBackPart != null) + ", " + (licenseFrontPart != null) + ", " + (licenseBackPart != null));

                // Return to main thread to check results and call API
                runOnUiThread(() -> {
                    if (idFrontPart == null || idBackPart == null || licenseFrontPart == null || licenseBackPart == null) {
                        Toast.makeText(this, "Lỗi khi chuẩn bị file, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Gửi xác minh");
                        return;
                    }

                    btnSubmit.setText("Đang tải lên...");

                    // Get current user from session
                    User currentUser = sessionManager.getUserDetails();
                    String userName = (currentUser != null && currentUser.getName() != null) ? currentUser.getName().trim() : "";

                    if (userName.isEmpty()) {
                        // Backend requires 'name' field — ask user to update profile
                        EditText input = new EditText(VerifyAccountActivity.this);
                        input.setHint("Họ và tên");
                        new AlertDialog.Builder(VerifyAccountActivity.this)
                                .setTitle("Tên bắt buộc")
                                .setMessage("Backend yêu cầu trường tên. Vui lòng nhập tên để tiếp tục gửi xác minh.")
                                .setView(input)
                                .setPositiveButton("Gửi", (dialog, which) -> {
                                    String entered = input.getText() != null ? input.getText().toString().trim() : "";
                                    if (entered.isEmpty()) {
                                        Toast.makeText(VerifyAccountActivity.this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                                        btnSubmit.setEnabled(true);
                                        btnSubmit.setText("Gửi xác minh");
                                        return;
                                    }
                                    submitWithName(entered, currentUser, token, idFrontPart, idBackPart, licenseFrontPart, licenseBackPart);
                                })
                                .setNegativeButton("Hủy", (dialog, which) -> {
                                    btnSubmit.setEnabled(true);
                                    btnSubmit.setText("Gửi xác minh");
                                    dialog.dismiss();
                                })
                                .setCancelable(false)
                                .show();
                        return;
                    }

                    // Create multipart text part for name
                    MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", null, RequestBody.create(MediaType.parse("text/plain"), userName));
                    RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), (currentUser != null && currentUser.getPhone() != null) ? currentUser.getPhone() : "");
                    RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), (currentUser != null && currentUser.getGender() != null) ? currentUser.getGender() : "");
                    
                    // Get ID and License numbers
                    String idNum = etIdNumber.getText() != null ? etIdNumber.getText().toString().trim() : "";
                    String licenseNum = etLicenseNumber.getText() != null ? etLicenseNumber.getText().toString().trim() : "";
                    RequestBody idNumber = RequestBody.create(MediaType.parse("text/plain"), idNum);
                    RequestBody licenseNumber = RequestBody.create(MediaType.parse("text/plain"), licenseNum);

                    // Call API
                    Call<ApiResponse<User>> call = userApiService.updateProfile(
                        "Bearer " + token,
                        namePart,
                        phone,
                        gender,
                        null,
                        licenseFrontPart,
                        licenseBackPart,
                        idFrontPart,
                        idBackPart,
                        licenseNumber,
                        idNumber
                    );

                    call.enqueue(new Callback<ApiResponse<User>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                            btnSubmit.setEnabled(true);
                            btnSubmit.setText("Gửi xác minh");

                            Log.d(TAG, "Response code: " + response.code());

                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<User> apiResponse = response.body();
                                Log.d(TAG, "Response success: " + apiResponse.isSuccess() + ", message: " + apiResponse.getMessage());
                                if (apiResponse.isSuccess()) {
                                    User updatedUser = apiResponse.getData();

                                    if (updatedUser != null) {
                                        sessionManager.createLoginSession(updatedUser, token);
                                        Log.d(TAG, "Updated user saved to session. KYC status: " + updatedUser.getVerificationStatus());
                                    }

                                    updateStatusFromSession();

                                    Toast.makeText(VerifyAccountActivity.this,
                                        "Đã gửi yêu cầu xác minh thành công!",
                                        Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e(TAG, "API returned success=false: " + apiResponse.getMessage());
                                    Toast.makeText(VerifyAccountActivity.this,
                                        "Lỗi: " + apiResponse.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String err = "Không thể kết nối server. Code: " + response.code();
                                try {
                                    if (response.errorBody() != null) {
                                        String errBody = response.errorBody().string();
                                        Log.e(TAG, "Error body: " + errBody);
                                        err += "\n" + errBody;
                                    }
                                } catch (Exception ex) {
                                    Log.w(TAG, "Error reading errorBody", ex);
                                }
                                Toast.makeText(VerifyAccountActivity.this, err, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                            btnSubmit.setEnabled(true);
                            btnSubmit.setText("Gửi xác minh");
                            Log.e(TAG, "Upload failure: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);

                            String errorMsg = "Lỗi kết nối: ";
                            if (t instanceof java.net.SocketTimeoutException) {
                                errorMsg += "Timeout - kiểm tra kết nối mạng hoặc thử ảnh nhỏ hơn";
                            } else if (t instanceof java.net.UnknownHostException) {
                                errorMsg += "Không thể kết nối server - kiểm tra internet";
                            } else {
                                errorMsg += t.getMessage();
                            }

                            Toast.makeText(VerifyAccountActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
                });

            } catch (Exception e) {
                Log.e(TAG, "Exception in background thread", e);
                runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Gửi xác minh");
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                executor.shutdown();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            if (fileUri == null) return null;

            // Create a temporary file
            File file = new File(getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");

            // Copy and COMPRESS content from Uri to file
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                Log.w(TAG, "Input stream is null for uri: " + fileUri);
                return null;
            }

            // Decode and compress bitmap to reduce file size
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                Log.w(TAG, "Failed to decode bitmap from uri: " + fileUri);
                return null;
            }

            // Compress to JPEG with quality 70% to reduce size significantly
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            outputStream.close();
            bitmap.recycle();

            Log.d(TAG, "Compressed file " + partName + ": " + file.length() + " bytes");

            // Create RequestBody
            RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/jpeg"),
                file
            );

            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

        } catch (Exception e) {
            Log.e(TAG, "prepareFilePart error for " + partName, e);
            return null;
        }
    }

    private void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            }
        } else {
            // Older versions: READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateStatusFromSession() {
        if (sessionManager == null) return;
        String kycStatus = sessionManager.getKycStatus();
        boolean verified = sessionManager.isVerified();

        String statusText = "Trạng thái: ";
        if (verified) {
            statusText += "✅ Đã xác minh";
            tvKycStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
            disableInputsForStatus();
        } else if (kycStatus != null && kycStatus.equalsIgnoreCase("pending")) {
            statusText += "⏳ Đang chờ xác minh";
            tvKycStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
            disableInputsForStatus();
        } else {
            statusText += "⚠️ Chưa gửi";
            tvKycStatus.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
            enableInputsForEditing();
        }

        tvKycStatus.setText(statusText);
        Log.d(TAG, "Status updated: verified=" + verified + ", kycStatus=" + kycStatus);
    }

    private void disableInputsForStatus() {
        Log.d(TAG, "Disabling inputs (verified/pending status)");
        // Disable clicking to change images, but keep them visible
        layoutIdFront.setClickable(false);
        layoutIdBack.setClickable(false);
        layoutLicenseFront.setClickable(false);
        layoutLicenseBack.setClickable(false);
        // Don't disable the layouts themselves - just make them not clickable
        // This allows images to still be displayed
        etIdNumber.setEnabled(false);
        etLicenseNumber.setEnabled(false);
        btnSubmit.setEnabled(false);
    }

    private void enableInputsForEditing() {
        Log.d(TAG, "Enabling inputs");
        layoutIdFront.setClickable(true);
        layoutIdBack.setClickable(true);
        layoutLicenseFront.setClickable(true);
        layoutLicenseBack.setClickable(true);
        layoutIdFront.setEnabled(true);
        layoutIdBack.setEnabled(true);
        layoutLicenseFront.setEnabled(true);
        layoutLicenseBack.setEnabled(true);
        etIdNumber.setEnabled(true);
        etLicenseNumber.setEnabled(true);
        // btnSubmit will be enabled only after all images selected
        checkAllUploaded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile from backend to get latest KYC status when user returns
        refreshProfileFromApi();
    }

    private void loadExistingKycData() {
        User user = sessionManager.getUserDetails();
        Log.d(TAG, "loadExistingKycData - User: " + (user != null ? "exists" : "null"));
        
        if (user == null) {
            Log.w(TAG, "No user in session");
            return;
        }
        
        if (user.getKyc() == null) {
            Log.w(TAG, "User has no KYC data");
            return;
        }

        User.Kyc kyc = user.getKyc();
        Log.d(TAG, "Loading existing KYC data - idNumber: " + kyc.getIdNumber() + 
                   ", licenseNumber: " + kyc.getLicenseNumber());

        // Load ID Number
        if (kyc.getIdNumber() != null && !kyc.getIdNumber().isEmpty()) {
            etIdNumber.setText(kyc.getIdNumber());
            Log.d(TAG, "Loaded ID Number: " + kyc.getIdNumber());
        }

        // Load License Number
        if (kyc.getLicenseNumber() != null && !kyc.getLicenseNumber().isEmpty()) {
            etLicenseNumber.setText(kyc.getLicenseNumber());
            Log.d(TAG, "Loaded License Number: " + kyc.getLicenseNumber());
        }

        // Load images using Glide or similar library
        if (kyc.getIdFrontImage() != null && kyc.getIdFrontImage().getUrl() != null) {
            loadImageFromUrl(kyc.getIdFrontImage().getUrl(), ivIdFront, placeholderIdFront);
            Log.d(TAG, "Loading ID Front image: " + kyc.getIdFrontImage().getUrl());
        }

        if (kyc.getIdBackImage() != null && kyc.getIdBackImage().getUrl() != null) {
            loadImageFromUrl(kyc.getIdBackImage().getUrl(), ivIdBack, placeholderIdBack);
            Log.d(TAG, "Loading ID Back image: " + kyc.getIdBackImage().getUrl());
        }

        if (kyc.getLicenseFrontImage() != null && kyc.getLicenseFrontImage().getUrl() != null) {
            loadImageFromUrl(kyc.getLicenseFrontImage().getUrl(), ivLicenseFront, placeholderLicenseFront);
            Log.d(TAG, "Loading License Front image: " + kyc.getLicenseFrontImage().getUrl());
        }

        if (kyc.getLicenseBackImage() != null && kyc.getLicenseBackImage().getUrl() != null) {
            loadImageFromUrl(kyc.getLicenseBackImage().getUrl(), ivLicenseBack, placeholderLicenseBack);
            Log.d(TAG, "Loading License Back image: " + kyc.getLicenseBackImage().getUrl());
        }
    }

    private void loadImageFromUrl(String imageUrl, ImageView imageView, View placeholder) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.w(TAG, "Image URL is null or empty");
            return;
        }

        Log.d(TAG, "Loading image from URL: " + imageUrl);
        
        // Hide placeholder and show ImageView first
        placeholder.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        
        // Use Glide to load image
        com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Glide FAILED to load image: " + imageUrl);
                        if (e != null) {
                            Log.e(TAG, "Glide error details: " + e.getMessage());
                            e.logRootCauses(TAG);
                        }
                        // Show placeholder again on error
                        runOnUiThread(() -> {
                            placeholder.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                        });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Glide SUCCESS - loaded image: " + imageUrl);
                        return false;
                    }
                })
                .into(imageView);
    }

    private void refreshProfileFromApi() {
        if (sessionManager == null || userApiService == null) return;
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) return;

        Call<ApiResponse<User>> call = userApiService.getUserProfile("Bearer " + token);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        // Persist the latest user (including kyc/isVerified)
                        sessionManager.createLoginSession(user, token);
                        // Reload KYC data
                        loadExistingKycData();
                        // Update UI
                        updateStatusFromSession();
                        Log.d(TAG, "Profile refreshed, kyc=" + user.getVerificationStatus());
                    }
                } else {
                    Log.w(TAG, "refreshProfileFromApi failed: code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.w(TAG, "refreshProfileFromApi onFailure", t);
            }
        });
    }

    // Helper to submit when name was entered by user in dialog
    private void submitWithName(String enteredName, User currentUser, String token,
                                MultipartBody.Part idFrontPart,
                                MultipartBody.Part idBackPart,
                                MultipartBody.Part licenseFrontPart,
                                MultipartBody.Part licenseBackPart) {
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang tải lên...");

        RequestBody nameRb = RequestBody.create(MediaType.parse("text/plain"), enteredName);
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), (currentUser != null && currentUser.getPhone() != null) ? currentUser.getPhone() : "");
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), (currentUser != null && currentUser.getGender() != null) ? currentUser.getGender() : "");
        MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", null, nameRb);
        
        // Get ID and License numbers
        String idNum = etIdNumber.getText() != null ? etIdNumber.getText().toString().trim() : "";
        String licenseNum = etLicenseNumber.getText() != null ? etLicenseNumber.getText().toString().trim() : "";
        RequestBody idNumber = RequestBody.create(MediaType.parse("text/plain"), idNum);
        RequestBody licenseNumber = RequestBody.create(MediaType.parse("text/plain"), licenseNum);

        Call<ApiResponse<User>> call = userApiService.updateProfile(
                "Bearer " + token,
                namePart,
                phone,
                gender,
                null,
                licenseFrontPart,
                licenseBackPart,
                idFrontPart,
                idBackPart,
                licenseNumber,
                idNumber
        );

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Gửi xác minh");

                Log.d(TAG, "submitWithName Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User updatedUser = response.body().getData();
                    if (updatedUser != null) {
                        sessionManager.createLoginSession(updatedUser, token);
                        updateStatusFromSession();
                        Log.d(TAG, "User updated with name. KYC: " + updatedUser.getVerificationStatus());
                    }
                    Toast.makeText(VerifyAccountActivity.this, "Đã gửi yêu cầu xác minh thành công!", Toast.LENGTH_LONG).show();
                } else {
                    String err = "Không thể gửi. Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errBody = response.errorBody().string();
                            Log.e(TAG, "submitWithName error body: " + errBody);
                            err += "\n" + errBody;
                        }
                    } catch (Exception ex) {
                        Log.w(TAG, "Error reading errorBody", ex);
                    }
                    Toast.makeText(VerifyAccountActivity.this, err, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Gửi xác minh");
                Log.e(TAG, "submitWithName failure: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);

                String errorMsg = "Lỗi: ";
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg += "Timeout - thử lại hoặc dùng ảnh nhỏ hơn";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMsg += "Không có kết nối internet";
                } else {
                    errorMsg += t.getMessage();
                }

                Toast.makeText(VerifyAccountActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
