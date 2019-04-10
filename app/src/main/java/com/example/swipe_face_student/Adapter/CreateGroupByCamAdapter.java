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
import android.widget.Button;
import android.widget.TextView;


import com.example.swipe_face_student.CreateClassGroupByCam;
import com.example.swipe_face_student.CreateClassGroupByHand;
import com.example.swipe_face_student.Model.Student;
import com.example.swipe_face_student.Model.Class;

import com.example.swipe_face_student.R;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class CreateGroupByCamAdapter extends RecyclerView.Adapter<CreateGroupByCamAdapter.ViewHolder> {

    public Context context;
    public List<Student> studentList;
    String TAG = "CreateGroupByCamAdapter";

    private CreateGroupByCamAdapter.transPageListener mTransPageListener;//adapter跳轉fragment


    public CreateGroupByCamAdapter(CreateClassGroupByCam createClassGroupByCam, List<Student> studentList) {
        this.context = createClassGroupByCam;
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
        holder.tvGroupStudentName.setText(studentList.get(position).getStudent_id()+studentList.get(position).getStudent_department()+studentList.get(position).getStudent_name());
        holder.btDeleteStudent.setOnClickListener(v -> {
            notifyItemChanged(position);
            String student_Id = studentList.get(position).StudentId;
            mTransPageListener.onTransPageClick(student_Id,studentList.get(position));

        });

        holder.mView.setOnClickListener(v -> {
            notifyItemChanged(position);
            String student_Id = studentList.get(position).StudentId;
            mTransPageListener.onTransPageClick(student_Id,studentList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView tvGroupStudentName;
        public Button btDeleteStudent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvGroupStudentName = mView.findViewById(R.id.groupDetailName);
            btDeleteStudent = mView.findViewById(R.id.deleteStudent);
        }
    }

    public interface transPageListener {
        public void onTransPageClick(String student_Id,Student student);
    }//adapter跳轉fragment並攜帶需要的資料

    public void setOnTransPageClickListener(CreateGroupByCamAdapter.transPageListener transPageListener) {
        this.mTransPageListener = transPageListener;
    }//adapter跳轉fragment
}
