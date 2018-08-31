package com.kakao.auth.authorization.authcode;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.kakao.network.ServerProtocol;
import com.kakao.util.helper.log.Logger;

/**
 * @author kevin.kang. Created on 2017. 7. 24..
 */

class APILevel9CookieManager implements KakaoCookieManager {
    protected static final String COOKIE_SEPERATOR = ";";
    protected static final String COOKIE_NAME_VALUE_DELIMITER = "=";

    protected final Context context;

    APILevel9CookieManager(final Context context) {
        this.context = context;
        CookieSyncManager.createInstance(context);
    }

    @Override
    public void flush() {
        CookieSyncManager cookieSyncManager = CookieSyncManager.getInstance();
        if (cookieSyncManager != null) {
            cookieSyncManager.sync();
        }
    }

    @Override
    public void removeCookiesForKakaoDomain() {
        removeCookie("kakao.com");
        removeCookie(".kakao.com");
        removeCookie( "kakao.co.kr");
        removeCookie(".kakao.co.kr");
        removeCookie(ServerProtocol.AUTH_AUTHORITY);
        removeCookie(ServerProtocol.AGE_AUTH_AUTHORITY);
        removeCookie(ServerProtocol.API_AUTHORITY);
    }

    void removeCookie(final String domain) {
        // CookieManager를 쓰려면 CookieSyncManager를 만들어야 하는 버그가 있다.
        CookieSyncManager syncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();

        // domain으로 쿠키를 삭제하는 API가 제공되지 않으므로 삭제하고 싶은 쿠키를 강제로 expire 시킨다음 removeExpiredCookie 호출한다.
        String cookieForDomain = cookieManager.getCookie(domain);
        if (cookieForDomain == null) {
            return;
        }

        String[] cookiesForDomain = cookieForDomain.split(COOKIE_SEPERATOR);
        for (String currentCookie : cookiesForDomain) {
            Logger.d("++ currentCookie : " + currentCookie);
            String[] cookieNameAndValue = currentCookie.split(COOKIE_NAME_VALUE_DELIMITER);
            if (cookieNameAndValue.length > 0 ) {
                String revisedCookie = cookieNameAndValue[0].trim() + "=;expires=Web, 18 Mar 2010 09:00:01 GMT;";
                cookieManager.setCookie(domain, revisedCookie);
            }
        }
        syncManager.sync();
    }
}
