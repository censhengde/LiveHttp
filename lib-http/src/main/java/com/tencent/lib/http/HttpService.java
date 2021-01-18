package com.tencent.lib.http;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Author：岑胜德 on 2020/12/12 09:00
 * <p>
 * 说明：
 */
public final class HttpService {

    private static final String TAG = "HttpService===>";
    private static final boolean DEBUG = true;
    static Map<String, String> sCommonHeader;
    static final HttpEngine sEngine;

    static {
        sEngine = new OkHttpEngine();
    }

    private HttpService() {
    }

    public static void setCommonHeader(Map<String, String> commonHeader) {
        sCommonHeader = commonHeader;
    }

    public static OneRequest get(@NonNull String url) {
        return new GetRequest(url);
    }

    public static OneRequest post(@NonNull String url) {
        return new PostRequest(url);
    }

    public static void download(@NonNull String url) {
    }

}
