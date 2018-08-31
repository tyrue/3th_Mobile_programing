package com.kakao.auth;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;

import com.kakao.auth.authorization.authcode.KakaoCookieManager;
import com.kakao.auth.authorization.authcode.KakaoWebViewActivity;
import com.kakao.network.ServerProtocol;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.Utility;
import com.kakao.util.helper.log.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that requests age authentication using webview.
 *
 * @author kevin.kang. Created on 2017. 5. 22..
 */

public class AgeAuthManager {
    private Handler sHandler;
    private KakaoCookieManager cookieManager;
    private static AgeAuthManager instance;

    /**
     * Returns a singleton instance of AgeAuthManager
     *
     * @return a singleton instance of AgeAuthManager
     */
    public static AgeAuthManager getInstance() {
        if (instance == null) {
            instance = new AgeAuthManager(new Handler(Looper.getMainLooper()), KakaoCookieManager.Factory.getInstance());
        }
        return instance;
    }

    AgeAuthManager(final Handler handler, final KakaoCookieManager cookieManager) {
        sHandler = handler;
        this.cookieManager = cookieManager;
    }

    private boolean requestWebviewAuth(Context context, Bundle ageAuthParams, boolean useSmsReceiver, ResultReceiver resultReceiver) {
        cookieManager.flush();

        boolean isUsingTimer = KakaoSDK.getAdapter().getSessionConfig().isUsingWebviewTimer();
        Uri uri = Utility.buildUri(ServerProtocol.AGE_AUTH_AUTHORITY, ServerProtocol.ACCESS_AGE_AUTH_PATH, ageAuthParams);
        Logger.d("AgeAuth request Url : " + uri);

        Intent intent = KakaoWebViewActivity.newIntent(context);
        intent.putExtra(KakaoWebViewActivity.KEY_URL, uri.toString());
        intent.putExtra(KakaoWebViewActivity.KEY_USE_WEBVIEW_TIMERS, isUsingTimer);
        intent.putExtra(KakaoWebViewActivity.KEY_USE_SMS_RECEIVER, useSmsReceiver);
        intent.putExtra(KakaoWebViewActivity.KEY_RESULT_RECEIVER, resultReceiver);

        context.startActivity(intent);
        return true;
    }

    /**
     * {@link com.kakao.auth.ErrorCode} NEED_TO_AGE_AUTHENTICATION(-405)가 발생하였을때 연령인증을 시도한다.
     * 이때 연령인증중 발생할 수 있는 sms수신여부를 해당앱의 permission이 존재하는지 여부를 보고 판단하도록 한다.
     *
     * @param context 현재 화면의 topActivity의 context
     * @param ageAuthParams {@link AgeAuthParamBuilder}를 통해 만든 연령인증에 필요한 파람들
     * @return status code
     */
    public int requestShowAgeAuthDialog(final Context context, final Bundle ageAuthParams) {
        return requestShowAgeAuthDialog(ageAuthParams, Utility.isUsablePermission(context, Manifest.permission.RECEIVE_SMS));
    }

    /**
     * {@link com.kakao.auth.ErrorCode} NEED_TO_AGE_AUTHENTICATION(-405)가 발생하였을때 연령인증을 시도한다.
     * 이때 연령인증중 발생할 수 있는 sms수신여부를 해당앱의 permission이 존재하는지 여부를 보고 판단하도록 한다. 별도의
     * 파라미터 없이 진행한다.
     *
     * @param context 현재 화면의 topActivity의 context
     * @return status code
     */
    public int requestShowAgeAuthDialog(final Context context) {
        return requestShowAgeAuthDialog(context, new Bundle());
    }

    /**
     *
     * @param ageAuthParams {@link Bundle} instance containing age authentication parameters
     * @param useSmsReceiver Whether to use SmsReceiver or not
     * @return status code
     */
    public int requestShowAgeAuthDialog(final Bundle ageAuthParams, final boolean useSmsReceiver) {
        final Activity activity = KakaoSDK.getCurrentActivity();
        if (useSmsReceiver && ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("This application does not have RECEIVE_SMS permission... Verification code will not auto-complete.");
        }

        final AgeAuthResult result = new AgeAuthResult();
        final CountDownLatch lock = new CountDownLatch(1);
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ResultReceiver resultReceiver = new ResultReceiver(sHandler) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            int status = AuthService.AgeAuthStatus.CLIENT_ERROR.getValue();
                            if (resultCode == KakaoWebViewActivity.RESULT_SUCCESS) {
                                String redirectUrl = resultData.getString(KakaoWebViewActivity.KEY_REDIRECT_URL);
                                if (redirectUrl != null) {
                                    if (Uri.parse(redirectUrl).getQueryParameter(StringSet.status) != null) {
                                        status = Integer.valueOf(Uri.parse(redirectUrl).getQueryParameter(StringSet.status));
                                    }
                                }
                            } else if (resultCode == KakaoWebViewActivity.RESULT_ERROR) {
                                result.setException((KakaoException) resultData.getSerializable(KakaoWebViewActivity.KEY_EXCEPTION));
                            }
                            result.getResult().set(status);
                            lock.countDown();
                        }
                    };
                    requestWebviewAuth(activity, ageAuthParams, useSmsReceiver, resultReceiver);
                } catch (Exception e) {
                    result.getResult().set(AuthService.AgeAuthStatus.CLIENT_ERROR.getValue());
                    result.setException(new KakaoException(e));
                    lock.countDown();
                }
            }
        });

        // 사용자가 취소를 하여도 종료.
        try {
            lock.await();
        } catch (InterruptedException ignor) {
            Logger.e(ignor.toString());
        }

        if (result.getException() != null) {
            throw result.getException();
        }
        return result.getResult().get();
    }

    /**
     * @deprecated  replaced by {@link #requestShowAgeAuthDialog(Context,Bundle)}
     * @param context Context for current activity
     * @param builder Parameter builder for age auth
     * @return status code
     */
    @Deprecated
    public int requestShowAgeAuthDialog(final Context context, final AgeAuthParamBuilder builder) {
        return requestShowAgeAuthDialog(builder, Utility.isUsablePermission(context, Manifest.permission.RECEIVE_SMS));
    }

    /**
     * @deprecated  replaced by {@link #requestShowAgeAuthDialog(Bundle,boolean)}
     * @param builder Parameter builder for age auth
     * @param useSmsReceiver whether SDK is able to receive SMS or not
     * @return status code
     */
    @Deprecated
    public int requestShowAgeAuthDialog(final AgeAuthParamBuilder builder, final boolean useSmsReceiver) {
        return requestShowAgeAuthDialog(builder.build(), useSmsReceiver);
    }

    static class AgeAuthResult {
        private AtomicInteger result;
        private KakaoException exception;

        public AgeAuthResult() {
            this.result = new AtomicInteger();
        }

        public AtomicInteger getResult() {
            return result;
        }

        public void setResult(AtomicInteger result) {
            this.result = result;
        }

        public KakaoException getException() {
            return exception;
        }

        public void setException(KakaoException exception) {
            this.exception = exception;
        }
    }
}
