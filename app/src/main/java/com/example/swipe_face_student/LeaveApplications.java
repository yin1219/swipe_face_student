package com.example.swipe_face_student;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Leave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
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
    private String student_id = "405401217";
    private String student_name;
    private String class_id;
    private String teacher_email;
    private ArrayList<String> class_idList;


    private Spinner spinner_leave_class;
    private Spinner spinner_leave_reason;
    private static TextView text_leave_date;
    private EditText edittext_leave_content;
    private Button btn_leave_date;
    private Button btn_upload_leave_photo;
    private Button btn_leave_cancel;
    private Button btn_leave_apply;
    private SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd");
    private StringBuffer date;
    private ArrayList<String> classList;
    private ImageView img_leave_photo;
    private Context context;

    private final int PICK_IMAGE_REQUEST = 71;


    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_applications);
        context = this;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        spinner_leave_class = (Spinner) findViewById(R.id.spinner_leave_class);
        spinner_leave_reason = (Spinner) findViewById(R.id.spinner_leave_reason);
        text_leave_date = (TextView) findViewById(R.id.text_leave_date);
        edittext_leave_content = (EditText) findViewById(R.id.edittext_leave_content);
        btn_upload_leave_photo = (Button) findViewById(R.id.btn_upload_leave_photo);
        btn_leave_date = (Button) findViewById(R.id.btn_leave_date);
        btn_leave_apply = (Button) findViewById(R.id.btn_leave_apply);
        btn_leave_cancel = (Button) findViewById(R.id.btn_leave_cancel) ;
        img_leave_photo = (ImageView) findViewById(R.id.img_leave_photo);

        ArrayAdapter<CharSequence> leave_reasonList = ArrayAdapter.createFromResource(this,
                R.array.leave_reason,
                android.R.layout.simple_spinner_dropdown_item);
        spinner_leave_reason.setAdapter(leave_reasonList);

        class_idList = new ArrayList<>();
        classList = new ArrayList<>();
        class_idList.add("NULL");

//
        getClassList();
        classList.add("--請選擇課程--");

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
                apply();
                Log.d(TAG, "leave.getTeacher_email onClick:" + leave.getTeacher_email());
            }
        });

        btn_leave_cancel.setOnClickListener(new View.OnClickListener() {
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
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img_leave_photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateTime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
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
                String dateTime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
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

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SelectPicture"), PICK_IMAGE_REQUEST);

    }


    private void apply() {

        setStudent_name();
        setTeacher_email(class_idList.get(spinner_leave_class.getSelectedItemPosition()));

        final String leave_content = edittext_leave_content.getText().toString();
        final String leave_date = text_leave_date.getText().toString();
        final String leave_reason = spinner_leave_reason.getSelectedItem().toString();
        final String leave_class = spinner_leave_class.getSelectedItem().toString();
        final String student_name = leave.getStudent_name();
        final String leave_check = "核准中";
        final String class_id = class_idList.get(spinner_leave_class.getSelectedItemPosition());
        final String student_id = this.student_id;
        final String teacher_email = leave.getTeacher_email();
        final Date leave_uploaddate = new Date();
        final String leave_photoUrl = UUID.randomUUID().toString();


        db = FirebaseFirestore.getInstance();


        leave.setClass_id(student_id);
        leave.setLeave_check(leave_check);
        leave.setLeave_content(leave_content);
        leave.setLeave_date(leave_date);
        leave.setLeave_reason(leave_reason);
        leave.setClass_name(leave_class);
        leave.setClass_id(class_id);
        leave.setLeave_uploaddate(leave_uploaddate);
        leave.setStudent_id(student_id);
        leave.setLeave_photoUrl(leave_photoUrl);

        Log.d(TAG, "leave_photoUrl:" + leave_photoUrl);
        StorageReference ref = storageReference.child("Leave_photo/" + leave_photoUrl);
        ref.putFile(filePath);

        Log.d(TAG, "afterStudent_name" + student_name);
        Log.d(TAG, "afterTeacher_email:" + teacher_email);

        db.collection("Leave").add(leave);

        Toast.makeText(this, "已送出請假申請", Toast.LENGTH_SHORT).show();
        finish();

    }


}
