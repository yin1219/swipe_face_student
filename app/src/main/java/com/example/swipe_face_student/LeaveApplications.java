package com.example.swipe_face_student;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Leave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class LeaveApplications extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseStorage storage;


    private Leave leave = new Leave();
    private String TAG = "LeaveApplications_FLAG";
    private String student_id;
    private String student_name;
    private String class_id;
    private String class_name;
    private String teacher_email;
    private ArrayList<String> class_idList;


    private Spinner spinner_leave_class;
    private Spinner spinner_leave_reason;
    private static TextView text_leave_date;
    private EditText edittext_leave_content;
    private TextView text_name;
    private Button btn_leave_date;
    private Button btn_upload_leave_photo;
    private ImageButton backIBtn;
    private Button btn_leave_apply;
    private SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd");
    private DecimalFormat df = new DecimalFormat("00");
    private StringBuffer date;
    private ArrayList<String> classList;
    private ImageView img_leave_photo;
    private Context context;
    private Boolean isAllClass = true;
    private Boolean isHaveImg = true;
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView img_pgbar;
    private AnimationDrawable ad;

    private String classStr, contentStr;


    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_applications);

        //init currentUser
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentFirebaseUser.getEmail();
        String[] currentUserIdToStringList = currentFirebaseUser.getEmail().split("@");
        student_id = currentUserIdToStringList[0];
        Log.d(TAG, "currentUserId: " + student_id);

        Bundle formLeaveList = getIntent().getExtras();
        isAllClass = formLeaveList.getBoolean("isAllClass");
        class_id = formLeaveList.getString("class_id");
        Log.d(TAG, "isAllClass:" + isAllClass.toString());
        Log.d(TAG, "class_id:" + class_id.toString());
        context = this;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        spinner_leave_class = (Spinner) findViewById(R.id.spinner_leave_class);
        spinner_leave_reason = (Spinner) findViewById(R.id.spinner_leave_reason);
        text_leave_date = (TextView) findViewById(R.id.text_leave_date);
        text_name = findViewById(R.id.text_name);
        edittext_leave_content = (EditText) findViewById(R.id.edittext_leave_content);
        btn_upload_leave_photo = (Button) findViewById(R.id.btn_upload_leave_photo);
        btn_leave_date = (Button) findViewById(R.id.btn_leave_date);
        btn_leave_apply = (Button) findViewById(R.id.btn_leave_apply);
        backIBtn = (ImageButton) findViewById(R.id.backIBtn);
        img_leave_photo = (ImageView) findViewById(R.id.img_leave_photo);


        ArrayAdapter<CharSequence> leave_reasonList = ArrayAdapter.createFromResource(this,
                R.array.leave_reason,
                android.R.layout.simple_spinner_dropdown_item);
        spinner_leave_reason.setAdapter(leave_reasonList);

        class_idList = new ArrayList<>();
        classList = new ArrayList<>();
        class_idList.add("NULL");

//        classStr = spinner_leave_class.getSelectedItem().toString();
//        contentStr = edittext_leave_content.getText().toString();


//
//        getClassList();
//        classList.add("--請選擇課程--");

        if (isAllClass) {
            getClassList();
            classList.add("--請選擇課程--");
            ((ViewManager) text_name.getParent()).removeView(text_name);
            ArrayAdapter<String> leave_classList = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, classList);
            spinner_leave_class.setAdapter(leave_classList);

            spinner_leave_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setTeacher_email(class_idList.get(spinner_leave_class.getSelectedItemPosition()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            ((ViewManager) spinner_leave_class.getParent()).removeView(spinner_leave_class);
            setClassName(class_id);
            setTeacher_email(class_id);
        }


        setStudent_name();

        Log.d(TAG, "leave.setStudent_name out get:" + leave.getStudent_name());

        initDate();
        date = new StringBuffer();

        btn_leave_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker(v);
            }

        });
        btn_upload_leave_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }

        });

        Log.d(TAG, "Date:" + text_leave_date.getText().toString());

        btn_leave_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllClass) {
                    if (spinner_leave_class.getSelectedItem().toString().equals("--請選擇課程--")) {
                        Toast.makeText(LeaveApplications.this, "請選擇課程", Toast.LENGTH_LONG).show();
                    } else {
                        apply();
                        Log.d(TAG, "leave.getTeacher_email onClick:" + leave.getTeacher_email());

                    }
                } else {
                    apply();
                    Log.d(TAG, "leave.getTeacher_email onClick:" + leave.getTeacher_email());
                }
            }
        });

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            String result = Matisse.obtainPathResult(data).get(0);
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img_leave_photo.setImageBitmap(bitmap);
                isHaveImg = true;
                Log.d(TAG, "isHaveImg =true;");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isHaveImg = false;
            Log.d(TAG, "isHaveImg =false;");

        }
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        String str_month = df.format(month + 1);
        String str_day = df.format(day);

//        String dateTime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
        String dateTime = String.valueOf(year) + "/" + str_month + "/" + str_day;

        text_leave_date.setText(dateTime);

    }

    public void datePicker(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String str_month = df.format(month + 1);
                String str_day = df.format(day);


                String dateTime = String.valueOf(year) + "/" + str_month + "/" + str_day;
                text_leave_date.setText(dateTime);
                Log.d(TAG, "Date inin:" + text_leave_date.getText().toString());
            }

        }, year, month, day).show();

        Log.d(TAG, "Date in:" + text_leave_date.getText().toString());
    }

    private void getClassList() {
        Query queryStudent_id = db.collection("Student").whereEqualTo("student_id", student_id);
        queryStudent_id.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if (!document.isEmpty()) {
                        class_idList.addAll((ArrayList<String>) document.getDocuments().get(0).get("class_id"));
                        Log.d(TAG, "class_id: " + class_idList);
                        for (String class_id : class_idList) {
                            db.collection("Class")
                                    .whereEqualTo("class_id", class_id)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    classList.add(document.get("class_name").toString());
                                                    Log.d(TAG, "class_id: " + class_id);
                                                    Log.d(TAG, "class_name: " + classList);

                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }


        });

    }

    private void setClassName(String class_id) {
        db = FirebaseFirestore.getInstance();
        db.collection("Class").whereEqualTo("class_id", class_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                class_name = document.get("class_name").toString();
                                text_name.setText(class_name);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void setStudent_name() {

        db = FirebaseFirestore.getInstance();
        db.collection("Student").whereEqualTo("student_id", student_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                student_name = document.get("student_name").toString();
                                leave.setStudent_name(student_name);
                                Log.d(TAG, "leave.setStudent_name in get:" + leave.getStudent_name());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void setTeacher_email(String class_id) {

        db = FirebaseFirestore.getInstance();
        db.collection("Class").whereEqualTo("class_id", class_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                teacher_email = document.get("teacher_email").toString();
                                leave.setTeacher_email(teacher_email);
                                Log.d(TAG, "leave.getTeacher_email in get:" + leave.getTeacher_email());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void chooseImage() {
        Matisse.from(LeaveApplications.this)
                .choose(MimeType.ofAll())//图片类型
                .countable(false)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(1)//可选的最大数
                .capture(true)//选择照片时，是否显示拍照
                .captureStrategy(new CaptureStrategy(true, "com.example.swipe_face_student.fileprovider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .imageEngine(new MyGlideEngine())//图片加载引擎
                .theme(R.style.Matisse_Zhihu)
                .forResult(PICK_IMAGE_REQUEST);//REQUEST_CODE_CHOOSE自定義
    }


    private void apply() {

        setStudent_name();
        if (isAllClass) {
            setTeacher_email(class_idList.get(spinner_leave_class.getSelectedItemPosition()));
        } else {
            Log.d(TAG, "class_id in apply() :" + class_id);
            setTeacher_email(class_id);
        }
        final String leave_content = edittext_leave_content.getText().toString();
        final String leave_date = text_leave_date.getText().toString();
        final String leave_reason = spinner_leave_reason.getSelectedItem().toString();
        final String leave_class;
        if (isAllClass) {
            leave_class = spinner_leave_class.getSelectedItem().toString();
        } else {
            Log.d(TAG, "class_id in apply() :" + class_id);
            leave_class = class_name;
        }

        final String student_name = leave.getStudent_name();
        final String leave_check = "未審核";
        final String apply_class_id;
        if (isAllClass) {
            apply_class_id = class_idList.get(spinner_leave_class.getSelectedItemPosition());
        } else {
            Log.d(TAG, "class_id in apply() :" + class_id);
            apply_class_id = class_id;
        }
        final String student_id = this.student_id;
        final String teacher_email = leave.getTeacher_email();
        final Date leave_uploaddate = new Date();
        final String student_registrationToken = FirebaseInstanceId.getInstance().getToken();
        final String leave_photoUrl = UUID.randomUUID().toString();

        Log.d("FCMToken LeaveApp", "token " + FirebaseInstanceId.getInstance().getToken());

        db = FirebaseFirestore.getInstance();


        leave.setLeave_check(leave_check);
        leave.setLeave_content(leave_content);
        leave.setLeave_date(leave_date);
        leave.setLeave_reason(leave_reason);
        leave.setClass_name(leave_class);
        leave.setClass_id(apply_class_id);
        leave.setLeave_uploaddate(leave_uploaddate);
        leave.setStudent_id(student_id);
        leave.setStudent_registrationToken(student_registrationToken);
        LayoutInflater lf = (LayoutInflater) LeaveApplications.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup vg = (ViewGroup) lf.inflate(R.layout.dialog_leave_applications, null);
        img_pgbar = (ImageView) vg.findViewById(R.id.img_pgbar);
        ad = (AnimationDrawable) img_pgbar.getDrawable();
        ad.start();
        android.app.AlertDialog.Builder builder1 = new AlertDialog.Builder(LeaveApplications.this);
        builder1.setView(vg);
        AlertDialog dialog = builder1.create();
        dialog.show();
        if (filePath != null) {
            leave.setLeave_photoUrl(leave_photoUrl);

            Log.d(TAG, "leave_photoUrl:" + leave_photoUrl);
            StorageReference ref = storageReference.child("Leave_photo/" + leave_photoUrl);
            ref.putFile(filePath);
        }
        Log.d(TAG, "afterStudent_name" + student_name);
        Log.d(TAG, "afterTeacher_email:" + teacher_email);

        db.collection("Leave").add(leave).addOnCompleteListener(task -> {
            dialog.dismiss();
            Toast.makeText(this, "已送出請假申請", Toast.LENGTH_SHORT).show();
            finish();
        });


    }


}
