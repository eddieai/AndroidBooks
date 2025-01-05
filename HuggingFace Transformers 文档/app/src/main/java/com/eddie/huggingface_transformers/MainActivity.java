package com.eddie.huggingface_transformers;

import android.os.Bundle;
import android.os.Build;
import android.app.AlertDialog;
import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ProgressBar;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;


public class MainActivity extends Activity {
    private String initURL = "https://huggingface.co/docs/transformers/v4.44.2/zh/index";
    private SharedPreferences sharedPreferences;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        CookieManager.getInstance().setAcceptCookie(true); // Enable Cookies

        webView = findViewById(R.id.web_view);

        webView.setWebViewClient(new WebViewClient(){
            // Force URL to be opened inside the WebView
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            // Set status bar background color to the top row color of WebView
            @Override
            public void onPageFinished(WebView view, String url){
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());

                // Extracting color from top-right pixel
                int pixel = bitmap.getPixel(bitmap.getWidth() - 2,1);
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                view.setDrawingCacheEnabled(false);
                int myNewColor = Color.rgb(redValue, greenValue, blueValue);

                // Set the status bar color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(myNewColor);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // Allow JavaScript Alert box
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                // This line will display the alert.
                new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();

                // Return true if you want to handle the alert with this function. Otherwise, return false and the alert will be handled by the browser 
                return true;
            }

            // Display Progress Bar while loading page
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false); //optional, this will display or hide the zoom control on the screen.
        webView.getSettings().setTextZoom(120); // where 150 is the percentage of font size
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        String userAgent = "Mozilla/5.0 (Linux; Android 13; Pixel 6a Build/TP1A.220624.021.A1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/114.0.5735.196 Mobile Safari/537.36";
        webView.getSettings().setUserAgentString(userAgent);

		// Force Dark Mode in WebView
		// if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
        //     WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        // }

        sharedPreferences = getSharedPreferences("Web_App", MODE_PRIVATE);
        String url = sharedPreferences.getString("lastUrl", initURL);
        webView.loadUrl(url);
    }

	// Keep last visted URL when pause or quit app
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastUrl", webView.getUrl());
        editor.apply();
    }

	// Press Back button will go to the past history of WebView
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            // If web view have back history, then go to the past history
            webView.goBack();
        } else {
            // If web view don't have back history, then exit from the app
            super.onBackPressed();
        }
    }
}