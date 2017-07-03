package kr.nazuna.seuaipushtestdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BSH on 2017-06-20.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    //private static final String AppTitle = "스짱의 푸시데모 ><"; // 포그라운드에서의 노티 타이틀
    private static int notifyNum = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String messageTitle, String messageBody) {
        /*String message="";
        String link="";
        try {
            JSONObject obj = new JSONObject(messageBody);
            link = obj.getString("link");
            message = obj.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Intent intent = new Intent(this, MainActivity.class);
        //Bundle bundle = new Bundle();
        //bundle.putString("url",link);
        //intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(R.raw.alarm);
        try {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setContentTitle(AppTitle)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    //.setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm))
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notifyNum, notificationBuilder.build());
            notifyNum += 1;
            Log.d("var", Integer.toString(notifyNum));
            if (notifyNum == 100) notifyNum = 0;
        } catch (Exception e) {
            Log.d("error", e.getMessage());
            Log.d("error", "노티에서 에러남.");
        }

    }
}
