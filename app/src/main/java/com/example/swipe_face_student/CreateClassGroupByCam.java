package com.example.swipe_face_student;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.swipe_face_student.Adapter.CreateGroupByCamAdapter;
import com.example.swipe_face_student.Adapter.CreateGroupByHandAdapter;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Student;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateClassGroupByCam extends AppCompatActivity {
    final String TAG = "CreateClassGroupByCam";
    String classId;//課程DocId
    String class_Id;//課程Id
    String classYear;//課程學年度
    String className;//課程名
    String updateName;//新增組員學號
    Integer classStuNum;//課程學生人數
    Integer groupNum;//課程小組數
    Integer groupNumHigh;//課程小組人數上限
    Integer groupNumLow;//課程小組人數下限
    ArrayList<String> studentIdList = new ArrayList<>();//拍照Api回傳的學號List
    List<String> studentIdAfCheckList = new ArrayList<>();//拍照回傳與課程裡ID檢查後產物
    FirebaseFirestore db ;
    List<Student> studentList;//For Adapter
    RecyclerView studentListRecycleView;
    CreateGroupByCamAdapter createGroupByCamAdapter;
    LinearLayout groupStudentSetAddLl;//Dialog For Add Student
    CardView cvNextStepButton;//下一步->確認組長
    ImageButton backIBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_class_group_bycam);

        //init db
        db = FirebaseFirestore.getInstance();

        //init Intent
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
        classId = bundle.getString("classId");
        studentIdList = bundle.getStringArrayList("listStudentIdFromUpload");
        for(int i =0;i<studentIdList.size();i++){

            Log.d(TAG,"listStudentIdFromUpload"+studentIdList.get(i));
        }

        //query DB
        DocumentReference docRef = db.collection("Class").document(classId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            Class aClass = documentSnapshot.toObject(Class.class);
            class_Id = aClass.getClass_id();
            groupNum = aClass.getGroup_num();
            groupNumHigh = aClass.getGroup_numHigh();
            groupNumLow = aClass.getGroup_numLow();
            Log.d(TAG,aClass.toString());
            Log.d(TAG,"class_Id"+class_Id+"classId"+classId+"classYear"+classYear+"className"+className+"classNum"+classStuNum);

        });

        //init xml
        cvNextStepButton = findViewById(R.id.nextStepButton);
        cvNextStepButton.setOnClickListener(v -> nextStep());
        groupStudentSetAddLl = findViewById(R.id.groupStudentSetAdd);
        groupStudentSetAddLl.setOnClickListener(v -> customClick(v));
        backIBtn = findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());

        //init Adapter
        studentList = new ArrayList<>();
        createGroupByCamAdapter = new CreateGroupByCamAdapter(this, studentList);

        //init RecycleView
        studentListRecycleView = findViewById(R.id.groupDividerStudentList);
        studentListRecycleView.setHasFixedSize(true);
        LinearLayoutManager mgr = new LinearLayoutManager(this);
        studentListRecycleView.setLayoutManager(mgr);
        studentListRecycleView.setAdapter(createGroupByCamAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(studentListRecycleView.getContext(),
                mgr.getOrientation());
        studentListRecycleView.addItemDecoration(dividerItemDecoration);


        //set data for adapter
        setAll();

        createGroupByCamAdapter.setOnTransPageClickListener((student_id, student) -> {
            for(int i = studentList.size() - 1; i >= 0; i--){
                Student item = studentList.get(i);
                if(student_id.equals(item.getStudent_id())){
                    studentList.remove(item);
                }
            }
            createGroupByCamAdapter.notifyDataSetChanged();
        });
    }

    private void setAll(){
        DocumentReference docRef = db.collection("Class")
                .document(classId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Class aClass = document.toObject(Class.class);
                    List<String> studentListStr = aClass.getStudent_id();//檢查課程裡的學號
                    //查看拍照來的studentIdList的學號是否存在於課程中studentListStr
                    for(int i = 0 ; i<studentIdList.size();i++){
                            if(studentListStr.contains(studentIdList.get(i))){
                                studentIdAfCheckList.add(studentIdList.get(i));
                                int finalI = i;
                                db.collection("Student").whereEqualTo("student_id",studentIdList.get(i))
                                        .addSnapshotListener((documentSnapshots, e) -> {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            }
                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                                    Student aStudent = doc.getDocument().toObject(Student.class).withId(studentIdList.get(finalI));
                                                    Log.d(TAG,aStudent.getStudent_id());
                                                    studentList.add(aStudent);
                                                    Log.d(TAG,"aStudent_id"+ studentList);
                                                    createGroupByCamAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                    }


                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void nextStep() {
        if(studentList.size()<groupNumLow||studentList.size()>groupNumHigh){
            Toast.makeText(CreateClassGroupByCam.this, "請確認人數是否正確", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.setClass(CreateClassGroupByCam.this,CreateGroupDecideLeader.class);
            bundle.putString("classId",classId);
            bundle.putParcelableArrayList("studentList", (ArrayList<? extends Parcelable>) studentList);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void customClick(View v) {
        LayoutInflater lf = (LayoutInflater) CreateClassGroupByCam.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup vg = (ViewGroup) lf.inflate(R.layout.dialog_group_detail_setting, null);
        final EditText etShow = vg.findViewById(R.id.et_name);
        new AlertDialog.Builder(CreateClassGroupByCam.this)
                .setView(vg)
                .setPositiveButton("確定", (dialog, which) -> {
                    updateName = etShow.getText().toString();
                    Log.d(TAG,"updateName"+updateName);
                    if (updateName.isEmpty()) {
                        Toast.makeText(CreateClassGroupByCam.this, "請輸入學號", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Log.d(TAG,"updateName\t112\t"+updateName);
                        DocumentReference docRef = db.collection("Class")
                                .document(classId);
                        docRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Class aClass = document.toObject(Class.class);
                                    List<String> studentListStr = aClass.getStudent_id();
                                    //如果輸入學號不存在於課程裡
                                    if (!studentListStr.contains(updateName)) {
                                        Toast.makeText(CreateClassGroupByCam.this, "該學號不存在於課程裡",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        db.collection("Student").whereEqualTo("student_id", updateName)
                                                .addSnapshotListener((documentSnapshots, e) -> {
                                                    if (e != null) {
                                                        Log.d(TAG, "Error :" + e.getMessage());
                                                    }

                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            Student aStudent = doc.getDocument().toObject(Student.class).withId(updateName);
                                                            Log.d(TAG,aStudent.getStudent_id());
                                                            studentList.add(aStudent);
                                                            createGroupByCamAdapter.notifyDataSetChanged();
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

        createGroupByCamAdapter.notifyDataSetChanged();
    }

        }