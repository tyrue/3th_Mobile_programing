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
package com.kakao.util.protocol;

import android.content.Context;
import android.content.Intent;

/**
 * This interface checks whether KakaoTalk or KakaoStory are installed or new enough to support
 * features offered by SDK. (such as Kakaolink or Kakao login)
 *
 * @author kevin.kang. Created on 2017. 5. 30..
 */

public interface KakaoProtocolService {
    /**
     * This method checks whether the given intent can be handled by the currently installed
     * kakaotalk or kakaostory.
     *
     * @param context Application context
     * @param intent Intent to be resolved. Usually intent targeting specific activities (by actions or schemes) of kakaotalk or kakaostory
     * @param minVersion Minimum version required for this intent and functionality
     * @return Resolved intent dervied from the intent provided as argument. If there are multiple
     */
    Intent resolveIntent(final Context context, final Intent intent, final int minVersion);

    /**
     * Factory class for maintaining singleton instance of KakaoProtocolService
     */
    class Factory {
        private static KakaoProtocolService instance;

        private Factory() {
        }

        /**
         * Returns a singleton instance of KakaoProtocolService used by this application
         *
         * @return a singleton instance of KakaoProtocolService used by this application
         */
        public static KakaoProtocolService getInstance() {
            if (instance == null) {
                synchronized (KakaoProtocolService.class) {
                    if (instance == null) {
                        instance = new KakaoApplicationServiceImpl();
                    }
                }
            }
            return instance;
        }
    }
}
