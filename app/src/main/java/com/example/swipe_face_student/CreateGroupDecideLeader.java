package com.example.swipe_face_student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.swipe_face_student.Adapter.CreateGroupDecideLeaderAdapter;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupDecideLeader extends AppCompatActivity {
    String classId;//課程DocId
    String groupLeader;//小組組長學號
    Integer groupNum;//課程小組數
    Integer group_bonus=0;//小組得分初始化
    List<Student> studentList = null;
    private final String TAG = "CreateGroupDecideLeader";
    FirebaseFirestore db;
    CardView cvFinishStepButton;
    CreateGroupDecideLeaderAdapter createGroupDecideLeaderAdapter;
    RecyclerView studentListRecycleView;
    List<String> student_id = new ArrayList();
    ImageButton backIBtn;
    OnFragmentSelectedListener mCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_decide_leader);

        //init db
        db = FirebaseFirestore.getInstance();

        //init bundle
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
        classId = bundle.getString("classId");
        studentList = bundle.getParcelableArrayList("studentList");
        for(int i = studentList.size() - 1; i >= 0; i--){
            Student item = studentList.get(i);
            Log.d(TAG,"\t63行"+item.getStudent_id());
        }


        //query DB
        if(!classId.isEmpty()) {
            DocumentReference docRef = db.collection("Class").document(classId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                Class aClass = documentSnapshot.toObject(Class.class);
                groupNum = aClass.getGroup_num();

            });
        }

        //init xml
        cvFinishStepButton = findViewById(R.id.finishButton);
        cvFinishStepButton.setOnClickListener(v -> finishStep());
        backIBtn = findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());

        //init Adapter
        createGroupDecideLeaderAdapter = new CreateGroupDecideLeaderAdapter(this, studentList);

        //init RecyclerView
        studentListRecycleView = findViewById(R.id.groupDividerStudentList);
        studentListRecycleView.setHasFixedSize(true);
        LinearLayoutManager mgr = new LinearLayoutManager(this);
        studentListRecycleView.setLayoutManager(mgr);
        studentListRecycleView.setAdapter(createGroupDecideLeaderAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(studentListRecycleView.getContext(),
                mgr.getOrientation());
        studentListRecycleView.addItemDecoration(dividerItemDecoration);


        //Send value from adapter to activity
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
              new IntentFilter("custom-message"));

    }

    private void finishStep() {

        //更新小組數目(+1)
        DocumentReference washingtonRef = db.collection("Class").document(classId);
        washingtonRef
                .update("group_num", ++groupNum)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

        //更新Class裡group_leader欄位
        Map<String, Object> groupUpdateLeader = new HashMap<>();
        groupUpdateLeader.put("group_leader", FieldValue.arrayUnion(groupLeader));
        db.collection("Class")
                .document(classId).update(groupUpdateLeader)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        Map<String, Object> group = new HashMap<>();
        for (int i = -0; i < studentList.size(); i++) {
            student_id.add(studentList.get(i).getStudent_id());
        }
        group.put("student_id", student_id);
        group.put("group_bonus",group_bonus);
        group.put("group_leader",groupLeader);
        group.put("group_num",groupNum);//前面已經加一這裡不用加
        db.collection("Class")
                .document(classId).collection("Group")
                .add(group)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        Intent intent = new Intent();
        intent.setClass(CreateGroupDecideLeader.this, CreateClassGroupSt1.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundleGroup = new Bundle();
        bundleGroup.putString("classId", classId);
        intent.putExtras(bundleGroup);
        startActivity(intent);


    }

    //Send value from adapter to activity
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String ItemName = intent.getStringExtra("item");
            groupLeader = intent.getStringExtra("groupLeader");
        }
    };
}

