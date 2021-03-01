package com.tencent.livehttp.core;

/**
 * Author：岑胜德 on 2021/3/1 14:34
 *
 * 说明：
 */
public final class ParamUtils {

    private ParamUtils() {
    }

    public static void checkNotNull(Object o, String msg) {
        if (o == null) {
            throw new IllegalArgumentException(msg);
        }
    }
}
