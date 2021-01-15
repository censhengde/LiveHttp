package com.tencent.libhttp.http;

import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
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
