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
    private static Map<String, String> sCommonHeader;
    private static final HttpEngine sEngine;

    static {
        sEngine = new OkHttpEngine();
    }

    private HttpService() {
    }

    public static void setCommonHeader(Map<String, String> commonHeader) {
        sCommonHeader = commonHeader;
    }

    public static OneRequest get(String url) {
        return new GetRequest(url);
    }

    public static OneRequest post(String url) {
        return new PostRequest(url);
    }

    public static void download(@NonNull String url) {
    }

    public static abstract class OneRequest {

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

        public OneRequest withParam(String key, Object value) {
            if (params == null) {
                params = new ArrayMap<>(6);
            }
            params.put(key, value);
            return this;
        }

        public OneRequest addHeader(String key, String value) {
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
         * 手动移除Callbak式
         *
         * @param callback
         */
        public abstract void doAsync(HttpCallback callback);

        /**
         * 自动移除Callbak式
         *
         * @param callback
         */
        public abstract void doAsync(LifecycleOwner owner, HttpCallback callback);

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

    /**
     * Get请求
     */
    public static final class GetRequest extends OneRequest {

        GetRequest(String url) {
            super(url);
        }

        @Override
        public HttpMessage doSync() {
            this.url = appendUrl(url, params);
            return sEngine.getSync(this);
        }

        @Override
        public void doAsync(HttpCallback callback) {
            this.channel = HttpChannel.newInstance();
            channel.readForever(callback);
            tag = tag == null ? url : tag;
            this.url = appendUrl(url, params);
            sEngine.getAsync(this);
        }

        @Override
        public void doAsync(LifecycleOwner owner, HttpCallback callback) {
            this.channel = HttpChannel.newInstance();
            channel.read(owner, callback);
            tag = tag == null ? url : tag;
            this.url = appendUrl(url, params);
            sEngine.getAsync(this);
        }

        @Override
        public void doAsync(LifecycleOwner owner, final Object observer) {
            this.channel = HttpChannel.newInstance();
            tag = tag == null ? url : tag;
            final Method target = resolveObserver(observer, (String) tag);
            if (target == null) {
                return;
            }
            target.setAccessible(true);
            channel.read(owner, new HttpCallback() {
                @Override
                public void onSucceed(@NonNull HttpMessage msg, @androidx.annotation.Nullable Object tag) {
                    try {
                        target.invoke(observer, msg);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull HttpMessage msg) {
                    try {
                        target.invoke(observer, msg);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
            this.url = appendUrl(url, params);
            sEngine.getAsync(this);
        }
    }

    /**
     * Post请求
     */
    public static final class PostRequest extends OneRequest {

        public PostRequest(String url) {
            super(url);
        }

        @Override
        public HttpMessage doSync() {
            tag = tag == null ? tag = url : tag;
            return sEngine.postSync(this);
        }

        @Override
        public void doAsync(HttpCallback callback) {
            tag = tag == null ? tag = url : tag;
            this.channel = HttpChannel.newInstance();
            channel.readForever(callback);
            sEngine.postAsync(this);
        }

        @Override
        public void doAsync(LifecycleOwner owner, HttpCallback callback) {
            tag = tag == null ? tag = url : tag;
            this.channel = HttpChannel.newInstance();
            channel.read(owner, callback);
            sEngine.postAsync(this);
        }

        @Deprecated
        @Override
        public void doAsync(LifecycleOwner owner, final Object observer) {
            this.channel = HttpChannel.newInstance();
            tag = tag == null ? url : tag;
            final Method target = resolveObserver(observer, (String) tag);
            if (target == null) {
                return;
            }
            target.setAccessible(true);
            channel.read(owner, new HttpCallback() {
                @Override
                public void onSucceed(@NonNull HttpMessage msg, @androidx.annotation.Nullable Object tag) {
                    try {
                        target.invoke(observer, msg);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull HttpMessage msg) {
                    try {
                        target.invoke(observer, msg);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
            sEngine.postAsync(this);
        }
    }
}
