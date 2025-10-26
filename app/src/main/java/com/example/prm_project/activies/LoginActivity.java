package com.example.prm_project.activies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.prm_project.R;

public class LoginActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnSignIn, btnGoogle, btnFacebook;
    private TextView tvForgotPassword, tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        // TODO: Implement actual login logic
        Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();

        // Navigate to main activity after successful login
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
