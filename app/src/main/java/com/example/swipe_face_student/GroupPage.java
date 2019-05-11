package com.example.swipe_face_student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.swipe_face_student.Adapter.GroupPageAdapter;
import com.example.swipe_face_student.Model.Group;
import com.example.swipe_face_student.Model.Student;
import com.example.swipe_face_student.GroupNumberForCh;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupPage extends AppCompatActivity {

    final String TAG = "GroupPage";
    private TextView tvGroupNum;
    private TextView tvGroupLeader;
    private TextView tvGroupBonus;
    private FirebaseFirestore db;
    private RecyclerView groupPageRecyclerView;
    private GroupPageAdapter groupPageAdapter;
    String classId;//課程DocId
    String class_Id;//課程Id
    String classYear;//課程學年度
    String className;//課程名
    String userId;//現在使用者學號
    List<Student> studentList;//For Adapter
    List<String> studentListFromGroup = new ArrayList();
    ImageButton ibBackIBtn;
    String student_id ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_page);

        //init db
        db = FirebaseFirestore.getInstance();

        //init currentUser
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentFirebaseUser.getEmail();
        String[] currentUserIdToStringList = currentFirebaseUser.getEmail().split("@");
        student_id = currentUserIdToStringList[0];
        Log.d(TAG,"currentUserId: "+student_id);

        //init xml
        tvGroupBonus = findViewById(R.id.groupBonus);
        tvGroupLeader = findViewById(R.id.groupLeader);
        tvGroupNum = findViewById(R.id.groupNum);
        ibBackIBtn = findViewById(R.id.backIBtn);
        ibBackIBtn.setOnClickListener(v -> finish());


        //init Intent
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
//        userId = bundle.getString("userId");
        class_Id = bundle.getString("class_Id");
        classId = bundle.getString("classId");
        classYear = bundle.getString("classYear");
        className = bundle.getString("className");

        //init Adapter
        studentList = new ArrayList<>();
        groupPageAdapter = new GroupPageAdapter(this, studentList);

        //init RecycleView
        groupPageRecyclerView = findViewById(R.id.groupPageRecyclerView);
        groupPageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mgr = new LinearLayoutManager(this);
        groupPageRecyclerView.setLayoutManager(mgr);
        groupPageRecyclerView.setAdapter(groupPageAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(groupPageRecyclerView.getContext(),
                mgr.getOrientation());
        groupPageRecyclerView.addItemDecoration(dividerItemDecoration);



        //init Method
        GroupNumberForCh groupNumberForCh = new GroupNumberForCh();

        //抓現在user判斷在Group裡哪個Doc
        db.collection("Class")
                .document(classId)
                .collection("Group")
                .whereArrayContains("student_id", "405401217")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            Log.d(TAG, "groupNum\t" + group.getGroup_num() + "\ngroupLeader\t" + group.getGroup_leader() + "\ngroupBonus\t" + group.getGroup_bonus());
                            studentListFromGroup = group.getStudent_id();
                            tvGroupNum.setText(groupNumberForCh.transNum(group.getGroup_num()));
                            tvGroupBonus.setText("小組得分 : "+group.getGroup_bonus().toString()+"\t分");
                            db.collection("Student")
                                    .whereEqualTo("student_id",group.getGroup_leader())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                                for (QueryDocumentSnapshot document2 : task1.getResult()) {
                                                    Student student = document2.toObject(Student.class);
                                                    tvGroupLeader.setText("小組組長 : " +student.getStudent_name());
                                                }

                                    } );
                        }
                        setData();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


    }

    private void setData() {
        for ( int i = 0; i < studentListFromGroup.size(); i++) {
            Log.d(TAG, "studentListFromGroup\t" + studentListFromGroup.get(i));
            int finalI = i;
            db.collection("Student").whereEqualTo("student_id", studentListFromGroup.get(i))
                    .addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            Log.d(TAG, "Error :" + e.getMessage());
                        }
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Student aStudent = doc.getDocument().toObject(Student.class).withId(studentListFromGroup.get(finalI));
                                Log.d(TAG,"aStudent_id"+ aStudent.getStudent_id());
                                Log.d(TAG,"aStudent_name"+ aStudent.getStudent_name());
                                Log.d(TAG,"aStudent"+ aStudent.toString());
                                studentList.add(aStudent);

                                groupPageAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }
}
