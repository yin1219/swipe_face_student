package com.example.swipe_face_student;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Bonus;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Performance;
import com.example.swipe_face_student.Model.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QuestionResponse extends AppCompatActivity {
    private final String TAG = "QuestionResponse";
    private FirebaseFirestore db;
    private RadioButton rbQuestionA;
    private RadioButton rbQuestionB;
    private RadioButton rbQuestionC;
    private RadioButton rbQuestionD;
    private TextView tvCountDownTime;
    private TextView tvSubmit;
    private CardView cvNextStep;
    private int selection = -1;
    private String classId;
    private String Answer;
    private String class_Id;
    private String PerformanceId;
    private String questionAnswer;
    private Integer class_answerbonus;
    private Performance performance;
    private Bonus bonus;
    private Date create_time;
    private ImageButton backIBtn;
    private String student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_response);

        //init db
        db = FirebaseFirestore.getInstance();

        //init currentUser
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentFirebaseUser.getEmail();
        String[] currentUserIdToStringList = currentFirebaseUser.getEmail().split("@");
        student_id = currentUserIdToStringList[0];
        Log.d(TAG,"currentUserId: "+student_id);

        //init Intent Bundle
        Intent Intent = getIntent(); /* 取得傳入的 Intent 物件 */
        Bundle bundle = Intent.getExtras();
        classId = bundle.getString("classId");

        //init xml
        rbQuestionA = findViewById(R.id.question_a);
        rbQuestionB = findViewById(R.id.question_b);
        rbQuestionC = findViewById(R.id.question_c);
        rbQuestionD = findViewById(R.id.question_d);
        tvCountDownTime = findViewById(R.id.tvCountDown);
        cvNextStep = findViewById(R.id.nextStepButton);
        tvSubmit = findViewById(R.id.tvSubmit);
        backIBtn = findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());


        //query
        DocumentReference docRefClass = db.collection("Class")
                .document(classId);
        docRefClass.get().addOnSuccessListener(documentSnapshotClass -> {
            Class aClass = documentSnapshotClass.toObject(Class.class);
            class_Id = aClass.getClass_id();
            class_answerbonus = aClass.getClass_answerbonus();
        });

        DocumentReference docRefQuestion = db.collection("Class")
                .document(classId)
                .collection("Question")
                .document("question");
        docRefQuestion.get().addOnSuccessListener(documentSnapshotClass -> {
            Question question = documentSnapshotClass.toObject(Question.class);
            ArrayList<String> A = new ArrayList<>();
            ArrayList<String> B = new ArrayList<>();
            ArrayList<String> C = new ArrayList<>();
            ArrayList<String> D = new ArrayList<>();
            A = question.getA();
            B = question.getB();
            C = question.getC();
            D = question.getD();
            if (A.size()!=0) {
                rbQuestionA.setChecked(true);
            }
            if (B.size()!=0) {
                rbQuestionB.setChecked(true);
            }
            if (C.size()!=0) {
                rbQuestionC.setChecked(true);
            }
            if (D.size()!=0) {
                rbQuestionD.setChecked(true);
            }
            Toast.makeText(this, "已提交", Toast.LENGTH_LONG).show();
            cvNextStep.setEnabled(false);
            tvSubmit.setTextColor(Color.parseColor("#5f5ecd"));


        });

        if (classId != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference docRefGroup = db.collection("Class").document(classId)
                    .collection("Question").document("question");
            docRefGroup.get().addOnSuccessListener(documentSnapshot -> {
                Question question = documentSnapshot.toObject(Question.class);
                create_time = question.getCreate_time();
                questionAnswer = question.getAnswer();
                Date nowTime = new Date();
                Long utCreate_Time = create_time.getTime();
                Long utNowTime = nowTime.getTime();
                Long sec = (utCreate_Time - utNowTime) / 1000;//秒差
                Log.d(TAG, sec.toString());
                String countDownTime = sec.toString();
                Log.d(TAG, "countDownTime : " + countDownTime);
                new CountDownTimer(sec * 1000, 1000) {

                    @Override
                    public void onFinish() {
                        Intent intentToAnalysis = new Intent();
                        intentToAnalysis.setClass(QuestionResponse.this, QuestionAnalysis.class);
                        intentToAnalysis.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Bundle bundleToAnalysis = new Bundle();
                        bundleToAnalysis.putString("classId", classId);
                        bundleToAnalysis.putString("Answer", Answer);
                        bundleToAnalysis.putString("class_Id", class_Id);
                        bundleToAnalysis.putInt("class_answerbonus", class_answerbonus);
                        intentToAnalysis.putExtras(bundleToAnalysis);
                        startActivity(intentToAnalysis);
                        finish();
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvCountDownTime.setText(millisUntilFinished / 1000 / 60 + "\t\t分\t\t" + millisUntilFinished / 1000 % 60 + "\t\t秒");
                    }

                }.start();
            });


        }
    }

    public void onSelect(View view) {
        switch (view.getId()) {
            case R.id.question_a:
                selection = 1;
                Log.d(TAG, "selection : " + selection);
                break;
            case R.id.question_b:
                selection = 2;
                Log.d(TAG, "selection : " + selection);
                break;
            case R.id.question_c:
                selection = 3;
                Log.d(TAG, "selection : " + selection);
                break;
            case R.id.question_d:
                selection = 4;
                Log.d(TAG, "selection : " + selection);
                break;
            case R.id.nextStepButton:
                Log.d(TAG, "selection : " + selection);
                if (selection != -1) {
                    Answer = null;
                    switch (selection) {
                        case 1:
                            Answer = "A";
                            break;
                        case 2:
                            Answer = "B";
                            break;
                        case 3:
                            Answer = "C";
                            break;
                        case 4:
                            Answer = "D";
                            break;
                    }


                    Map<String, Object> classQuestionResponse = new HashMap<>();
                    classQuestionResponse.put(Answer, FieldValue.arrayUnion(student_id));//getuser

                    db.collection("Class")
                            .document(classId)
                            .collection("Question")
                            .document("question")
                            .update(classQuestionResponse);

                    Toast.makeText(this, "已提交", Toast.LENGTH_LONG).show();
                    cvNextStep.setEnabled(false);
                    tvSubmit.setTextColor(Color.parseColor("#5f5ecd"));
                    if (Answer != null) {
                        if (questionAnswer.equals(Answer)) {
                            setPoint();

                        }
                    }


                } else {
                    Toast.makeText(this, "請確認是否填寫", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void setPoint() {
        performance = new Performance();
        bonus = new Bonus();
        bonus.setBonus_reason("回答問題");
        bonus.setBonus_time(create_time);
        bonus.setClass_id(class_Id);
        bonus.setStudent_id(student_id);//currentUserId
        db.collection("Performance")
                .whereEqualTo("class_id", class_Id)
                .whereEqualTo("student_id", student_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String PerformanceId = document.getId();
                            Log.d(TAG, "PerformanceId:" + PerformanceId);

                            performance = document.toObject(Performance.class);
                            performance.setPerformance_totalBonus(performance.getPerformance_totalBonus() + class_answerbonus);
                            db.collection("Performance").document(PerformanceId).set(performance);
                            Log.d(TAG, "come here");

                            db.collection("Performance")
                                    .document(PerformanceId)
                                    .collection("Bonus")
                                    .add(bonus)
                                    .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
}
