package com.example.swipe_face_student;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.swipe_face_student.Model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TeacherInfo extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "TeacherInfo";

    private String teacherEmail;
    private String strTeacherOfficeTime = "";
    private ArrayList<String> teacherOfficeTime;

    private TextView text_teacher_name;
    private TextView text_teacher_email;
    private TextView text_teacher_office;
    private TextView text_teacher_officetime;
    private Teacher teacher;
    private ImageButton backIBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        //init Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        teacherEmail = bundle.getString("teacherEmail");

        text_teacher_name =findViewById(R.id.text_teacher_name);
        text_teacher_email = findViewById(R.id.text_teacher_email);
        text_teacher_office = findViewById(R.id.text_teacher_office);
        text_teacher_officetime = findViewById(R.id.text_teacher_officetime);
        backIBtn = findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());
        teacherOfficeTime = new ArrayList<>();

        db.collection("Teacher")
                .whereEqualTo("teacher_email", teacherEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                teacher = document.toObject(Teacher.class);
                                teacherOfficeTime = teacher.getTeacher_officetime();
                                int count = 1;
                                for (String str :teacherOfficeTime){
                                    int now =count % 3 ;
                                    switch (now){
                                        case 0 :
                                            strTeacherOfficeTime = strTeacherOfficeTime + str + "\n";
                                            count++;
                                            break;
                                        case 1:
                                            strTeacherOfficeTime = strTeacherOfficeTime + str + " ";
                                            count++;
                                            break;

                                        case 2:
                                            strTeacherOfficeTime = strTeacherOfficeTime + str + "-";
                                            count++;
                                            break;
                                    }
                                }
                                Log.d(TAG, "officeArray" + teacherOfficeTime.toString());
                                Log.d(TAG,"officeTime:" + strTeacherOfficeTime);

                                text_teacher_name.setText(teacher.getTeacher_name());
                                text_teacher_email.setText(teacher.getTeacher_email());
                                text_teacher_office.setText(teacher.getTeacher_office());
                                text_teacher_officetime.setText(strTeacherOfficeTime);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
