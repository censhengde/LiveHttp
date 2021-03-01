package com.tencent.livehttp.core;

import androidx.annotation.NonNull;

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
    void getAsync(@NonNull GetRequest request);


    /**
     * Get同步请求
     * @param request
     * @return
     */
    @NonNull
    Response getSync(@NonNull GetRequest request);

    /**
     * Post异步请求
     * @param request
     */
    void postAsync(@NonNull PostRequest request);

    /**
     * Post同步步请求
     * @param request
     * @return
     */
    @NonNull
    Response postSync(@NonNull PostRequest request);

    /**
     * 下载
     *
     * @param request
     */
    void download(@NonNull DownloadRequest request);

    void upload(@NonNull UploadRequest request);
}
