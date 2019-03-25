package com.example.swipe_face_student.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.swipe_face_student.CreateClassGroupByHand;
import com.example.swipe_face_student.Model.Student;
import com.example.swipe_face_student.Model.Class;

import com.example.swipe_face_student.R;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class CreateGroupByHandAdapter extends RecyclerView.Adapter<CreateGroupByHandAdapter.ViewHolder> {

    public Context context;
    FirebaseFirestore db;
    public List<Student> studentList;
    String TAG = "GroupDetailAdapter";
    String groupLeader;

    public CreateGroupByHandAdapter(CreateClassGroupByHand createClassGroupByHand, List<Student> studentList) {
        this.context = createClassGroupByHand;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_group_student_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        holder.tvGroupStudentName.setText(studentList.get(position).getStudent_id());



    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView tvGroupStudentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvGroupStudentName = mView.findViewById(R.id.groupDetailName);
        }
    }
}
