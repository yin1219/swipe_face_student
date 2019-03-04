package com.example.swipe_face_student;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
在Bt設置點擊後加入
Matisse.from(MainActivity.this)
                        .choose(MimeType.ofAll())//图片类型
                        .countable(false)//true:选中后显示数字;false:选中后显示对号
                        .maxSelectable(9)//可选的最大数
                        .capture(true)//选择照片时，是否显示拍照
                        .captureStrategy(new CaptureStrategy(true, "com.example.kl.home.fileprovider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                        .imageEngine(new GlideEngine())//图片加载引擎
                        .theme(R.style.Matisse_Zhihu)
                        .forResult(REQUEST_CODE_CHOOSE);//REQUEST_CODE_CHOOSE自定義
onActivityResult裡加入
if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            UploadPhoto upload = new UploadPhoto();
            result = Matisse.obtainResult(data);
            upload.uploadMultipart(this,result);
        }
* */

public class UploadPhoto {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//抓現在登入user
    String email = user.getEmail();//抓user.email
    String [] uriEmailArray = email.split("@");
    String uriEmail = uriEmailArray[0];
    OkHttpClient client = new OkHttpClient();
    String url = "http://192.168.0.13:8080/ProjectApi/api/FaceApi/TrainFace/"+uriEmail;
    public void uploadFile(List<String> img) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//setType一定要Multipart
        for (int i = 0; i <img.size() ; i++) {//用迴圈去RUN多選照片
            File file=new File(img.get(i));
            if (file !=null) {
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

            }
        });

    }


}