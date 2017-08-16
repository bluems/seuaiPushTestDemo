package kr.happyline.LuckyPoint;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * Created by BSH on 2017-06-20.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    private static final String SETTINGS_NAME = "idData";
    private static  final  String url="http://209.126.67.94:8880";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        if (getSettingItem("id") != null)// 서버에 기기 고유 토큰 저장
            sendRegistrationToServer(getSettingItem("id"), token);
    }

    private void sendRegistrationToServer(String id, String token) {
        //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("ID", id)
                .add("Token", token)
                .build();

        Request request = new Request.Builder()
                .url(url+"/push_token")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSettingItem(String SETTINGS_ITEM) {
        return getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE).getString(SETTINGS_ITEM, null);
    }
}
