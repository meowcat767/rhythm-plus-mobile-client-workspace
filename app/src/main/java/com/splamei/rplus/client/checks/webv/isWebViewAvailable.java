package com.splamei.rplus.client.checks.webv;

import android.content.Context;
import android.webkit.WebView;
import com.splamei.rplus.client.checks.isDevMode;

public class isWebViewAvailable {
    public static boolean isWebViewAvailable(Context context) {
        try {
            WebView webView = new WebView(context);
            webView.destroy();
            isDevMode.devToast(context, "WebView is available!");
            return true;
        } catch (Exception e) {
            isDevMode.devToast(context, "WebView unavailable!");
            return false;
        }
    }
}
