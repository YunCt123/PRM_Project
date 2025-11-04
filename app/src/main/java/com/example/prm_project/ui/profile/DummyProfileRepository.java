package com.example.prm_project.ui.profile;

import com.example.prm_project.R;

/**
 * Tiny in-memory repository that returns a dummy Profile.
 * Replace with network code later.
 */
public class DummyProfileRepository {

    public static Profile getDummyProfile() {
        // Use drawables that exist in the project (fall back handled by fragment)
        int avatar = R.drawable.user; // ensure this drawable exists in your project
        int cover = R.drawable.xe1;   // may not exist during early development

        return new Profile(
                "Phuoc Nguyen",
                "phuoc@gmail.com",
                42,
                3,
                10,
                "156.8 km",
                avatar,
                cover,
                "01/01/2000", // birthDate
                "0901234567"   // phone
        );
    }
}
