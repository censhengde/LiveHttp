package com.tencent.example.http;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.tencent.livehttp.core.DownloadRecord;
import com.tencent.livehttp.core.DownloadRequest;
import com.tencent.livehttp.core.GetRequest;
import com.tencent.livehttp.core.HttpChannel;
import com.tencent.livehttp.core.HttpEngine;
import com.tencent.livehttp.core.LoggingInterceptor;
import com.tencent.livehttp.core.PostRequest;
import com.tencent.livehttp.core.Response;
import com.tencent.livehttp.core.UploadRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

/**
 * Author：Shengde·Cen on 2020/11/4 15:50
 * <p>
 * 说明：采用LiveData解决网络数据回调但页面已经不存在的安全隐患
 */
public final class OkHttpEngine implements HttpEngine {

    private static final boolean DEBUG = false;
    private static final int CACHE_SIZE = 10 * 1024 * 1024;
    private static final String TAG = "OkHttpEngine";


    private final OkHttpClient mClient;
    private final File cacheFile = new File("", "cache");
    private final Cache cache = new Cache(cacheFile, CACHE_SIZE);
    /**
     * OkHttpClient 配置
     */
    public OkHttpEngine() {
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
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
        mClient = clientBuilder
                .cache(cache)
                .addInterceptor(new LoggingInterceptor())
                .build();

    }



    @Override
    public void getAsync(@NonNull GetRequest request) {
        final String url = request.getUrl();
        final Map<String, String> header = request.getHeader();
        final Object tag = request.getTag();
        final HttpChannel channel = request.getChannel();
        showLog(url);

        final Request okRequest = createGetRequest(url, header);
        Call call = mClient.newCall(okRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                debugSleep(5000);//模拟耗时
                final Response msg = Response.obtain();
                msg.tag = tag;
                msg.isSucceed = false;
                msg.msg = e.getMessage();
                if (channel != null) {
                    showLog("数据返回："+msg);
                    channel.write(msg);
                }

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                final Response res = Response.obtain();
                debugSleep(5000);
                res.tag = tag;
                if (response.isSuccessful()) {
                    res.isSucceed = true;
                    ResponseBody body = response.body();
                    if (body != null) {
                        res.setBodyStr(body.string());
                    }
                } else {
                    res.isSucceed = false;
                }
                if (channel != null) {
                    showLog("数据返回："+res);
                    channel.write(res);
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
    Response getSync(@NonNull GetRequest request) {
        final String url = request.getUrl();
        final Map<String, String> header = request.getHeader();
        final Object tag = request.getTag();
        showLog(url);
        final Request okRequest = createGetRequest(url, header);
        final Call call = mClient.newCall(okRequest);
        okhttp3.Response response = null;
        final Response res = Response.obtain();
        res.tag = tag;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    res.setBodyStr(body.string());
                }
                res.isSucceed = true;
            } else {
                res.isSucceed = false;
                res.msg = response.message();
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.isSucceed = false;
            res.msg = e.getMessage();
        }
        return res;
    }

    @Override
    public void postAsync(@NonNull PostRequest request) {
        final String url = request.getUrl();
        final Map<String, Object> params = request.getParams();
        final Map<String, String> header = request.getHeader();
        final Object tag = request.getTag();
        final HttpChannel channel = request.getChannel();
        showLog(url);
        final Request okRequest = createPostRequest(url, params, header);
        Call call = mClient.newCall(okRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                final Response msg = Response.obtain();
                msg.tag = tag;
                msg.msg = e.getMessage();
                msg.isSucceed = false;
                if (channel != null) {
                    channel.write(msg);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                final Response res = Response.obtain();
                res.tag = tag;
                if (response.isSuccessful()) {
                    try {
                        res.isSucceed = true;
                        ResponseBody body = response.body();
                        if (body != null) {
                            res.setBodyStr(body.string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        res.isSucceed = false;
                        res.msg = e.getMessage();
                    }

                } else {
                    res.isSucceed = false;
                    res.msg = response.message();
                }
                if (channel != null) {
                    channel.write(res);
                }
            }
        });
    }

    @Override
    public @NonNull
    Response postSync(@NonNull PostRequest request) {
        final String url = request.getUrl();
        final Map<String, Object> params = request.getParams();
        final Map<String, String> header = request.getHeader();
        final Object tag = request.getTag();
        final Request okRquest = createPostRequest(url, params, header);
        Call call = mClient.newCall(okRquest);
        okhttp3.Response response = null;
        Response msg = Response.obtain();
        msg.tag = tag;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                msg.isSucceed = true;
                ResponseBody body = response.body();
                if (body != null) {
                    msg.setBodyStr(body.string());
                }
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

    @Override
    public void download(@NonNull final DownloadRequest request) {
        final String url = request.getUrl();
        final String savePath = request.getSavePath();
        final String fileName = request.getFileName();
        final MutableLiveData<DownloadRecord> record=request.getRecordChannel();
        showLog("开启下载任务：" + request.getId());
        final Request dowloadRequest = new Request.Builder().url(url).build();
        mClient.newCall(dowloadRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()){
                    ResponseBody body=response.body();
                    if (body!=null){
                        long current = 0;
                        final  File saveFile=new File(savePath,fileName);
                        BufferedSink sink = Okio.buffer(Okio.sink(saveFile));
                        Buffer buffer = sink.buffer();
                        long total = body.contentLength();
                        long len;
                        int bufferSize = 200 * 1024; //200kb
                        BufferedSource source = body.source();
                        while ((len = source.read(buffer, bufferSize)) != -1) {
                            current += len;
                            int progress = ((int) ((current * 100 / total)));
                            record.postValue(null);//
                        }
                        source.close();
                        sink.close();
                    }
                }
            }
        });
    }

    @Override
    public void upload(@NonNull UploadRequest request) {

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
     * @param header
     * @return
     */
    private Request createGetRequest(String url, Map<String, String> header) {
        final Request.Builder builder = new Request.Builder();
        builder.get().url(url);
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /*模拟耗时*/
    private void debugSleep(long ms) {
        if (DEBUG){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        }
    }

    private void showLog(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
