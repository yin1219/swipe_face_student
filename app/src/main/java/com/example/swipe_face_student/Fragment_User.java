package com.example.swipe_face_student;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.swipe_face_student.Model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


public class Fragment_User extends Fragment implements FragmentBackHandler {

    private final String TAG = "Fragment_User";
    private CardView cvInfo,logout;

    OnFragmentSelectedListener mCallback;//Fragment傳值


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment__user, container, false);

    }

    @Override
    public boolean onBackPressed()  {
        return BackHandlerHelper.handleBackPress(this);
    }//fragment 返回鍵

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //inti xml
        cvInfo = view.findViewById(R.id.cvInfo);

        cvInfo.setOnClickListener(v -> {
            mCallback.onFragmentSelected(TAG, "toUserInfo");//fragment傳值
        });

        logout = (CardView) getView().findViewById(R.id.logout);
        logout.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent();
            i.setClass(getActivity(),WelcomePage.class);
            startActivity(i);

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
