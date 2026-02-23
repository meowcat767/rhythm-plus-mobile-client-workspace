package com.splamei.rplus.client.ui.error;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BatteryWarn {

    public static void checkBatteryLevel(Context context) {
        // Get battery info
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter); // ✅ use context

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;

            if (batteryPct <= 20) {
                // Toast warning
                Toast.makeText(context, "Battery low! Please charge your device.", Toast.LENGTH_LONG).show();

                // Dialog warning
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Low Battery")
                        .setMessage("Your battery is below 20%. It's recommended to charge your device.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }
}