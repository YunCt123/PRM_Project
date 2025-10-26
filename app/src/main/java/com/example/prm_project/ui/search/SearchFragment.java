package com.example.prm_project.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_project.databinding.FragmentSearchBinding;
import com.example.prm_project.ui.home.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private SearchVehicleAdapter vehicleAdapter;
    private final List<Vehicle> vehicleList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        try {
            viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
            binding = FragmentSearchBinding.inflate(inflater, container, false);
            if (binding == null) {
                Log.e(TAG, "Failed to inflate binding");
                return null;
            }
            View root = binding.getRoot();

            setupRecyclerView();
            setupSearchInput();
            setupChips();

            // Ensure 'All' is selected by default so UI shows unfiltered data
            if (binding.chipAll != null) {
                binding.chipAll.setChecked(true);
            }

            // Observe filtered vehicles from ViewModel
            viewModel.getFilteredVehicles().observe(getViewLifecycleOwner(), vehicles -> {
                try {
                    List<Vehicle> vehiclesToShow = vehicles != null ? vehicles : new ArrayList<>();
                    List<Vehicle> afterCategory = applyCategoryFilter(vehiclesToShow);
                    updateResults(afterCategory);
                } catch (Exception ex) {
                    Log.e(TAG, "Error applying filtered vehicles", ex);
                    showError("Error showing search results: " + ex.getMessage());
                }
            });

            return root;
        } catch (Exception ex) {
            Log.e(TAG, "Error in onCreateView", ex);
            showError("Failed to initialize search: " + ex.getMessage());
            return null;
        }
    }

    private void setupRecyclerView() {
        try {
            if (binding == null || binding.rvSearchResults == null) {
                Log.e(TAG, "RecyclerView binding is null");
                return;
            }
            vehicleAdapter = new SearchVehicleAdapter(vehicleList);
            binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvSearchResults.setAdapter(vehicleAdapter);
        } catch (Exception ex) {
            Log.e(TAG, "Error setting up RecyclerView", ex);
            showError("Failed to setup vehicle list: " + ex.getMessage());
        }
    }

    private void setupSearchInput() {
        // Search button
        if (binding != null && binding.btnSearchExec != null) {
            binding.btnSearchExec.setOnClickListener(v -> executeSearch());
        }

        // IME action "Search" on keyboard
        if (binding != null && binding.etSearchInput != null) {
            binding.etSearchInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    executeSearch();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupChips() {
        if (binding == null || binding.chipGroupFilters == null) return;

        binding.chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            try {
                // When chips change, re-apply category filter on latest ViewModel data
                List<Vehicle> current = viewModel.getFilteredVehicles().getValue();
                if (current == null) current = new ArrayList<>();
                List<Vehicle> afterCategory = applyCategoryFilter(current);
                updateResults(afterCategory);
            } catch (Exception ex) {
                Log.e(TAG, "Error in chip filter change", ex);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Filter error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void executeSearch() {
        if (binding == null) return;
        String q = binding.etSearchInput.getText() != null ? binding.etSearchInput.getText().toString().trim() : "";
        if (q.isEmpty()) {
            // if empty, just show full list
            viewModel.search("", null, null);
            if (isAdded()) Toast.makeText(requireContext(), "Showing all vehicles", Toast.LENGTH_SHORT).show();
        } else {
            // We reuse search(...) first parameter as query (name or location)
            viewModel.search(q, null, null);
        }
    }

    private List<Vehicle> applyCategoryFilter(List<Vehicle> input) {
        if (input == null) return new ArrayList<>();
        if (binding == null) return input;
        List<Integer> checked = binding.chipGroupFilters.getCheckedChipIds();
        if (checked == null || checked.isEmpty()) return input; // no filter

        // If 'All' is selected (chip_all), return unfiltered
        for (Integer id : checked) {
            if (id == binding.chipAll.getId()) {
                return input;
            }
        }

        // Build category set (we'll just match 'SUV', 'Sedan', 'Economy' against vehicle.details)
        List<String> categories = new ArrayList<>();
        for (Integer id : checked) {
            if (id == binding.chipSuv.getId()) categories.add("suv");
            else if (id == binding.chipSedan.getId()) categories.add("sedan");
            else if (id == binding.chipEconomy.getId()) categories.add("economy");
        }

        if (categories.isEmpty()) return input;

        List<Vehicle> out = new ArrayList<>();
        for (Vehicle v : input) {
            String details = v.getDetails() != null ? v.getDetails().toLowerCase() : "";
            for (String cat : categories) {
                if (details.contains(cat)) {
                    out.add(v);
                    break;
                }
            }
        }
        return out;
    }

    private void showError(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateResults(List<Vehicle> results) {
        try {
            if (vehicleList == null) return;
            vehicleList.clear();
            if (results != null) {
                vehicleList.addAll(results);
            }
            if (vehicleAdapter != null) {
                vehicleAdapter.notifyDataSetChanged();
            }
            if (binding != null && binding.tvEmpty != null) {
                binding.tvEmpty.setVisibility(vehicleList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error updating results", ex);
            showError("Failed to update results: " + ex.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
