package com.tencent.livehttp.core;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Author：Shengde·Cen on 2020/11/4 15:47
 * <p>
 * 说明：模仿android.os.Message
 */
public final class Response {

    private final static String TAG = "HttpMessage===>";

    private static final Object sPoolSync = new Object();
    private static Response sPool;
    private static int sPoolSize = 0;
    private static final int FLAG_IN_USE = 1 << 0;

    private static final int MAX_POOL_SIZE = 7;

    private static boolean gCheckRecycle = true;
    Response next;
    int flags;

    public boolean isSucceed = false;
    public String msg = "--";
    public Object tag;//标识符，用来区别每个请求的响应数据。（保留字段）
    @Nullable
    InputStream body;

    @Nullable
    public InputStream getBody() {
        return body;
    }

    public void setBody(@Nullable InputStream body) {
        this.body = body;
    }

    @Nullable
    public String getBodyStr() {
        return bodyStr;
    }

    public void setBodyStr(@Nullable String bodyStr) {
        this.bodyStr = bodyStr;
    }

    @Nullable
    String bodyStr = "null";


    private Response() {

    }


    public static Response obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Response m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Response();
    }


    void recycle() {
        if (isInUse()) {
            if (gCheckRecycle) {
                throw new IllegalStateException("This message cannot be recycled because it "
                        + "is still in use.");
            }
            return;
        }

        body = null;
        isSucceed = false;
        tag = null;
        bodyStr = null;
        Log.e(TAG, "recycle===>");

        //回收
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    private boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    public @Nullable
    <T> T parseObject(Class<T> tClass) {
        return parseObject(tClass, null);
    }

    public @Nullable
    <T> T parseObject(Class<T> tClass, @Nullable String key) {
        T res = null;
        try {
            final String json = key == null ? asString() : getValueByKey(key);
            res = parseObject(json, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @param <T>
     * @return
     */
    public @Nullable
    <T> T parseObject(String json, Class<T> tClass) {
        T result = null;
        try {
            result = JSON.parseObject(json, tClass);
        } catch (Exception e) {
            Log.e(TAG, "jsonObject 解析异常");
        }
        return result;
    }

    public @Nullable
    <T> List<T> parseArray(Class<T> tClass) {
        return parseArray(tClass, -1);
    }

    public @Nullable
    <T> List<T> parseArray(Class<T> tClass, int index) {
        List<T> res = null;
        try {
            final String json = index < 0 ? asString() : getValueByIndex(index);
            res = parseArray(json, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 当外层json字符串是数组时
     *
     * @return
     */
    public @Nullable
    <T> List<T> parseArray(String json, Class<T> tClass) {
        List<T> result = null;
        try {
            result = JSON.parseArray(json, tClass);
        } catch (Exception e) {
            Log.e(TAG, "jsonArray解析异常");
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 字节流转字符串
     *
     * @return
     */
    private @Nullable
    String convertToString() {
        String str = null;
        if (body != null) {
            final StringBuilder sb = new StringBuilder();
            String line;
            final BufferedReader br = new BufferedReader(new InputStreamReader(body));
            while (true) {
                try {
                    if ((line = br.readLine()) == null) {
                        break;
                    }
                    sb.append(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            str = sb.toString();
        }
        return str;
    }

    @NonNull
    public String asString() {
        if (bodyStr == null) {
            bodyStr = convertToString();
        }
        return bodyStr == null ? "" : bodyStr;
    }

    /**
     * 当json是对象时，获取json字符串中指定节点的值
     *
     * @param key 指定的节点名称
     * @return
     */
    public @Nullable
    String getValueByKey(String json, String key) {
        String result = null;
        try {
            JSONObject object = JSON.parseObject(json);
            if (object != null) {
                result = object.getString(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public @Nullable
    String getValueByKey(String key) {
        String result = null;
        try {
            JSONObject object = JSON.parseObject(asString());
            if (object != null) {
                result = object.getString(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 当json是数组时，获取json字符串中指定节点的值
     *
     * @param index 指定的节点的索引
     * @return
     */
    public @Nullable
    String getValueByIndex(String json, int index) {
        String result = null;
        try {
            JSONArray object = JSON.parseArray(json);
            if (object != null) {
                result = object.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public @Nullable
    String getValueByIndex(int index) {
        String result = null;
        try {
            JSONArray object = JSON.parseArray(asString());
            if (object != null) {
                result = object.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        return "HttpMessage{" +
                "isSucceed=" + isSucceed +
                ", msg='" + msg + '\'' +
                ", tag=" + tag +
                ", body=" + body +
                ", bodyStr='" + bodyStr + '\'' +
                '}';
    }
}
