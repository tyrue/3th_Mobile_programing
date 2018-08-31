package com.kakao.sdk.ageauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.ageauth.sample.R;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

/**
 * @author leoshin
 */
public class AgeAuthLoginActivity extends Activity {
    private final ISessionCallback callback = new SessionCallback();
    private Session session;

    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_kakao_login);

        session = Session.getCurrentSession();
        session.addCallback(callback);
        session.checkAndImplicitOpen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.removeCallback(callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {
        /**
         * 세션이 오픈되었으면 가입페이지로 이동 한다.
         */
        @Override
        public void onSessionOpened() {
            //뺑글이 종료
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            Logger.i("++ MySessionStatusCallback::onSessionOpened()");
            AgeAuthLoginActivity.this.onSessionOpened();
        }

        /**
         * 세션이 삭제되었으니 로그인 화면이 보여야 한다.
         * @param exception  에러가 발생하여 close가 된 경우 해당 exception
         */
        @Override
        public void onSessionOpenFailed(final KakaoException exception) {
            //뺑글이 종료
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈을 못했으니 다시 로그인 버튼 노출.
            Logger.i("++ onSessionClosed()");
            if(exception != null) {
                KakaoToast.makeToast(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(exception);
            }
        }
    }

    protected void onSessionOpened() {
        requestMe();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                redirectMainActivity();
            }

            @Override
            public void onNotSignedUp() {
                requestSignUp();
            }
        });
    }

    protected void redirectMainActivity() {
        final Intent intent = new Intent(this, AgeAuthMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestSignUp() {
        UserManagement.requestSignup(new ApiResponseCallback<Long>() {
            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onSuccess(Long result) {
                requestMe();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                final String message = "UsermgmtResponseCallback : failure : " + errorResult;
                Logger.w(message);
                KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }
        }, null);
    }
}
