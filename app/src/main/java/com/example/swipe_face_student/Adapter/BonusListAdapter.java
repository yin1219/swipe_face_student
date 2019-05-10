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

        SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy/MM/dd");
        String bonusType = BonusList.get(position).getBonus_reason();

        String bonusTime = myFmt2.format(BonusList.get(position).getBonus_time());
        holder.bouns_reason.setText(BonusList.get(position).getBonus_reason());
        holder.bonus_time.setText(bonusTime);

        if(bonusType.equals("點人答題")){
            holder.plusBonus.setText(BonusList.get(position).getRDanswerBonus());
        }
        else if(bonusType.equals("回答問題")){
            holder.plusBonus.setText(BonusList.get(position).getAnswerBonus());
        }

    }

    @Override
    public int getItemCount() {
        return BonusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView bouns_reason;
        public TextView bonus_time;
        public TextView plusBonus;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;

            bouns_reason = (TextView) mView.findViewById(R.id.textViewBonusReason);
            bonus_time = (TextView) mView.findViewById(R.id.textViewBonusTime);
            plusBonus = (TextView) mView.findViewById(R.id.plusBonus);
        }
    }

}

