package com.kakao.auth.authorization.authcode;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;

import com.kakao.util.helper.log.Logger;

/**
 * @author kevin.kang. Created on 2017. 7. 24..
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class APILevel21CookieManager extends APILevel9CookieManager implements KakaoCookieManager {

    APILevel21CookieManager(final Context context) {
        super(context);
    }

    @Override
    public void flush() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager != null) {
            cookieManager.flush();
        }
    }

    void removeCookie(final String domain) {
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
            if (cookieNameAndValue.length > 0) {
                String revisedCookie = cookieNameAndValue[0].trim() + "=;expires=Web, 18 Mar 2010 09:00:01 GMT;";
                cookieManager.setCookie(domain, revisedCookie);
            }
        }
        cookieManager.flush();
    }
}
