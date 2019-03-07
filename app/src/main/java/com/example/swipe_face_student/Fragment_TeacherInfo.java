package com.example.swipe_face_student;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Fragment_TeacherInfo extends Fragment {

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

    OnFragmentSelectedListener mCallback;//Fragment傳值

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        Bundle args = new Bundle();//fragment傳值
        args = getArguments();//fragment傳值
        teacherEmail = args.getString("info");
        Log.d(TAG, "teacherEmail:" + teacherEmail);//fragment傳值
        Toast.makeText(getContext(), "現在老師:" + teacherEmail, Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_info, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        text_teacher_name = (TextView) view.findViewById(R.id.text_teacher_name);
        text_teacher_email = (TextView) view.findViewById(R.id.text_teacher_email);
        text_teacher_office = (TextView) view.findViewById(R.id.text_teacher_office);
        text_teacher_officetime = (TextView) view.findViewById(R.id.text_teacher_officetime);
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
