package com.example.prm_project.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.prm_project.R;
import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.UserApiService;
import com.example.prm_project.databinding.FragmentEditProfileBinding;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.User;
import com.example.prm_project.models.UserUpdateRequest;
import com.example.prm_project.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfile";
    private FragmentEditProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private SessionManager sessionManager;
    private UserApiService userApiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize API and session
        sessionManager = new SessionManager(requireContext());
        userApiService = ApiClient.getClient().create(UserApiService.class);

        // Populate fields from ViewModel
        if (profileViewModel.getName().getValue() != null) {
            binding.etFullName.setText(profileViewModel.getName().getValue());
        }
        if (profileViewModel.getEmail().getValue() != null) {
            binding.etEmail.setText(profileViewModel.getEmail().getValue());
        }
        if (profileViewModel.getBirthDate().getValue() != null) {
            binding.etBirthDate.setText(profileViewModel.getBirthDate().getValue());
        }
        if (profileViewModel.getPhone().getValue() != null) {
            binding.etPhone.setText(profileViewModel.getPhone().getValue());
        }

        // Date picker for birth date
        binding.etBirthDate.setOnClickListener(v -> {
            // Show DatePickerDialog
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            new android.app.DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                binding.etBirthDate.setText(date);
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });

        // Save button
        binding.btnSaveProfile.setOnClickListener(v -> {
            saveProfileToServer();
        });

        // Back button
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(EditProfileFragment.this).popBackStack());

        return root;
    }

    private void saveProfileToServer() {
        String name = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String birthDate = binding.etBirthDate.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        // Validate fields
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert date format from dd/MM/yyyy to yyyy-MM-dd for API
        String apiDateFormat = null;
        if (!birthDate.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputFormat.parse(birthDate);
                if (date != null) {
                    apiDateFormat = outputFormat.format(date);
                }
            } catch (ParseException e) {
                Log.w(TAG, "Date parsing error", e);
                // Try using the date as-is if it's already in correct format
                apiDateFormat = birthDate;
            }
        }

        // Get token
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare Authorization header
        String bearerToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        // Create request object
        UserUpdateRequest request = new UserUpdateRequest(name, email, apiDateFormat, phone);

        // Show loading state
        binding.btnSaveProfile.setEnabled(false);
        binding.btnSaveProfile.setText("Đang lưu...");

        Log.d(TAG, "Updating profile: " + name + ", " + email + ", " + apiDateFormat + ", " + phone);

        // Make API call
        Call<ApiResponse<User>> call = userApiService.updateBasicProfile(bearerToken, request);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                // Reset button state
                binding.btnSaveProfile.setEnabled(true);
                binding.btnSaveProfile.setText("Lưu thay đổi");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User updatedUser = apiResponse.getData();
                        Log.d(TAG, "Profile updated successfully: " + updatedUser.toString());

                        // Update ViewModel
                        profileViewModel.setName(updatedUser.getName());
                        profileViewModel.setEmail(updatedUser.getEmail());
                        profileViewModel.setBirthDate(birthDate); // Keep display format
                        profileViewModel.setPhone(updatedUser.getPhone());

                        // Update session
                        sessionManager.createLoginSession(updatedUser, token);

                        Toast.makeText(getContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Cập nhật thất bại";
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                        Log.w(TAG, "API error: " + errorMsg);
                    }
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Server error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Reset button state
                binding.btnSaveProfile.setEnabled(true);
                binding.btnSaveProfile.setText("Lưu thay đổi");

                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Network error", t);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
