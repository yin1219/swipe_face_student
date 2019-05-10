package com.example.swipe_face_student;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.swipe_face_student.Model.Leave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveRecord extends AppCompatActivity {

    private static final String TAG = "Leavelog";

    private TextView class_name;
    private TextView leave_reason;
    private TextView leave_check;
    private TextView student_name;
    private TextView leave_date;
    private TextView leave_uploaddate;
    private TextView leave_content;
    private ImageView leave_photo;
    private ImageButton backIBtn;
    private CircleImageView imageViewStudent;

    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaverecord);

        class_name = (TextView) findViewById(R.id.textClassName);
        leave_reason = (TextView) findViewById(R.id.textLeaveReason);
        leave_check = (TextView) findViewById(R.id.textLeaveCheck);
        student_name = (TextView) findViewById(R.id.textStudentName);
        leave_date = (TextView) findViewById(R.id.textLeaveDate);
        leave_uploaddate = (TextView) findViewById(R.id.textLeaveUploadDate);
        leave_content = (TextView) findViewById(R.id.textLeaveContent);
        leave_photo = (ImageView) findViewById(R.id.imageViewLeavePhotot);
        backIBtn = (ImageButton) findViewById(R.id.backIBtn);
        imageViewStudent = findViewById(R.id.imageViewStudent);

        mFirestore = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Leave_photo");
        StorageReference storageReferenceS = FirebaseStorage.getInstance().getReference().child("student_photo");

        Bundle bundle = getIntent().getExtras();
        String leaveId = bundle.getString("id");
        ;
        Log.d(TAG, "Check leaveId : " + leaveId);


        DocumentReference docRef = mFirestore.collection("Leave").document(leaveId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Leave leave = document.toObject(Leave.class);

                        SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy/MM/dd");


                        String leaveUpdloadDate = myFmt2.format(leave.getLeave_uploaddate());

                        String photoUrlS = leave.getStudent_id();
                        StorageReference pathS = storageReferenceS.child(photoUrlS);
                        Log.d("TEST", pathS.toString());
                        Glide.with(LeaveRecord.this)
                                .load(pathS)
                                .into(imageViewStudent);

                        String checkColor = leave.getLeave_check();

                        class_name.setText(leave.getClass_name());
                        leave_reason.setText(leave.getLeave_reason());
                        leave_check.setText(leave.getLeave_check());

                        student_name.setText(leave.getStudent_name());
                        leave_date.setText(leave.getLeave_date());
                        leave_uploaddate.setText(leaveUpdloadDate);
                        leave_content.setText(leave.getLeave_content());
                        String photoUrl = leave.getLeave_photoUrl();


                        StorageReference path = storageReference.child(photoUrl);
                        Log.d("TEST", path.toString());
                        Glide.with(LeaveRecord.this)
                                .load(path)
                                .into(leave_photo);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
            }
        });

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "TEST BACK Item");
                finish();
            }
        });


    }



}