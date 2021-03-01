package com.tencent.livehttp.core;

import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

/**
 * Author：岑胜德 on 2020/11/9 10:11
 * <p>
 * 说明：Http数据传输通道
 */
public final class HttpChannel {

    private final MutableLiveData<Response> mChannel;

     static HttpChannel newInstance() {
        return new HttpChannel();
    }

    //屏蔽构造器，防止用户new出匿名对象
    private HttpChannel() {
        mChannel = new MutableLiveData<>();
    }

    /**
     * 写数据端，可在任何线程调用
     *
     * @param data
     */
    public void write(Response data) {
        //判断是否主线程
        if ( Looper.getMainLooper().equals(Looper.myLooper())) {
            mChannel.setValue(data);
        } else {
            mChannel.postValue(data);
        }
    }

    /**
     * 读数据端，只能在UI线程调用
     *
     * @param owner
     * @param callback
     */
    @MainThread
    public void read(@NonNull LifecycleOwner owner, @NonNull final HttpCallback callback) {
        mChannel.observe(owner,
                httpMessage -> {
                    if (httpMessage.isSucceed) {
                        callback.onSucceed(httpMessage);
                    } else {
                        callback.onFailure(httpMessage);
                    }
                    httpMessage.recycle();//回收
                }
        );
    }


     void readForever(final HttpCallback callback){
         mChannel.observeForever(httpMessage -> {
             if (httpMessage.isSucceed) {
                 callback.onSucceed(httpMessage);
             } else {
                 callback.onFailure(httpMessage);
             }
             httpMessage.recycle();
        });
    }


}
