package com.tencent.lib.http;

import static com.tencent.lib.http.HttpService.sCommonHeader;

import android.util.ArrayMap;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Author：岑胜德 on 2021/1/15 17:23
 *
 * 说明：
 */
public  abstract class OneRequest {

    @NonNull
    String url;
    @Nullable
    Map<String, Object> params;
    @Nullable
    Map<String, String> header;
    @NonNull
    Object tag;
    @Nullable
    HttpChannel channel;

    OneRequest(@NonNull String url) {
        this.url = url;
        if (sCommonHeader != null) {
            header = new ArrayMap<>();
            header.putAll(sCommonHeader);
        }
    }

    public OneRequest addParam(@NonNull String key, @NonNull Object value) {
        if (params == null) {
            params = new ArrayMap<>(6);
        }
        params.put(key, value);
        return this;
    }

    public OneRequest addHeader(@NonNull String key, @NonNull String value) {
        if (header == null) {
            header = new ArrayMap<>();
        }
        header.put(key, value);
        return this;
    }

    /**
     * 同步式
     *
     * @return
     */
    public abstract HttpMessage doSync();

    /**
     * 不能自动移除Callbak式
     *
     * @param callback
     */
    public abstract void doAsync(@NonNull HttpCallback callback);

    /**
     * 自动移除Callbak式
     *
     * @param callback
     */
    public abstract void doAsync(@NonNull LifecycleOwner owner, @NonNull HttpCallback callback);

    /**
     * 自动移除Callbak式+注解方法监听回调式
     */
    @Deprecated//设计不太成熟，先别用
    public abstract void doAsync(LifecycleOwner owner, final Object observer);

    protected final @Nullable
    Method resolveObserver(Object o, String key) {
//            Class<?> clazz = o.getClass();
//            final Method[] methods = clazz.getDeclaredMethods();
//            Method targetM = null;
//            for (Method method : methods) {
//                HttpRespone respone = method.getAnnotation(HttpRespone.class);
//                if (respone != null && respone.key().equals(key)) {
//                    targetM = method;
//                    break;
//                }
//            }
//            return targetM;
        return null;
    }

    static String appendUrl(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        if (urlBuilder.indexOf("?") <= 0) {
            urlBuilder.append("?");
        } else {
            if (!urlBuilder.toString().endsWith("?")) {
                urlBuilder.append("&");
            }
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            urlBuilder.append("&").append(entry.getKey()).append("=").append(encode(entry.getValue().toString()));
        }

        return urlBuilder.toString();
    }

    // URL不允许有空格等字符，如果参数值有空格，需要此方法转换
    static String encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
