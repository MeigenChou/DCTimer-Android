package com.dctimer.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.util.Utils;
import com.dctimer.widget.CustomToolbar;

import static com.dctimer.APP.SCREEN_ORIENTATION;
import static com.dctimer.APP.screenOri;

public class WebActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progress;
    private String webUrl;
    private int uiMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    //5.0
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setStatusBarColor(0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   //4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        webUrl = intent.getStringExtra("web");
        String title = intent.getStringExtra("title");

        LinearLayout layout = findViewById(R.id.layout);
        layout.setBackgroundColor(APP.getBackgroundColor());
        int gray = Utils.grayScale(APP.getBackgroundColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //6.0
            if (gray > 200) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0
            if (gray > 200) {
                getWindow().setStatusBarColor(0x44000000);
            } else {
                getWindow().setStatusBarColor(0);
            }
        }

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        if (title != null) toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(APP.getBackgroundColor());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setItemColor(APP.getTextColor());
        if (Build.VERSION.SDK_INT >= 19)
            WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebClient());
        webView.setWebChromeClient(new ChromeClient());
        webView.setBackgroundColor(0);
        webView.getBackground().setAlpha(0);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        progress = findViewById(R.id.progress);

        //屏幕方向
        setRequestedOrientation(SCREEN_ORIENTATION[screenOri]);

        webView.loadUrl(webUrl);
        uiMode = getResources().getConfiguration().uiMode;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.w("dct", "configure change " + newConfig.uiMode);
        super.onConfigurationChanged(newConfig);
        if (newConfig.uiMode != uiMode) {
            uiMode = newConfig.uiMode;
            if ((uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                Log.w("dct", "深色模式");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
            } else {
                Log.w("dct", "浅色模式");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.web, menu);
        //mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                webView.reload();
                return true;
            case R.id.action_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                return true;
            case R.id.action_forward:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progress.setVisibility(View.GONE);
            } else {
                if (progress.getVisibility() == View.GONE)
                    progress.setVisibility(View.VISIBLE);
                progress.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class WebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.w("dct", "on page finish");
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.w("dct", "on receive ssl error");
            handler.proceed();
        }
    }
}
