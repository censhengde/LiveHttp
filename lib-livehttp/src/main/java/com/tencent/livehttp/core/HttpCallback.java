package com.tencent.livehttp.core;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

/**
 * Author：Shengde·Cen on 2020/11/4 15:36
 * <p>
 * 说明：
 */
public interface HttpCallback {
    @UiThread//
    void onSucceed(@NonNull Response msg);
    @UiThread
    void onFailure(@NonNull Response msg);

}
