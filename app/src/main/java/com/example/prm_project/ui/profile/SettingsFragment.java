package com.example.prm_project.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.UserApiService;
import com.example.prm_project.databinding.FragmentSettingsBinding;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.ProfileSettingsUpdateRequest;
import com.example.prm_project.models.User;
import com.example.prm_project.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    private SessionManager sessionManager;
    private UserApiService userApiService;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize API and session
        sessionManager = new SessionManager(requireContext());
        userApiService = ApiClient.getClient().create(UserApiService.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Setup gender dropdown
        String[] genderOptions = new String[]{"male", "female", "other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, genderOptions);
        binding.actvGender.setAdapter(adapter);

        // Load current user data
        loadCurrentUserData();

        // Back button
        binding.btnBack.setOnClickListener(v ->
            NavHostFragment.findNavController(SettingsFragment.this).popBackStack()
        );

        // Save button
        binding.btnSave.setOnClickListener(v -> saveSettings());

        return root;
    }

    private void loadCurrentUserData() {
        Log.d(TAG, "Loading current user data...");

        // Get token
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "No token found");
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare Authorization header
        String bearerToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        // Call API to get latest user data
        Call<ApiResponse<User>> call = userApiService.getUserProfile(bearerToken);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();

                        Log.d(TAG, "Loaded user from API: " + user.getName());

                        // Update UI with API data
                        if (user.getName() != null) {
                            binding.etName.setText(user.getName());
                            Log.d(TAG, "Set name: " + user.getName());
                        }

                        if (user.getPhone() != null) {
                            binding.etPhone.setText(user.getPhone());
                            Log.d(TAG, "Set phone: " + user.getPhone());
                        }

                        if (user.getGender() != null) {
                            binding.actvGender.setText(user.getGender(), false);
                            Log.d(TAG, "Set gender: " + user.getGender());
                        }

                        // Update ViewModel and Session
                        profileViewModel.setName(user.getName());
                        profileViewModel.setPhone(user.getPhone());
                        sessionManager.createLoginSession(user, token);

                    } else {
                        Log.e(TAG, "API returned error: " + apiResponse.getMessage());
                        loadFromViewModelFallback();
                    }
                } else {
                    Log.e(TAG, "API call failed: " + response.code());
                    loadFromViewModelFallback();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                loadFromViewModelFallback();
            }
        });
    }

    private void loadFromViewModelFallback() {
        Log.d(TAG, "Loading from ViewModel as fallback...");

        // Load from ViewModel as fallback
        if (profileViewModel.getName().getValue() != null) {
            String name = profileViewModel.getName().getValue();
            binding.etName.setText(name);
            Log.d(TAG, "Loaded name from ViewModel: " + name);
        }
        if (profileViewModel.getPhone().getValue() != null) {
            String phone = profileViewModel.getPhone().getValue();
            binding.etPhone.setText(phone);
            Log.d(TAG, "Loaded phone from ViewModel: " + phone);
        }

        // Load gender from session
        User user = sessionManager.getUserDetails();
        if (user != null && user.getGender() != null) {
            binding.actvGender.setText(user.getGender(), false);
            Log.d(TAG, "Loaded gender from session: " + user.getGender());
        } else {
            Log.d(TAG, "No gender found in session");
        }
    }

    private void saveSettings() {
        String name = binding.etName.getText() != null ?
                binding.etName.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ?
                binding.etPhone.getText().toString().trim() : "";
        String gender = binding.actvGender.getText().toString().trim();

        // Validate fields
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gender.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
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
        ProfileSettingsUpdateRequest request = new ProfileSettingsUpdateRequest(name, phone, gender);

        // Show loading state
        binding.btnSave.setEnabled(false);
        binding.btnSave.setText("Đang lưu...");

        Log.d(TAG, "Updating settings: " + name + ", " + phone + ", " + gender);

        // Make API call
        Call<ApiResponse<User>> call = userApiService.updateProfileSettings(bearerToken, request);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                // Reset button state
                binding.btnSave.setEnabled(true);
                binding.btnSave.setText("LƯU THAY ĐỔI");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User updatedUser = apiResponse.getData();
                        Log.d(TAG, "Settings updated successfully");
                        Log.d(TAG, "Updated name: " + updatedUser.getName());
                        Log.d(TAG, "Updated phone: " + updatedUser.getPhone());
                        Log.d(TAG, "Updated gender: " + updatedUser.getGender());

                        // Update ViewModel
                        profileViewModel.setName(updatedUser.getName());
                        profileViewModel.setPhone(updatedUser.getPhone());

                        Log.d(TAG, "ViewModel updated - Name: " + profileViewModel.getName().getValue());
                        Log.d(TAG, "ViewModel updated - Phone: " + profileViewModel.getPhone().getValue());

                        // Update session
                        sessionManager.createLoginSession(updatedUser, token);

                        // Verify session was updated
                        User sessionUser = sessionManager.getUserDetails();
                        if (sessionUser != null) {
                            Log.d(TAG, "Session updated - Name: " + sessionUser.getName());
                            Log.d(TAG, "Session updated - Phone: " + sessionUser.getPhone());
                            Log.d(TAG, "Session updated - Gender: " + sessionUser.getGender());
                        }

                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(SettingsFragment.this).popBackStack();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "Cập nhật thất bại";
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
                binding.btnSave.setEnabled(true);
                binding.btnSave.setText("LƯU THAY ĐỔI");

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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "SettingsFragment onResume - reloading data");
        // Reload data from ViewModel when returning to this fragment
        loadCurrentUserData();
    }
}
