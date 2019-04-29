package com.example.swipe_face_student;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Bonus;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Performance;
import com.example.swipe_face_student.Model.Question;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class QuestionAnalysis extends AppCompatActivity {
    private final String TAG = "QuestionAnalysis";
    private PieChart mChart;
    private ArrayList<String> A = new ArrayList<>();
    private ArrayList<String> B = new ArrayList<>();
    private ArrayList<String> C = new ArrayList<>();
    private ArrayList<String> D = new ArrayList<>();
    private String classId;
    private String currentUserId;
    private FirebaseFirestore db;
    private TextView tvChoiceA;
    private TextView tvChoiceB;
    private TextView tvChoiceC;
    private TextView tvChoiceD;
    private TextView tvCorrectAnswer;
    private ImageButton backIBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_analysis);

        //init db
        db = FirebaseFirestore.getInstance();
//        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
//        currentFirebaseUser.getEmail();
//        String[] currentUserIdToStringList = currentFirebaseUser.getEmail().split("@");
//        currentUserId = currentUserIdToStringList[0];

        //init Intent Bundle
        Intent Intent = getIntent(); /* 取得傳入的 Intent 物件 */
        Bundle bundle = Intent.getExtras();
        classId = bundle.getString("classId");

        //init xml
        mChart = findViewById(R.id.chart);
        tvChoiceA = findViewById(R.id.textA);
        tvChoiceB = findViewById(R.id.textB);
        tvChoiceC = findViewById(R.id.textC);
        tvChoiceD = findViewById(R.id.textD);
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer);
        backIBtn =findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());


        if (classId != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference docRefGroup = db.collection("Class").document(classId)
                    .collection("Question").document("question");
            docRefGroup.get().addOnSuccessListener(documentSnapshot -> {
                Question question = documentSnapshot.toObject(Question.class);
                A = question.getA();
                B = question.getB();
                C = question.getC();
                D = question.getD();
                tvChoiceA.setText(A.size()+"\t位");
                tvChoiceB.setText(B.size()+"\t位");
                tvChoiceC.setText(C.size()+"\t位");
                tvChoiceD.setText(D.size()+"\t位");
                tvCorrectAnswer.setText("正確答案為 : "+question.getAnswer());




                //https://blog.csdn.net/zcmain/article/details/53611245
                mChart.setDrawHoleEnabled(false);   //設置pieChart中心圓心
                mChart.getDescription().setEnabled(false);    //设置pieChart图表的描述
                mChart.setBackgroundColor(Color.WHITE);      //设置pieChart图表背景色
                mChart.animateY(1400, Easing.EaseInOutQuad);  //設置pieChart動畫
                // 设置 pieChart 图表Item文本属性
                mChart.setDrawEntryLabels(true);              //设置pieChart是否只显示饼图上百分比不显示文字（true：下面属性才有效果）
                mChart.setEntryLabelColor(Color.BLACK);       //设置pieChart图表文本字体颜色
                mChart.setEntryLabelTextSize(30f);            //设置pieChart图表文本字体大小

                mChart.setRotationEnabled(true);              //设置pieChart图表是否可以手动旋转
                mChart.setHighlightPerTapEnabled(true);       //设置piecahrt图表点击Item高亮是否可用

                Legend l = mChart.getLegend();
                l.setEnabled(false);
                setData();


            });


        }
    }

    private void setData() {
        ArrayList<PieEntry> pieEntryList = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ffb75a"));
        colors.add(Color.parseColor("#c52020"));
        colors.add(Color.parseColor("#82ae65"));
        colors.add(Color.parseColor("#0806b1"));
        //饼图实体 PieEntry
        if(A.size()!=0){
            PieEntry ChartA = new PieEntry(A.size(), "A");
            pieEntryList.add(ChartA);
        }if(B.size()!=0){
            PieEntry ChartB = new PieEntry(B.size(), "B");
            pieEntryList.add(ChartB);
        }if(C.size()!=0){
            PieEntry ChartC = new PieEntry(C.size(), "C");
            pieEntryList.add(ChartC);
        }if(D.size()!=0){
            PieEntry ChartD = new PieEntry(D.size(), "D");
            pieEntryList.add(ChartD);
        }




        //饼状图数据集 PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setSliceSpace(3f);           //设置饼状Item之间的间隙
        pieDataSet.setSelectionShift(10f);      //设置饼状Item被选中时变化的距离
        pieDataSet.setColors(colors);           //为DataSet中的数据匹配上颜色集(饼图Item颜色)

//            //文字向外拉
//            pieDataSet.setSelectionShift(5f);
//            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//            pieDataSet.setValueLinePart1OffsetPercentage(80.f);
//            pieDataSet.setValueLinePart1Length(0.2f);
//            pieDataSet.setValueLinePart2Length(0.4f);

        pieDataSet.setDrawValues(false);    //不顯示Data A.size(); b.size();
        //最终数据 PieData
        PieData pieData = new PieData(pieDataSet);

        mChart.setData(pieData);
        mChart.highlightValues(null);
        mChart.invalidate();                    //将图表重绘以显示设置的属性和数据
    }


}
