package com.example.prm_project.activies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.prm_project.R;
import com.example.prm_project.models.User;
import com.example.prm_project.repository.AuthRepository;
import com.example.prm_project.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private CheckBox cbTerms;
    private MaterialButton btnSignUp;
    private TextView tvSignIn;
    
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();
        sessionManager = new SessionManager(this);
        initViews();
        setClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        cbTerms = findViewById(R.id.cb_terms);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvSignIn = findViewById(R.id.tv_sign_in);

        // Default gender selection to avoid sending null/empty value.
        rbMale.setChecked(true);
    }

    private void setClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnSignUp.setOnClickListener(v -> registerUser());

        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String fullName = getText(etFullName);
        String email = getText(etEmail);
        String phone = getText(etPhone);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        // Validation
        if (fullName.isEmpty()) {
            etFullName.setError("Enter full name");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Enter phone number");
            etPhone.requestFocus();
            return;
        }

        String selectedGender = getSelectedGender();
        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("Đăng ký...");
        
        // Gọi API register - role mặc định "renter"
        authRepository.register(fullName, email, password, phone, selectedGender, "renter", 
            new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User user, String token) {
                    dismissProgressDialog();
                    sessionManager.createLoginSession(user, token);
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                }

                @Override
                public void onError(String errorMessage) {
                    dismissProgressDialog();
                    String normalizedError = errorMessage != null ? errorMessage.toLowerCase() : "";
                    if (normalizedError.contains("email")) {
                        etEmail.setError(errorMessage);
                        etEmail.requestFocus();
                    } else if (normalizedError.contains("số điện thoại") || normalizedError.contains("phone")) {
                        etPhone.setError(errorMessage);
                        etPhone.requestFocus();
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private String getSelectedGender() {
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_male) {
            return "male";
        }
        if (checkedId == R.id.rb_female) {
            return "female";
        }
        return "";
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
