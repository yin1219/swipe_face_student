package com.example.swipe_face_student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class CreateClassGroupSt1 extends AppCompatActivity {
    String classId, class_Id, classYear, className;
    TextView tvClassName;
    Button btGroupDividerByhand, btGroupDividerByCam;
    Integer classNum;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createclass_group_st1);

        //init xml
        tvClassName = findViewById(R.id.textViewClassName);
        btGroupDividerByhand = findViewById(R.id.group_divider_byhand);
        btGroupDividerByCam = findViewById(R.id.group_divider_byCam);

        //init Intent
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
        class_Id = bundle.getString("class_Id");
        classId = bundle.getString("classId");
        classYear = bundle.getString("classYear");
        className = bundle.getString("className");
        classNum = bundle.getInt("classStuNum");
        tvClassName.setText(classYear + " " + className);
        btGroupDividerByhand.setOnClickListener(v -> {
            Intent intentCreateClassGroupByHand = new Intent();
            intentCreateClassGroupByHand.setClass(this, CreateClassGroupByHand.class);
            Bundle bundleGroup = new Bundle();
            bundleGroup.putString("class_Id", class_Id);
            bundleGroup.putString("classId", classId);
            bundleGroup.putString("classYear", classYear);
            bundleGroup.putString("className", className);
            bundleGroup.putInt("classNum", classNum);
            intentCreateClassGroupByHand.putExtras(bundleGroup);
            startActivity(intentCreateClassGroupByHand);
        });
        btGroupDividerByCam.setOnClickListener(v -> {
            Intent intentCreateClassGroupByCam = new Intent();
            intentCreateClassGroupByCam.setClass(this, CreateClassGroupByCam.class);
            Bundle bundleGroup = new Bundle();
            bundleGroup.putString("class_Id", class_Id);
            bundleGroup.putString("classId", classId);
            bundleGroup.putString("classYear", classYear);
            bundleGroup.putString("className", className);
            bundleGroup.putInt("classNum", classNum);
            intentCreateClassGroupByCam.putExtras(bundleGroup);
            startActivity(intentCreateClassGroupByCam);
        });
    }
}
