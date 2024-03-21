package com.example.webview;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class TiledeskActivity extends AppCompatActivity {


    public static final int REQUEST_SELECT_FILE = 100;
    public ValueCallback<Uri[]> uploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiledesk);

        WebView myWebView = (WebView) findViewById(R.id.tiledesk);


        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        myWebView.setWebChromeClient(new FileChooserWebChromeClient(this) {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("TiledeskActivity", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });

//        allow to snap photos
        webSettings.setAllowFileAccess(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        myWebView.loadUrl("https://widget.tiledesk.com/v6/assets/twp/index.html?tiledesk_projectid=<CHANGE_IT>&tiledesk_fullscreenMode=true&tiledesk_hideHeaderCloseButton=true&tiledesk_open=true");

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            uploadMessage = null;
        }
    }


    public class FileChooserWebChromeClient extends WebChromeClient {

        private Activity myActivity;

        public FileChooserWebChromeClient(TiledeskActivity myActivity) {
            this.myActivity = myActivity;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                myActivity.startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(myActivity, "Cannot open file chooser", Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }
    }

                     }
