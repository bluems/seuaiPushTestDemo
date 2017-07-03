package kr.nazuna.seuaipushtestdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;//웹뷰s
    private WebSettings mWebSettings;//웹뷰 세팅파일
    private static final String url = "http://150.95.140.215:3300";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context myApp = this;

        if (!setTitleColor()) {

        }

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();


        mWebView = (WebView) findViewById(R.id.webview); //레이어 연결
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
                new AlertDialog.Builder(myApp)
                        .setTitle("AlertDialog")
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
                return true;
            }
        });
        mWebView.setWebViewClient(new WebViewClient()); // 링크 클릭시 새창 방지
        mWebView.addJavascriptInterface(new AndroidBridge(this), "betApp");
        mWebSettings = mWebView.getSettings(); // 세부 세팅 가져오기
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false); //줌
        mWebSettings.setBuiltInZoomControls(true); // 줌
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 캐시 사용 차단

        // HTML5 Local Storage Enable
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);

        File dir = getCacheDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        mWebSettings.setAppCachePath(dir.getPath());
        mWebSettings.setAppCacheEnabled(true);


        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebView.loadUrl(url);
    }

    private boolean setTitleColor() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(Color.parseColor("#F3962F"));
                }
            }
        } catch (Exception e) {
            Log.d("error", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class AndroidBridge {           // 웹뷰에서 호출하는 안드로이드 함수
        Context mContext;

        AndroidBridge(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void onTokenRefresh(String id) {
            Log.i("id", id);
            SharedPreferences prefs = getSharedPreferences("idData", 0);
            SharedPreferences.Editor editor = prefs.edit();
            String strId = id.toString();
            editor.putString("id", strId);
            editor.commit();

            String token = FirebaseInstanceId.getInstance().getToken();
            if (prefs.getString("id", null) != null)// 서버에 기기 고유 토큰 저장
                sendRegistrationToServer(prefs.getString("id", null), token);

        }

        private void sendRegistrationToServer(String id, String token) {
            //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("ID", id)
                    .add("Token", token)
                    .build();

            Request request = new Request.Builder()
                    .url("http://150.95.140.215:3300/push_token")
                    .post(body)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

