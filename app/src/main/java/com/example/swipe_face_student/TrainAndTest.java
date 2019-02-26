package com.example.swipe_face_student;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class TrainAndTest extends AppCompatActivity {

    private Button test , train;
    private int REQUEST_CODE_CHOOSE = 69;
    public List<String> result;
    ResponseBody responseBody;
    String responseData;
    String name,id,email,department,school;
    OkHttpClient client = new OkHttpClient();
    String url = "http://192.168.1.10:8080/ProjectApi/api/FaceApi/RetrievePhoto";
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_and_test);

        TrainAndTestPermissionsDispatcher.StoragePermissionsWithPermissionCheck(this);
        mContext = getApplicationContext();
        train = (Button)findViewById(R.id.buttonTrain);
        test = (Button)findViewById(R.id.buttonTest);
        train.setOnClickListener(v ->{

            Matisse.from(TrainAndTest.this)
                    .choose(MimeType.ofAll())//图片类型
                    .countable(false)//true:选中后显示数字;false:选中后显示对号
                    .maxSelectable(9)//可选的最大数
                    .capture(true)//选择照片时，是否显示拍照
                    .captureStrategy(new CaptureStrategy(true, "com.example.kl.home.fileprovider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                    .imageEngine(new MyGlideEngine())//图片加载引擎
                    .theme(R.style.Matisse_Zhihu)
                    .forResult(REQUEST_CODE_CHOOSE = 6);//REQUEST_CODE_CHOOSE自定義
            Log.i("Create Android", "Test選圖");


        });
        test.setOnClickListener(v ->{
            Matisse.from(TrainAndTest.this)
                    .choose(MimeType.ofAll())//图片类型
                    .countable(false)//true:选中后显示数字;false:选中后显示对号
                    .maxSelectable(9)//可选的最大数
                    .capture(true)//选择照片时，是否显示拍照
                    .captureStrategy(new CaptureStrategy(true, "com.example.kl.home.fileprovider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                    .imageEngine(new MyGlideEngine())//图片加载引擎
                    .theme(R.style.Matisse_Zhihu)
                    .forResult(REQUEST_CODE_CHOOSE = 9);//REQUEST_CODE_CHOOSE自定義
            Log.i("Create Android", "Test選圖");

        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && requestCode == 6) {
            Log.i("Create Android", "Test4");
            Log.d("Matisse", "Uris: " + Matisse.obtainResult(data));
            Log.d("Matisse", "Paths: " + Matisse.obtainPathResult(data));
            Log.e("Matisse", "Use the selected photos with original: " + String.valueOf(Matisse.obtainOriginalState(data)));
            UploadPhoto example = new UploadPhoto();
            result = Matisse.obtainPathResult(data);
            example.uploadFile(result);
            Log.i("Create Android", "Test5");


        }
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && requestCode == 9) {
            Log.i("Create Android", "Test4");
            Log.d("Matisse", "Uris: " + Matisse.obtainResult(data));
            Log.d("Matisse", "Paths: " + Matisse.obtainPathResult(data));
            Log.e("Matisse", "Use the selected photos with original: " + String.valueOf(Matisse.obtainOriginalState(data)));
            TrainAndTest example = new TrainAndTest();
            result = Matisse.obtainPathResult(data);
            example.retrieveFile(result);
            Log.i("Create Android", "Test5");
        }

    }
    public void retrieveFile(List<String> img) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//setType一定要Multipart
        for (int i = 0; i <img.size() ; i++) {//用迴圈去RUN多選照片
            File file=new File(img.get(i));
            if (file !=null) {
                builder.addFormDataPart("photos", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            }//前面是para  中間是抓圖片名字 後面是創一個要求
        }

        MultipartBody requestBody = builder.build();//建立要求

        okhttp3.Request request = new okhttp3.Request.Builder()
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
    public static Context getmContext(){
        return mContext;
    }

    private void parseJsonWithJsonObject(Response response) throws IOException {
        int turn = 0;
        responseBody =response.body();
        responseData = responseBody.string();
        try{
            JSONArray jsonArray=new JSONArray(responseData);
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject obj=jsonArray.getJSONObject(i);
                /*Hero hero = new Hero(
                        obj.getString("student_name"),
                        obj.getString("student_id"),
                        obj.getString("student_email"),
                        obj.getString("student_department"),
                        obj.getString("student_school")

                );*/
                name = obj.getString("student_name");
                id = obj.getString("student_id");
                email =        obj.getString("student_email");
                department =        obj.getString("student_department");
                school =        obj.getString("student_school");

                Log.i("name",name);
                Log.i("id",id);
                Log.i("email",email);
                Log.i("department",department);
                Log.i("school",school);
                ToastUtils.show(getmContext(),"名字:"+name+"\n"+"學號: "+id+"\n"+"email:"+email+"\n"+"系所:"+department+"\n"+"學校:"+school);
                turn += 1;

                //heroList.add(hero);


            }

            //adapter = new HeroAdapter(heroList, getmContext());

            //get_recyclerview().setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (turn != 0 && name != null){
            Intent i = new Intent();
            i.setClass(TrainAndTest.this,MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TrainAndTestPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void StoragePermissions() {
    }
}
