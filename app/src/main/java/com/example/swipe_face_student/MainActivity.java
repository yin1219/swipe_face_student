package com.example.swipe_face_student;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements OnFragmentSelectedListener {
    private static final String TAG = "BACKFLAG";
    private TextView mTextMessage;
    private String student_id = "405401114";
    private ViewPager viewPager;

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    // 設置默認進來是tab 顯示的頁面
    private void setDefaultFragment() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new Fragment_ClassList());
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.content, new Fragment_ClassList());
                    transaction.addToBackStack(new Fragment_ClassList().getClass().getName());
                    transaction.commit();

                    return true;
                case R.id.navigation_leave:

                    Fragment_LeaveList fragment_leaveList = new Fragment_LeaveList();
                    Bundle args = new Bundle();
                    args.putString("info",null);
                    args.putString("student_id", student_id);
                    Log.d(TAG,"MAIN ARG:"+args);
                    fragment_leaveList.setArguments(args);
                    transaction.replace(R.id.content, new Fragment_LeaveList());
                    transaction.addToBackStack(new Fragment_LeaveList().getClass().getName());
                    transaction.commit();
                    return true;
                case R.id.navigation_notification:
                    transaction.replace(R.id.content, new Fragment_Notifications());
                    transaction.addToBackStack(new Fragment_Notifications().getClass().getName());

                    transaction.commit();
                    return true;

                case R.id.navigation_user:
                    transaction.replace(R.id.content, new Fragment_User());
                    transaction.addToBackStack(new Fragment_User().getClass().getName());

                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__homepage);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setDefaultFragment();

        MainActivityPermissionsDispatcher.PermissionsWithPermissionCheck(this);
    }


    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void Permissions() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onBackPressed() {

        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
            Log.d(TAG, "ERROR");
        } else {
            Log.d(TAG, "success");
        }
    }//fragment退回鍵

    @Override
    public void onFragmentSelected(String info, String fragmentKey) {
        Log.d(TAG, "onFragmentSelected");
        if (fragmentKey.equals("toClassList")) {
            Fragment_ClassDetail fragmentClassDetail = new Fragment_ClassDetail();
            Bundle args = new Bundle();
            args.putString("info", info);
            args.putString("student_id", student_id);
            fragmentClassDetail.setArguments(args);
            Log.d(TAG, " MAIN");
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content, fragmentClassDetail).commit();
        }//判斷是哪個fragment傳來的請求

        else if (fragmentKey.equals("toAttendanceList")) {
            Fragment_AttendanceList fragment_attendanceList = new Fragment_AttendanceList();
            Bundle args = new Bundle();
            args.putString("info", info);
            args.putString("student_id", student_id);
            fragment_attendanceList.setArguments(args);
            Log.d(TAG, " toAttendanceList");
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content, fragment_attendanceList).commit();
        }//判斷是哪個fragment傳來的請求

        else if (fragmentKey.equals("toLeaveManage")) {
            Fragment_LeaveList fragment_leaveList = new Fragment_LeaveList();
            Bundle args = new Bundle();
            args.putString("info", info);
            args.putString("student_id", student_id);
            fragment_leaveList.setArguments(args);
            Log.d(TAG, " toLeaveManage");
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content, fragment_leaveList).commit();
        }//判斷是哪個fragment傳來的請求

        else if (fragmentKey.equals("toTeacherInfo")) {
            Fragment_TeacherInfo fragment_teacherInfo = new Fragment_TeacherInfo();
            Bundle args = new Bundle();
            args.putString("info", info);
            fragment_teacherInfo.setArguments(args);
            Log.d(TAG, " ToTeacherInfo");
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content, fragment_teacherInfo).commit();
        }//判斷是哪個fragment傳來的請求


    }//fragment傳值並換頁


}
