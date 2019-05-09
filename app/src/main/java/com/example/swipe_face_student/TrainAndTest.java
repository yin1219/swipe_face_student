package com.example.swipe_face_student;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import static android.app.Activity.RESULT_OK;
import static com.zhihu.matisse.internal.utils.PathUtils.getPath;

@RuntimePermissions
public class TrainAndTest extends AppCompatActivity {

    private Button btTestPhoto;
    private Button btTrainPhtot;
    private Button btTakePhoto;
    private int REQUEST_CODE_CHOOSE = 69;
    private int REQUEST_CODE_TEST = 123;
    public List<String> result;
    ResponseBody responseBody;
    String responseData;
    String name, id, email, department, school;
    OkHttpClient client = new OkHttpClient();
    String url = "http://" + FlassSetting.getIp() + ":8080/ProjectApi/api/FaceApi/RetrievePhoto";
    private static Context mContext;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//抓現在登入user
    private FirebaseFirestore db;
    String email1 = user.getEmail();//抓user.email
    String[] uriEmailArray = email1.split("@");
    String uriEmail = uriEmailArray[0];
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_and_test);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        TrainAndTestPermissionsDispatcher.StoragePermissionsWithPermissionCheck(this);
        mContext = getApplicationContext();
        btTrainPhtot = findViewById(R.id.buttonTrain);
        btTestPhoto = findViewById(R.id.buttonTest);
        btTakePhoto = findViewById(R.id.btTakePhoto);
        btTakePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            startActivityForResult(intent, 0);
        });
        btTrainPhtot.setOnClickListener(v -> {

            Matisse.from(TrainAndTest.this)
                    .choose(MimeType.ofAll())//图片类型
                    .countable(false)//true:选中后显示数字;false:选中后显示对号
                    .maxSelectable(9)//可选的最大数
                    .capture(false)//选择照片时，是否显示拍照
                    .captureStrategy(new CaptureStrategy(true, "com.example.swipe_face_student.fileprovider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                    .imageEngine(new MyGlideEngine())//图片加载引擎
                    .theme(R.style.Matisse_Zhihu)
                    .forResult(REQUEST_CODE_CHOOSE);//REQUEST_CODE_CHOOSE自定義
            Log.i("Create Android", "Test選圖");


        });
        btTestPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_TEST);
            Log.i("Create Android", "Test選圖");
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Train Face And Upload Face To DB
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> a;
            a = Matisse.obtainResult(data);


            Uri uri = a.get(0);
            Log.i("uri:", uri.toString());
            String path = getPath(TrainAndTest.this, uri);
            Uri file = Uri.fromFile(new File(path));
            Log.i("Create Android", "Test4");
            Log.d("Matisse", "Uris: " + Matisse.obtainResult(data));
            Log.d("Matisse", "Paths: " + Matisse.obtainPathResult(data));
            Log.e("Matisse", "Use the selected photos with original: " + String.valueOf(Matisse.obtainOriginalState(data)));
            UploadPhoto example = new UploadPhoto();
            result = Matisse.obtainPathResult(data);
            example.uploadFile(result);
            Log.i("Create Android", "Test5");

            //上傳圖片到storage
            StorageReference riversRef = mStorageRef.child("student_photo/" + uriEmail);
            UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(TrainAndTest.this, "上傳圖片失敗 !", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("image_url", riversRef.toString());
                    Log.i("imageUrl", riversRef.toString());

                    //查詢符合使用者學號的docId並寫入圖片的網址
                    Query query = db.collection("Student").whereEqualTo("student_id", uriEmail);
                    query.get().addOnCompleteListener(task -> {
                        QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;

                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            Log.i("documentUrl", documentSnapshot.getId());
                            String docId = documentSnapshot.getId();
                            db.collection("Student").document(docId).update(user);


                        }
                    });

                    Toast.makeText(TrainAndTest.this, "上傳圖片成功", Toast.LENGTH_SHORT).show();
                }
            });


        }
        //Test Face Retrieve
        if (requestCode == REQUEST_CODE_TEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            Log.d("TEST", tempUri.getPath() + tempUri.toString());
            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            TrainAndTest example = new TrainAndTest();
            example.retrieveFile(finalFile.getAbsolutePath());

        }
    }

    public void retrieveFile(String img) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//setType一定要Multipart
        File file = new File(img);
        if (file != null) {
            builder.addFormDataPart("photos", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }//前面是para  中間是抓圖片名字 後面是創一個要求


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

    public static Context getmContext() {
        return mContext;
    }

    private void parseJsonWithJsonObject(Response response) throws IOException {
        responseBody = response.body();
        responseData = responseBody.string();
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                /*Hero hero = new Hero(
                        obj.getString("student_name"),
                        obj.getString("student_id"),
                        obj.getString("student_email"),
                        obj.getString("student_department"),
                        obj.getString("student_school")

                );*/
                name = obj.getString("student_name");
                id = obj.getString("student_id");
                email = obj.getString("student_email");
                department = obj.getString("student_department");
                school = obj.getString("student_school");

                Log.i("name", name);
                Log.i("id", id);
                Log.i("email", email);
                Log.i("department", department);
                Log.i("school", school);


                //ToastUtils.show(getmContext(),"名字:"+name+"\n"+"學號: "+id+"\n"+"email:"+email+"\n"+"系所:"+department+"\n"+"學校:"+school);

                //heroList.add(hero);
            }
            getResultIntent(name,id);

            //adapter = new HeroAdapter(heroList, getmContext());

            //get_recyclerview().setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    //抓JSON內容後Intent
    private void getResultIntent(String name ,String id) {
        if (id.equals(uriEmail)) {
            ToastUtils.show(getmContext(), name + "歡迎使用 !");
            Intent intentCreateClassGroupByHand = new Intent();
            intentCreateClassGroupByHand.setClass(this, MainActivity.class);
            startActivity(intentCreateClassGroupByHand);

        } else {
            ToastUtils.show(getmContext(), "辨識失敗 !" + "請再多試試 !");
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
