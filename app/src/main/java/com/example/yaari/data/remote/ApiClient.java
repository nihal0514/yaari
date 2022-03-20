package com.example.yaari.data.remote;

import com.example.yaari.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
  //  public static String BASE_URL="http://192.168.42.249/yaari/public/";
    public static String BASE_URL="http://192.168.1.44/yaari/public/";
    private static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        //logging interceptor isliye use karte hai ham kyuki data load ho rha hai ya nhi logcat se dekhne ke liye
        //logcat mai search mai okhttp daal dene ka toh saara details aajata hai jo task perform kar rhe hau uska
        //for eg. agar login ke button par click kiye toh user ka saara details aajeyaga
        HttpLoggingInterceptor httpLoggingInterceptor=new HttpLoggingInterceptor();
        if(BuildConfig.DEBUG){
            httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        }else{
            httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        }
        OkHttpClient client=new OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS)             //iska matlab data upload kar rhe hai
                .readTimeout(10,TimeUnit.SECONDS)               //read timeout data load karne ke liye
                .addInterceptor(httpLoggingInterceptor).build();
        if(retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }

}
