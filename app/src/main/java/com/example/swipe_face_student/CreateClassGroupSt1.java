package com.example.swipe_face_student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe_face_student.Model.Group;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    String groupCreateTime;//課程創建時間
    Integer classStuNum;//課程學生人數
    Integer groupNum;//課程小組數
    Integer groupNumHigh;//課程小組人數上限
    Integer groupNumLow;//課程小組人數下限
    TextView tvClassName;//課程名TV
    TextView tvGroupNum;//人數限制TV
    TextView tvCreateTime;//結束時間TV
    Button btGroupDividerByhand;//手動分組按鈕
    Button btGroupDividerByCam;//拍照分組按鈕
    String studentIdFromUpload;//接收回傳JSON的studentId
    List<String> listStudentIdFromUpload = new ArrayList<>();
    List<String> photoPathToUploadClass;//接收Matisse的路徑
    OkHttpClient client = new OkHttpClient();
    ResponseBody responseBody;
    String responseData;
    final int REQUEST_CODE_CHOOSE = 123;
    String url = "http://192.168.1.137:8080/ProjectApi/api/FaceApi/RetrievePhoto";
    boolean isGroup = false;
    SimpleDateFormat sdf = new SimpleDateFormat("E yyyy/MM/dd");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createclass_group_st1);


        //init db
        db = FirebaseFirestore.getInstance();

        //init xml
        tvClassName = findViewById(R.id.textViewClassName);
        tvGroupNum = findViewById(R.id.tvGroupNum);
        tvCreateTime = findViewById(R.id.tvCreateTime);
        btGroupDividerByhand = findViewById(R.id.group_divider_byhand);
        btGroupDividerByCam = findViewById(R.id.group_divider_byCam);

        //init Intent
        Intent Intent = getIntent();
        Bundle bundle = Intent.getExtras();
        class_Id = bundle.getString("class_Id");
        classId = bundle.getString("classId");
        classYear = bundle.getString("classYear");
        className = bundle.getString("className");
        classStuNum = bundle.getInt("classStuNum");
        groupNum = bundle.getInt("groupNum");
        groupNumHigh = bundle.getInt("groupNumHigh");
        groupNumLow = bundle.getInt("groupNumLow");
        groupCreateTime =bundle.getString("groupCreateTime");

        //set TextView
        tvClassName.setText(String.format("%s\t%s", classYear, className));
        tvGroupNum.setText("人數限制\t" + groupNumLow.toString() + "\t~\t" + groupNumHigh.toString());
        tvCreateTime.setText("結束時間 : "+groupCreateTime);

        //check isGroup
        db.collection("Class")
                .document(classId)
                .collection("Group")
                .whereArrayContains("student_id", "405401217")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            if(group.getStudent_id().contains("405401217")){
                                isGroup = true;
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        //Button
        btGroupDividerByhand.setOnClickListener(v -> {
            if(!isGroup){
                Intent intentCreateClassGroupByHand = new Intent();
                intentCreateClassGroupByHand.setClass(this, CreateClassGroupByHand.class);
                Bundle bundleGroup = new Bundle();
                bundleGroup.putString("class_Id", class_Id);
                bundleGroup.putString("classId", classId);
                bundleGroup.putString("classYear", classYear);
                bundleGroup.putString("className", className);
                bundleGroup.putInt("classStuNum", classStuNum);
                bundleGroup.putInt("groupNum", groupNum);
                bundleGroup.putInt("groupNumHigh", groupNumHigh);
                bundleGroup.putInt("groupNumLow", groupNumLow);
                intentCreateClassGroupByHand.putExtras(bundleGroup);
                startActivity(intentCreateClassGroupByHand);
            }
            else{
                Toast.makeText(CreateClassGroupSt1.this, "已分組", Toast.LENGTH_SHORT).show();
            }

        });
        btGroupDividerByCam.setOnClickListener(v -> {
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
            photoPathToUploadClass = Matisse.obtainPathResult(data);
        }
        if (!photoPathToUploadClass.isEmpty()) {
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
                    parseJsonWithJsonObject(response);
                }
            });

        }

    }

    //抓JSON內容後Intent
    private void getResultIntent(List<String> listStudentIdFromUpload) {
        Intent intentCreateClassGroupByHand = new Intent();
        intentCreateClassGroupByHand.setClass(this, CreateClassGroupByCam.class);
        Bundle bundleGroup = new Bundle();
        bundleGroup.putString("class_Id", class_Id);
        bundleGroup.putString("classId", classId);
        bundleGroup.putString("classYear", classYear);
        bundleGroup.putString("className", className);
        bundleGroup.putInt("classStuNum", classStuNum);
        bundleGroup.putInt("groupNum", groupNum);
        bundleGroup.putInt("groupNumHigh", groupNumHigh);
        bundleGroup.putInt("groupNumLow", groupNumLow);
        bundleGroup.putString("photoPathToUploadClass",photoPathToUploadClass.get(0));
        bundleGroup.putStringArrayList("listStudentIdFromUpload", (ArrayList<String>) listStudentIdFromUpload);
        intentCreateClassGroupByHand.putExtras(bundleGroup);
        startActivity(intentCreateClassGroupByHand);
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
                listStudentIdFromUpload.add(studentIdFromUpload);
            }
            getResultIntent(listStudentIdFromUpload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

