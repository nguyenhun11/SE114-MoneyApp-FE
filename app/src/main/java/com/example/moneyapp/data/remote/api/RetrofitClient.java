package com.example.moneyapp.data.remote.api;

import android.content.Context;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.utils.DateConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://nguyenhun11-001-site1.site4future.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = chain -> {
                Request original = chain.request();

                String path = original.url().encodedPath();
                if (path.contains("/Auth/login") ||
                        path.contains("/Auth/register") ||
                        path.contains("/Auth/refresh-token")) {
                    return chain.proceed(original);
                }

                Context appContext = context.getApplicationContext();
                String token = PreferenceManager.getInstance(appContext).getToken();

                if (token != null && !token.isEmpty()) {
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body());
                    return chain.proceed(requestBuilder.build());
                }

                return chain.proceed(original);
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .authenticator(new TokenAuthenticator(context))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context1) -> 
                            DateConverter.convertStringToDate(json.getAsString()))
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}