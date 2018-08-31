package com.kakao.auth.api;

import android.content.Context;

import com.kakao.test.common.KakaoTestCase;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

/**
 * @author kevin.kang. Created on 2017. 5. 19..
 */

public class AuthApiTest extends KakaoTestCase {
    private Context context;
    private AuthApi authApi;

    @Before
    public void setup() {
        super.setup();
        context = RuntimeEnvironment.application;
        authApi = new AuthApi();
    }

    @Test
    public void testRequestAccessToken() {

    }

    @Test
    public void testRequestAccesTokenInfo() {

    }
}
