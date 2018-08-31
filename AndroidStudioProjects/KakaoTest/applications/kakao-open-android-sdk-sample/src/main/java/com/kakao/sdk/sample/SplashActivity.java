package com.kakao.sdk.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.Session;
import com.kakao.sdk.sample.common.RootLoginActivity;

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
                if (!Session.getCurrentSession().checkAndImplicitOpen()) {
                    Intent intent = new Intent(SplashActivity.this, RootLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, KakaoServiceListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }

            }
        }, 500);
    }
}
