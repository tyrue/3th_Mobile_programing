package com.kakao.auth.authorization.accesstoken;

import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.api.AuthApi;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.tasks.KakaoResultTask;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.util.concurrent.RoboExecutorService;

import java.util.Date;

/**
 * @author kevin.kang. Created on 2017. 5. 19..
 */

public class KakaoAccessTokenManagerTest extends KakaoTestCase {
    private Context context;
    private AuthApi authApi;
    private String appKey = "sample_app_key";
    private String authCode = "auth_code";
    private String refreshToken = "refresh_token";
    private String clientSecret = "client_secret";
    private ApprovalType approvalType = ApprovalType.INDIVIDUAL;
    private KakaoTaskQueue taskQueue;
    private KakaoAccessTokenManager manager;

    @Before
    public void setup() {
        super.setup();
        context = RuntimeEnvironment.application;
        authApi = new AuthApi();
        taskQueue = Mockito.spy(new KakaoTaskQueue(new RoboExecutorService()));
        authApi = Mockito.spy(authApi);
        manager = Mockito.spy(new KakaoAccessTokenManager(context, authApi, taskQueue, appKey, clientSecret, approvalType));
    }

    @Test
    public void testRequestAccessTokenByAuthCode() {
        try {
            AccessTokenImpl accessToken = new AccessTokenImpl("access_token", "refresh_token", new Date(), new Date());
            Mockito.doReturn(accessToken).when(authApi).requestAccessToken(context, appKey, authCode, null, clientSecret, approvalType.toString());
            manager.requestAccessTokenByAuthCode(authCode, null).get();
            Mockito.verify(authApi).requestAccessToken(context, appKey, authCode, null, clientSecret, approvalType.toString());
        } catch (Exception e) {

        }
    }

    @Test
    public void testGetAccessTokenTask() {
        KakaoResultTask<AccessToken> task = manager.getAccessTokenTask(authCode, null);
        try {
            Mockito.doReturn(AccessToken.Factory.createEmptyToken()).when(authApi).requestAccessToken(context, appKey, authCode, null, clientSecret, approvalType.toString());
            task.call();
            Mockito.verify(authApi).requestAccessToken(context, appKey, authCode, null, clientSecret, approvalType.toString());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetRefreshTokenTask() {
        KakaoResultTask<AccessToken> task = manager.getRefreshTokenTask(refreshToken, null);
        try {
            Mockito.doReturn(AccessToken.Factory.createEmptyToken()).when(authApi).requestAccessToken(context, appKey, null, refreshToken, clientSecret, approvalType.toString());
            task.call();
            Mockito.verify(authApi).requestAccessToken(context, appKey, null, refreshToken, clientSecret, approvalType.toString());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetAccessTokenInfoTask() {
        KakaoResultTask<AccessTokenInfoResponse> task = manager.getAccessTokenInfoResponseTask(null);
        try {
            Mockito.doReturn(null).when(authApi).requestAccessTokenInfo();
            task.call();
            Mockito.verify(authApi).requestAccessTokenInfo();
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }
}
