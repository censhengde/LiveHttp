package com.tencent.libhttp;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.tencent.libhttp.http.HttpCallback;
import com.tencent.libhttp.http.HttpMessage;
import com.tencent.libhttp.http.HttpService;

public class MainActivity extends AppCompatActivity {
  TextView mTvContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent=findViewById(R.id.tv_content);
    }

    public void request(View view) {
        String url="https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=1&spn=0&di=6160&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3363295869%2C2467511306&os=892371676%2C71334739&simid=4203536407%2C592943110&adpicid=0&lpn=0&ln=1469&fr=&fmq=1610705379420_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fgimg2.baidu.com%2Fimage_search%2Fsrc%3Dhttp%3A%2F%2Fa0.att.hudong.com%2F30%2F29%2F01300000201438121627296084016.jpg%26refer%3Dhttp%3A%2F%2Fa0.att.hudong.com%26app%3D2002%26size%3Df9999%2C10000%26q%3Da80%26n%3D0%26g%3D0n%26fmt%3Djpeg%3Fsec%3D1613297382%26t%3D18aa6ca1a376459c9e2fed54123c8678&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bfhyvg8_z%26e3Bv54AzdH3F4AzdH3Fetjo_z%26e3Brir%3Fwt1%3Dmb9l9&gsm=2&rpstart=0&rpnum=0&islist=&querylist=&force=undefined";
        HttpService.get(url)
                .doAsync(this, new HttpCallback() {
            @Override
            public void onSucceed(@NonNull HttpMessage msg) {
                Toast.makeText(MainActivity.this,"succeed"+msg,Toast.LENGTH_SHORT).show();
                mTvContent.setText(msg.toString());
            }

            @Override
            public void onFailure(@NonNull HttpMessage msg) {
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
