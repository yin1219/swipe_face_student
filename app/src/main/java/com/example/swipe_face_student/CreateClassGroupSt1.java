package com.example.swipe_face_student;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Class;
import com.example.swipe_face_student.Model.Group;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CreateClassGroupSt1 extends AppCompatActivity {
    final String TAG = "CreateClassGroupSt1";
    FirebaseFirestore db;
    String classId;//課程DocId
    String class_Id;//課程Id
    String classYear;//課程學年度
    String className;//課程名
    Date groupCreateTime;//課程創建時間
    Integer classStuNum;//課程學生人數
    Integer groupNum;//課程小組數
    Integer groupNumHigh;//課程小組人數上限
    Integer groupNumLow;//課程小組人數下限
    TextView tvClassName;//課程名TV
    TextView tvGroupNum;//人數限制TV
    TextView tvCreateTime;//結束時間TV
    CardView cvGroupDividerByhand;//手動分組按鈕
    CardView cvGroupDividerByCam;//拍照分組按鈕
    String studentIdFromUpload;//接收回傳JSON的studentId
    String student_id;
    List<String> listStudentIdFromUpload = new ArrayList<>();
    List<String> photoPathToUploadClass;//接收Matisse的路徑
    ImageButton backIBtn;
    ImageView img_pgbar;
    AnimationDrawable ad;
    OkHttpClient client = new OkHttpClient();
    ResponseBody responseBody;
    String responseData;
    final int REQUEST_CODE_CHOOSE = 123;
    String url = "http://"+FlassSetting.getIp()+":8080/ProjectApi/api/FaceApi/RetrievePhoto";
    boolean isGroup = false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createclass_group_st1);


        //init db
        db = FirebaseFirestore.getInstance();

        //init currentUser
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentFirebaseUser.getEmail();
        String[] currentUserIdToStringList = currentFirebaseUser.getEmail().split("@");
        student_id = currentUserIdToStringList[0];
        Log.d(TAG,"currentUserId: "+student_id);

        //init xml
        tvClassName = findViewById(R.id.textViewClassName);
        tvGroupNum = findViewById(R.id.tvGroupNum);
        tvCreateTime = findViewById(R.id.tvCreateTime);
        cvGroupDividerByhand = findViewById(R.id.group_divider_byhand);
        cvGroupDividerByCam = findViewById(R.id.group_divider_byCam);
        backIBtn = findViewById(R.id.backIBtn);
        backIBtn.setOnClickListener(v -> finish());

        //init Intent
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
        classId = bundle.getString("classId");

        //query DB
        if(!classId.isEmpty()){
            DocumentReference docRef = db.collection("Class").document(classId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                Class aClass = documentSnapshot.toObject(Class.class);
                class_Id = aClass.getClass_id();
                classYear = aClass.getClass_year();
                className = aClass.getClass_name();
                classStuNum = aClass.getStudent_total();
                groupNum = aClass.getGroup_num();
                groupNumHigh = aClass.getGroup_numHigh();
                groupNumLow = aClass.getGroup_numLow();
                groupCreateTime = aClass.getCreate_time();
                Log.d(TAG,aClass.toString());

                //set TextView
                tvClassName.setText(String.format("%s\t%s", classYear, className));
                tvGroupNum.setText("人數限制\t" + groupNumLow.toString() + "\t~\t" + groupNumHigh.toString());
                tvCreateTime.setText("結束時間 : "+sdf.format(groupCreateTime));
            });
        }


        //check isGroup
        db.collection("Class")
                .document(classId)
                .collection("Group")
                .whereArrayContains("student_id", student_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            if(group.getStudent_id().contains(student_id)){
                                isGroup = true;
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        //Button
        cvGroupDividerByhand.setOnClickListener(v -> {
            if(!isGroup){
                Intent intentCreateClassGroupByHand = new Intent();
                intentCreateClassGroupByHand.setClass(this, CreateClassGroupByHand.class);
                Bundle bundleGroup = new Bundle();
                bundleGroup.putString("classId", classId);
                intentCreateClassGroupByHand.putExtras(bundleGroup);
                startActivity(intentCreateClassGroupByHand);
            }
            else{
                Toast.makeText(CreateClassGroupSt1.this, "已分組", Toast.LENGTH_SHORT).show();
            }

        });
        cvGroupDividerByCam.setOnClickListener(v -> {
            if(!isGroup){
                Matisse.from(CreateClassGroupSt1.this)
                        .choose(MimeType.ofAll())//图片类型
                        .countable(false)//true:选中后显示数字;false:选中后显示对号
                        .maxSelectable(9)//可选的最大数
                        .capture(true)//选择照片时，是否显示拍照
                        .captureStrategy(new CaptureStrategy(true, "com.example.swipe_face_student.fileprovider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                        .imageEngine(new GlideModule())//图片加载引擎
                        .theme(R.style.Matisse_Zhihu)
                        .forResult(REQUEST_CODE_CHOOSE);//REQUEST_CODE_CHOOSE自定義
            }else{
                Toast.makeText(CreateClassGroupSt1.this, "已分組", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            if (null != data) {
                photoPathToUploadClass = Matisse.obtainPathResult(data);
                //LoadingDialog
                LayoutInflater lf = (LayoutInflater) CreateClassGroupSt1.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup vg = (ViewGroup) lf.inflate(R.layout.dialog_retreive, null);
                img_pgbar = vg.findViewById(R.id.img_pgbar);
                ad = (AnimationDrawable) img_pgbar.getDrawable();
                ad.start();
                android.app.AlertDialog.Builder builder1 = new AlertDialog.Builder(CreateClassGroupSt1.this);
                builder1.setView(vg);
                AlertDialog dialog = builder1.create();
                dialog.show();
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//setType一定要Multipart
                for (int i = 0; i < photoPathToUploadClass.size(); i++) {//用迴圈去RUN多選照片
                    File file = new File(photoPathToUploadClass.get(i));
                    if (file != null) {
                        builder.addFormDataPart("photos", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                    }//前面是para  中間是抓圖片名字 後面是創一個要求
                }


                MultipartBody requestBody = builder.build();//建立要求

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("Create Android", "Test失敗");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("Create Android", "Test成功");
                    dialog.dismiss();
                    parseJsonWithJsonObject(response);
                }
            });

        }

    }

    }

    //解析回傳JSON檔
    private void parseJsonWithJsonObject(Response response) throws IOException {
        responseBody = response.body();
        responseData = responseBody.string();
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                studentIdFromUpload = obj.getString("student_id");
                Log.d(TAG,"studentIdFromUpload"+studentIdFromUpload);
                if(studentIdFromUpload == null){
                    continue;
                }
                listStudentIdFromUpload.add(studentIdFromUpload);
            }
            getResultIntent(listStudentIdFromUpload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //抓JSON內容後Intent
    private void getResultIntent(List<String> listStudentIdFromUpload) {
        if (listStudentIdFromUpload.isEmpty()) {
            ToastUtils.show(this, "辨識失敗 !" + "請再多試試 !");
        } else {
            Intent intentCreateClassGroupByHand = new Intent();
            intentCreateClassGroupByHand.setClass(this, CreateClassGroupByCam.class);
            Bundle bundleGroup = new Bundle();
            bundleGroup.putString("classId", classId);
            bundleGroup.putStringArrayList("listStudentIdFromUpload", (ArrayList<String>) listStudentIdFromUpload);
            intentCreateClassGroupByHand.putExtras(bundleGroup);
            startActivity(intentCreateClassGroupByHand);
        }

    }


}

