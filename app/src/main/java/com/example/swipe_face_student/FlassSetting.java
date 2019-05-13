package com.example.swipe_face_student;

import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FlassSetting {
    final String TAG = "FlassSetting";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    static String ip="140.136.155.123";

    void setIp(){
        DocumentReference docRef = db.collection("System").document("system");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    this.ip = document.getString("ip");
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }


    static String getIp() {
        return ip;
    }



}
