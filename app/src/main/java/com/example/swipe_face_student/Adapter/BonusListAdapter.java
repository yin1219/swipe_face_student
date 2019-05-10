package com.example.swipe_face_student.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swipe_face_student.Model.Bonus;
import com.example.swipe_face_student.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class BonusListAdapter extends RecyclerView.Adapter<BonusListAdapter.ViewHolder> {


    @NonNull


    public List<Bonus> BonusList;
    public Context context;


    public BonusListAdapter(Context context, List<Bonus> BonusList) {

        this.BonusList = BonusList;
        this.context = context;

    }


    @Override

    public BonusListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bouns_list_item, parent, false);
        return new BonusListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BonusListAdapter.ViewHolder holder, int position) {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date attendanceDate = BonusList.get(position).getBonus_time();

        holder.text_bonus_date.setText(sdFormat.format(attendanceDate));


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
        return BonusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView text_bonus_date;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            text_bonus_date = (TextView) mView.findViewById(R.id.text_bonus_date);

        }
    }
}

