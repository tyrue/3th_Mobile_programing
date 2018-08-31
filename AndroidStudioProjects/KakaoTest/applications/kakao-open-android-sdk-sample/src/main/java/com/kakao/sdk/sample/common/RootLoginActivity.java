/**
 * Copyright 2014-2015 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.sdk.sample.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kakao.auth.Session;
import com.kakao.sdk.sample.R;

/**
 * 샘플에서 사용하게 될 로그인 페이지
 * 세션을 오픈한 후 action을 override해서 사용한다.
 *
 * @author MJ
 */
public class RootLoginActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            setContentView(R.layout.layout_common_kakao_login);

            Button activityButton = (Button) findViewById(R.id.button_login_with_activity);
            Button fragmentButton = (Button) findViewById(R.id.button_login_with_fragment);
            activityButton.setOnClickListener(this);
            fragmentButton.setOnClickListener(this);
        } else {
            redirectSignupActivity();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_login_with_activity:
                intent = new Intent(RootLoginActivity.this, SampleLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.button_login_with_fragment:
                intent = new Intent(RootLoginActivity.this, LoginFragmentActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
