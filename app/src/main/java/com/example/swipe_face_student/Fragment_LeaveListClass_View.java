package com.example.swipe_face_student;

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

import com.example.swipe_face_student.Adapter.LeaveListClassDetailAdapter;
import com.example.swipe_face_student.Model.Leave;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fragment_LeaveListClass_View extends Fragment {
    private static final String TAG = "LeaveListC_View";
    String class_id, student_id;
    String check_way, list_way;

    private RecyclerView mMainList;
    private FirebaseFirestore mFirestore;
    private LeaveListClassDetailAdapter leaveListClassDetailAdapter;
    private List<Leave> leaveList;
    private Bundle arg;

    private TextView tvNoData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        arg = getArguments();//fragment傳值
        student_id = arg.getString("PassStudent_id");
        class_id = arg.getString("PassClass_Id");
        check_way = arg.getString("PassCheck_Way");
        list_way = arg.getString("PassList_Way");
        Log.d(TAG,"TEST LOG RUN Times_class_v " + arg.toString());


        Log.d(TAG, "TEST LV  " + student_id);
        Log.d(TAG, "TEST LV   " + class_id);
        Log.d(TAG, "TEST LV   " + check_way);
        Log.d(TAG, "TEST LV   " + list_way);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leavelist_v, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        leaveList = new ArrayList<>();
        leaveListClassDetailAdapter = new LeaveListClassDetailAdapter(view.getContext(),leaveList);

        mFirestore = FirebaseFirestore.getInstance();

        mMainList = view.findViewById(R.id.leave_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMainList.setAdapter(leaveListClassDetailAdapter);
        setClassLeave(list_way);

        tvNoData = view.findViewById(R.id.tvNoData);



    }


    public void setClassLeave(String listWay){
        leaveList.clear();
        mFirestore.collection("Leave").whereEqualTo("leave_check",listWay)
                .whereEqualTo("class_id",class_id)
                .whereEqualTo("student_id",student_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Log.d(TAG,"error00" + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){
                        Log.d(TAG,"here" );

                        String leaveRecordId = doc.getDocument().getId();

                        Leave leave = doc.getDocument().toObject(Leave.class).withId(leaveRecordId);
                        leave.setCheckWay("課堂中");
                        leaveList.add(leave);

                        leaveListClassDetailAdapter.notifyDataSetChanged();
                    }

                }
                if(leaveList.isEmpty()){
                    Log.d(TAG,"here0" );
                    leaveListClassDetailAdapter.notifyDataSetChanged();
                    mMainList.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }
                else {
                    tvNoData.setVisibility(View.GONE);
                }
            }
        });


    }


}

