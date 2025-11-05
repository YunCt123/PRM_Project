package com.example.prm_project.activies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.UserApiService;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.User;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAccountActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private FrameLayout layoutIdFront, layoutIdBack, layoutLicenseFront, layoutLicenseBack;
    private ImageView ivIdFront, ivIdBack, ivLicenseFront, ivLicenseBack;
    private View placeholderIdFront, placeholderIdBack, placeholderLicenseFront, placeholderLicenseBack;
    private MaterialButton btnSubmit;
    
    private Uri idFrontUri, idBackUri, licenseFrontUri, licenseBackUri;
    private int currentImageType = 0; // 1: ID Front, 2: ID Back, 3: License Front, 4: License Back

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private UserApiService userApiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        initViews();
        setupImagePicker();
        setupListeners();

        // Initialize API service
        userApiService = ApiClient.getClient().create(UserApiService.class);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        layoutIdFront = findViewById(R.id.layoutIdFront);
        layoutIdBack = findViewById(R.id.layoutIdBack);
        layoutLicenseFront = findViewById(R.id.layoutLicenseFront);
        layoutLicenseBack = findViewById(R.id.layoutLicenseBack);
        btnSubmit = findViewById(R.id.btnSubmit);

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
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();

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
        imageView.setImageURI(imageUri);
        imageView.setVisibility(View.VISIBLE);
        placeholder.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Upload listeners - open image picker
        layoutIdFront.setOnClickListener(v -> {
            currentImageType = 1;
            openImagePicker();
        });

        layoutIdBack.setOnClickListener(v -> {
            currentImageType = 2;
            openImagePicker();
        });

        layoutLicenseFront.setOnClickListener(v -> {
            currentImageType = 3;
            openImagePicker();
        });

        layoutLicenseBack.setOnClickListener(v -> {
            currentImageType = 4;
            openImagePicker();
        });

        btnSubmit.setOnClickListener(v -> {
            if (idFrontUri == null || idBackUri == null || licenseFrontUri == null || licenseBackUri == null) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ảnh giấy tờ", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadKYCDocuments();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void checkAllUploaded() {
        if (idFrontUri != null && idBackUri != null && licenseFrontUri != null && licenseBackUri != null) {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.primary_green, null));
        }
    }

    private void uploadKYCDocuments() {
        // Show loading
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang tải lên...");

        try {
            // Get token from SharedPreferences
            String token = sharedPreferences.getString("auth_token", "");
            if (token.isEmpty()) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Convert URIs to MultipartBody.Part
            MultipartBody.Part idFrontPart = prepareFilePart("kyc.idFrontImage", idFrontUri);
            MultipartBody.Part idBackPart = prepareFilePart("kyc.idBackImage", idBackUri);
            MultipartBody.Part licenseFrontPart = prepareFilePart("kyc.licenseFrontImage", licenseFrontUri);
            MultipartBody.Part licenseBackPart = prepareFilePart("kyc.licenseBackImage", licenseBackUri);

            // Create empty RequestBody for optional fields
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), "");

            // Call API
            Call<ApiResponse<User>> call = userApiService.updateProfile(
                "Bearer " + token,
                name,
                phone,
                gender,
                null, // avatar
                licenseFrontPart,
                licenseBackPart,
                idFrontPart,
                idBackPart
            );

            call.enqueue(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Gửi xác minh");

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<User> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            Toast.makeText(VerifyAccountActivity.this,
                                "Đã gửi yêu cầu xác minh thành công!",
                                Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(VerifyAccountActivity.this,
                                "Lỗi: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VerifyAccountActivity.this,
                            "Không thể kết nối server. Vui lòng thử lại!",
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Gửi xác minh");
                    Toast.makeText(VerifyAccountActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Gửi xác minh");
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            // Create a temporary file
            File file = new File(getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");

            // Copy content from Uri to file
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Create RequestBody
            RequestBody requestFile = RequestBody.create(
                MediaType.parse(getContentResolver().getType(fileUri)),
                file
            );

            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
