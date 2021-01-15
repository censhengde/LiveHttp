package com.tencent.libhttp.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author：Shengde·Cen on 2020/11/4 15:36
 * <p>
 * 说明：
 */
public interface HttpCallback {
//    void onStart(Object tag);

    void onSucceed(@NonNull HttpMessage msg, @Nullable Object tag);

    void onFailure(@NonNull HttpMessage msg);

}
