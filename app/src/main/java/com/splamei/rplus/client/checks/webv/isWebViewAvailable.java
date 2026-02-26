package com.splamei.rplus.client.checks.webv;

import android.content.Context;
import android.webkit.WebView;

public class isWebViewAvailable {
    public static boolean isWebViewAvailable(Context context) {
        try {
            WebView webView = new WebView(context);
            webView.destroy();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
