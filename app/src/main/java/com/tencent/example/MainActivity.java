package com.tencent.example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.livehttp.core.HttpCallback;
import com.tencent.livehttp.core.Response;
import com.tencent.livehttp.core.LiveHttp;

public class MainActivity extends AppCompatActivity {
  TextView mTvContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent=findViewById(R.id.tv_content);
    }

    public void request(View view) {
        String url = "http//:www.xxx.com";
        LiveHttp.get(url)
                .addParam("param1", "value1")
                .addParam("param2", "value2")
                .addParam("param3", "value3")
                .enqueue(this, new HttpCallback() {
            @Override
            public void onSucceed(@NonNull Response msg) {
                UserBean user = msg.parseObject(UserBean.class);//解析对象。
//                user=msg.parseObject(UserBean.class,"key");//根据key来解析特定json字段。
//                List<UserBean> users=msg.parseArray(UserBean.class);//解析数组。
//                users=msg.parseArray(UserBean.class,4);//根据index解析特定json字段。

                if (user != null) {
                    mTvContent.setText(user.toString());
                }

//                Toast.makeText(MainActivity.this,"succeed"+msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Response msg) {
                Toast.makeText(MainActivity.this,"failure"+msg,Toast.LENGTH_SHORT).show();
                mTvContent.setText(msg.toString());

            }
        });

    }

    public void skip(View view) {
        startActivity(new Intent(this,Main2Activity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("Main1===>","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Main1===>","onDestroy");
    }
}
