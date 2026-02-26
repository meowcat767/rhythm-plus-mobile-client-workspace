package com.splamei.rplus.client.utils;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;

public class WebViewUtils {
    public void clearWebViewData(Context context) {
        WebView webView = new WebView(context);
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();

        WebStorage.getInstance().deleteAllData();
    }
}
