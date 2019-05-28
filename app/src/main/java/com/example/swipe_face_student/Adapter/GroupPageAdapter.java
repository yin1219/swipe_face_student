package com.example.swipe_face_student.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swipe_face_student.GroupPage;
import com.example.swipe_face_student.Model.Student;
import com.example.swipe_face_student.R;

import java.util.List;

public class GroupPageAdapter extends RecyclerView.Adapter<GroupPageAdapter.ViewHolder> {

    public Context context;
    public List<Student> studentList;
    String TAG = "GroupPageAdapter";

    public GroupPageAdapter(GroupPage groupPage, List<Student> studentList) {
        this.context = groupPage;
        this.studentList = studentList;
        Log.d(TAG,"StudentList:"+studentList.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_page_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        holder.tvGroupStudentName.setText(
                String.format("%s\t\t%s\t\t%s",
                        studentList.get(position).getStudent_id(),
                        studentList.get(position).getStudent_department(),
                        studentList.get(position).getStudent_name()));


    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return studentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView tvGroupStudentName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvGroupStudentName = mView.findViewById(R.id.groupDetailName);
        }
    }

}
