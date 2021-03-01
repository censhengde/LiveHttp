package com.tencent.livehttp.core;

import static com.tencent.livehttp.core.LiveHttp.sEngine;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author：岑胜德 on 2021/1/15 17:25
 *
 * 说明：
 */
public  final class GetRequest extends OneRequest {

    GetRequest(String url) {
        super(url);
    }

    @Override
    public Response execute() {
        this.url = appendUrl(url, params);
        return sEngine.getSync(this);
    }

    @Override
    public void enqueue(@NonNull HttpCallback callback) {
        this.channel = HttpChannel.newInstance();
        channel.readForever(callback);
        tag = tag == null ? url : tag;
        this.url = appendUrl(url, params);
        sEngine.getAsync(this);
    }

    @Override
    public void enqueue(@NonNull LifecycleOwner owner, @NonNull HttpCallback callback) {
        this.channel = HttpChannel.newInstance();
        channel.read(owner, callback);
        tag = tag == null ? url : tag;
        this.url = appendUrl(url, params);
        sEngine.getAsync(this);
    }

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
            public void onSucceed(@NonNull Response msg) {
                try {
                    target.invoke(observer, msg);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Response msg) {
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

