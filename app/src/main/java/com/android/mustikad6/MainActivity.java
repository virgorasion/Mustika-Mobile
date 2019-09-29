package com.android.mustikad6;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.URL;

public class MainActivity extends AppCompatActivity{

    SwipeRefreshLayout swipeRefreshLayout;
    WebView webView;
    WebSettings webSettings;
    String URL = "https://mustikad6.000webhostapp.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        swipeRefreshLayout = findViewById(R.id.refresh);
        webView = findViewById(R.id.webView);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        DownloadManager.Request request = new DownloadManager.Request(
                                Uri.parse(url));
                        request.setMimeType(mimeType);
                        String cookies = CookieManager.getInstance().getCookie(url);
                        request.addRequestHeader("cookie", cookies);
                        request.addRequestHeader("User-Agent", userAgent);
                        request.setDescription("Downloading File...");
                        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                        url, contentDisposition, mimeType));
                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Izinkan Akses Download !", Toast.LENGTH_LONG).show();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permissions, 1);
                            }
                        }
                    }
                }
            }
        });

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
//        webSettings.getUseWideViewPort();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();

                if (null != swipeRefreshLayout){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

//        webView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                swipeRefreshLayout.setRefreshing(true);
//            }
//
//        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(URL);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else{
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}