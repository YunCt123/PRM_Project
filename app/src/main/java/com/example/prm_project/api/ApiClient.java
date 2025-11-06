package com.example.prm_project.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://be-ev-rental-system-production.up.railway.app/";
    private static Retrofit retrofit;
    private static final String MULTIPART_TAG = "MultipartDebug";

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging interceptor for debugging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Interceptor to log multipart parts (text parts only) for debugging
            Interceptor multipartDebugger = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    RequestBody body = request.body();
                    if (body instanceof MultipartBody) {
                        MultipartBody mb = (MultipartBody) body;
                        for (MultipartBody.Part part : mb.parts()) {
                            try {
                                okhttp3.Headers headers = part.headers();
                                String partInfo = headers != null ? headers.toString() : "(no-headers)";
                                RequestBody rb = part.body();
                                MediaType mt = rb != null ? rb.contentType() : null;
                                if (rb != null && mt != null && ("text".equals(mt.type()) || (mt.subtype() != null && mt.subtype().contains("plain")))) {
                                    Buffer buffer = new Buffer();
                                    rb.writeTo(buffer);
                                    String value = buffer.readUtf8();
                                    Log.d(MULTIPART_TAG, partInfo + " => " + value);
                                } else {
                                    Log.d(MULTIPART_TAG, partInfo + " => <binary/octet or image> (contentType=" + mt + ")");
                                }
                            } catch (Exception ex) {
                                Log.w(MULTIPART_TAG, "Error reading multipart part", ex);
                            }
                        }
                    }

                    return chain.proceed(request);
                }
            };

            // OkHttp client with logging + multipart debugger + TIMEOUTS
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(multipartDebugger)
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .build();

            // Gson configuration
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAuthenticatedClient(Context context) {
        if (authenticatedRetrofit == null) {
            // Logging interceptor for debugging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client builder with auth interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        SessionManager sessionManager = new SessionManager(context);
                        String token = sessionManager.getToken();

                        Request.Builder requestBuilder = original.newBuilder();
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.addHeader("Authorization", "Bearer " + token);
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    })
                    .build();

            // Gson configuration
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            authenticatedRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return authenticatedRetrofit;
    }
}