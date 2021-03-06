package com.example.swipe_face_student;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Student;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class Fragment_LeaveList extends Fragment {
    private static final String TAG = "LeaveList001";

    private FirebaseFirestore db;
    private FloatingActionButton fab_leave;
    private boolean isAllClass = true;
    private List<String> classList;


    private String class_id = "null", student_id;


    OnFragmentSelectedListener mCallback;//Fragment傳值


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();//fragment傳值
        student_id = args.getString("student_id");

        db = FirebaseFirestore.getInstance();

        Log.d(TAG, "TEST arg : " + args.toString());
        Log.d(TAG, "class_id:" + class_id);//fragment傳值
        Log.d(TAG, "student_id:" + student_id);//fragment傳值


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment__leave_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getClassList(student_id);

        FragmentTabHost tabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        fab_leave = (FloatingActionButton) view.findViewById(R.id.fab_leave);
        tabHost.setup(getActivity(), getFragmentManager(), R.id.realtabcontent);


        //1

        Bundle arg1 = new Bundle();
        arg1.putString("PassStudent_id", student_id);
        arg1.putString("PassList_Way", "未審核");

        tabHost.addTab(tabHost.newTabSpec("Checking")
                        .setIndicator("未審核"),
                Fragment_LeaveList_View.class,
                arg1);
        Log.d(TAG, "TEST LOG RUN Times  " + arg1.toString());

        //2

        Bundle arg2 = new Bundle();
        arg2.putString("PassStudent_id", student_id);
        arg2.putString("PassList_Way", "准假");
        tabHost.addTab(tabHost.newTabSpec("Check0")
                        .setIndicator("准假"),
                Fragment_LeaveList_View.class,
                arg2);
        //3

        Bundle arg3 = new Bundle();
        arg3.putString("PassStudent_id", student_id);
        arg3.putString("PassList_Way", "不准假");

        tabHost.addTab(tabHost.newTabSpec("Check1")
                        .setIndicator("不准假"),
                Fragment_LeaveList_View.class,
                arg3);

        fab_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classList.size() == 0) {
                    Toast.makeText(getContext(), "尚未加入任何課程", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent();
                    Bundle formLeaveList = new Bundle();
                    formLeaveList.putString("class_id", class_id);
                    formLeaveList.putBoolean("isAllClass", true);
                    i.putExtras(formLeaveList);
                    i.setClass(getActivity().getApplicationContext(), LeaveApplications.class);
                    startActivity(i);
                }

            }
        });
    }

    public void getClassList(String student_id) {
        db.collection("Student")
                .whereEqualTo("student_id", student_id)
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (e != null) {
                        Log.d(TAG, "Error :" + e.getMessage());
                    }
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Student student = doc.getDocument().toObject(Student.class);
                            classList = student.getClass_id();
                        }
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

}
