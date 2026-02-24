package com.splamei.rplus.client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.DialogInterface;

import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.content.Intent;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.splamei.rplus.client.ui.error.BatteryWarn;
import com.splamei.rplus.client.ui.error.WebViewErrorHandler;
import com.splamei.rplus.client.ui.settings.SettingsMenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    // Main data
    public static String myVerCode = "1004";

    // Url and Webview data
    public static String urlToLoad = "https://rhythm-plus.com/"; // Full URL to load
    public static String mainUrl = "https://rhythm-plus.com/"; // Must start with URL to allow loading
    public static String gameUrl = "https://rhythm-plus.com/game/"; // Must start with URL to hide settings
    public static String v2Url = "https://v2.rhythm-plus.com/";
    public static String v2GameUrl = "https://v2.rhythm-plus.com/game/";
    public static String urlForNewTab = "auth.rhythm-plus.com"; // Must contain to open the second tab
    public static String urlForNewTabClosure = "auth.rhythm-plus.com/__/auth/handler?state="; // Must contain to close the second tab and return
    public static String webView1UserAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) RhythmPlus-SplameiClient/1004 Mobile Safari/537.36";
    public static String webView2UserAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.7632.76 Mobile Safari/537.36";
    public static String updateUrl = "https://www.veemo.uk/net/r-plus/mobile/ver";
    public static String noticesUrl = "https://www.veemo.uk/net/r-plus/mobile/notices";

    // String data
    public static String secondTabNormalCloseMessage = "Welcome to Rhythm Plus";
    public static String secondTabLoadToastMessage = "Please wait while the sign-in page loads";

    public boolean lockSso = false;



    WebView webView;
    WebView loginView;
    WebViewClient webViewClient;
    WebViewClient loginClient;

    CoordinatorLayout coordinatorLayout;

    LinearProgressIndicator progressIndicator;

    boolean hasShownAuth = false;
    boolean showingAuthPage = false;
    boolean pageLoaded = false;
    public static final String ERROR_CHANNEL_ID = "error_channel";
    public static final String MISC_CHANNEL_ID = "misc_channel";

    // Vibration stuff
    boolean enableVibrations = true;
    Vibrator vibrator;
    VibrationEffect annoyedVibrationEffect;
    VibrationEffect un_happyVibrationEffect;

    RequestQueue ExampleRequestQueue;

    @SuppressLint({"SetJavaScriptEnabled", "QueryPermissionsNeeded"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        BatteryWarn.checkBatteryLevel(this); // check on first start
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        android.util.Log.i("onCreate", "Client starting...");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        createChannel(this, MISC_CHANNEL_ID, "Misc", "Other notifications used by the client", NotificationManager.IMPORTANCE_DEFAULT);
        createChannel(this, ERROR_CHANNEL_ID, "Errors", "Notifications sent when errors occur", NotificationManager.IMPORTANCE_HIGH);

        try {
            Intent aboutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rhythm-plus.com"));
            Intent licenceIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/splamei/rhythm-plus-mobile-client/blob/master/LICENSE"));

            // Only create shortcut if there's an app to handle it
            if (aboutIntent.resolveActivity(getPackageManager()) != null) {
                ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(this, "more")
                        .setShortLabel("About")
                        .setLongLabel("About the client")
                        .setIcon(IconCompat.createWithResource(this, R.drawable.icon))
                        .setRank(0)
                        .setIntent(aboutIntent)
                        .build();
                ShortcutManagerCompat.pushDynamicShortcut(this, shortcut);
            }

            if (licenceIntent.resolveActivity(getPackageManager()) != null) {
                ShortcutInfoCompat licenceShortcut = new ShortcutInfoCompat.Builder(this, "licence")
                        .setShortLabel("Licence")
                        .setLongLabel("Client Licence")
                        .setIcon(IconCompat.createWithResource(this, R.drawable.icon))
                        .setRank(1)
                        .setIntent(licenceIntent)
                        .build();
                ShortcutManagerCompat.pushDynamicShortcut(this, licenceShortcut);
            }
        } catch(Resources.NotFoundException e) {
            android.util.Log.e("ShortcutError", "Failed to create shortcut: " + e);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        annoyedVibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
        un_happyVibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);

        ExampleRequestQueue = Volley.newRequestQueue(MainActivity.this);
        coordinatorLayout = findViewById(R.id.main);
        progressIndicator = findViewById(R.id.progressBar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1008);
        }

        int UI_OPTIONS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);

        android.util.Log.i("onCreate", "Starting WebView....");

        webView = findViewById(R.id.mainWeb);
        FrameLayout errorLayout = findViewById(R.id.errorLayout);
        webView.setWebViewClient(new WebViewErrorHandler(this, webView, errorLayout));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);


        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setDatabaseEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setInitialScale(1);
        webView.getSettings().setUserAgentString(webView1UserAgent);


        loginView = findViewById(R.id.loginWeb);
        loginView.setVisibility(View.GONE);
        loginView.getSettings().setJavaScriptEnabled(true);
        loginView.getSettings().setAllowContentAccess(true);
        loginView.getSettings().setUseWideViewPort(true);
        loginView.getSettings().setLoadsImagesAutomatically(true);


        loginView.getSettings().setLoadWithOverviewMode(true);
        loginView.getSettings().setDomStorageEnabled(true);
        loginView.setHorizontalScrollBarEnabled(false);
        loginView.getSettings().setDatabaseEnabled(false);
        loginView.getSettings().setBuiltInZoomControls(true);
        loginView.getSettings().setDisplayZoomControls(false);
        loginView.getSettings().setAllowFileAccess(false);
        loginView.setScrollbarFadingEnabled(false);
        loginView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        loginView.setInitialScale(1);
        loginView.getSettings().setUserAgentString(webView2UserAgent);

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        //new SettingsMenu();
        settingsButton.setOnClickListener(v -> SettingsMenu.showMenu(MainActivity.this));
        
        android.util.Log.i("onCreate", "WebView setting created. Now setting up the wait handler");

        Handler handler = new Handler();
        Runnable slowLoadRunnable = () -> {
            if (!pageLoaded){
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        "It's taking a while to load... Don't worry, we're still working hard", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };

        android.util.Log.i("onCreate", "Wait handler made, now making the web view clients");

        webViewClient = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                String url = request.getUrl().toString();
                if ((url.startsWith(mainUrl) && !url.contains(urlForNewTab)) || url.startsWith(urlToLoad))
                {
                    // load my page
                    return false;
                }
                else if (url.contains(urlForNewTab))
                {
                    if (lockSso)
                    {
                        showDialogBox(MainActivity.this, "You can't do that", "Signing in that way is disabled in this release of the client. Please sign in via email instead.\n\nIf you need to sign in this way, please use a release that supports signing in this way or convert your account to use email sign in\n\nFor more information, please email us or join our Discord.", "Ok", "", null, null);

                        triggerVibration(annoyedVibrationEffect);

                        return true;
                    }

                    hasShownAuth = false;
                    pageLoaded = false;
                    showingAuthPage = true;

                    webView.setVisibility(View.GONE);
                    loginView.setVisibility(View.VISIBLE);
                    settingsButton.setVisibility(View.INVISIBLE);
                    loginView.setWebViewClient(loginClient);

                    loginView.loadUrl(url);

                    loginView.clearHistory();

                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                            secondTabLoadToastMessage, Snackbar.LENGTH_LONG);
                    snackbar.show();

                    handler.postDelayed(slowLoadRunnable, 8000);

                    return true;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
                return true;
            }
        };

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                if (newProgress < 100)
                {
                    progressIndicator.setVisibility(View.VISIBLE);
                    progressIndicator.setProgress(newProgress);
                }
                else
                {
                    progressIndicator.setProgress(100);
                    progressIndicator.setVisibility(View.GONE);

                    String webViewUrl = webView.getUrl();

                    if (Objects.requireNonNull(webViewUrl).equals(mainUrl))
                    {
                        ImageView imageView = findViewById(R.id.splashImg);
                        imageView.setVisibility(View.INVISIBLE);

                        ImageView backImg = findViewById(R.id.backImg);
                        backImg.setVisibility(View.INVISIBLE);

                        webView.setVisibility(View.VISIBLE);

                        pageLoaded = true;
                        handler.removeCallbacks(slowLoadRunnable);
                    }

                    if (webViewUrl.startsWith(gameUrl) || showingAuthPage)
                    {
                        settingsButton.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        settingsButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        loginClient = new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                if (url.contains(urlForNewTabClosure))
                {
                    webView.setVisibility(View.VISIBLE);
                    loginView.setVisibility(View.GONE);
                    settingsButton.setVisibility(View.VISIBLE);

                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                            secondTabNormalCloseMessage, Snackbar.LENGTH_LONG);
                    snackbar.show();

                    triggerVibration(un_happyVibrationEffect);

                    loginView.loadUrl("about:blank");

                    showingAuthPage = false;
                }
            }
        };

        loginView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                if (newProgress < 100)
                {
                    progressIndicator.setVisibility(View.VISIBLE);
                    progressIndicator.setProgress(newProgress);
                }
                else
                {
                    progressIndicator.setProgress(100);
                    progressIndicator.setVisibility(View.GONE);

                    pageLoaded = true;
                    handler.removeCallbacks(slowLoadRunnable);
                }
            }
        });

        if (SettingsMenu.isInv2Mode(this))
        {
            mainUrl = v2Url;
            urlToLoad = v2Url;
            gameUrl = v2GameUrl;
        }

        webView.setWebViewClient(webViewClient);
        webView.loadUrl(urlToLoad);

        handler.postDelayed(slowLoadRunnable, 8000);

        android.util.Log.i("onCreate", "Client Started. Now checking for updates...");

        // Again, we don't need to specify a string here
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, updateUrl, response -> {
            if (fileExists(MainActivity.this, "checkCode.dat"))
            {
                if (!readFile(MainActivity.this, "checkCode.dat").strip().equals(response.strip()))
                {
                    saveToFile(MainActivity.this, "checkCode.dat", response.strip());
                    newUpdate(MainActivity.this, response.strip());
                }
            }
            else
            {
                saveToFile(MainActivity.this, "checkCode.dat", response);
                newUpdate(MainActivity.this, response);
            }
        }, e -> {
            android.util.Log.i("onCreate", "Failed to check for updates " + e);
            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                    "Something went wrong while checking for updates!", Snackbar.LENGTH_LONG);
            snackbar.show();

            triggerVibration(un_happyVibrationEffect);
        });

        ExampleRequestQueue.add(ExampleStringRequest);

        android.util.Log.i("onCreate", "Now checking for notices");

        // We don't need to specify a string here.
        StringRequest NoticesStringRequest = new StringRequest(Request.Method.GET, noticesUrl, response -> {
            try
            {
                String regex = ";";
                String[] splitNotices;

                splitNotices = response.split(regex);

                String seenNotices = readFile(MainActivity.this, "seenNotices.dat").strip();
                if (!seenNotices.contains(splitNotices[3]) && !splitNotices[0].equals("NONE"))
                {
                    saveToFile(MainActivity.this, "seenNotices.dat", splitNotices[3]);
                    showNewNotice(MainActivity.this, splitNotices[0], splitNotices[1], splitNotices[2]);
                }
            }
            catch (Exception e)
            {
                android.util.Log.i("onCreate", "Failed to decode notices - " + e);
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        "Something went wrong while checking for notices! (Decode error)", Snackbar.LENGTH_LONG);
                snackbar.show();

                triggerVibration(un_happyVibrationEffect);
            }
        }, e -> {
            android.util.Log.i("onCreate", "Failed to get current notices - " + e);
            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                    "Something went wrong while checking for notices!", Snackbar.LENGTH_LONG);
            snackbar.show();

            triggerVibration(un_happyVibrationEffect);
        });

        ExampleRequestQueue.add(NoticesStringRequest);
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if (keyCode == KeyEvent.KEYCODE_BACK)
            {
                if (webView.getVisibility() == View.VISIBLE)
                {
                    if (webView.canGoBack())
                    {
                        webView.goBack();
                    }
                    return true;
                }
                else if (loginView.getVisibility() == View.VISIBLE)
                {
                    if (loginView.canGoBack())
                    {
                        loginView.goBack();
                    }
                    else
                    {
                        webView.setVisibility(View.VISIBLE);
                        loginView.setVisibility(View.GONE);

                        showingAuthPage = false;

                        loginView.loadUrl("about:blank");
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void createChannel(Context context, final String ID, String title, String description, int importance)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null && notificationManager.getNotificationChannel(ID) == null)
        {
            NotificationChannel channel = new NotificationChannel(ID, title, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.MAGENTA);
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotification(Context context, final String ID, String title, String message, int importance, int id)
    {
        android.util.Log.i("sendNotification", "Sending notification - '" + title + "' - '" + message + "'");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(importance);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (notificationManagerCompat.areNotificationsEnabled())
        {
            notificationManagerCompat.notify(id, builder.build());
        }
    }

    public static boolean sendNotificationWithURL(Context context, final String ID, String title, String message, int importance, String url, int notificationID)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        android.util.Log.i("sendNotificationWithURL", "Sending notification - '" + title + "' - '" + message + "'");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(importance)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Set the pending intent for the notification

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        if (notificationManagerCompat.areNotificationsEnabled())
        {
            notificationManagerCompat.notify(notificationID, builder.build());

            return true;
        }

        return false;
    }

    public static void showDialogBox(Context context, String title, String text, String button1Text, String button2text, DialogInterface.OnClickListener button1Pressed, DialogInterface.OnClickListener button2Pressed)
    {
        if (button2text.isEmpty())
        {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton(button1Text, button1Pressed)
                    .show();
        }
        else
        {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton(button1Text, button1Pressed)
                    .setNegativeButton(button2text, button2Pressed)
                    .show();
        }
    }

    public static String readFile(Context context, String fileName)
    {
        File file = new File(context.getFilesDir(), fileName);
        StringBuilder text = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
        }
        catch (IOException e)
        {
            android.util.Log.e("readFile", "Failed to read file '" + fileName + "'! - " + e);
        }

        return text.toString();
    }

    public static void saveToFile(Context context, String fileName, String content)
    {
        try (FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE))
        {
            outputStream.write(content.getBytes());
        }
        catch (IOException e)
        {
            android.util.Log.e("saveToFile", "Failed to save file '" + fileName + "'! - " + e);
        }
    }

    public static boolean fileExists(Context context, String fileName)
    {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    public static void newUpdate(Context context, String response)
    {
        if (!myVerCode.contains(response))
        {
            android.util.Log.i("newUpdate", "New update to the client! Showing user");

            showDialogBox(
                    context,
                    "New update!",
                    "There is a new update to the client. It's recommended that you update for the latest fixes and changes as not updating can break the client!",
                    "Update",
                    "Later",
                    (dialog, which) -> {

                        Toast.makeText(
                                context,
                                "GitHub should now open via the app or website",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/splamei/rplus-mobile-client/releases")
                        );

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    },
                    null
            );
        }
    }

    public static void showNewNotice(Context context, String title, String text, String url)
    {
        if (url.contains("NONE"))
        {
            showDialogBox(context, title, text, "Ok", "", null, null);
        }
        else
        {
            showDialogBox(
                    context,
                    title,
                    text,
                    "Ok",
                    "More",
                    null,
                    ((dialog, which) -> {
                        Toast.makeText(context,
                                "Opening the link!",
                                Toast.LENGTH_SHORT
                                ).show();

                        context.startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        );
                    })
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check battery again when returning to the app
        BatteryWarn.checkBatteryLevel(this);
    }

    public void triggerVibration(VibrationEffect vibrationEffect)
    {
        if (!enableVibrations) { return; }

        try
        {
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect);
        } catch (Exception e)
        {
            android.util.Log.e("triggerVibration", "Failed to vibrate the device! Now disabling vibrations - " + e);
        }
    }
}
