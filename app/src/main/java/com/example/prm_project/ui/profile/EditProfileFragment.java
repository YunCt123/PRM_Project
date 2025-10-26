package com.example.prm_project.ui.profile;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.prm_project.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private boolean passwordVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Back button
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(EditProfileFragment.this).navigateUp());

        // Save button (temporary behavior)
        binding.btnSave.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Profile saved", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(EditProfileFragment.this).navigateUp();
        });

        // Toggle password visibility
        binding.ivTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.ivTogglePassword.setImageResource(com.example.prm_project.R.drawable.ic_visibility_24dp);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.ivTogglePassword.setImageResource(com.example.prm_project.R.drawable.ic_visibility_off_24dp);
            }
            // Move cursor to end
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        // Avatar click
        binding.ivAvatarEdit.setOnClickListener(v -> Toast.makeText(getContext(), "Change avatar", Toast.LENGTH_SHORT).show());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

