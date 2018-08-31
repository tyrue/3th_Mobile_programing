package com.kakao.auth.authorization.authcode;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.StringSet;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.auth.helper.StartActivityWrapper;
import com.kakao.auth.mocks.TestAppConfig;
import com.kakao.auth.mocks.TestAuthCodeRequestFactory;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.AppConfig;
import com.kakao.util.exception.KakaoException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowCookieManager;
import org.robolectric.shadows.ShadowLooper;

/**
 * @author kevin.kang. Created on 2017. 6. 1..
 */

public class WebAuthCodeServiceTest extends KakaoTestCase {
    private AppConfig appConfig = TestAppConfig.createTestAppConfig();
    private WebAuthCodeService service;
    private Handler handler;
    private KakaoCookieManager cookieManager;
    private ISessionConfig sessionConfig;

    private String expectedAuthCode = "12345";
    private String authCodePostfix = "?code=" + expectedAuthCode;
    private String accessDeniedErrorString = "access_denied";
    private String otherErrorString = "other_error";
    private String errorDesc = "This is an error message.";

    private String redirectUri = StringSet.REDIRECT_URL_PREFIX + appConfig.getAppKey() + StringSet.REDIRECT_URL_POSTFIX;
    private String wrongRedirectUri = StringSet.REDIRECT_URL_PREFIX + appConfig.getAppKey() + "2" + StringSet.REDIRECT_URL_POSTFIX;
    private String correctRedirectUri = redirectUri + authCodePostfix;
    private String errorRedirectUri = redirectUri + errorDesc;

    private Integer expectedRequestCode = 1;
    private Integer actualRequestCode;
    private AuthorizationResult authorizationResult;
    private KakaoException kakaoException;

    @Before
    public void setup() {
        super.setup();
        handler = new Handler(ShadowLooper.getMainLooper());
        cookieManager = Mockito.spy(new KakaoCookieManager() {
            @Override
            public void flush() {

            }

            @Override
            public void removeCookiesForKakaoDomain() {

            }
        });

        sessionConfig = new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
            }

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            @Override
            public boolean isSecureMode() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return false;
            }
        };
        service = Mockito.spy(new WebAuthCodeService(RuntimeEnvironment.application, handler, cookieManager, sessionConfig));

        authorizationResult = null;
        actualRequestCode = -1;
    }

    @Test
    public void testIsLoginAvailable() {
        Assert.assertTrue(service.isLoginAvailable());
    }

    @Test
    public void testHandleActivityResult() {
        Assert.assertFalse(service.handleActivityResult(0, 0, new Intent(), null));
    }

    @Test
    public void testRequestAuthCode() {
        final AuthCodeRequest request = TestAuthCodeRequestFactory.createAuthCodeRequest(2, appConfig.getAppKey(), sessionConfig, null);
        StartActivityWrapper wrapper = new StartActivityWrapper(Robolectric.buildActivity(Activity.class).get());
        service.requestAuthCode(request, wrapper, getAuthCodeListener());

        Mockito.verify(cookieManager).flush();
        Mockito.verify(service).startActivity(ArgumentMatchers.eq(wrapper), ArgumentMatchers.any(Intent.class));

    }

    @Test
    public void testCreateAccountLoginIntent() {
        final AuthCodeRequest request = TestAuthCodeRequestFactory.createAuthCodeRequest(2, appConfig.getAppKey(), sessionConfig, null);
        StartActivityWrapper wrapper = new StartActivityWrapper(Robolectric.buildActivity(Activity.class).get());
        Intent intent = service.createAccountLoginIntent(wrapper.getContext(), request, getAuthCodeListener());

        Assert.assertTrue(intent.hasExtra(KakaoWebViewActivity.KEY_URL));
        Assert.assertTrue(intent.hasExtra(KakaoWebViewActivity.KEY_EXTRA_HEADERS));
        Assert.assertTrue(intent.hasExtra(KakaoWebViewActivity.KEY_USE_WEBVIEW_TIMERS));
        Assert.assertTrue(intent.hasExtra(KakaoWebViewActivity.KEY_RESULT_RECEIVER));


        Uri uri = Uri.parse(intent.getStringExtra(KakaoWebViewActivity.KEY_URL));
        Assert.assertEquals(appConfig.getAppKey(), uri.getQueryParameter(StringSet.client_id));
        Assert.assertEquals(StringSet.code, uri.getQueryParameter(StringSet.response_type));
        Assert.assertEquals(request.getRedirectURI(), uri.getQueryParameter(StringSet.redirect_uri));
    }

    @Test
    public void testOnReceivedResult() {
        Bundle bundle = new Bundle();
        bundle.putString(KakaoWebViewActivity.KEY_REDIRECT_URL, correctRedirectUri);
        service.onReceivedResult(expectedRequestCode, KakaoWebViewActivity.RESULT_SUCCESS, bundle, getAuthCodeListener());

        Assert.assertEquals(expectedRequestCode, actualRequestCode);
        Assert.assertTrue(authorizationResult.isSuccess());
        Assert.assertEquals(correctRedirectUri, authorizationResult.getRedirectURL());
        Assert.assertNull(authorizationResult.getException());
    }

    @Test
    public void testOnReceivedResultWithErrorRedirectUri() {

        String errorUri = redirectUri + "?error=" + otherErrorString + "&error_description=" + errorDesc;
        Bundle bundle = new Bundle();
        bundle.putString(KakaoWebViewActivity.KEY_REDIRECT_URL, errorUri);
        service.onReceivedResult(expectedRequestCode, KakaoWebViewActivity.RESULT_SUCCESS, bundle, getAuthCodeListener());

        Assert.assertEquals(expectedRequestCode, actualRequestCode);
        Assert.assertFalse(authorizationResult.isSuccess());
        Assert.assertTrue(authorizationResult.isAuthError());
        Assert.assertEquals(errorDesc, authorizationResult.getResultMessage());
        Assert.assertNull(authorizationResult.getException());
    }

    @Test
    public void testOnReceivedResultWithAccessDeniedError() {
        String errorUri = redirectUri + "?error=" + accessDeniedErrorString + "&error_description=" + errorDesc;
        Bundle bundle = new Bundle();
        bundle.putString(KakaoWebViewActivity.KEY_REDIRECT_URL, errorUri);
        service.onReceivedResult(expectedRequestCode, KakaoWebViewActivity.RESULT_SUCCESS, bundle, getAuthCodeListener());

        Assert.assertEquals(expectedRequestCode, actualRequestCode);
        Assert.assertFalse(authorizationResult.isSuccess());
        Assert.assertTrue(authorizationResult.isCanceled());
        Assert.assertTrue(authorizationResult.getResultMessage().contains("cancel button"));
        Assert.assertNull(authorizationResult.getException());
    }

    @Test
    public void testOnReceivedResultWithKakaoException() {
        KakaoException exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KakaoWebViewActivity.KEY_EXCEPTION, exception);

        service.onReceivedResult(expectedRequestCode, KakaoWebViewActivity.RESULT_ERROR, bundle, getAuthCodeListener());
        Assert.assertTrue(authorizationResult.isAuthError());
        Assert.assertFalse(authorizationResult.isSuccess());
        Assert.assertNotNull(authorizationResult.getException());
    }

    @Test
    public void testOnReceivedResultWithKakaoExceptionMessage() {
        String errorMsg = "Authorization failed.";
        KakaoException exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, errorMsg);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KakaoWebViewActivity.KEY_EXCEPTION, exception);

        service.onReceivedResult(expectedRequestCode, KakaoWebViewActivity.RESULT_ERROR, bundle, getAuthCodeListener());
        Assert.assertTrue(authorizationResult.isAuthError());
        Assert.assertFalse(authorizationResult.isSuccess());
        Assert.assertNotNull(authorizationResult.getException());
        Assert.assertEquals(errorMsg, authorizationResult.getResultMessage());
        Assert.assertEquals(errorMsg, authorizationResult.getException().getMessage());
    }

    @Test
    public void testOnReceivedResultWithCancel() {
        KakaoException cancelException = new KakaoException(KakaoException.ErrorType.CANCELED_OPERATION, "cancel");
        Bundle bundle = new Bundle();
        bundle.putSerializable(KakaoWebViewActivity.KEY_EXCEPTION, cancelException);

        service.onReceivedResult(expectedRequestCode, KakaoWebViewActivity.RESULT_ERROR, bundle, getAuthCodeListener());

        Assert.assertTrue(authorizationResult.isCanceled());
        Assert.assertFalse(authorizationResult.isSuccess());
        Assert.assertNull(authorizationResult.getException());
        Assert.assertTrue(authorizationResult.getResultMessage().contains("cancel"));
    }

    private AuthCodeListener getAuthCodeListener() {
        return new AuthCodeListener() {
            @Override
            public void onAuthCodeReceived(int requestCode, AuthorizationResult result) {
                actualRequestCode = requestCode;
                authorizationResult = result;
            }
        };
    }
}
