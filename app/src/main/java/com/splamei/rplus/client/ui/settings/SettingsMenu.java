package com.splamei.rplus.client.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.splamei.rplus.client.R;


public class SettingsMenu {
    private final Activity activity;
    private final ImageButton button;

    public SettingsMenu(Activity activity, ImageButton button) {
        this.activity = activity;
        this.button = button;
        setupButton();
    }
    private void setupButton() {
        button.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
        String[] options = {
                "Debug"
        };

        new MaterialAlertDialogBuilder(activity)
                .setTitle("Settings")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(activity, "Debug", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
    }

}
