package com.example.swipe_face_student;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class S_SignUp extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "S_SignUp";
    private Button buttonRegister;
    private ImageButton backBtn;
    private EditText editTextName;
    private EditText editTextId;
    private EditText editTextEmail;
    private EditText editTextPassword1;
    private EditText editTextPassword2;
    private ImageView img_pgbar;
    private AnimationDrawable ad;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private Spinner spinner1;
    private Spinner spinner2;

    private String[] school = {"請選擇學校","私立輔仁大學","國立台灣大學"}; //宣告字串陣列
    private String[] department = {"請選擇科系","資訊管理學系"," 企業管理學系"," 統計資訊學系"," 會計學系"}; //宣告字串陣列
    //,"金融與國際企業學系",
    //            " 中國文學系"," 歷史學系"," 哲學系","新聞傳播學系","影像傳播學系"," 廣告傳播學系"," 法律學系"," 法律學系"," 財經法律學系"," 織品服裝學系",
    //            " 食品科學系"," 兒童與家庭學系"," 餐旅管理學系"," 營養科學系"

    private ArrayAdapter<String> listAdapter; //宣告listAdapter物件
    private ArrayAdapter<String> listAdapter2; //宣告listAdapter物件

    int t,request;
    private String student_registrationToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);


        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        backBtn = (ImageButton) findViewById(R.id.backIBtn);

        editTextName = (EditText) findViewById(R.id.input_name);
        editTextId = (EditText)findViewById(R.id.editTextId);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword1 = (EditText) findViewById(R.id.editTextPassword1);
        editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle.getInt("request") == 0){
            request = 0;
        }
        if(bundle.getInt("request") == 1){
            request =1;
        }


        buttonRegister.setOnClickListener(this);
        backBtn.setOnClickListener(view -> {
            Intent i = new Intent();
            if (request == 0){
                i.setClass(this,WelcomePage.class);
            }
            if (request == 1){
                i.setClass(this,SignIn.class);
            }
            startActivity(i);
            finish();
        });

        editTextPassword1.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Drawable d = getResources().getDrawable(R.drawable.baseline_check_circle_24px);
                    d.setBounds(0, 0, 70, 70);
                    editTextPassword1.setCompoundDrawables(null,null,d,null);
                    //d.setBounds(0, 0, 10, 30); //必须设置大小，否则不显示
                    //editTextPassword2.setError("密碼確認成功", d);
                    t = 0;
                    // 此处为得到焦点时的处理内容
                } else {
                    String password1 = editTextPassword1.getText().toString().trim();
                    if(password1.length() < 6){
                        //length of password1  is small than 6
                        //Drawable d = getResources().getDrawable(R.drawable.baseline_cancel_24px);
                        //d.setBounds(0, 0, 40, 40);
                        // editTextPassword1.setCompoundDrawables(null,null,d,null);

                        //Toast.makeText(this,"密碼確認有誤",Toast.LENGTH_SHORT).show();
                        //Drawable d = getResources().getDrawable(R.drawable.baseline_cancel_24px);
                        //d.setBounds(0, 0, 10, 30); //必须设置大小，否则不显示
                        editTextPassword1.setError("密碼至少六碼");
                        //stopping the function execution further
                        t = 1;
                        return;
                    }else{
                        t = 0;
                    }

                    // 此处为失去焦点时的处理内容
                }
            }
        });

        editTextPassword2.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Drawable d = getResources().getDrawable(R.drawable.baseline_check_circle_24px);
                    d.setBounds(0, 0, 70, 70);
                    editTextPassword2.setCompoundDrawables(null,null,d,null);
                    //d.setBounds(0, 0, 10, 30); //必须设置大小，否则不显示
                    //editTextPassword2.setError("密碼確認成功", d);
                    t = 0;
                    // 此处为得到焦点时的处理内容
                } else {
                    String password1 = editTextPassword1.getText().toString().trim();
                    String password2 = editTextPassword2.getText().toString().trim();
                    if (!password1.equals(password2)){
                        // Drawable d = getResources().getDrawable(R.drawable.baseline_cancel_24px);
                        // d.setBounds(0, 0, 40, 40);
                        //editTextPassword2.setCompoundDrawables(null,null,d,null);

                        //Toast.makeText(this,"密碼確認有誤",Toast.LENGTH_SHORT).show();
                        //Drawable d = getResources().getDrawable(R.drawable.baseline_cancel_24px);
                        //d.setBounds(0, 0, 10, 30); //必须设置大小，否则不显示
                        editTextPassword2.setError("密碼確認錯誤");
                        t = 1;
                        return ;

                    }
                    else if(TextUtils.isEmpty(password2)){
                        //Drawable d = getResources().getDrawable(R.drawable.baseline_cancel_24px);
                        //d.setBounds(0, 0, 40, 40);
                        // editTextPassword2.setCompoundDrawables(null,null,d,null);

                        //Toast.makeText(this,"密碼確認有誤",Toast.LENGTH_SHORT).show();
                        //Drawable d = getResources().getDrawable(R.drawable.baseline_cancel_24px);
                        //d.setBounds(0, 0, 10, 30); //必须设置大小，否则不显示
                        editTextPassword2.setError("請再次確認密碼");
                        t = 1;
                        return ;
                    }else {
                        t = 0;
                    }
                    // 此处为失去焦点时的处理内容
                }
            }
        });

        spinner1=(Spinner)findViewById(R.id.spinner_school); //指向畫面上id為changetype1的Spinner物件
        spinner2=(Spinner)findViewById(R.id.spinner_department); //指向畫面上id為changetype1的Spinner物件
        listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, school);
        listAdapter2 = new ArrayAdapter<String>(this, R.layout.myspinner, department);

        listAdapter.setDropDownViewResource(R.layout.myspinner_dropdown);
        listAdapter2.setDropDownViewResource(R.layout.myspinner_dropdown);

        spinner1.setAdapter(listAdapter);
        spinner2.setAdapter(listAdapter2);
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password1 = editTextPassword1.getText().toString().trim();
        String password2 = editTextPassword2.getText().toString().trim();

        final String school = spinner1.getSelectedItem().toString();
        final String department = spinner2.getSelectedItem().toString();
        String [] uriEmailArray = email.split("@");
        String uriEmail = uriEmailArray[0];



        if(TextUtils.isEmpty(name)) {
            //name1 is empty
            Toast.makeText(this, "請輸入名字", Toast.LENGTH_SHORT).show();
            t = 1;
            //stopping the function execution further
            return;
        }else{
            t = 0;
        }
        Log.e("test1",Integer.toString(t));

        if(school == "請選擇學校") {
            //school is empty
            Toast.makeText(this, "請選擇學校", Toast.LENGTH_SHORT).show();
            t = 1;
            //stopping the function execution further
            return;
        }else {
            t = 0;
        }
        Log.e("test3",Integer.toString(t));
        if(department == "請選擇科系") {
            //department is empty
            Toast.makeText(this, "請選擇系所", Toast.LENGTH_SHORT).show();
            t = 1;
            //stopping the function execution further
            return;
        }else {
            t = 0;
        }
        Log.e("test4",Integer.toString(t));

        if(TextUtils.isEmpty(id)) {
            //id is empty
            Toast.makeText(this, "請輸入學號", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            t = 1;
            return;
        }else {
            t = 0;
        }
        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "請輸入email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            t = 1;
            return;
        }else if (isVaildEmailFormat() != true){
            Toast.makeText(this,"請輸入email格式",Toast.LENGTH_SHORT).show();
            t = 1;
            return;

        }else if(!uriEmail.equals(id)  ){
            Toast.makeText(this,"請填有學號的學校信箱",Toast.LENGTH_LONG).show();
            t = 1;
            return;
        }else{
            t = 0;
        }


        Log.e("test5",Integer.toString(t));

        if(TextUtils.isEmpty(password1)) {
            //password1 is empty
            Toast.makeText(this, "請輸入密碼", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            t = 1;
            return;
        }
        else if(password1.length() < 6){
            //length of password1  is small than 6
            Toast.makeText(this,"密碼請輸入至少六碼",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            t = 1;
            return;
        }else{
            t = 0;
        }
        Log.e("test6",Integer.toString(t));

        if(TextUtils.isEmpty(password2)) {
            //password2 is empty
            Toast.makeText(this, "請再次輸入密碼", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            t = 1;

            return;
        }
        else if (!password1.equals(password2)){

            Toast.makeText(this,"密碼確認有誤",Toast.LENGTH_SHORT).show();
            t = 1;
            return ;

        }else {
            t = 0;
        }
        Log.e("test7",Integer.toString(t));


        //if validations are ok
        //we will first show a progressbar
        if(t == 0) {
            LayoutInflater lf = (LayoutInflater) S_SignUp.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup vg = (ViewGroup) lf.inflate(R.layout.dialog_sign_up,null);
            img_pgbar = (ImageView)vg.findViewById(R.id.img_pgbar);
            ad = (AnimationDrawable)img_pgbar.getDrawable();
            ad.start();
            android.app.AlertDialog.Builder builder1 = new AlertDialog.Builder(S_SignUp.this);
            builder1.setView(vg);
            AlertDialog dialog = builder1.create();
            dialog.show();
            Log.e("work","go");

            //progressDialog.setMessage("Registering User...");
            //progressDialog.show();
            firebaseAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        student_registrationToken = FirebaseInstanceId.getInstance().getToken();

                        Map<String, Object> user = new HashMap<>();
                        user.put("student_name", name);
                        user.put("student_id",id);
                        user.put("student_email", email);
                        user.put("student_school", school);
                        user.put("student_department", department);
                        user.put("student_registrationToken",student_registrationToken);


                        db.collection("Student").add(user).addOnCompleteListener(taskToAddUser -> {
                            Toast.makeText(S_SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();


                        });
                        Intent i = new Intent();
                        i.setClass(S_SignUp.this,LeadingPage.class);
                        startActivity(i);
                    }
                    else{
                        dialog.dismiss();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(S_SignUp.this, "註冊失敗",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

//            firebaseAuth.createUserWithEmailAndPassword(email,password1).addOnSuccessListener(authResult -> {
//                student_registrationToken = FirebaseInstanceId.getInstance().getToken();
//
//                Map<String, Object> user = new HashMap<>();
//                user.put("student_name", name);
//                user.put("student_id",id);
//                user.put("student_email", email);
//                user.put("student_school", school);
//                user.put("student_department", department);
//                user.put("student_registrationToken",student_registrationToken);
//
//
//                db.collection("Student").add(user).addOnCompleteListener(task -> {
//                    Toast.makeText(S_SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    finish();
//
//
//                });
//                Intent i = new Intent();
//                i.setClass(S_SignUp.this,LeadingPage.class);
//                startActivity(i);
//            });
        }
    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            registerUser();
        }
    }

    private  boolean isVaildEmailFormat() {
        EditText etMail = (EditText) findViewById(R.id.editTextEmail);
        if (etMail == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(etMail.getText().toString()).matches();
    }
}