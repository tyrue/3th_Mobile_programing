package com.kakao.auth.authorization.accesstoken;


import com.kakao.auth.StringSet;
import com.kakao.auth.network.response.AuthResponse;
import com.kakao.network.response.ResponseBody;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.helper.SharedPreferencesCache;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;


import java.util.Date;

/**
 * This class mocks KakaoSDK, Utility, SharedPreferenceCache, ResponseBody for solely unit-testing AccessTokenImpl class.
 * @author kevin.kang
 * Created by kevin.kang on 16. 8. 11..
 */
public class AccessTokenImplTest extends KakaoTestCase {

    private static final String appKey = "appkey";
    private static final String accessToken = "accessToken";
    private static final String refreshToken = "refreshToken";
    private static final Long expiresIn = 3600 * 12L;
    private static final Long expiresAt = new Date().getTime() + expiresIn;
    private static final String FAKE_KEY_HASH = "lMXltzn4zSwq0EhwLKAo+k0zhqI=";

    private SharedPreferencesCache cache;
    private AccessTokenImpl tokenInfo;
    @Before
    public void setup() {
        super.setup();
        cache = Mockito.spy(new SharedPreferencesCache(RuntimeEnvironment.application.getApplicationContext(), appKey));
    }

    @Test
    public void testCreateEmptyToken() {
        tokenInfo = (AccessTokenImpl) TestAccessTokenFactory.createEmptyAccessToken();
        Assert.assertNotNull(tokenInfo);
        Assert.assertNull(tokenInfo.getAccessToken());
        Assert.assertNull(tokenInfo.getRefreshToken());
        Assert.assertFalse(tokenInfo.hasValidAccessToken());
        Assert.assertFalse(tokenInfo.hasRefreshToken());
    }

    @Test
    public void testCreateFromCache() {
        tokenInfo = new AccessTokenImpl(new TestAccessToken());
        Assert.assertTrue(tokenInfo.hasValidAccessToken());
        Assert.assertTrue(tokenInfo.hasRefreshToken());
        Assert.assertEquals("access_token", tokenInfo.getAccessToken());
        Assert.assertEquals("refresh_token", tokenInfo.getRefreshToken());
    }

    @Test
    public void createFromResponse() {
        try {
            AccessToken tokenInfo = new AccessTokenImpl(createTokenResponseBody(true));

            Assert.assertEquals("access_token", tokenInfo.getAccessToken());
            Assert.assertEquals("refresh_token", tokenInfo.getRefreshToken());
            Assert.assertTrue(tokenInfo.hasValidAccessToken());
            Assert.assertTrue(tokenInfo.hasRefreshToken());


            ResponseBody body = createTokenResponseBody(false);
            tokenInfo = new AccessTokenImpl(body);
            Assert.assertEquals("access_token", tokenInfo.getAccessToken());
            Assert.assertNull(tokenInfo.getRefreshToken());
            Assert.assertTrue(tokenInfo.hasValidAccessToken());
            Assert.assertFalse(tokenInfo.hasRefreshToken());

        } catch (ResponseBody.ResponseBodyException e) {
            e.printStackTrace();
        } catch (AuthResponse.AuthResponseStatusError authResponseStatusError) {
            authResponseStatusError.printStackTrace();
        }
    }

    @Test
    public void createFromResponseWithException() {
    }

    ResponseBody createTokenResponseBody(boolean hasRefreshToken) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(StringSet.access_token, "access_token");
            jsonObject.put(StringSet.expires_in, 43199);
            if (hasRefreshToken) {
                jsonObject.put(StringSet.refresh_token, "refresh_token");
            }
            return new ResponseBody(200, jsonObject);
        } catch (JSONException|ResponseBody.ResponseBodyException e) {
            return null;
        }
    }
}
