package com.kakao.auth.authorization.accesstoken;

import com.kakao.auth.helper.Encryptor;
import com.kakao.auth.mocks.TestEncryptor;
import com.kakao.auth.mocks.TestPersistentKVStore;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author kevin.kang. Created on 2017. 8. 9..
 */

public class EncryptedAccessTokenTest extends KakaoTestCase {
    private EncryptedAccessToken finalTokenInfo;
    private EncryptedAccessToken tokenInfo;
    private AccessToken decoratedAccessToken;

    @Before
    public void setup() {
        super.setup();
        decoratedAccessToken = TestAccessTokenFactory.createTestToken();
        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, null, true, new TestPersistentKVStore()));
        finalTokenInfo = Mockito.spy(new EncryptedAccessToken(null, null, true, new TestPersistentKVStore()));
    }

    @Test
    public void setEncryptor() {
        finalTokenInfo.setEncryptor(new TestEncryptor());
        Mockito.verify(finalTokenInfo).initAccessToken();
    }

    /**
     * Tests secure mode false -> true
     */
    @Test
    public void initWithEncryption() {
        Mockito.doReturn(false).when(tokenInfo).getLastSecureMode();
        tokenInfo.setEncryptor(new TestEncryptor());
        Assert.assertEquals("access_token", tokenInfo.getAccessToken());
        Assert.assertEquals("refresh_token", tokenInfo.getRefreshToken());
        Assert.assertTrue(tokenInfo.hasValidAccessToken());
        Assert.assertTrue(tokenInfo.hasRefreshToken());

        Mockito.reset(tokenInfo);

        Assert.assertNotEquals("access_token", decoratedAccessToken.getAccessToken());
        Assert.assertNotEquals("refres_token", decoratedAccessToken.getRefreshToken());
        Assert.assertTrue(decoratedAccessToken.hasValidAccessToken());
        Assert.assertTrue(decoratedAccessToken.hasRefreshToken());
        Assert.assertTrue(tokenInfo.getLastSecureMode());
    }

    /**
     * Tests secure mode false -> false, and true -> true
     */
    @Test
    public void initFalseToFalse() {
        // create EncryptedAccessToken
        decoratedAccessToken = TestAccessTokenFactory.createEmptyAccessToken();
        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, null, false, new TestPersistentKVStore()));

        // test if empty tokens are successfully created.
        Assert.assertNull(tokenInfo.getAccessToken());
        Assert.assertNull(tokenInfo.getRefreshToken());
        Assert.assertFalse(tokenInfo.hasValidAccessToken());
        Assert.assertFalse(tokenInfo.hasRefreshToken());

        // check if access token is created successfully from decorated access token
        decoratedAccessToken = TestAccessTokenFactory.createTestToken();
        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, null, false, new TestPersistentKVStore()));
        Mockito.doReturn(false).when(tokenInfo).getLastSecureMode(); // initial state: false
        tokenInfo.setEncryptor(new TestEncryptor());

        Assert.assertEquals("access_token", tokenInfo.getAccessToken());
        Assert.assertEquals("refresh_token", tokenInfo.getRefreshToken());
        Assert.assertTrue(tokenInfo.hasValidAccessToken());
        Assert.assertTrue(tokenInfo.hasRefreshToken());

        Assert.assertEquals("access_token", decoratedAccessToken.getAccessToken());
        Assert.assertEquals("refresh_token", decoratedAccessToken.getRefreshToken());
        Assert.assertTrue(decoratedAccessToken.hasValidAccessToken());
        Assert.assertTrue(decoratedAccessToken.hasRefreshToken());

        Assert.assertFalse(tokenInfo.getLastSecureMode());
    }

    /**
     * Tests secure mode true -> false
     */
    @Test
    public void initWithDecryption() {
        // create encrypted decorated access token
        decoratedAccessToken = TestAccessTokenFactory.createTestToken("access_tokenaccess_token", "refresh_tokenrefresh_token");
        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, null, false, new TestPersistentKVStore()));
        Mockito.doReturn(true).when(tokenInfo).getLastSecureMode(); // initial state: true
        tokenInfo.setEncryptor(new TestEncryptor());

        Mockito.reset(tokenInfo);

        Assert.assertEquals("access_token", tokenInfo.getAccessToken());
        Assert.assertEquals("refresh_token", tokenInfo.getRefreshToken());
        Assert.assertTrue(tokenInfo.hasValidAccessToken());
        Assert.assertTrue(tokenInfo.hasRefreshToken());

        Assert.assertEquals(tokenInfo.getAccessToken(), decoratedAccessToken.getAccessToken());
        Assert.assertEquals(tokenInfo.getRefreshToken(), decoratedAccessToken.getRefreshToken());
        Assert.assertEquals(tokenInfo.accessTokenExpiresAt(), decoratedAccessToken.accessTokenExpiresAt());
        Assert.assertEquals(tokenInfo.refreshTokenExpiresAt(), decoratedAccessToken.refreshTokenExpiresAt());
        Assert.assertFalse(tokenInfo.getLastSecureMode());
    }

    /**
     * Tests secure mode true -> true
     */
    @Test
    public void initTrueToTrue() {
        decoratedAccessToken = TestAccessTokenFactory.createTestToken("access_tokenaccess_token", "refresh_tokenrefresh_token");
        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, null, true, new TestPersistentKVStore()));
        Mockito.doReturn(true).when(tokenInfo).getLastSecureMode(); // initiali state: true
        tokenInfo.setEncryptor(new TestEncryptor());

        Assert.assertEquals("access_token", tokenInfo.getAccessToken());
        Assert.assertEquals("refresh_token", tokenInfo.getRefreshToken());
        Assert.assertTrue(tokenInfo.hasValidAccessToken());
        Assert.assertTrue(tokenInfo.hasRefreshToken());
        Assert.assertNotEquals(tokenInfo.getAccessToken(), decoratedAccessToken.getAccessToken());
        Assert.assertNotEquals(tokenInfo.getRefreshToken(), decoratedAccessToken.getRefreshToken());

        Assert.assertTrue(tokenInfo.getLastSecureMode());
    }

    @Test
    public void updateWithEncryption() {
        decoratedAccessToken = TestAccessTokenFactory.createTestToken();
        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, new TestEncryptor(), true, new TestPersistentKVStore()));

        Assert.assertEquals("access_token", tokenInfo.getAccessToken());
        Assert.assertEquals("refresh_token", tokenInfo.getRefreshToken());
        Assert.assertTrue(tokenInfo.hasValidAccessToken());
        Assert.assertTrue(tokenInfo.hasRefreshToken());
        Assert.assertNotEquals(tokenInfo.getAccessToken(), decoratedAccessToken.getAccessToken());
        Assert.assertNotEquals(tokenInfo.getRefreshToken(), decoratedAccessToken.getRefreshToken());

        tokenInfo.updateAccessToken(TestAccessTokenFactory.createTestToken("access_token2", "refresh_token2"));

        Assert.assertEquals("access_token2", tokenInfo.getAccessToken());
        Assert.assertEquals("refresh_token2", tokenInfo.getRefreshToken());
        Assert.assertTrue(tokenInfo.hasValidAccessToken());
        Assert.assertTrue(tokenInfo.hasRefreshToken());
        Assert.assertEquals("access_token2access_token2", decoratedAccessToken.getAccessToken());
        Assert.assertEquals("refresh_token2refresh_token2", decoratedAccessToken.getRefreshToken());
    }

    @Test
    public void decryptionFails() {
        decoratedAccessToken = TestAccessTokenFactory.createTestToken("access_tokenaccess_token", "refresh_tokenrefresh_token");
        Encryptor encryptor = Mockito.spy(new TestEncryptor());
        try {
            Mockito.doThrow(new IllegalArgumentException()).when(encryptor).decrypt(ArgumentMatchers.anyString());
        } catch (Exception e) {
            Assert.fail();
        }

        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, null, true, new TestPersistentKVStore()));
        Mockito.doReturn(true).when(tokenInfo).getLastSecureMode(); // initiali state: true
        tokenInfo.setEncryptor(encryptor);

        Assert.assertNull(tokenInfo.getAccessToken());
        Assert.assertNull(tokenInfo.getRefreshToken());
        Assert.assertFalse(tokenInfo.hasValidAccessToken());
        Assert.assertFalse(tokenInfo.hasRefreshToken());

        Assert.assertTrue(tokenInfo.getLastSecureMode());
    }

    @Test
    public void encryptionFails() {
        decoratedAccessToken = TestAccessTokenFactory.createEmptyAccessToken();
        Encryptor encryptor = Mockito.spy(new TestEncryptor());
        try {
            Mockito.doThrow(new IllegalArgumentException()).when(encryptor).encrypt(ArgumentMatchers.anyString());
        } catch (Exception e) {
            Assert.fail();
        }

        tokenInfo = Mockito.spy(new EncryptedAccessToken(decoratedAccessToken, null, true, new TestPersistentKVStore()));
        Mockito.doReturn(true).when(tokenInfo).getLastSecureMode(); // initiali state: true
        tokenInfo.setEncryptor(encryptor);

        tokenInfo.updateAccessToken(TestAccessTokenFactory.createTestToken("access_token", "refresh_token"));

        Assert.assertNull(tokenInfo.getAccessToken());
        Assert.assertNull(tokenInfo.getRefreshToken());
        Assert.assertFalse(tokenInfo.hasValidAccessToken());
        Assert.assertFalse(tokenInfo.hasRefreshToken());

        Assert.assertTrue(tokenInfo.getLastSecureMode());
    }
}
