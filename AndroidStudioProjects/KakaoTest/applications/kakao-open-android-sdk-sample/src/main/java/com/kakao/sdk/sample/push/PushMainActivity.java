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
package com.kakao.sdk.sample.push;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.network.ErrorResult;
import com.kakao.push.PushMessageBuilder;
import com.kakao.push.PushService;
import com.kakao.push.response.model.PushTokenInfo;
import com.kakao.sdk.sample.R;
import com.kakao.sdk.sample.common.RootLoginActivity;
import com.kakao.sdk.sample.common.SampleSignupActivity;
import com.kakao.sdk.sample.common.log.Logger;
import com.kakao.sdk.sample.common.widget.DialogBuilder;
import com.kakao.sdk.sample.common.widget.KakaoToast;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.Utility;

import java.util.Arrays;
import java.util.List;

/**
 * 푸시토큰 등록/삭제, 나에게 푸시 메시지 보내기를 테스트 한다.
 * 유효한 세션이 있다는 검증을 {@link RootLoginActivity}로 부터 받고 보여지는 로그인 된 페이지이다.
 * @author MJ
 */
public class PushMainActivity extends Activity {
    protected static final String PROPERTY_DEVICE_ID = "device_id";
    /**
     * @param savedInstanceState activity 내려갈 때 저장해둔 정보
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_push_main);
        ((TextView) findViewById(R.id.text_title)).setText(getString(R.string.text_push));

        findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 나에게 푸시 보내기 버튼, 명시적 푸시 토큰 삭제 버튼, 명시적 푸시 토큰 등록 버튼, 로그아웃(암묵적 푸시토큰 삭제) 버튼에 대한 처리를 진행한다.
     *
     * @param view 클릭된 view
     */
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.send_button:
                sendPushMessageToMe();
                break;
            case R.id.unregistger_button:
                deregisterPushToken();
                break;
            case R.id.registger_button:
                PushService.registerPushToken(new KakaoPushResponseCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer result) {
                        KakaoToast.makeToast(getApplicationContext(), "succeeded to register push token", Toast.LENGTH_SHORT).show();
                    }
                }, FirebaseInstanceId.getInstance().getToken(), KakaoSDK.getAdapter().getPushConfig().getDeviceUUID(), Utility.getAppVersion(this));
                break;
            case R.id.logout_button:
                logout();
                break;
            case R.id.unlink_button:
                unlink();
                break;
            case R.id.tokens_button:
                getPushTokens();
                break;
        }
    }

    private void logout() {
        deregisterPushToken();
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    private void unlink() {
        deregisterPushTokenAll();
        UserManagement.requestUnlink(new UnLinkResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.d("failure to unlink. msg = " + errorResult);
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(Long result) {
                redirectLoginActivity();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                redirectSignupActivity();
            }

        });
    }

    private void deregisterPushTokenAll() {
        PushService.deregisterPushTokenAll(new KakaoPushResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                KakaoToast.makeToast(getApplicationContext(), "succeeded to deregister all push token of this user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deregisterPushToken() {
        PushService.deregisterPushToken(new KakaoPushResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                KakaoToast.makeToast(getApplicationContext(), "succeeded to deregister push token", Toast.LENGTH_SHORT).show();
            }
        }, KakaoSDK.getAdapter().getPushConfig().getDeviceUUID());
    }

    private void sendPushMessageToMe() {
        final String testMessage = new PushMessageBuilder("{\"content\":\"테스트 메시지\", \"friend_id\":1, \"noti\":\"test\"}").toString();
        if (testMessage == null) {
            Logger.w("failed to create push Message");
        } else {
            PushService.sendPushMessage(new KakaoPushResponseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    KakaoToast.makeToast(getApplicationContext(), "succeeded to send message", Toast.LENGTH_SHORT).show();
                }
            }, testMessage, KakaoSDK.getAdapter().getPushConfig().getDeviceUUID());
        }
    }

    private void getPushTokens() {
        PushService.getPushTokens(new KakaoPushResponseCallback<List<PushTokenInfo>>() {
            @Override
            public void onSuccess(List<PushTokenInfo> result) {
                String message = "succeeded to get push tokens." +
                        "\ncount=" + result.size() +
                        "\ntokens=" + Arrays.toString(result.toArray(new PushTokenInfo[result.size()]));

                new DialogBuilder(PushMainActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    private abstract class KakaoPushResponseCallback<T> extends ApiResponseCallback<T> {
        @Override
        public void onFailure(ErrorResult errorResult) {
            KakaoToast.makeToast(getApplicationContext(), "failure : " + errorResult, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            redirectSignupActivity();
        }
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, RootLoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, SampleSignupActivity.class);
        startActivity(intent);
        finish();
    }
}
