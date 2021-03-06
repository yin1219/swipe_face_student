package com.example.swipe_face_student.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.R;

import java.util.ArrayList;
import java.util.List;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder> {


    @NonNull

    private transPageListener mTransPageListener;//adapter跳轉fragment
    public List<Class> ClassList;
    public Context context;


    public ClassListAdapter(Context context,List<Class> ClassList) {

        this.ClassList = ClassList;
        this.context =context;

    }


    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classlist_itam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ArrayList<String> class_schoolyeat = new ArrayList<String>();


        holder.class_name.setText(ClassList.get(position).getClass_name());
        holder.class_schoolyear.setText(ClassList.get(position).getClass_year());
        holder.student_total.setText(ClassList.get(position).getStudent_total().toString());
        holder.class_id.setText(ClassList.get(position).getClass_id());
        String classId= ClassList.get(position).classId;



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Flag:", "click");
                notifyItemChanged(position);

                mTransPageListener.onTransPageClick(classId);




            }
        });


    }

    @Override
    public int getItemCount() {
        return ClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView class_name;
        public TextView class_schoolyear;
        public TextView student_total;
        public TextView class_id;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            class_name = (TextView) mView.findViewById(R.id.class_name);
            class_schoolyear = (TextView) mView.findViewById(R.id.class_schoolyear);
            student_total = (TextView) mView.findViewById(R.id.student_total);
            class_id = (TextView) mView.findViewById(R.id.class_id);

        }
    }

    public interface transPageListener {
        public void onTransPageClick(String classId);
    }//adapter跳轉fragment並攜帶需要的資料
    public void setOnTransPageClickListener(transPageListener transPageListener) {
        this.mTransPageListener = transPageListener;
    }//adapter跳轉fragment




}
