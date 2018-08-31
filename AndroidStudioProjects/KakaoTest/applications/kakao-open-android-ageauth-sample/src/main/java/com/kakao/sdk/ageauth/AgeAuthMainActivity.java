package com.kakao.sdk.ageauth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.kakao.auth.AgeAuthParamBuilder;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.AuthService.AgeAuthLevel;
import com.kakao.auth.AuthService.AgeLimit;
import com.kakao.auth.callback.AccountErrorResult;
import com.kakao.auth.callback.AccountResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.ageauth.sample.R;
import com.kakao.sdk.ageauth.widget.KakaoDialogSpinner;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.UserManagement.AgeAuthProperty;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.AgeAuthResponse;
import com.kakao.usermgmt.response.AgeAuthResponse.AgeAuthLimitStatus;
import com.kakao.util.helper.log.Logger;

/**
 * @author leoshin
 */
public class AgeAuthMainActivity extends Activity implements OnClickListener {
    private KakaoDialogSpinner standard;
    private EditText authFrom;
    private CheckBox cbSkipTerms, cbWesternAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        findViewById(R.id.unlink).setOnClickListener(this);
        findViewById(R.id.check_age_auth).setOnClickListener(this);

        standard = (KakaoDialogSpinner)findViewById(R.id.ageauth_level);
        authFrom = (EditText)findViewById(R.id.auth_from);
        cbWesternAge = (CheckBox) findViewById(R.id.is_western_age);
        cbSkipTerms = (CheckBox) findViewById(R.id.is_skip_terms);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unlink:
                requestUnlink();
                break;
            case R.id.check_age_auth:
                requestCheckAgeAuth();
                break;
        }
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(this, AgeAuthLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestUnlink() {
        UserManagement.requestUnlink(new UnLinkResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                KakaoToast.makeToast(getApplicationContext(), errorResult.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(Long result) {
                finish();
            }
        });
    }

    /**
     * 해당 앱이 19세 2차 인증을 한 유저에 대해서만 앱을 쓸 수 있다는 가정하에 만들어짐.
     * @return true : 연령인증 필요, false : 연령인증 불필요.
     */
    private boolean isNeedToAgeAuthentication(AgeAuthResponse ageAuthResponse, int checkAgeAuthStandard) {
        int actualAuthLevelCode = ageAuthResponse.getAuthLevelCode();
        AgeAuthLimitStatus actualStatus = ageAuthResponse.getAgeAuthLimitStatus();
        String actualAuthenticatedAt = ageAuthResponse.getAuthenticatedAt();
        String actualCI = ageAuthResponse.getCI();

        // 1. 연령인증을 받지 않은 경우.
        if(actualAuthenticatedAt == null) {
            return true;
        }

        // 2. 연령이 미달인 경우, 연령 정보를 모르는 경우.
        if (actualStatus != AgeAuthLimitStatus.BYPASS_AGE_LIMIT) {
            return true;
        }

        int expectedAuthLevelCode = 1;
        boolean needsCI = false;
        boolean needsAuthenticatedAt = false;
        switch (checkAgeAuthStandard) {
            case 0:
                break;
            case 1:
                expectedAuthLevelCode = 2;
                break;
            case 2:
                expectedAuthLevelCode = 2;
                needsCI = true;
                break;
            case 3:
                expectedAuthLevelCode = 2;
                needsCI = true;
                needsAuthenticatedAt = true;
                break;
        }

        // 3. 연령 레벨이 맞지 않는 경우.
        if (actualAuthLevelCode < expectedAuthLevelCode) {
            return true;
        }

        // 4. 추가 정보 - CI가 필요한데 없는 경우
        if (needsCI && actualCI == null) {
            return true;
        }

        // 5. 추가정보 - 30일 이내 인증 정보
        if(needsAuthenticatedAt){
            try {
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                dt.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = dt.parse(actualAuthenticatedAt);

                long time = date.getTime();
                long diff = System.currentTimeMillis() - time;
                long expiredTime = 60L * 1000L * 60L * 24L * 30L; // 30일L
                if (diff > expiredTime) {
                    return true;
                }
            } catch (ParseException e) {
                Logger.e(e);
            }
        }
        return false;
    }

    private void requestCheckAgeAuth() {
        ArrayList<AgeAuthProperty> properties = new ArrayList<AgeAuthProperty>();
        AgeLimit ageLimit = null;
        final int checkAgeAuthStandard = standard.getSelectedItemPosition();
        switch (checkAgeAuthStandard) {
            case 0:
                ageLimit = null;
                break;
            case 1:
                ageLimit = AgeLimit.LIMIT_19;
                break;
            case 2:
                ageLimit = AgeLimit.LIMIT_19;
                properties.add(AgeAuthProperty.ACCOUNT_CI);
                break;
            case 3:
                ageLimit = AgeLimit.LIMIT_19;
                properties.add(AgeAuthProperty.ACCOUNT_CI);
                break;
        }

        UserManagement.requestAgeAuthInfo(new ApiResponseCallback<AgeAuthResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                redirectLoginActivity();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                KakaoToast.makeToast(getApplicationContext(), errorResult.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(AgeAuthResponse result) {
                Logger.i(result.toString());
                if (isNeedToAgeAuthentication(result, checkAgeAuthStandard)) {
                    showAgeAuthenticationDialog(checkAgeAuthStandard);
                } else {
                    KakaoToast.makeToast(getApplicationContext(), "Not need to show Age authentication", Toast.LENGTH_SHORT).show();
                    Logger.d("Not need to show Age authentication Dialog");
                }
            }
        }, ageLimit, properties);
    }

    private void showAgeAuthenticationDialog(int checkAgeAuthStandard) {
        AgeAuthParamBuilder builder = new AgeAuthParamBuilder();

        if(checkAgeAuthStandard > 0 ){
            builder.setAuthLevel(AgeAuthLevel.LEVEL_2).setAgeLimit(AgeLimit.LIMIT_19);
        }
        builder.setSkipTerm(cbSkipTerms.isChecked()).setIsWesternAge(cbWesternAge.isChecked()).setAuthFrom(authFrom.getText().toString());

        AuthService.requestShowAgeAuthDialog(new AccountResponseCallback() {
            @Override
            public void onSuccess(Integer result) {
                KakaoToast.makeToast(getApplicationContext(), "Request Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAgeAuthFailure(AccountErrorResult result) {
                if (result.getException() != null) {
                    Logger.e(result.getException().getMessage());
                    if (result.getException().getCause() != null) {
                        Logger.e("cause: " + result.getException().getCause().getMessage());
                    }
                }
                Logger.e("AgeAuth Failure : " +  result.getStatus());
                KakaoToast.makeToast(getApplicationContext(), result.getErrorMessage() + "(" + result.getStatus() + ")", Toast.LENGTH_LONG).show();
            }
        },  builder.build(), true);
    }
}
