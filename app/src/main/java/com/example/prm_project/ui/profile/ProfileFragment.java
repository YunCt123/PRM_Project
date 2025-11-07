package com.example.prm_project.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.prm_project.R;
import com.example.prm_project.activies.VerifyAccountActivity;
import com.example.prm_project.databinding.FragmentProfileBinding;
import com.example.prm_project.utils.SessionManager;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());

        Log.d(TAG, "ProfileFragment onCreateView");

        // Header: cập nhật tên, email
        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            Log.d(TAG, "Name changed in ViewModel: " + name);
            binding.tvName.setText(name);
        });
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            Log.d(TAG, "Email changed in ViewModel: " + email);
            binding.tvEmail.setText(email);
        });

        // Observe loading state
        profileViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                // Show loading indicator if you have one in layout
                // binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                // binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Observe error messages
        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Gán icon, tiêu đề, sự kiện cho từng mục menu
        setupMenuItems(root);

        // Sự kiện cho mục Cài Đặt (Chỉnh sửa hồ sơ)
        LinearLayout llSettings = root.findViewById(R.id.llSettings);
        if (llSettings != null) {
            llSettings.setOnClickListener(v -> {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.navigation_edit_profile);
            });
        }

        // Nút Đăng xuất
        Button btnLogout = root.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> Toast.makeText(getContext(), "Đăng xuất", Toast.LENGTH_SHORT).show());

        return root;
    }

    private void setupMenuItems(View root) {
        // Xác minh tài khoản
        LinearLayout llVerify = root.findViewById(R.id.llVerify);
        ((ImageView) llVerify.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_check_black_24dp);
        
        // Update verification status text
        TextView tvVerifyTitle = llVerify.findViewById(R.id.tvMenuTitle);
        updateVerificationStatus(tvVerifyTitle);
        
        llVerify.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VerifyAccountActivity.class);
            startActivity(intent);
        });

        // Lịch sử thuê xe
        LinearLayout llHistory = root.findViewById(R.id.llHistory);
        ((ImageView) llHistory.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_calendar_24dp);
        ((TextView) llHistory.findViewById(R.id.tvMenuTitle)).setText("Lịch sử thuê xe");
        llHistory.setOnClickListener(v -> NavHostFragment.findNavController(ProfileFragment.this)
                .navigate(R.id.navigation_booking_history));

        // Cài đặt
        LinearLayout llSettings = root.findViewById(R.id.llSettings);
        ((ImageView) llSettings.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_settings);
        ((TextView) llSettings.findViewById(R.id.tvMenuTitle)).setText("Cài đặt");
        llSettings.setOnClickListener(v -> NavHostFragment.findNavController(ProfileFragment.this)
                .navigate(R.id.navigation_settings));

        // Trợ giúp & Hỗ trợ
        LinearLayout llSupport = root.findViewById(R.id.llSupport);
        ((ImageView) llSupport.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_search_24dp);
        ((TextView) llSupport.findViewById(R.id.tvMenuTitle)).setText("Trợ giúp & Hỗ trợ");
        llSupport.setOnClickListener(v -> Toast.makeText(getContext(), "Trợ giúp & Hỗ trợ", Toast.LENGTH_SHORT).show());

        // Về chúng tôi
        LinearLayout llAbout = root.findViewById(R.id.llAbout);
        ((ImageView) llAbout.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_home_black_24dp);
        ((TextView) llAbout.findViewById(R.id.tvMenuTitle)).setText("Về chúng tôi");
        llAbout.setOnClickListener(v -> Toast.makeText(getContext(), "Về chúng tôi", Toast.LENGTH_SHORT).show());
    }

    /**
     * Update verification status display
     */
    private void updateVerificationStatus(TextView tvVerifyTitle) {
        if (sessionManager == null || tvVerifyTitle == null) return;
        
        boolean isVerified = sessionManager.isVerified();
        String kycStatus = sessionManager.getKycStatus();
        
        if (isVerified) {
            tvVerifyTitle.setText("Xác minh tài khoản ✅");
        } else if ("pending".equalsIgnoreCase(kycStatus)) {
            tvVerifyTitle.setText("Xác minh tài khoản ⏳");
        } else {
            tvVerifyTitle.setText("Xác minh tài khoản");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ProfileFragment onResume - reloading profile");
        // Reload profile data from API when fragment resumes
        if (profileViewModel != null) {
            profileViewModel.loadUserProfile();
        }
        
        // Update verification status when returning to fragment
        View root = getView();
        if (root != null) {
            LinearLayout llVerify = root.findViewById(R.id.llVerify);
            if (llVerify != null) {
                TextView tvVerifyTitle = llVerify.findViewById(R.id.tvMenuTitle);
                updateVerificationStatus(tvVerifyTitle);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}