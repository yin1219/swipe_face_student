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
import com.example.swipe_face_student.Model.Attendance;
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
    private Class aclass;
    private Performance performance;
    private Attendance attendance;
    private Bonus bonus;
    private List<Bonus> bonusList;
    private String class_id;
    private String student_id;
    private String performanceId;
    private int minus = 0;
    OnFragmentSelectedListener mCallback;//Fragment傳值

    private TextView text_performance_totalBonus;
    private RecyclerView bonus_list;

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

        bonusList = new ArrayList<>();
        bonusListAdapter = new BonusListAdapter(getActivity().getApplicationContext(), bonusList);

        bonus_list = (RecyclerView) getView().findViewById(R.id.bonus_list);
        text_performance_totalBonus = (TextView) getView().findViewById(R.id.text_performance_totalBonus);
        bonus_list.setHasFixedSize(true);
        bonus_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        bonus_list.setAdapter(bonusListAdapter);
        Log.d(TAG, "Flag1");
        db.collection("Performance")
                .whereEqualTo("class_id", class_id)
                .whereEqualTo("student_id", student_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Error :" + e.getMessage());
                        }
                        performance  = documentSnapshots.getDocuments().get(0).toObject(Performance.class);
                        performanceId = documentSnapshots.getDocuments().get(0).getId();
                        text_performance_totalBonus.setText(""+performance.getPerformance_totalBonus());
                        Log.d(TAG, "in" + performance.getPerformance_totalBonus());

                        db.collection("/Performance/"+performanceId+"/Bonus")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot
                                                                documentSnapshots, FirebaseFirestoreException e) {
                                        if (e != null) {

                                            Log.d(TAG, "Error :" + e.getMessage());
                                        }
                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                                bonus = doc.getDocument().toObject(Bonus.class);
                                                bonusList.add(bonus);

                                                bonusListAdapter.notifyDataSetChanged();

                                            }
                                        }

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


}
