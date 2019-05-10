package com.example.swipe_face_student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.opencensus.tags.Tag;

import static com.example.swipe_face_student.BackHandlerHelper.handleBackPress;


public class Fragment_ClassDetail extends Fragment implements FragmentBackHandler {


    private String TAG = "ClassDetail";
    private String classId, class_id;
    private Class aclass;
    private Class firestore_class;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GridLayout gridLayout;
    private TextView text_class_title;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//抓現在登入user
    String email1 = user.getEmail();//抓user.email
    String [] uriEmailArray = email1.split("@");
    String student_id = uriEmailArray[0];

    OnFragmentSelectedListener mCallback;//Fragment傳值


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        Bundle args = new Bundle();//fragment傳值
        args = getArguments();//fragment傳值
        assert args != null;
        classId = args.getString("info");
        Log.d(TAG, "classId:" + classId);//fragment傳值
        Toast.makeText(getContext(), "現在課程代碼是" + classId, Toast.LENGTH_LONG).show();

        return inflater.inflate(R.layout.fragment_fragment_class_detail, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "classId2:" + classId);
        text_class_title = (TextView) view.findViewById(R.id.text_class_title);
        gridLayout = (GridLayout) view.findViewById(R.id.grid_class_detail);

        setClass(new FirebaseCallback() {
            @Override
            public void onCallback(Class firestore_class) {

                text_class_title.setText(firestore_class.getClass_name());

                setSingleEvent(gridLayout, firestore_class);
            }
        });

        getClass_id(classId);


    }


    private void setClass(FirebaseCallback firebaseCallback) {
        firestore_class = new Class();
        Log.d(TAG, "setClass class_id:" + classId);
        Task<DocumentSnapshot> documentSnapshotTask = db.collection("Class")
                .document(classId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        firestore_class = documentSnapshot.toObject(Class.class);
                        Log.d(TAG, "setClass here");
                        firebaseCallback.onCallback(firestore_class);
                    }
                });


    }

    @Override
    public boolean onBackPressed() {
        if (handleBackPress(this)) {
            //外理返回鍵
            return true;
        } else {
            // 如果不包含子Fragment
            // 或子Fragment沒有外理back需求
            // 可如直接 return false;
            // 註：如果Fragment/Activity 中可以使用ViewPager 代替 this
            //
            return handleBackPress(this);
        }
    }//fragment 返回鍵

    public interface FirebaseCallback {
        void onCallback(Class firestore_class);
    }//處理firestore非同步的問題 回調接頭

//    public interface OnFragmentSelectedListener {
//        public void onFragmentSelected(String info, String fragmentKey);
//    }//Fragment傳值

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnFragmentSelectedListener) context;//fragment傳值
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Mush implement OnFragmentSelectedListener ");
        }
    }

    // we are setting onClickListener for each element 處理選項
    private void setSingleEvent(GridLayout gridLayout, Class firestore_class) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            CardView cardView = (CardView) gridLayout.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(view -> {
                Toast.makeText(getContext(), "Clicked at index " + finalI,
                        Toast.LENGTH_SHORT).show();
                switch (finalI) {
                    case 0:
                        DocumentReference docRefClass = db.collection("Class").document(classId);
                        docRefClass.get().addOnSuccessListener(documentSnapshot -> {
                            Class classCheckQuestion = documentSnapshot.toObject(Class.class);
                            if(!classCheckQuestion.isQuestion_state()){
                                Toast.makeText(getContext(), "尚未開始",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                DocumentReference questionDoc = db.collection("Class").document(classId)
                                        .collection("Question").document("question");
                                questionDoc.get().addOnSuccessListener(documentSnapshot2 -> {
                                    Question question = documentSnapshot2.toObject(Question.class);
                                    Date create_time = question.getCreate_time();
                                    Date nowDate = new Date();

                                    if(create_time.before(nowDate)){
                                        Intent intentToAnalysis = new Intent();
                                        intentToAnalysis.setClass(getActivity(), QuestionAnalysis.class);
                                        Bundle bundleToAnalysis = new Bundle();
                                        bundleToAnalysis.putString("classId", classId);
                                        intentToAnalysis.putExtras(bundleToAnalysis);
                                        getActivity().startActivity(intentToAnalysis);
                                    }
                                    else{
                                        Intent intentToQuestion = new Intent();
                                        intentToQuestion.setClass(getActivity(), QuestionResponse.class);
                                        Bundle bundleToQuestion = new Bundle();
                                        bundleToQuestion.putString("classId", classId);
                                        intentToQuestion.putExtras(bundleToQuestion);
                                        getActivity().startActivity(intentToQuestion);
                                    }

                                });
                            }
                        });


                        break;
                    case 1:
                        //抓Class->DocClass資料
                        DocumentReference docRef = db.collection("Class").document(classId);
                        docRef.get().addOnSuccessListener(documentSnapshot -> {
                            Class classG = documentSnapshot.toObject(Class.class);

                            Date groupCreateTime = classG.getCreate_time();
                            Log.d(TAG, "groupCreateDate" + groupCreateTime.toString());
                            Date date = new Date();
                            Log.d(TAG, "NowDate" + date.toString());

                            //判斷狀態 -> 開始分組尚未確認時間已過
                            if(!classG.isGroup_state() && !classG.isGroup_state_go()){
                                Toast.makeText(getContext(), "尚未分組",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else if(!classG.isGroup_state() && classG.isGroup_state_go()){
                                if (groupCreateTime.before(date)) {
                                    Toast.makeText(getContext(), "分組時間已過",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), CreateClassGroupSt1.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("classId", classId);
                                    intent.putExtras(bundle);
                                    getActivity().startActivity(intent);
                                }
                            }
                            else{
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), GroupPage.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("classId", classId);
//                                        bundle.putString("userId",userId);
                                bundle.putString("class_Id", classG.getClass_id());
                                bundle.putString("classYear", classG.getClass_year());
                                bundle.putString("className", classG.getClass_name());
                                bundle.putInt("classStuNum", classG.getStudent_id().size());
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                            }
                        });

                        break;
                    case 2:

                        mCallback.onFragmentSelected(firestore_class.getClass_id(), "toAttendanceList");//fragment傳值

                        break;
                    case 3:

                        mCallback.onFragmentSelected(firestore_class.getClass_id(), "toClassPerformance");//fragment傳值
                        break;
                    case 4:
                        Intent intentLeave = new Intent();
                        Bundle bundleleave = new Bundle();
                        bundleleave.putString("info",class_id);
                        bundleleave.putString("student_id",student_id);
                        Log.d(TAG,"LeaveListN set bundle" + bundleleave.toString());
                        intentLeave.putExtras(bundleleave);
                        intentLeave.setClass(getActivity(),Activity_LeaveListClass.class);
                        startActivity(intentLeave);
                        break;
                    case 5:
                        Log.d(TAG, "case5" + firestore_class.getTeacher_email());
                        mCallback.onFragmentSelected(firestore_class.getTeacher_email(), "toTeacherInfo");//fragment傳值

                        break;
                }
            });
        }
    }

    public void getClass_id(String classId){
        DocumentReference docRef = db.collection("Class").document(classId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Class aClass = document.toObject(Class.class);
                        class_id = aClass.getClass_id();
                    }
                }
            }
        });
    }


}
