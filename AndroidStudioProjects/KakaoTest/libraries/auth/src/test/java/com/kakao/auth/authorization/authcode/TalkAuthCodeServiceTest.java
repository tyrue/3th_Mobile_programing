package com.kakao.auth.authorization.authcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthCodeCallback;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.StringSet;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.auth.helper.StartActivityWrapper;
import com.kakao.auth.mocks.TestAppConfig;
import com.kakao.auth.mocks.TestAuthCodeRequestFactory;
import com.kakao.auth.mocks.TestKakaoProtocolService;
import com.kakao.network.ErrorResult;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.AppConfig;
import com.kakao.util.protocol.KakaoProtocolService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kevin.kang. Created on 2017. 6. 1..
 */

public class TalkAuthCodeServiceTest extends KakaoTestCase {
    private AppConfig appConfig = TestAppConfig.createTestAppConfig();
    private ISessionConfig sessionConfig;
    private Activity activity;
    private TalkAuthCodeService service;
    private KakaoProtocolService protocolService;
    private List<String> events;

    private String redirectUri = StringSet.REDIRECT_URL_PREFIX + appConfig.getAppKey() + StringSet.REDIRECT_URL_POSTFIX;
    private String wrongRedirectUri = StringSet.REDIRECT_URL_PREFIX + appConfig.getAppKey() + "2" + StringSet.REDIRECT_URL_POSTFIX;
    private String expectedAuthCode = "12345";
    private String authCodePostfix = "?code=" + expectedAuthCode;
    private String correctRedirectUri = redirectUri + authCodePostfix;

    @Before
    public void setup() {
        super.setup();

        events = new ArrayList<>();

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

        activity = Robolectric.buildActivity(Activity.class).get();
        protocolService = Mockito.spy(new TestKakaoProtocolService());
        service = Mockito.spy(new TalkAuthCodeService(RuntimeEnvironment.application, appConfig, sessionConfig, protocolService));
    }

    @Test
    public void testCreateLoginIntent() {
        Bundle bundle = new Bundle();
        bundle.putString(StringSet.approval_type, ApprovalType.INDIVIDUAL.toString());
        Intent intent = service.createLoggedInActivityIntent(bundle);

        Assert.assertEquals(TalkAuthCodeService.INTENT_ACTION_LOGGED_IN_ACTIVITY, intent.getAction());
        Assert.assertTrue(intent.getCategories().contains(Intent.CATEGORY_DEFAULT));
        Assert.assertEquals(appConfig.getAppKey(), intent.getExtras().get(TalkAuthCodeService.EXTRA_APPLICATION_KEY));
        Assert.assertEquals(service.redirectUriString, intent.getExtras().get(TalkAuthCodeService.EXTRA_REDIRECT_URI));
        Assert.assertEquals(appConfig.getKaHeader(), intent.getExtras().get(TalkAuthCodeService.EXTRA_KA_HEADER));
        Assert.assertEquals(TalkAuthCodeService.PROTOCOL_VERSION, intent.getExtras().get(TalkAuthCodeService.EXTRA_PROTOCOL_VERSION));

        Assert.assertTrue(intent.getExtras().get(TalkAuthCodeService.EXTRA_EXTRAPARAMS) instanceof Bundle);

        Bundle extras = (Bundle) intent.getExtras().get(TalkAuthCodeService.EXTRA_EXTRAPARAMS);
        Assert.assertNotNull(extras);
        Assert.assertEquals(ApprovalType.INDIVIDUAL.toString(), extras.get(StringSet.approval_type));

    }

    @Test
    public void testIsLoginAvailable() {
        Assert.assertTrue(service.isLoginAvailable());
        Mockito.doReturn(null).when(protocolService).resolveIntent(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());
        Assert.assertFalse(service.isLoginAvailable());
    }

    @Test
    public void testRequestAuthCode() {
        final AuthCodeRequest request = TestAuthCodeRequestFactory.createAuthCodeRequest(2, appConfig.getAppKey(), sessionConfig, getAuthCodeCallback());
        StartActivityWrapper wrapper = new StartActivityWrapper(activity);

        final AuthCodeListener listener = new AuthCodeListener() {
            @Override
            public void onAuthCodeReceived(int requestCode, AuthorizationResult result) {
                events.add("success");
            }
        };
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                listener.onAuthCodeReceived(2, AuthorizationResult.createSuccessAuthCodeResult(correctRedirectUri));
                return true;
            }
        }).when(service).startActivityForResult(ArgumentMatchers.any(StartActivityWrapper.class), ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());
        service.requestAuthCode(request, wrapper, listener);

        Assert.assertTrue(events.contains("success"));
    }

    @Test
    public void testRequestAuthCodeWithPass() {
        final AuthCodeRequest request = TestAuthCodeRequestFactory.createAuthCodeRequest(2, appConfig.getAppKey(), sessionConfig, getAuthCodeCallback());
        StartActivityWrapper wrapper = new StartActivityWrapper(activity);

        final AuthCodeListener listener = new AuthCodeListener() {
            @Override
            public void onAuthCodeReceived(int requestCode, AuthorizationResult result) {
                events.add("success");
            }
        };
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return false;
            }
        }).when(service).startActivityForResult(ArgumentMatchers.any(StartActivityWrapper.class), ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());
        service.requestAuthCode(request, wrapper, listener);
        Assert.assertFalse(events.contains("success"));
    }

    @Test
    public void testRequestAuthCodeWithoutTalk() {
        final AuthCodeRequest request = TestAuthCodeRequestFactory.createAuthCodeRequest(2, appConfig.getAppKey(), sessionConfig, getAuthCodeCallback());
        StartActivityWrapper wrapper = new StartActivityWrapper(activity);

        final AuthCodeListener listener = new AuthCodeListener() {
            @Override
            public void onAuthCodeReceived(int requestCode, AuthorizationResult result) {
                events.add("success");
            }
        };
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return false;
            }
        }).when(service).startActivityForResult(ArgumentMatchers.any(StartActivityWrapper.class), ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());
        Mockito.doReturn(null).when(service).createLoggedInActivityIntent(ArgumentMatchers.any(Bundle.class));
        Assert.assertFalse(service.requestAuthCode(request, wrapper, listener));
    }

    private AuthCodeCallback getAuthCodeCallback() {
        return new AuthCodeCallback() {
            @Override
            public void onAuthCodeReceived(String authCode) {
            }

            @Override
            public void onAuthCodeFailure(ErrorResult errorResult) {
            }
        };
    }
}
