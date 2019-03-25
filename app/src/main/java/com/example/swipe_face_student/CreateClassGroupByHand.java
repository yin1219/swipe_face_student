package com.example.swipe_face_student;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Adapter.CreateGroupByHandAdapter;
import com.example.swipe_face_student.Model.Group;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateClassGroupByHand extends AppCompatActivity {
    String classId, classYear, className, class_Id;
    TextView stCreateclasstime, tvClassName, tvClassNum;
    EditText etGroupNumLow, etGroupNumHigh;
    Button btNextStepButton;
    Integer classNum;
    FirebaseFirestore db;
    CreateGroupByHandAdapter createGroupByHandAdapter;
    RecyclerView studentListRecycleView;
    AttributeCheck attributeCheck = new AttributeCheck();
    List<Student> studentList;
    LinearLayout groupStudentSetAddLl;
    private final String TAG = "CreateClassGroupByHand";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createclass_group_byhand);

        //init Intent
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
        class_Id = bundle.getString("class_Id");
        classId = bundle.getString("classId");
        classYear = bundle.getString("classYear");
        className = bundle.getString("className");
        classNum = bundle.getInt("classStuNum");

        //init xml
        btNextStepButton = findViewById(R.id.nextStepButton);
        groupStudentSetAddLl = findViewById(R.id.groupStudentSetAdd);
        groupStudentSetAddLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customClick(v);
            }
        });

        //init Adapter
        studentList = new ArrayList<>();
        createGroupByHandAdapter = new CreateGroupByHandAdapter(this, studentList);

        //init RecycleView
        studentListRecycleView = findViewById(R.id.groupDividerStudentList);
        studentListRecycleView.setHasFixedSize(true);
        LinearLayoutManager mgr = new LinearLayoutManager(this);
        studentListRecycleView.setLayoutManager(mgr);
        studentListRecycleView.setAdapter(createGroupByHandAdapter);

    }


    public void customClick(View v) {
        LayoutInflater lf = (LayoutInflater) CreateClassGroupByHand.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup vg = (ViewGroup) lf.inflate(R.layout.dialog_group_detail_setting, null);
        final EditText etShow = vg.findViewById(R.id.et_name);
        new AlertDialog.Builder(CreateClassGroupByHand.this)
                .setView(vg)
                .setPositiveButton("確定", (dialog, which) -> {
                    String updateName = etShow.getText().toString();
                    if (updateName.isEmpty()) {
                        Toast.makeText(CreateClassGroupByHand.this, "請輸入學號", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        DocumentReference docRef = db.collection("Class").document(classId);
                        docRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Class aClass = document.toObject(Class.class);
                                    List<String> studentListStr = aClass.getStudent_id();
                                    if (!studentListStr.contains(updateName)) {//如果輸入學號不存在於課程裡
                                        Toast.makeText(CreateClassGroupByHand.this, "該學號不存在於課程裡", Toast.LENGTH_SHORT).show();
                                    } else {
                                        db.collection("Student").whereEqualTo("student_id", updateName).addSnapshotListener((documentSnapshots, e) -> {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            }

                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                                    Student aStudent = doc.getDocument().toObject(Student.class).withId(updateName);
                                                    studentList.add(aStudent);
                                                    createGroupByHandAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        });
                    }
                })
                .setNegativeButton("取消", null).show();

        createGroupByHandAdapter.notifyDataSetChanged();
    }
}
