package com.example.prm_project.activies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.ImageView;
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

    private static final String TAG = "RegisterActivity";
    private ImageView ivBack;
    private TextInputEditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
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

        // Initialize repository and session manager
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
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        cbTerms = findViewById(R.id.cb_terms);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvSignIn = findViewById(R.id.tv_sign_in);
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

        // Show progress dialog
        showProgressDialog("Đang đăng ký...");

        // Default values for gender and role
        String gender = "male"; // Default, có thể thêm RadioButton để user chọn
        String role = "renter"; // Default role theo API doc

        // Call API register
        authRepository.register(fullName, email, password, phone, gender, role, 
            new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User user, String token) {
                    dismissProgressDialog();
                    
                    // Save session
                    sessionManager.createLoginSession(user, token);
                    
                    Log.d(TAG, "Registration successful: " + user.getEmail());
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to main activity
                    navigateToMain();
                }

                @Override
                public void onError(String errorMessage) {
                    dismissProgressDialog();
                    
                    Log.e(TAG, "Registration failed: " + errorMessage);
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
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
