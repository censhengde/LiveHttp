package com.tencent.libhttp.http;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.libhttp.http.HttpService.GetRequest;
import com.tencent.libhttp.http.HttpService.PostRequest;
import okhttp3.internal.http2.Header;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author：Shengde·Cen on 2020/11/4 15:50
 * <p>
 * 说明：采用LiveData解决网络数据回调但页面已经不存在的安全隐患
 */
public final class OkHttpEngine implements HttpEngine {

    private final OkHttpClient mClient;
    private static final boolean DEBUG = true;
    private static final String TAG = "OkHttpEngine";

    /**
     * okhttp配置
     */
    public OkHttpEngine() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        // try {
        // SSLContext sslContext;
        // sslContext = SSLContext.getInstance("SSL");
        // sslContext.init(null, trustManager,new SecureRandom());
        // SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        // httpClient.sslSocketFactory(sslSocketFactory, trustManager[0]);
        // httpClient.hostnameVerifier(hostnameVerifier);
        // } catch (GeneralSecurityException e) {
        // throw new RuntimeException(e);
        // }
        mClient = clientBuilder.build();

    }

    @Override
    public void getAsync(@NonNull GetRequest request) {
        final String url = request.url;
        final Map<String, Object> params = request.params;
        final Map<String, String> header = request.header;
        final Object tag = request.tag;
        final HttpChannel channel = request.channel;
        showLog(url);
        final Request okRequest = createGetRequest(url, params, header);
        Call call = mClient.newCall(okRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                final HttpMessage msg = HttpMessage.obtain();
                msg.tag = tag;
                msg.isSucceed = false;
                msg.msg = e.getMessage();
                if (channel != null) {
                    channel.write(msg);
                }

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final HttpMessage msg = HttpMessage.obtain();
                msg.tag = tag;
                if (response.isSuccessful()) {
                    msg.isSucceed = true;
                    if (response.body() != null) {
                        msg.bodyStr = response.body().string();
                    }
                } else {
                    msg.isSucceed = false;
                }
                if (channel != null) {
                    channel.write(msg);
                }

            }
        });
    }

    /**
     * 同步请求
     *
     * @return
     */
    @Override
    public @NonNull
    HttpMessage getSync(@NonNull GetRequest request) {
        final String url = request.url;
        final Map<String, Object> params = request.params;
        final Map<String, String> header = request.header;
        final Object tag = request.tag;
        showLog(url);
        final Request okRequest = createGetRequest(url, params, header);
        final Call call = mClient.newCall(okRequest);
        Response response = null;
        final HttpMessage msg = HttpMessage.obtain();
        msg.tag = tag;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                msg.bodyStr = response.body().string();
                msg.isSucceed = true;
            } else {
                msg.isSucceed = false;
                msg.msg = response.message();
            }

        } catch (Exception e) {
            e.printStackTrace();
            msg.isSucceed = false;
            msg.msg = e.getMessage();
        }
        return msg;
    }

    @Override
    public void postAsync(@NonNull PostRequest request) {
        final String url = request.url;
        final Map<String, Object> params = request.params;
        final Map<String, String> header = request.header;
        final Object tag = request.tag;
        final HttpChannel channel = request.channel;
        showLog(url);
        final Request okRequest = createPostRequest(url, params, header);
        Call call = mClient.newCall(okRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                final HttpMessage msg = HttpMessage.obtain();
                msg.tag = tag;
                msg.msg = e.getMessage();
                msg.isSucceed = false;
                if (channel != null) {
                    channel.write(msg);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                final HttpMessage msg = HttpMessage.obtain();
                msg.tag = tag;
                if (response.isSuccessful()) {
                    try {
                        msg.isSucceed = true;
                        msg.bodyStr = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        msg.isSucceed = false;
                        msg.msg = e.getMessage();
                    }

                } else {
                    msg.isSucceed = false;
                    msg.msg = response.message();
                }
                if (channel != null) {
                    channel.write(msg);
                }
            }
        });
    }

    @Override
    public @NonNull
    HttpMessage postSync(@NonNull PostRequest request) {
        final String url = request.url;
        final Map<String, Object> params = request.params;
        final Map<String, String> header = request.header;
        final Object tag = request.tag;
        final Request okRquest = createPostRequest(url, params, header);
        Call call = mClient.newCall(okRquest);
        Response response = null;
        HttpMessage msg = HttpMessage.obtain();
        msg.tag = tag;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                msg.isSucceed = true;
                msg.bodyStr = response.body().string();
            } else {
                msg.isSucceed = false;
                msg.msg = response.message();
            }
        } catch (IOException e) {
            e.printStackTrace();
            msg.isSucceed = false;
            msg.msg = e.getMessage();
        }
        return msg;
    }


    private RequestBody appendBody(Map<String, Object> params) {
        FormBody.Builder body = new FormBody.Builder();
        if (params == null || params.isEmpty()) {
            return body.build();
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry == null || entry.getValue() == null) {
                continue;
            }
            // entry.getValue()=null
            body.add(entry.getKey(), entry.getValue().toString());
        }
        return body.build();
    }

    /**
     * 创建post请求
     *
     * @param url
     * @param params
     * @param header
     * @return
     */
    private Request createPostRequest(String url, Map<String, Object> params, Map<String, String> header) {
        final Request.Builder builder = new Request.Builder();
        builder.post(appendBody(params)).url(url);
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), (String) entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 创建get请求
     *
     * @param url
     * @param params
     * @param header
     * @return
     */
    private Request createGetRequest(String url, Map<String, Object> params, Map<String, String> header) {
        final Request.Builder builder = new Request.Builder();
        builder.get().url(url);
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void showLog(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
