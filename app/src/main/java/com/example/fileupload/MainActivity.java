package com.example.fileupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.fileupload.RetrofitUtils.RService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    //请求码是唯一值即可
    final Integer REQUEST_CODE = 1;
    RService rService;
    File file;
    Retrofit retrofit;
    String username = "77", password = "123";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // 打开系统的文件选择器
    public void pickFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    // 获取文件的真实路径//Intent { dat=content://com.android.providers.downloads.documents/document/raw:/storage/emulated/0/Download/files/text.txt flg=0x1 }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            // 用户未选择任何文件，直接返回
            return;
        }
        Uri uri = data.getData(); // 获取用户选择文件的URI
        // 通过ContentProvider查询文件路径
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // 未查询到，说明为普通文件，可直接通过URI获取文件路径
            String path = uri.getPath();
            /**
             * 进行上传
             */
            /*File file=new File(path);
            uploadFile(file);*/
            new Thread() {
                @Override
                public void run() {
                    uploadFile(path);
                }
            }.start();
            return;
        }
        if (cursor.moveToFirst()) {
            // 多媒体文件，从数据库中获取文件的真实路径
            int term = cursor.getColumnIndex("_data");
            String path="/storage/self/primary/Download/files/text2.txt";
            //String path = cursor.getString(term >= 0 ? term : 0);
            /**
             * 进行上传
             */
            new Thread() {
                @Override
                public void run() {
                    uploadFile(path);
                }
            }.start();
        }
        cursor.close();
    }

    /*// 使用OkHttp上传文件--有冲突，和retrofit2导入Callback不同
    public void uploadFile(File file) {
        OkHttpClient client = new OkHttpClient();
        MediaType contentType = MediaType.parse("text/plain"); // 上传文件的Content-Type
        RequestBody body = RequestBody.create(contentType, file); // 上传文件的请求体
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw") // 上传地址
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 文件上传成功
                if (response.isSuccessful()) {
                    Log.i("Haoxueren", "onResponse: " + response.body().string());
                } else {
                    Log.i("Haoxueren", "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 文件上传失败
                Log.i("Haoxueren", "onFailure: " + e.getMessage());
            }
        });
    }*/

    //使用retrofit上传文件

    /**
     * 上传文件
     *
     * @param filePathName 文件路径及文件名
     */
    public void uploadFile(String filePathName) {
        // 生成Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.4:8080/test/")//本机网络路径！！至关重要
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 生成Service
        rService = retrofit.create(RService.class);

        // 要上传的文件
        file = new File(filePathName);

        // 执行请求
        RequestBody requestBody=RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part part= MultipartBody.Part.createFormData("file",file.getName(),requestBody);


        Call<ResponseBody> call = rService.upLoadFile(part);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("TAG", "SUCCEEDED");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("TAG", "FAILED");
                t.printStackTrace();
            }
        });
    }
}