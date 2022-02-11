package com.proteam.showroomaudit.views.utility;

import com.proteam.showroomaudit.views.activities.ApiResponce;
import com.proteam.showroomaudit.views.activities.AuditiinfofromDatabase;
import com.proteam.showroomaudit.views.activities.Auditimformation;
import com.proteam.showroomaudit.views.activities.Checkmodel;
import com.proteam.showroomaudit.views.activities.ServerResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AuditApi {


    @Headers("Content-Type:application/json")
    @POST("Audit_file_upload/insertDataToTable/")
    Call<ApiResponce> validateaudit( @Body Auditimformation auditinfo);

    @Multipart
    @POST("api/Audit_file_upload")
    Call<ApiResponce> fileupload1(@Part MultipartBody.Part file);

    @Headers("Content-Type:application/json")
    @POST("Audit_file_upload/itemlist")
    Call<ApiResponce> validatedatabase(@Body AuditiinfofromDatabase audit);


    @Headers("Content-Type:application/json")
    @POST("Audit_file_upload/itemlistdelete")
    Call<ApiResponce> validatecheck(@Body Checkmodel model);

    @Multipart
    @POST("api/Audit_file_upload")
    Call<ServerResponse> uploadFile(@Part MultipartBody.Part file);
}
