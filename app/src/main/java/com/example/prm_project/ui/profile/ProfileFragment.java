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
import com.example.prm_project.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observe name and subtitle/email
        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> binding.tvName.setText(name));
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> binding.tvEmail.setText(email));

        // Set cover image (use existing drawable)
        binding.ivCover.setImageResource(R.drawable.xe1);

        // When avatar is clicked, navigate to Edit Profile
        binding.ivAvatar.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.navigation_edit_profile);
        });

        // Menu click handlers (temporary: show a toast)
        binding.llAddress.setOnClickListener(v ->
                Toast.makeText(getContext(), getString(R.string.my_address), Toast.LENGTH_SHORT).show());

        binding.llAccount.setOnClickListener(v ->
                Toast.makeText(getContext(), getString(R.string.account), Toast.LENGTH_SHORT).show());

        binding.llNotifications.setOnClickListener(v ->
                Toast.makeText(getContext(), getString(R.string.notifications), Toast.LENGTH_SHORT).show());

        binding.llDevices.setOnClickListener(v ->
                Toast.makeText(getContext(), getString(R.string.devices), Toast.LENGTH_SHORT).show());

        binding.llPasswords.setOnClickListener(v ->
                Toast.makeText(getContext(), getString(R.string.passwords), Toast.LENGTH_SHORT).show());

        binding.llLanguage.setOnClickListener(v ->
                Toast.makeText(getContext(), getString(R.string.language), Toast.LENGTH_SHORT).show());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}