package com.tencent.libhttp.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

/**
 * Author：Shengde·Cen on 2020/11/4 15:36
 * <p>
 * 说明：
 */
public interface HttpCallback {
    @UiThread//
    void onSucceed(@NonNull HttpMessage msg);
    @UiThread
    void onFailure(@NonNull HttpMessage msg);

}
