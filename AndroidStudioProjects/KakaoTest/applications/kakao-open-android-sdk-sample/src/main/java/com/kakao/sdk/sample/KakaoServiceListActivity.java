package com.kakao.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.kakao.sdk.sample.common.BaseActivity;
import com.kakao.sdk.sample.kakaostory.KakaoStoryMainActivity;
import com.kakao.sdk.sample.kakaotalk.KakaoTalkMainActivity;
import com.kakao.sdk.sample.push.PushMainActivity;
import com.kakao.sdk.sample.usermgmt.UsermgmtMainActivity;

public class KakaoServiceListActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_service_list);

        findViewById(R.id.kakao_story).setOnClickListener(this);
        findViewById(R.id.kakao_talk).setOnClickListener(this);
        findViewById(R.id.kakao_push).setOnClickListener(this);
        findViewById(R.id.kakao_usermgmt).setOnClickListener(this);
        findViewById(R.id.title_back).setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kakao_story:
                startActivity(new Intent(this, KakaoStoryMainActivity.class));
                break;
            case R.id.kakao_talk:
                startActivity(new Intent(this, KakaoTalkMainActivity.class));
                break;
            case R.id.kakao_push:
                startActivity(new Intent(this, PushMainActivity.class));
                break;
            case R.id.kakao_usermgmt:
                startActivity(new Intent(this, UsermgmtMainActivity.class));
                break;
            default:
                break;
        }
    }
}
