package com.example.prm_project.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
            String name = binding.etFullName.getText().toString().trim();
            String birthDate = binding.etBirthDate.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            // Validate fields (simple)
            if (name.isEmpty() || birthDate.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            profileViewModel.setName(name);
            profileViewModel.setBirthDate(birthDate);
            profileViewModel.setPhone(phone);
            Toast.makeText(getContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
        });

        // Back button
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(EditProfileFragment.this).popBackStack());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
