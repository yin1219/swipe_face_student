package com.example.swipe_face_student.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.swipe_face_student.CreateGroupDecideLeader;
import com.example.swipe_face_student.Model.Student;
import com.example.swipe_face_student.R;

import java.util.List;

public class CreateGroupDecideLeaderAdapter extends  RecyclerView.Adapter<CreateGroupDecideLeaderAdapter.ViewHolder>  {
    public Context context;
    public List<Student> studentList;
    String TAG = "CreateGroupDecideLeaderAdapter";
    private int lastSelectedPosition = -1;



    public CreateGroupDecideLeaderAdapter(CreateGroupDecideLeader createGroupDecideLeader, List<Student> studentList) {
        this.context = createGroupDecideLeader;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_group_student_leader_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvGroupStudentName.setText(String.format("%s\t\t%s\t\t%s", studentList.get(position).getStudent_id(),
                studentList.get(position).getStudent_department(), studentList.get(position).getStudent_name()));
        holder.rbGroupLeader.setChecked(lastSelectedPosition == position);
        if(lastSelectedPosition != -1){
            Log.d(TAG,"rbPosition"+studentList.get(lastSelectedPosition).getStudent_id());
            String groupLeader = studentList.get(lastSelectedPosition).getStudent_id();
            Log.d(TAG,"groupLeader\t49\t"+groupLeader);

            Log.d(TAG,"groupLeader\t51\t"+groupLeader);

            //Send value from adapter to activity
            Intent intent = new Intent("custom-message");
            intent.putExtra("groupLeader",groupLeader);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return studentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView tvGroupStudentName;
//        RadioGroup rgGroupLeader;
        RadioButton rbGroupLeader;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvGroupStudentName = mView.findViewById(R.id.groupDetailName);
//            rgGroupLeader = mView.findViewById(R.id.rgGroupLeader);
            rbGroupLeader = mView.findViewById(R.id.rbGroupLeader);

            rbGroupLeader.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();

            });
        }
    }


}


