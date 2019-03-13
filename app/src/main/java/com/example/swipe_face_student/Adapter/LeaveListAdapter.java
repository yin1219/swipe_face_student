package com.example.swipe_face_student.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Leave;
import com.example.swipe_face_student.LeaveRecord;
import com.example.swipe_face_student.Model.LeaveRcord;
import com.example.swipe_face_student.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class LeaveListAdapter extends RecyclerView.Adapter<LeaveListAdapter.ViewHolder> {

    public Context context;

    public List<Leave> leaveList;
    private String TAG = "Leave";

    public LeaveListAdapter(Context context, List<Leave> leaveList) {
        this.leaveList = leaveList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leavelist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd");
        String leave_uploaddate = myFmt2.format(leaveList.get(position).getLeave_uploaddate()).toString();

        holder.leave_reason.setText(leaveList.get(position).getLeave_reason());
        holder.leave_check.setText(leaveList.get(position).getLeave_check());
        holder.leave_date.setText(leave_uploaddate);


        String leaveRecordId = leaveList.get(position).LeaveId;
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeaveRecord.class);
                intent.putExtra("leaveId",leaveRecordId);
                context.startActivity(intent);
                Toast.makeText(context, "Id  :  " + leaveRecordId, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return leaveList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView leave_reason;
        public TextView leave_check;
        public TextView leave_date;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;


            leave_reason = (TextView) mView.findViewById(R.id.leaveReason);
            leave_check = (TextView) mView.findViewById(R.id.leaveCheck);
            leave_date = (TextView) mView.findViewById(R.id.leaveDate);

        }
    }

}
