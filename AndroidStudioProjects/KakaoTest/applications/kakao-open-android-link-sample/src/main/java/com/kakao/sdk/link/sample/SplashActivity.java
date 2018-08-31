package com.kakao.sdk.link.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author leoshin
 * Created by leoshin on 15. 6. 18..
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        findViewById(R.id.splash).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, KakaoServiceListActivity.class));
                finish();
            }
        }, 500);
    }
}
