package com.example.prm_project.ui.profile;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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

        // Toggle password visibility
        binding.ivTogglePassword.setOnClickListener(v -> {
            if (binding.etPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                // show
                binding.etPassword.setTransformationMethod(null);
                binding.ivTogglePassword.setImageResource(R.drawable.ic_visibility_24dp);
            } else {
                // hide
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.ivTogglePassword.setImageResource(R.drawable.ic_visibility_off_24dp);
            }
            // keep cursor at end
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        // Save changes back to the ViewModel (in-memory)
        binding.btnSave.setOnClickListener(v -> {
            String newName = binding.etFullName.getText() == null ? "" : binding.etFullName.getText().toString().trim();
            String newEmail = binding.etEmail.getText() == null ? "" : binding.etEmail.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            profileViewModel.setName(newName);
            profileViewModel.setEmail(newEmail);

            Toast.makeText(getContext(), getString(R.string.edit) + " " + getString(R.string.title_profile), Toast.LENGTH_SHORT).show();

            // Navigate back to ProfileFragment
            NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
        });

        // Back without saving
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(EditProfileFragment.this).popBackStack());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
