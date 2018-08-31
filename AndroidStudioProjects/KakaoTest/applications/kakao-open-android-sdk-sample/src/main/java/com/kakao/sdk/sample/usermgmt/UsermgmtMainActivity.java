/**
 * Copyright 2014-2016 Kakao Corp.
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
package com.kakao.sdk.sample.usermgmt;

import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.sample.R;
import com.kakao.sdk.sample.common.BaseActivity;
import com.kakao.sdk.sample.common.log.Logger;
import com.kakao.sdk.sample.common.widget.KakaoToast;
import com.kakao.sdk.sample.common.widget.ProfileLayout;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

/**
 * 가입된 사용자가 보게되는 메인 페이지로 사용자 정보 불러오기/update, 로그아웃, 탈퇴 기능을 테스트 한다.
 */
public class UsermgmtMainActivity extends BaseActivity {
    private UserProfile userProfile;
    private ProfileLayout profileLayout;
    private ExtraUserPropertyLayout extraUserPropertyLayout;

    /**
     * 로그인 또는 가입창에서 넘긴 유저 정보가 있다면 저장한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        userProfile = UserProfile.loadFromCache();
        if(userProfile != null)
            showProfile();
    }

    /**
     * 사용자의 정보를 변경 저장하는 API를 호출한다.
     */
    private void onClickUpdateProfile() {
        final Map<String, String> properties = extraUserPropertyLayout.getProperties();
        UserManagement.requestUpdateProfile(new UsermgmtResponseCallback<Long>() {
            @Override
            public void onSuccess(Long result) {
                userProfile.updateUserProfile(properties);
                if (userProfile != null) {
                    userProfile.saveUserToCache();
                }
                KakaoToast.makeToast(getApplicationContext(), "succeeded to update user profile", Toast.LENGTH_SHORT).show();
                Logger.d("succeeded to update user profile" + userProfile);
                showProfile();
            }

        }, properties);
    }

    private void onClickAccessTokenInfo() {
        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                // not happened
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get access token info. msg=" + errorResult;
                Logger.e(message);
                KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
                Logger.d("this access token is for userId=" + userId);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Logger.d("this access token expires after " + expiresInMilis + " milliseconds.");

                KakaoToast.makeToast(getApplicationContext(), "this access token for user(id="+ userId+") expires after " + expiresInMilis + " milliseconds.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
            .setMessage(appendMessage)
            .setPositiveButton(getString(R.string.com_kakao_ok_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserManagement.requestUnlink(new UnLinkResponseCallback() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                Logger.e(errorResult.toString());
                            }

                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {
                                redirectLoginActivity();
                            }

                            @Override
                            public void onNotSignedUp() {
                                redirectSignupActivity();
                            }

                            @Override
                            public void onSuccess(Long result) {
                                redirectLoginActivity();
                            }
                        });
                        dialog.dismiss();
                    }
                })
            .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }


    private void showProfile() {
        if (profileLayout != null) {
            profileLayout.setUserProfile(userProfile);
        }

        if (extraUserPropertyLayout != null) {
            extraUserPropertyLayout.showProperties(userProfile.getProperties());
        }
    }

    private void initializeView() {
        setContentView(R.layout.layout_usermgmt_main);
        ((TextView)findViewById(R.id.text_title)).setText(getString(R.string.text_usermgmt));
        findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initializeButtons();
        initializeProfileView();
    }

    private void initializeButtons() {
        final Button buttonMe = (Button) findViewById(R.id.buttonMe);
        buttonMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                profileLayout.requestMe();
            }
        });

        final Button buttonUpdateProfile = (Button) findViewById(R.id.buttonUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });

        final Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickLogout();
            }
        });

        final Button unlinkButton = (Button) findViewById(R.id.unlink_button);
        unlinkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickUnlink();
            }
        });

        final Button tokenInfoButton = (Button) findViewById(R.id.token_info_button);
        tokenInfoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickAccessTokenInfo();
            }
        });
    }

    private void initializeProfileView() {
        profileLayout = (ProfileLayout) findViewById(R.id.com_kakao_user_profile);
        profileLayout.setMeResponseCallback(new MeResponseCallback() {
            @Override
            public void onNotSignedUp() {
                redirectSignupActivity();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.e(message);
                KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile result) {
                KakaoToast.makeToast(getApplicationContext(), "succeeded to get user profile", Toast.LENGTH_SHORT).show();
                if (result != null) {
                    UsermgmtMainActivity.this.userProfile = result;
                    userProfile.saveUserToCache();
                    showProfile();
                }
            }
        });

        extraUserPropertyLayout = (ExtraUserPropertyLayout) findViewById(R.id.extra_user_property);
    }

    private abstract class UsermgmtResponseCallback<T> extends ApiResponseCallback<T> {
        @Override
        public void onNotSignedUp() {
            redirectSignupActivity();
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            String message = "failed to get user info. msg=" + errorResult;
            Logger.e(message);
            KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            redirectLoginActivity();
        }
    }
}
