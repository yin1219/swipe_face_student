package com.example.swipe_face_student;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
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


public class FaceEngine {
    final String url = "http://192.168.1.10:8080/ProjectApi/api/FaceApi/";
    Context mContext;
    ResponseBody responseBody;
    String responseData;
    String studentFromApiId;
    final String TAG ="FaceEngine";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//抓現在登入user
    String email = user.getEmail();//抓user.email
    String [] uriEmailArray = email.split("@");
    String uriEmail = uriEmailArray[0];
    OkHttpClient client = new OkHttpClient();
    List<String> studentIdListFromApi = new ArrayList<>();
        class TaineFace{
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
                    .url(url+"TrainFace/"+uriEmail)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("Create Android", "Test失敗");
                    //Toast.makeText(context,"上傳失敗 !",Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("Create Android", "Test成功");
                    //Toast.makeText(context,"上傳成功 !",Toast.LENGTH_SHORT).show();


                }
            });

        }
    }
    class RetrievePhoto{
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
                    .url(url+"RetrievePhoto/")
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("Create Android", "Test失敗");
                    //Toast.makeText(context,"上傳失敗 !",Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("Create Android", "Test成功");
                    responseBody =response.body();
                    responseData = responseBody.string();
                    try{
                        JSONArray jsonArray=new JSONArray(responseData);
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject obj=jsonArray.getJSONObject(i);

                            studentFromApiId = obj.getString("student_id");
                            studentIdListFromApi.add(studentFromApiId);
                            Log.d(TAG,"studentFromApiId"+studentFromApiId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
