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

import com.example.swipe_face_student.Adapter.BonusListAdapter;
import com.example.swipe_face_student.Model.Bonus;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Performance;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClassPerformance extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
    private RecyclerView bonus_list;
    private String answerBouns;
    private String RDanswerBonus;
    private TextView text_class_year;
    private TextView text_class_name;
    private TextView text_performance_totalBonus;
    private TextView tvNoData;
    private ImageButton backIBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_performance);

        //init Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        class_id = bundle.getString("class_id");
        student_id = bundle.getString("student_id");


        //init xml
        tvNoData = findViewById(R.id.tvNoData);
        text_performance_totalBonus = findViewById(R.id.text_performance_totalBonus);
        text_class_year =findViewById(R.id.text_class_year);
        text_class_name = findViewById(R.id.text_class_name);
        backIBtn = findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());

        bonusList = new ArrayList<>();
        bonusListAdapter = new BonusListAdapter(this, bonusList);

        bonus_list = findViewById(R.id.bonus_list);
        bonus_list.setHasFixedSize(true);
        bonus_list.setLayoutManager(new LinearLayoutManager(this));
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
                if (bonusList.isEmpty()) {
                    bonus_list.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                } else {
                    tvNoData.setVisibility(View.GONE);
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
