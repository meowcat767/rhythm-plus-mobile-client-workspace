package com.splamei.rplus.client.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsMenu {

    private static final String PREFS_NAME = "rplus_config";
    private static final String KEY_DEV_MODE = "development_mode";

    public static void showMenu(Activity activity) {
        // Load current value
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean devModeEnabled = prefs.getBoolean(KEY_DEV_MODE, false);

        // Layout for the dialog
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Label
        TextView label = new TextView(activity);
        label.setText("Development Mode");
        label.setTextSize(18f);

        // Switch
        Switch devSwitch = new Switch(activity);
        devSwitch.setChecked(devModeEnabled);
        devSwitch.setText(devModeEnabled ? "Enabled" : "Disabled");

        // Update text when toggled
        devSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            devSwitch.setText(isChecked ? "Enabled" : "Disabled");
            prefs.edit().putBoolean(KEY_DEV_MODE, isChecked).apply();
        });

        layout.addView(label);
        layout.addView(devSwitch);

        // Show dialog
        new MaterialAlertDialogBuilder(activity)
                .setTitle("Settings")
                .setView(layout)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static boolean isDevMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DEV_MODE, false);
    }
}