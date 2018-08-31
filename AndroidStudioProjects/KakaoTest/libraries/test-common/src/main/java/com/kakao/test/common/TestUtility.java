package com.kakao.test.common;

import java.util.Date;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2016. 10. 10..
 */

public class TestUtility {
    private static final String accessToken = "accessToken";
    private static final String refreshToken = "refreshToken";
    private static final Long expiresIn = 3600 * 12L;
    private static final Long expiresAt = new Date().getTime() + expiresIn;

    public static String getFakeKeyHash() {
        return "lMXltzn4zSwq0EhwLKAo+k0zhqI=";
    }

    public static String getFakeAppKey() {
        return "1076e5ad28edada1acf30951a8a11c31";
    }

    public static String getFakeAuthCode() {
        return "lMXltzn4zSwq0EhwLKAo+k0zhqI=";
    }

    public static String getFakeAccessToken() {
        return "lMXltzn4zSwq0EhwLKAo+k0zhqI=";
    }
}
