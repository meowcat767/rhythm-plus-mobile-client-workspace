package com.splamei.rplus.client.checks;

import static com.splamei.rplus.client.ui.settings.SettingsMenu.KEY_DEV_MODE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class isDevMode {
    private static boolean isDevMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_DEV_MODE, false);
    }

    public static void devToast(Context context, String message) {
        if (isDevMode(context)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Log.d("DEV_MODE", message);
        }
    }
}
