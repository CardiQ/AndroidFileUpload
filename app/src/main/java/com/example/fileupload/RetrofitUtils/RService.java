package com.example.fileupload.RetrofitUtils;

import java.io.File;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RService {

        @Multipart
        @POST("post2")
        Call<ResponseBody> upLoadFile(//？？没用UploadBean
                @Part MultipartBody.Part file);

}
