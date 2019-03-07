package com.example.swipe_face_student;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Adapter.AttendanceListAdapter;
import com.example.swipe_face_student.Model.Attendance;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Rollcall;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Fragment_AttendanceList extends Fragment {

    private FirebaseFirestore db;

    private AttendanceListAdapter attendanceListAdapter;

    private String TAG = "Attendance";
    private Class aclass;
    private Rollcall rollcall;
    private Attendance attendance;
    private List<Attendance> attendanceList;
    private String class_id;
    private String student_id;
    private int minus = 0;
    OnFragmentSelectedListener mCallback;//Fragment傳值

    private TextView text_class_totalpoints;
    private RecyclerView attendance_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = new Bundle();//fragment傳值
        args = getArguments();//fragment傳值
        class_id = args.getString("info");
        student_id = args.getString("student_id");
        Log.d(TAG, "class_id:" + class_id);//fragment傳值
        Toast.makeText(getContext(), "現在課程:" + class_id, Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        attendanceList = new ArrayList<>();
        attendanceListAdapter = new AttendanceListAdapter(getActivity().getApplicationContext(), attendanceList);

        attendance_list = (RecyclerView) getView().findViewById(R.id.attendance_list);
        text_class_totalpoints = (TextView) getView().findViewById(R.id.text_class_totalpoints);
        attendance_list.setHasFixedSize(true);
        attendance_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        attendance_list.setAdapter(attendanceListAdapter);
        Log.d(TAG, "Flag1");
        db.collection("Class").
                whereEqualTo("class_id", class_id).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Error :" + e.getMessage());
                        }
                        Class aClass = documentSnapshots.getDocuments().get(0).toObject(Class.class);

                        Log.d(TAG, "in" + class_id.toString());

                        db.collection("Rollcall").

                                whereEqualTo("class_id", class_id).

                                addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot
                                                                documentSnapshots, FirebaseFirestoreException e) {
                                        if (e != null) {

                                            Log.d(TAG, "Error :" + e.getMessage());
                                        }
                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                                rollcall = doc.getDocument().toObject(Rollcall.class);
                                                setAttendance(rollcall);
                                                attendanceList.add(attendance);
                                                attendanceListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        int totalPoints = aClass.getClass_totalpoints() - minus;
                                        Log.d(TAG,"point:" + totalPoints);
                                        text_class_totalpoints.setText(String.valueOf(totalPoints));
                                    }
                                });
                    }


                });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnFragmentSelectedListener) context;//fragment傳值
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Mush implement OnFragmentSelectedListener ");
        }
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
        }


    }


}
