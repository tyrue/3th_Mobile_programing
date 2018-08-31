package com.kakao.auth.authorization.accesstoken;

import com.kakao.test.common.KakaoTestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 7. 25..
 */

public class AccessTokenTest extends KakaoTestCase {
    private AccessToken tokenInfo;
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void createFromCache() {
        tokenInfo = new AccessTokenImpl(new TestAccessToken());
    }
}
