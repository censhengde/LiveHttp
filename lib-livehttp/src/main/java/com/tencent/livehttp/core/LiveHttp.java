package com.tencent.livehttp.core;

import androidx.annotation.NonNull;
import java.util.Map;

/**
 * Author：岑胜德 on 2020/12/12 09:00
 * <p>
 * 说明：有生命感知能力的网络引擎库
 */
public final class LiveHttp {

    private static final String TAG = "LiveHttp===>";
    private static final boolean DEBUG = true;
    static Map<String, String> sCommonHeader;
    static HttpEngine sEngine;



    public static void init(@NonNull HttpEngine engine) {
        sEngine = engine;
    }

    private LiveHttp() {
    }

    public static void setCommonHeader(Map<String, String> commonHeader) {
        sCommonHeader = commonHeader;
    }

    public static OneRequest get(@NonNull String url) {
        ParamUtils.checkNotNull(sEngine,"HttpEngine 不允许为null 请先调用 init()方法 ");
        return new GetRequest(url);
    }

    public static OneRequest post(@NonNull String url) {
        ParamUtils.checkNotNull(sEngine,"HttpEngine 不允许为null 请先调用 init()方法 ");
        return new PostRequest(url);
    }

    @Deprecated//开发中。。。
    public static DownloadRequest download(@NonNull String url) {
        ParamUtils.checkNotNull(sEngine,"HttpEngine 不允许为null 请先调用 init()方法 ");
        return new DownloadRequest(url);
    }

    @Deprecated//开发中。。。
    public static UploadRequest upload(@NonNull String url){
        ParamUtils.checkNotNull(sEngine,"HttpEngine 不允许为null 请先调用 init()方法 ");
        return new  UploadRequest(url);
    }
}
