package com.example.swipe_face_student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.swipe_face_student.Adapter.AttendanceListAdapter;
import com.example.swipe_face_student.Model.Attendance;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Performance;
import com.example.swipe_face_student.Model.Rollcall;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttendaceList extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AttendanceListAdapter attendanceListAdapter;
    private Performance performance;
    private String TAG = "Attendance";
    private Class aclass;
    private Rollcall rollcall;
    private Attendance attendance;
    private List<Attendance> attendanceList;
    private String class_id;
    private String student_id;
    private int minus = 0;
    private TextView tvNoData;
    private TextView tvTotalPoints;
    private ImageButton backIBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendace_list);

        //init Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        class_id = bundle.getString("class_id");
        student_id = bundle.getString("student_id");

        //xml
        tvNoData = findViewById(R.id.tvNoData);
        tvTotalPoints = findViewById(R.id.text_class_totalpoints);
        backIBtn = findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());

        //init List<Object> For Adapter
        attendanceList = new ArrayList<>();

        //init Adapter
        attendanceListAdapter = new AttendanceListAdapter(this, attendanceList);

        //init RecyclerView
        RecyclerView recyclerViewAttendanceList = findViewById(R.id.attendance_list);
        recyclerViewAttendanceList.setHasFixedSize(true);
        recyclerViewAttendanceList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAttendanceList.setAdapter(attendanceListAdapter);

        if (class_id != null && student_id != null) {
            db.collection("Class").
                    whereEqualTo("class_id", class_id)
                    .addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            Log.d(TAG, "Error :" + e.getMessage());
                        }
                        Class aClass = documentSnapshots.getDocuments().get(0).toObject(Class.class);

                        Log.d(TAG, "in" + class_id.toString());

                        db.collection("Rollcall").
                                whereEqualTo("class_id", class_id)
                                .orderBy("rollcall_time", Query.Direction.DESCENDING)
                                .addSnapshotListener((documentSnapshots1, e1) -> {
                                    if (e1 != null) {
                                        Log.d(TAG, "Error :" + e1.getMessage());
                                    }
                                    for (DocumentChange doc : documentSnapshots1.getDocumentChanges()) {
                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                            rollcall = doc.getDocument().toObject(Rollcall.class);
                                            setAttendance(rollcall);
                                            attendanceList.add(attendance);
                                            attendanceListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    if (attendanceList.isEmpty()) {
                                        recyclerViewAttendanceList.setVisibility(View.GONE);
                                        tvNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoData.setVisibility(View.GONE);
                                    }
                                    int totalPoints = aClass.getClass_totalpoints();
                                    Log.d(TAG, "point:" + totalPoints);
                                    setPerformanceAttendancePoint(totalPoints);
                                });
                    });

        }


    }

    private void setPerformanceAttendancePoint(int totalPoints) {
        db.collection("Performance")
                .whereEqualTo("class_id", class_id)
                .whereEqualTo("student_id", student_id)
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (e != null) {
                        Log.d(TAG, "Error :" + e.getMessage());
                    }
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            performance = doc.getDocument().toObject(Performance.class);
                        }
                    }
                    int attendancePoint = performance.getPerformance_totalAttendance();
                    tvTotalPoints.setText(String.valueOf(attendancePoint) + "/" + String.valueOf(totalPoints));
                });
    }

    private void setAttendance(Rollcall rollcall) {
        attendance = new Attendance();
        attendance.setStudent_id(student_id);
        attendance.setAttendance_time(rollcall.getRollcall_time());

        if (rollcall.getRollcall_absence().contains(student_id)) {
            attendance.setAttendance_status("缺席");
            minus++;
        } else if (rollcall.getRollcall_attend().contains(student_id)) {
            attendance.setAttendance_status("出席");
        } else if (rollcall.getRollcall_casual().contains(student_id)) {
            attendance.setAttendance_status("事假");
        } else if (rollcall.getRollcall_funeral().contains(student_id)) {
            attendance.setAttendance_status("喪假");
        } else if (rollcall.getRollcall_offical().contains(student_id)) {
            attendance.setAttendance_status("公假");
        } else if (rollcall.getRollcall_sick().contains(student_id)) {
            attendance.setAttendance_status("病假");
        } else if (rollcall.getRollcall_late().contains(student_id)) {
            attendance.setAttendance_status("遲到");
        } else {
            attendance.setAttendance_status("Null");
        }


    }


}
