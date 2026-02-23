package com.splamei.rplus.client.ui.settings;

import android.annotation.SuppressLint;
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
    private static final String KEY_V2_MODE = "version_two_mode";

    @SuppressLint("SetTextI18n")
    public static void showMenu(Activity activity) {
        // Load current value
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean devModeEnabled = prefs.getBoolean(KEY_DEV_MODE, false);
        boolean v2ModeEnabled = prefs.getBoolean(KEY_V2_MODE, false);

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

        // v2 Label
        TextView v2Label = new TextView(activity);
        v2Label.setText("V2 Mode");
        v2Label.setTextSize(18f);

        // v2 Switch
        Switch v2Switch = new Switch(activity);
        v2Switch.setChecked(v2ModeEnabled);
        v2Switch.setText("Restart required");

        // v2 Update text when toggled
        v2Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            v2Switch.setText("Restart required");
            prefs.edit().putBoolean(KEY_V2_MODE, isChecked).apply();
        });

        layout.addView(label);
        layout.addView(devSwitch);

        layout.addView(v2Label);
        layout.addView(v2Switch);

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