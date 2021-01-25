package com.tencent.lib.http;

/**
 * Author：岑胜德 on 2021/1/15 17:27
 *
 * 说明：
 */

import static com.tencent.lib.http.HttpService.sEngine;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Post请求
 */
public  final class PostRequest extends OneRequest {

     PostRequest(String url) {
        super(url);
    }

    @Override
    public HttpMessage execute() {
        tag = tag == null ? tag = url : tag;
        return sEngine.postSync(this);
    }

    @Override
    public void enqueue(HttpCallback callback) {
        tag = tag == null ? tag = url : tag;
        this.channel = HttpChannel.newInstance();
        channel.readForever(callback);
        sEngine.postAsync(this);
    }

    @Override
    public void enqueue(LifecycleOwner owner, HttpCallback callback) {
        tag = tag == null ? tag = url : tag;
        this.channel = HttpChannel.newInstance();
        channel.read(owner, callback);
        sEngine.postAsync(this);
    }

    @Deprecated
    @Override
    public void enqueue(LifecycleOwner owner, final Object observer) {
        this.channel = HttpChannel.newInstance();
        tag = tag == null ? url : tag;
        final Method target = resolveObserver(observer, (String) tag);
        if (target == null) {
            return;
        }
        target.setAccessible(true);
        channel.read(owner, new HttpCallback() {
            @Override
            public void onSucceed(@NonNull HttpMessage msg) {
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
