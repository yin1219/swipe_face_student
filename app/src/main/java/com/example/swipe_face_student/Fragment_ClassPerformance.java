package com.example.swipe_face_student;

import android.content.Context;
import android.net.Uri;
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
import com.example.swipe_face_student.Adapter.BonusListAdapter;
import com.example.swipe_face_student.Model.Bonus;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Performance;
import com.example.swipe_face_student.Model.Rollcall;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Fragment_ClassPerformance extends Fragment {
    private FirebaseFirestore db;

    private BonusListAdapter bonusListAdapter;

    private String TAG = "ClassPerferformance";
    private Class aClass;
    private Performance performance;
    private Bonus bonus;
    private List<Bonus> bonusList;
    private String class_id;
    private String student_id;
    private String performanceId;
    private int minus = 0;
    OnFragmentSelectedListener mCallback;//Fragment傳值

    private RecyclerView bonus_list;
    private String answerBouns;
    private String RDanswerBonus;
    private TextView text_class_year;
    private TextView text_class_name;
    private TextView text_performance_totalBonus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = new Bundle();//fragment傳值
        args = getArguments();//fragment傳值
        class_id = args.getString("info");
        student_id = args.getString("student_id");
        Log.d(TAG,args.toString());
        Log.d(TAG, "class_id:" + class_id);//fragment傳值
        Toast.makeText(getContext(), "現在課程:" + class_id, Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_performance, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        text_performance_totalBonus = (TextView) view.findViewById(R.id.text_performance_totalBonus);
        text_class_year =(TextView) view.findViewById(R.id.text_class_year);
        text_class_name = (TextView) view.findViewById(R.id.text_class_name);

        bonusList = new ArrayList<>();
        bonusListAdapter = new BonusListAdapter(view.getContext(), bonusList);

        bonus_list = (RecyclerView) view.findViewById(R.id.bonus_list);
        bonus_list.setHasFixedSize(true);
        bonus_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        bonus_list.setAdapter(bonusListAdapter);

        getPoints(class_id);
        setInfor(student_id, class_id ,aClass);

    }

    private void setInfor(String student_id, String class_id , Class aclass) {
        db.collection("Performance")
                .whereEqualTo("student_id", student_id)
                .whereEqualTo("class_id", class_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "error" + e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "here");
                        performanceId = doc.getDocument().getId();
                        Performance preformance = doc.getDocument().toObject(Performance.class);
                        text_performance_totalBonus.setText(Integer.toString(preformance.getPerformance_totalBonus()));

                        listBonus(student_id, class_id, performanceId);

                    }

                }
            }

        });
    }

    private void listBonus(String student_id, String class_id, String perforDocId){
        db.collection("Performance").document(perforDocId).collection("Bonus").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Log.d(TAG,"error" + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){
                        Bonus bonus = doc.getDocument().toObject(Bonus.class);

                        bonus.setAnswerBonus(answerBouns);
                        bonus.setRDanswerBonus(RDanswerBonus);

                        bonusList.add(bonus);

                        bonusListAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    private void getPoints( String class_id){
        db.collection("Class")
                .whereEqualTo("class_id", class_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "error" + e.getMessage());
                }
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "here");
                        Class aClass = doc.getDocument().toObject(Class.class);

                        text_class_year.setText(aClass.getClass_year());
                        text_class_name.setText(aClass.getClass_name());
                        answerBouns = Integer.toString(aClass.getClass_answerbonus());
                        RDanswerBonus = Integer.toString(aClass.getClass_rdanswerbonus());


                    }

                }
            }

        });
    }


}
