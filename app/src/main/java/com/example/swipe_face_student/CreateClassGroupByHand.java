package com.example.swipe_face_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
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
import android.widget.Toast;

import com.example.swipe_face_student.Adapter.CreateGroupByHandAdapter;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Student;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class CreateClassGroupByHand extends AppCompatActivity {
    String classId;//課程DocId
    String class_Id;//課程Id
    String classYear;//課程學年度
    String className;//課程名
    String updateName;//新增組員學號
    Integer classStuNum;//課程學生人數
    Integer groupNum;//課程小組數
    Integer groupNumHigh;//課程小組人數上限
    Integer groupNumLow;//課程小組人數下限
    Button btNextStepButton;//下一步->確認組長
    FirebaseFirestore db;
    CreateGroupByHandAdapter createGroupByHandAdapter;
    RecyclerView studentListRecycleView;
    List<Student> studentList;//For Adapter
    LinearLayout groupStudentSetAddLl;//Dialog For Add Student
    private final String TAG = "CreateClassGroupByHand";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createclass_group_byhand);

        //init db
        db = FirebaseFirestore.getInstance();

        //init Intent
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
        assert bundle != null;
        class_Id = bundle.getString("class_Id");
        classId = bundle.getString("classId");
        classYear = bundle.getString("classYear");
        className = bundle.getString("className");
        classStuNum = bundle.getInt("classStuNum");
        groupNum = bundle.getInt("groupNum");
        groupNumHigh = bundle.getInt("groupNumHigh");
        groupNumLow = bundle.getInt("groupNumLow");
        Log.d(TAG,"class_Id"+class_Id+"classId"+classId+"classYear"+classYear+"className"+className+"classNum"+classStuNum);

        //init xml
        btNextStepButton = findViewById(R.id.nextStepButton);
        btNextStepButton.setOnClickListener(v -> nextStep());
        groupStudentSetAddLl = findViewById(R.id.groupStudentSetAdd);
        groupStudentSetAddLl.setOnClickListener(this::customClick);

        //init Adapter
        studentList = new ArrayList<>();
        createGroupByHandAdapter = new CreateGroupByHandAdapter(this, studentList);

        //init RecycleView
        studentListRecycleView = findViewById(R.id.groupDividerStudentList);
        studentListRecycleView.setHasFixedSize(true);
        LinearLayoutManager mgr = new LinearLayoutManager(this);
        studentListRecycleView.setLayoutManager(mgr);
        studentListRecycleView.setAdapter(createGroupByHandAdapter);

        createGroupByHandAdapter.setOnTransPageClickListener((student_id, student) -> {
            for(int i = studentList.size() - 1; i >= 0; i--){
                Student item = studentList.get(i);
                if(student_id.equals(item.getStudent_id())){
                    studentList.remove(item);
                }
            }
            createGroupByHandAdapter.notifyDataSetChanged();
        });

    }

    private void nextStep() {
        if(studentList.size()<groupNumLow||studentList.size()>groupNumHigh){
            Toast.makeText(CreateClassGroupByHand.this, "請確認人數是否正確", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.setClass(CreateClassGroupByHand.this,CreateGroupDecideLeader.class);
            bundle.putString("class_Id",class_Id);
            bundle.putString("classId",classId);
            bundle.putString("classYear",classYear);
            bundle.putString("className",className);
            bundle.putInt("groupNum",groupNum);
            bundle.putParcelableArrayList("studentList", (ArrayList<? extends Parcelable>) studentList);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void customClick(View v) {
        LayoutInflater lf = (LayoutInflater) CreateClassGroupByHand.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        ViewGroup vg = (ViewGroup) lf.inflate(R.layout.dialog_group_detail_setting, null);
        final EditText etShow = vg.findViewById(R.id.et_name);
        new AlertDialog.Builder(CreateClassGroupByHand.this)
                .setView(vg)
                .setPositiveButton("確定", (dialog, which) -> {
                    updateName = etShow.getText().toString();
                    Log.d(TAG,"updateName"+updateName);
                    if (updateName.isEmpty()) {
                        Toast.makeText(CreateClassGroupByHand.this, "請輸入學號", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG,"updateName\t112\t"+updateName);
                        DocumentReference docRef = db.collection("Class")
                                .document(classId);
                        docRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                assert document != null;
                                if (document.exists()) {
                                    Class aClass = document.toObject(Class.class);
                                    assert aClass != null;
                                    List<String> studentListStr = aClass.getStudent_id();
                                    //如果輸入學號不存在於課程裡
                                    if (!studentListStr.contains(updateName)) {
                                        Toast.makeText(CreateClassGroupByHand.this, "該學號不存在於課程裡",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        db.collection("Student").whereEqualTo("student_id", updateName)
                                                .addSnapshotListener((documentSnapshots, e) -> {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            }


                                            assert documentSnapshots != null;

                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                                    Student aStudent = doc.getDocument().toObject(Student.class).withId(updateName);
                                                    Log.d(TAG,aStudent.getStudent_id());
                                                    studentList.add(aStudent);
                                                    Log.d(TAG,"aStudent_id"+ studentList);

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
