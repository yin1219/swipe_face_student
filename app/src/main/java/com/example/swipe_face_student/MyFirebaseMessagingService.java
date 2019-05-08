package com.example.swipe_face_student;

import android.content.Context;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.Context.MODE_PRIVATE;

//FCM
public class MyFirebaseMessagingService  extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM", "onMessageReceived:"+remoteMessage.getFrom());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }



    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
