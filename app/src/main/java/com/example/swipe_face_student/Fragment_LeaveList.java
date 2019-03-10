package com.example.swipe_face_student;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.swipe_face_student.Adapter.LeaveListAdapter;
import com.example.swipe_face_student.Model.Leave;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Fragment_LeaveList extends Fragment implements FragmentBackHandler {
    private static final String TAG = "LeaveList";
    private RecyclerView mMainList;
    private FirebaseFirestore mFirestore;
    private LeaveListAdapter leaveListAdapter;
    private String class_id;
    private String student_id;


    private List<Leave> leaveList;

    OnFragmentSelectedListener mCallback;//Fragment傳值

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = new Bundle();//fragment傳值
        args = getArguments();//fragment傳值
        Log.d(TAG,"Bundle "+new Bundle());
//        Log.d(TAG,"args0: "+args.toString());
        if (args!=null){
            class_id = args.getString("info");
            student_id = args.getString("student_id");
            Log.d(TAG,"args: "+args.toString());

        }else {
            class_id = null;
        }

        Log.d(TAG, "class_id:" + class_id);//fragment傳值
        Toast.makeText(getContext(), "現在課程:" + class_id, Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment__leave_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        leaveList = new ArrayList<>();
        leaveListAdapter = new LeaveListAdapter(getActivity().getApplicationContext(), leaveList);
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab_leave);
        mMainList = (RecyclerView) getView().findViewById(R.id.leave_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setAdapter(leaveListAdapter);

        if (class_id == null) {
            setAllLeave();
        }else {
            setClassLeave();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getActivity().getApplicationContext(), LeaveApplications.class);
                startActivity(i);
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


    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }//fragment 返回鍵


    private void setAllLeave() {
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Leave").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "error" + e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String leaveId = doc.getDocument().getId();

                        Leave leave = doc.getDocument().toObject(Leave.class).withId(leaveId);
                        leaveList.add(leave);

                        leaveListAdapter.notifyDataSetChanged();
                    }

                }
            }


        });
    }


    private void setClassLeave() {
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Leave").whereEqualTo("class_id", class_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "error" + e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String leaveId = doc.getDocument().getId();

                        Leave leave = doc.getDocument().toObject(Leave.class).withId(leaveId);
                        leaveList.add(leave);

                        leaveListAdapter.notifyDataSetChanged();
                    }

                }
            }


        });
    }

}
