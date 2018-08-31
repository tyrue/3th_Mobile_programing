package com.kakao.util.apicompatibility;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.webkit.CookieManager;

import com.kakao.util.helper.log.Logger;

/**
 * @author leoshin on 15. 11. 4.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
class APILevel21Compatibility extends APILevel19Compatibility {
    @Override
    public void removeCookie(Context context, String domain) {
        // CookieManager를 쓰려면 CookieSyncManager를 만들어야 하는 버그가 있다.
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

    @Override
    public void removeCookie(Context context, String domain, String name) {
        // CookieManager를 쓰려면 CookieSyncManager를 만들어야 하는 버그가 있다.
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
            if (cookieNameAndValue.length > 0 && cookieNameAndValue[0].trim().startsWith(name)) {
                String revisedCookie = cookieNameAndValue[0].trim() + "=;expires=Web, 18 Mar 2010 09:00:01 GMT;";
                cookieManager.setCookie(domain, revisedCookie);
            }
        }
        cookieManager.flush();
    }
}
