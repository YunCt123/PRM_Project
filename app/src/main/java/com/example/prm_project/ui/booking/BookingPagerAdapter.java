package com.example.prm_project.ui.booking;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BookingPagerAdapter extends FragmentStateAdapter {

    public BookingPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return different fragments for each tab
        return BookingListFragment.newInstance(getBookingStatus(position));
    }

    @Override
    public int getItemCount() {
        return 3; // Three tabs: Active, Completed, Cancelled
    }

    private String getBookingStatus(int position) {
        switch (position) {
            case 0:
                return "ACTIVE";
            case 1:
                return "COMPLETED";
            case 2:
                return "CANCELLED";
            default:
                return "ACTIVE";
        }
    }
}
