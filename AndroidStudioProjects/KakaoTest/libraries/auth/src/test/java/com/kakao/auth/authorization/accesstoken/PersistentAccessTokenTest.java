package com.kakao.auth.authorization.accesstoken;

import com.kakao.auth.mocks.TestPersistentKVStore;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.helper.PersistentKVStore;
import com.kakao.util.helper.log.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @author kevin.kang. Created on 2017. 7. 25..
 */

public class PersistentAccessTokenTest extends KakaoTestCase {
    private PersistentAccessToken accessToken;
    private PersistentKVStore persistentKVStore;

    @Before
    public void setup() {
        super.setup();
        persistentKVStore = new TestPersistentKVStore();
        accessToken = new PersistentAccessToken(null, persistentKVStore);
    }

    @Test
    public void init() {
        Assert.assertFalse(accessToken.hasValidAccessToken());
        Assert.assertFalse(accessToken.hasRefreshToken());
        Assert.assertEquals(0, accessToken.getRemainingExpireTime());
    }

    @Test
    public void updateAccessToken() {
        accessToken.updateAccessToken(createTestToken());
        Assert.assertEquals("access_token", accessToken.getAccessToken());
        Assert.assertEquals("refresh_token", accessToken.getRefreshToken());
        Assert.assertTrue(accessToken.hasValidAccessToken());
        Assert.assertTrue(accessToken.hasRefreshToken());
        Assert.assertFalse(accessToken.getRemainingExpireTime() == 0);
    }

    @Test
    public void updateWithEmptyAccessToken() {
        accessToken.updateAccessToken(createEmptyAccessToken());

        Assert.assertFalse(accessToken.hasValidAccessToken());
        Assert.assertFalse(accessToken.hasRefreshToken());

        Assert.assertEquals(null, accessToken.getAccessToken());
        Assert.assertEquals(null, accessToken.getRefreshToken());
        Assert.assertEquals(0, accessToken.getRemainingExpireTime());
    }

    @Test
    public void updateWithAccessTokenOnly() {
        accessToken.updateAccessToken(createWithAccessTokenOnly());
        Assert.assertTrue(accessToken.hasValidAccessToken());
        Assert.assertFalse(accessToken.hasRefreshToken());

        Assert.assertEquals("access_token", accessToken.getAccessToken());
        Assert.assertNull(accessToken.getRefreshToken());
        Assert.assertTrue(accessToken.getRemainingExpireTime() != 0);
    }

    @Test
    public void clearTokens() {
        accessToken.updateAccessToken(createTestToken());
        Assert.assertTrue(accessToken.hasRefreshToken());
        Assert.assertTrue(accessToken.hasValidAccessToken());

        accessToken.clearAccessToken();
        Assert.assertFalse(accessToken.hasValidAccessToken());
        Assert.assertFalse(accessToken.hasRefreshToken());
        Assert.assertTrue(accessToken.getRemainingExpireTime() == 0);
    }

    AccessToken createTestToken() {
        Date accessTokenDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        Date refreshTokenDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30);
        AccessToken updateToken = new AccessTokenImpl("access_token", "refresh_token", accessTokenDate, refreshTokenDate);
        return updateToken;
    }

    AccessToken createEmptyAccessToken() {
        return new AccessTokenImpl(null, null, null, null);
    }

    AccessToken createWithAccessTokenOnly() {
        return new AccessTokenImpl("access_token", null, new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24), null);
    }
}
