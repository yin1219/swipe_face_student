package com.example.swipe_face_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.swipe_face_student.Adapter.ClassListAdapter;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Student;
import com.example.swipe_face_student.Model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Fragment_ClassList extends Fragment implements FragmentBackHandler {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "Fragment_ClassList";
    private ImageView imNoData;
    private RecyclerView mMainList;
    private ClassListAdapter classListAdapter;
    private List<Class> classList; // For Adapter
    private
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//抓現在登入user
    String email1 = user.getEmail();//抓user.email
    String [] uriEmailArray = email1.split("@");
    private String student_id = uriEmailArray[0];
    private Teacher teacher;
    //    private ArrayList<String> class_id = new ArrayList<>();
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private String classId; //classDocId
    private String studentId; //studentDocId
    OnFragmentSelectedListener mCallback;//Fragment傳值


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment__class_list, container, false);
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //init db
        db = FirebaseFirestore.getInstance();

        //init Adapter
        classList = new ArrayList<>();
        classListAdapter = new ClassListAdapter(getActivity().getApplicationContext(), classList);

        //init xml
        imNoData = view.findViewById(R.id.imNoData);

        //init FabXml
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab_leave);

        //init RecyclerView
        mMainList = getView().findViewById(R.id.class_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setAdapter(classListAdapter);

        // db query studentId
        db.collection("Student")
                .whereEqualTo("student_id", student_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            studentId = document.getId();
                            Log.d(TAG, "studentId : " + studentId);
                            if (studentId != null) {
                                setAllClassList();
                                Log.d(TAG,"測試一開始setAllClassList跑幾次");
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        classListAdapter.setOnTransPageClickListener(new ClassListAdapter.transPageListener() {

            @Override
            public void onTransPageClick(String classId2) {
                Log.d(TAG, "onTransPageClick0" + classId2);
                mCallback.onFragmentSelected(classId2, "toClassList");//fragment傳值
            }
        });//Fragment換頁

        fab.setOnClickListener(v -> {
            fabAddClass();
        });
    }

    private void fabAddClass() {
        ArrayList<String> class_idForCheck = new ArrayList<>();
        LayoutInflater lf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        ViewGroup vg = (ViewGroup) lf.inflate(R.layout.dialog_add_class, null);
        final EditText etShow = vg.findViewById(R.id.et_name);
        new AlertDialog.Builder(getActivity())
                .setView(vg)
                .setPositiveButton("確定", (dialog, which) -> {
                    final String classKey = etShow.getText().toString();
                    if (classKey.isEmpty()) {
                        Toast.makeText(getActivity(), "請輸入課程代碼", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Dialog取消");
                    } else {
                        Log.d(TAG, "Dialog確定");
                        db.collection("Class")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            class_idForCheck.add(document.getString("class_id"));
                                            Log.d(TAG, "class_idForCheck : " + class_idForCheck);

                                        }
                                        //判斷input class_id 是否存在
                                        if (!class_idForCheck.contains(classKey)) {
                                            Toast.makeText(getActivity(), "查無此課程代碼", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d(TAG, "check classKey: " + classKey);
                                            Query drQueryTotalAttendance = db.collection("Class")
                                                    .whereEqualTo("class_id", classKey);
                                            drQueryTotalAttendance.get().addOnCompleteListener(taskForQueryClassKetDocId -> {
                                                if (taskForQueryClassKetDocId.isSuccessful()) {
                                                    String classId = null;
                                                    for (QueryDocumentSnapshot dsQueryTotalAttendance : taskForQueryClassKetDocId.getResult()) {
                                                        classId = dsQueryTotalAttendance.getId();

                                                    }
                                                    setClassStudent(classId,classKey);
                                                    DocumentReference drQueryAttendancePoint = db.collection("Class").document(classId);
                                                    drQueryAttendancePoint.get().addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            DocumentSnapshot dsQueryAttendancePoint = task1.getResult();
                                                            if (dsQueryAttendancePoint.exists()) {
                                                                int performanceTotalAttendance = dsQueryAttendancePoint.toObject(Class.class).getClass_totalpoints();
                                                                Map<String, Object> addPerformance = new HashMap<>();
                                                                addPerformance.put("class_id", classKey);
                                                                addPerformance.put("performance_totalAttendance", performanceTotalAttendance);
                                                                addPerformance.put("performance_totalBonus", (0));
                                                                addPerformance.put("performance_absenceTimes",(0));
                                                                addPerformance.put("student_id", "405401217");//user.get..... 不是這個測試的
                                                                db.collection("Performance")
                                                                        .add(addPerformance)
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                        });
                                                                Log.d(TAG,"測試Performance跑幾次");

                                                            }
                                                        }
                                                    });

                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            });
//                                            setAllClassList();
//                                        classListAdapter.notifyDataSetChanged();


                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                });




                                }


                })
                .setNegativeButton("取消", null).show();
}

    private void setClassStudent(String classId,String classKey){
        Map<String, Object> addClass = new HashMap<>();
        addClass.put("student_id", FieldValue.arrayUnion(student_id));
        db.collection("Class")
                .document(classId)
                .update(addClass)
                .addOnSuccessListener(aVoid -> {
                });
        //db upload Student
        Map<String, Object> addStudent = new HashMap<>();
        addStudent.put("class_id", FieldValue.arrayUnion(classKey));
        db.collection("Student")
                .document(studentId)
                .update(addStudent)
                .addOnSuccessListener(aVoid -> {
                });
        }
    private void setAllClassList() {

        classList.clear();
        if (classList.isEmpty()) {

            db.collection("Class").
                    whereArrayContains("student_id", student_id).
                    addSnapshotListener((documentSnapshots1, e1) -> {
                        if (e1 != null) {
                            Log.d(TAG, "Error :" + e1.getMessage());
                        }
                        for (DocumentChange doc : documentSnapshots1.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String classId = doc.getDocument().getId();
                                Log.d(TAG, "312 classId : " + classId);
                                Class aClass = doc.getDocument().toObject(Class.class).withId(classId);
                                classList.add(aClass);
                                classListAdapter.notifyDataSetChanged();
                            }
                        }
                        if (classList.isEmpty()) {
                            mMainList.setVisibility(View.GONE);
                            imNoData.setVisibility(View.VISIBLE);
                        }
                        else {
                            mMainList.setVisibility(View.VISIBLE);
                            imNoData.setVisibility(View.GONE);
                        }
                    });


//            DocumentReference docRef = db.collection("Student")
//                    .document(studentId);
//            docRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    assert document != null;
//                    if (document.exists()) {
//                        Student student = document.toObject(Student.class);
//                        ArrayList<String> class_id = new ArrayList<>();
//                        class_id = student.getClass_id();
//                        for (int i = 0; i < class_id.size(); i++) {
//                            Log.d(TAG, "Student裡的class_id : " + class_id.get(i));
//                        }
//                        for (String class_idForCheck : class_id) {
//                            db.collection("Class").
//                                    whereEqualTo("class_id", class_idForCheck).
//                                    addSnapshotListener((documentSnapshots1, e1) -> {
//                                        if (e1 != null) {
//                                            Log.d(TAG, "Error :" + e1.getMessage());
//                                        }
//                                        for (DocumentChange doc : documentSnapshots1.getDocumentChanges()) {
//                                            if (doc.getType() == DocumentChange.Type.ADDED) {
//                                                String classId = doc.getDocument().getId();
//                                                Log.d(TAG, "312 classId : " + classId);
//                                                Class aClass = doc.getDocument().toObject(Class.class).withId(classId);
//                                                classList.add(aClass);
//                                                classListAdapter.notifyDataSetChanged();
//                                            }
//                                        }
//                                    });
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            });
        }
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }//fragment 返回鍵

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
}
