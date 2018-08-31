/*
  Copyright 2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.auth.authorization.authcode;

import android.os.Build;

import com.kakao.auth.KakaoSDK;

/**
 * This is a wrapper interface for managing webview cookies. Either CookieManager or CookieSyncManager
 * is used depending on Android API level.
 *
 * @author kevin.kang. Created on 2017. 5. 22..
 */

public interface KakaoCookieManager {
    /**
     * Flushes cashes to persistent storage.
     */
    void flush();

    /**
     * Removes all cookies for kakao domains.
     */
    void removeCookiesForKakaoDomain();


    class Factory {
        private static KakaoCookieManager instance;

        /**
         * Returns a singleton instance for managing webview cookies for the application.
         *
         * @return a singleton instance for managing webview cookies for the application
         */
        public static KakaoCookieManager getInstance() {
            if (instance == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    instance = new APILevel21CookieManager(KakaoSDK.getAdapter().getApplicationConfig().getApplicationContext());
                } else {
                    instance = new APILevel9CookieManager(KakaoSDK.getAdapter().getApplicationConfig().getApplicationContext());
                }
            }
            return instance;
        }
    }
}
