package com.splamei.rplus.client.ui.settings;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.button.MaterialButton;
import com.splamei.rplus.client.MainActivity;

public class SettingsMenu {

    private static final String PREFS_NAME = "rplus_config";
    public static final String KEY_DEV_MODE = "development_mode";
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
        MaterialSwitch devSwitch = new MaterialSwitch(activity);
        devSwitch.setChecked(devModeEnabled);
        devSwitch.setText(devModeEnabled ? "Logs will be displayed" : "Logs will be hidden");

        // Update text when toggled
        devSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            devSwitch.setText(isChecked ? "Logs will be displayed" : "Logs will be hidden");
            prefs.edit().putBoolean(KEY_DEV_MODE, isChecked).apply();
        });

        layout.addView(label);
        layout.addView(devSwitch);



        // v2 Label
        TextView v2Label = new TextView(activity);
        v2Label.setText("V2 Mode");
        v2Label.setTextSize(18f);

        // v2 Switch
        MaterialSwitch v2Switch = new MaterialSwitch(activity);
        v2Switch.setChecked(v2ModeEnabled);
        v2Switch.setText("A restart will be required");

        // v2 Update text when toggled
        v2Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_V2_MODE, isChecked).apply();

            if (isChecked)
            {
                Toast.makeText(activity, "An access code from the Discord is needed to use v2!", Toast.LENGTH_LONG).show();
            }

            Toast.makeText(activity, "A restart is required for v2 mode to take affect", Toast.LENGTH_LONG).show();
        });

        layout.addView(v2Label);
        layout.addView(v2Switch);

        // Made with love + about
        TextView madeWithLove = new TextView(activity);
        madeWithLove.setText("Made with <3 by Splamei and contributors\n\n(c) 2024-2026 Splamei + Contributors. MIT Licence\n");
        madeWithLove.setTextSize(10f);

        layout.addView(madeWithLove);

        // More info button
        MaterialButton moreInfo = new MaterialButton(activity);
        moreInfo.setText("More info");
        moreInfo.setBackgroundColor(Color.DKGRAY);
        moreInfo.setTextColor(Color.WHITE);
        moreInfo.setTextSize(12f);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.veemo.uk/r-plus-splamei-client/"));
                    startActivity(activity, launchIntent, null);
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(activity, "No app was found to do this. Is a web browser installed?", Toast.LENGTH_LONG).show();
                    android.util.Log.e("openAboutUrl", "Unable to open about URL! (Not app found) - " + e);
                }
            }
        });

        layout.addView(moreInfo);


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

    public static boolean isInv2Mode(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_V2_MODE, false);
    }
}