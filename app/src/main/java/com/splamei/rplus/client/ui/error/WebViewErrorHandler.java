package com.splamei.rplus.client.ui.error;

import android.content.Context;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.splamei.rplus.client.R;


public class WebViewErrorHandler extends WebViewClient {
    @SuppressWarnings("FieldCanBeLocal")
    private final Context context;
    private final WebView webView;
    private final FrameLayout errorLayout;

    public WebViewErrorHandler(Context context, WebView webView, FrameLayout errorLayout) {
        this.context = context;
        this.webView = webView;
        this.errorLayout = errorLayout;
    }
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, android.webkit.WebResourceError error) {
        super.onReceivedError(view, request, error);
        // only handle main page errors, ignore subresources
        if (request.isForMainFrame()) {
            showErrorPage();
        }
    }

    private void showErrorPage() {
        if (errorLayout != null) {
            webView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            TextView textView = errorLayout.findViewById(R.id.errorText);
            Button retryButton = errorLayout.findViewById(R.id.retryButton);
            textView.setText("Oops! Unable to load page.\nCheck your internet connection, and try again.");
            retryButton.setOnClickListener(v -> {
                errorLayout.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.reload();
            });
        } else {
            // fallback HTML
            String html = "<html><body><h1>Oops! Unable to load page.</h1><p>Check your internet connection, and try again.</p></body></html>";
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }

}
