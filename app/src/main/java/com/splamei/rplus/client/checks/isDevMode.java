package com.splamei.rplus.client.checks;

import static com.splamei.rplus.client.ui.settings.SettingsMenu.KEY_DEV_MODE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class isDevMode {
    private static boolean isDevMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_DEV_MODE, false);
    }
}
