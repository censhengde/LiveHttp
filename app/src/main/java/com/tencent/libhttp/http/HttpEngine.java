package com.tencent.libhttp.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

/**
 * Author：Shengde·Cen on 2020/11/4 15:34
 * <p>
 * 说明：
 */
public interface HttpEngine {
    /**
     * Get异步请求
     * @param request
     */
    void getAsync(@NonNull HttpService.GetRequest request);


    /**
     * Get同步请求
     * @param request
     * @return
     */
    @NonNull
    HttpMessage getSync(@NonNull HttpService.GetRequest request);

    /**
     * Post异步请求
     * @param request
     */
    void postAsync(@NonNull HttpService.PostRequest request);

    /**
     * Post同步步请求
     * @param request
     * @return
     */
    @NonNull
    HttpMessage postSync(@NonNull HttpService.PostRequest request);



}
