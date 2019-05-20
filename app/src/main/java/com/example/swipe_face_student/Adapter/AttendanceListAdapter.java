package com.example.swipe_face_student.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swipe_face_student.Model.Attendance;
import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Rollcall;
import com.example.swipe_face_student.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.ViewHolder> {


    @NonNull


    public List<Attendance> AttendanceList;
    public Context context;


    public AttendanceListAdapter(Context context, List<Attendance> AttendanceList) {

        this.AttendanceList = AttendanceList;
        this.context = context;

    }


    @Override

    public AttendanceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_item, parent, false);
        return new AttendanceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceListAdapter.ViewHolder holder, int position) {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date attendanceDate = AttendanceList.get(position).getAttendance_time();
        String attendanceCheck = AttendanceList.get(position).getAttendance_status();

        holder.tvAttendDate.setText(sdFormat.format(attendanceDate));
//        holder.tvAttendanceState.setText(AttendanceList.get(position).getAttendance_status());
        if(attendanceCheck.equals("出席")){
            holder.tvAttendanceState.setText(attendanceCheck);
            holder.tvAttendanceState.setTextColor(context.getResources().getColor(R.color.attendance));
            holder.tvAttendanceState.setBackground(context.getResources().getDrawable(R.drawable.attendance_state_attend_backgroud));
        }
        else if(attendanceCheck.equals("缺席")){
            holder.tvAttendanceState.setText(attendanceCheck);
            holder.tvAttendanceState.setTextColor(context.getResources().getColor(R.color.absense));
            holder.tvAttendanceState.setBackground(context.getResources().getDrawable(R.drawable.attendance_state_absense_backgroudsd));
        }
        else if(attendanceCheck.equals("遲到")){
            holder.tvAttendanceState.setText(attendanceCheck);
            holder.tvAttendanceState.setTextColor(context.getResources().getColor(R.color.late));
            holder.tvAttendanceState.setBackground(context.getResources().getDrawable(R.drawable.attendance_state_late_backgroud));
        }
        else if(attendanceCheck.equals("請假")){
            holder.tvAttendanceState.setText(attendanceCheck);
            holder.tvAttendanceState.setTextColor(context.getResources().getColor(R.color.leave));
            holder.tvAttendanceState.setBackground(context.getResources().getDrawable(R.drawable.attendance_state_leave_backgroud));
        }else{
            holder.tvAttendanceState.setText(attendanceCheck);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Flag:", "click");
                notifyItemChanged(position);


            }
        });


    }

    @Override
    public int getItemCount() {
        return AttendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvAttendDate;
        TextView tvAttendanceState;


        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            tvAttendDate = mView.findViewById(R.id.tvAttendDate);
            tvAttendanceState = mView.findViewById(R.id.tvAttendanceState);

        }
    }


}
