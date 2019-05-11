package com.example.swipe_face_student;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_LeaveListClass extends AppCompatActivity {

    private static final String TAG = "LeaveClass";
    private FloatingActionButton fab_leave;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//抓現在登入user
    String email1 = user.getEmail();//抓user.email
    String [] uriEmailArray = email1.split("@");
    String student_id = uriEmailArray[0];

    private String class_id;
    private String checkWay; //確認假單批改完後導向
    private ImageButton backIBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leavelistclass);

        Bundle args = getIntent().getExtras();
        Log.d(TAG,"Bundle "+new Bundle());

            class_id = args.getString("info");
            student_id = args.getString("student_id");
            checkWay = "課堂中";
            Log.d(TAG,"args: "+args.toString());

        backIBtn = (ImageButton) findViewById(R.id.backIBtn) ;
        fab_leave = (FloatingActionButton) findViewById(R.id.fab_leave);

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Log.d(TAG, "class_id:" + class_id);//fragment傳值
        Log.d(TAG, "student_id:" + student_id);//fragment傳值
        Log.d(TAG, "checkWay:" + checkWay);//fragment傳值

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);

        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);


        //1
        Bundle arg1 = new Bundle();
        arg1.putString("PassStudent_id", student_id);
        arg1.putString("PassClass_Id", class_id);
        arg1.putString("PassCheck_Way", checkWay);
        arg1.putString("PassList_Way", "未審核");

        tabHost.addTab(tabHost.newTabSpec("Checking")
                        .setIndicator("未審核"),
                Fragment_LeaveListClass_View.class,
                arg1);
        Log.d(TAG, "TEST LOG RUN Times_class " + arg1.toString());

        //2

        Bundle arg2 = new Bundle();
        arg2.putString("PassStudent_id", student_id);
        arg2.putString("PassClass_Id", class_id);
        arg2.putString("PassCheck_Way", checkWay);
        arg2.putString("PassList_Way", "准假");

        tabHost.addTab(tabHost.newTabSpec("Check0")
                        .setIndicator("准假"),
                Fragment_LeaveListClass_View.class,
                arg2);
        //3

        Bundle arg3 = new Bundle();
        arg3.putString("PassStudent_id", student_id);
        arg3.putString("PassClass_Id", class_id);
        arg3.putString("PassCheck_Way", checkWay);
        arg3.putString("PassList_Way", "不准假");

        tabHost.addTab(tabHost.newTabSpec("Check1")
                        .setIndicator("不准假"),
                Fragment_LeaveListClass_View.class,
                arg3);

        fab_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Bundle formLeaveList = new Bundle();
                formLeaveList.putString("class_id",class_id);
//                if (isAllClass){
//                    formLeaveList.putBoolean("isAllClass",true);
//                }else{
                    formLeaveList.putBoolean("isAllClass",false);
//                }

                i.putExtras(formLeaveList);
                i.setClass(Activity_LeaveListClass.this, LeaveApplications.class);
                startActivity(i);
            }
        });



    }


}
