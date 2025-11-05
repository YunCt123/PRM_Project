package com.example.prm_project.activies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ImageView ivBack;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnSignIn, btnGoogle, btnFacebook;
    private TextView tvForgotPassword, tvSignUp;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize repository and session manager
        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnGoogle = findViewById(R.id.btn_google);
        btnFacebook = findViewById(R.id.btn_facebook);
        tvForgotPassword = findViewById(R.id.forgot);
        tvSignUp = findViewById(R.id.signup);
    }

    private void setClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnSignIn.setOnClickListener(v -> loginUser());

        btnGoogle.setOnClickListener(v ->
            Toast.makeText(LoginActivity.this, "Google sign-in (not implemented)", Toast.LENGTH_SHORT).show()
        );

        btnFacebook.setOnClickListener(v ->
            Toast.makeText(LoginActivity.this, "Facebook sign-in (not implemented)", Toast.LENGTH_SHORT).show()
        );

        tvForgotPassword.setOnClickListener(v ->
            Toast.makeText(LoginActivity.this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        );

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = getText(etEmail);
        String password = getText(etPassword);

        // Validation
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

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            etPassword.requestFocus();
            return;
        }

        // Show progress dialog
        showProgressDialog("Đang đăng nhập...");

        // Call API login
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user, String token) {
                dismissProgressDialog();
                
                // Save session
                sessionManager.createLoginSession(user, token);
                
                Log.d(TAG, "Login successful: " + user.getEmail());
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                
                // Navigate to main activity
                navigateToMain();
            }

            @Override
            public void onError(String errorMessage) {
                dismissProgressDialog();
                
                Log.e(TAG, "Login failed: " + errorMessage);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
