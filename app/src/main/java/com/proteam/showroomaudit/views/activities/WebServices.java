package com.proteam.showroomaudit.views.activities;


import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proteam.showroomaudit.views.utility.AuditApi;
import com.proteam.showroomaudit.views.utility.OnResponseListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WebServices<T> {
    T t;
    Call<T> call=null;
    public T getT() {
        return t;
    }

    public void setT(T t) {

        this.t = t;
    }

    ApiType apiTypeVariable;
    Context context;
    OnResponseListener<T> onResponseListner;
    private static OkHttpClient.Builder builder;

    public enum ApiType {
        auditinfoIn,auditdata,uploadfile,checkmodel
    }

    public WebServices(OnResponseListener<T> onResponseListner) {
        this.onResponseListner = onResponseListner;

        if (onResponseListner instanceof Activity) {
            this.context = (Context) onResponseListner;
        } else if (onResponseListner instanceof IntentService) {
            this.context = (Context) onResponseListner;
        } else if (onResponseListner instanceof android.app.DialogFragment) {
            android.app.DialogFragment dialogFragment = (android.app.DialogFragment) onResponseListner;
            this.context = dialogFragment.getActivity();
        }else if (onResponseListner instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) onResponseListner;
            this.context = fragment.getActivity();
        }
        else if (onResponseListner instanceof Adapter) {

            this.context = (Context) onResponseListner;
        }
        else if (onResponseListner instanceof Adapter) {
            this.context = (Context) onResponseListner;
        }
        else {
            //android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) onResponseListner;
            //this.context = fragment.getActivity();
        }

        builder = getHttpClient();
    }

    public WebServices(Context context, OnResponseListener<T> onResponseListner) {
        this.onResponseListner = onResponseListner;
        this.context = context;
        builder = getHttpClient();
    }


    public OkHttpClient.Builder getHttpClient() {

        if (builder == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(10000, TimeUnit.SECONDS);
            client.readTimeout(10000, TimeUnit.SECONDS).build();
            client.addInterceptor(loggingInterceptor);
            /*to pass header information with request*/
            client.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().addHeader("Content-Type", "application/json").build();
                    return chain.proceed(request);
                }
            });

            return client;
        }
        return builder;
    }

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private Retrofit getRetrofitClient(String api)
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(api)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }


    public void fileupload(String api, ApiType apiTypes, String path, String fname) {


        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/vnd.ms-excel"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());


        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient("https://pda.proteam.co.in/en/");

        AuditApi auditApi=retrofit.create(AuditApi.class);

        //call=(Call<T>)auditApi.validateaudit(fileToUpload,audit1,rack1,rack_completed1,total_itemqty1,total_rack1,user_id1,total_untagged1);

        call=(Call<T>)auditApi.fileupload1(fileToUpload);
        //call=auditApi.fileupload1(fileToUpload,filename);

        call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {


                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
                Log.e("debug,", t.toString());
            }
        });

    }


    public void check(String api, ApiType apiTypes, Checkmodel checkmodel)
    {
        https://pda.proteam.co.in/en/api/Audit_file_upload/itemlistdelete
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient("https://pda.proteam.co.in/en/api/");

        AuditApi auditApi=retrofit.create(AuditApi.class);

        //call=(Call<T>)auditApi.validateaudit(fileToUpload,audit1,rack1,rack_completed1,total_itemqty1,total_rack1,user_id1,total_untagged1);

        call=(Call<T>)auditApi.validatecheck(checkmodel);


        call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });

    }

    public void imformationIn(String api, ApiType apiTypes, Auditimformation auditimformation)
    {

        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient("https://pda.proteam.co.in/en/api/");

        AuditApi auditApi=retrofit.create(AuditApi.class);

            //call=(Call<T>)auditApi.validateaudit(fileToUpload,audit1,rack1,rack_completed1,total_itemqty1,total_rack1,user_id1,total_untagged1);

        call=(Call<T>)auditApi.validateaudit(auditimformation);

            call.enqueue(new retrofit2.Callback<T>() {
                @Override
                public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                    t=(T)response.body();
                    onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    onResponseListner.onResponse(null, apiTypeVariable, false,0);
                }
            });

    }

    public void auditDatabase(String api, ApiType apiTypes, AuditiinfofromDatabase auditinfodata)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient("https://pda.proteam.co.in/en/api/");

        AuditApi auditApi=retrofit.create(AuditApi.class);

        call=(Call<T>)auditApi.validatedatabase(auditinfodata);


        call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });

    }

}

