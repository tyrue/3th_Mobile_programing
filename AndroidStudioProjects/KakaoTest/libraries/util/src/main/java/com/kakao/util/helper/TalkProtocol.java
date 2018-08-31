/**
 * Copyright 2014-2015 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.util.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Talk과 통신하기 위한 protocol
 * authorization을 talk을 통해 하는 경우
 * kakaolink를 사용하는 경우
 *
 * @author MJ
 */
@Deprecated
public class TalkProtocol extends KakaoServiceProtocol {
    // capri login, kakaolink3.5
    private static final int TALK_MIN_VERSION_SUPPORT_CAPRI = 139; // android 4.2.0
    private static final int TALK_MIN_VERSION_SUPPORT_LINK_40 = 1400255; // 6.0.0
    private static final String INTENT_ACTION_LOGGED_IN_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_IN_ACTIVITY";
    private static final int TALK_MIN_VERSION_SUPPORT_PROJEC_LOGIN = 178; // android 4.7.5

    @Deprecated
    public static Intent createKakakoTalkLinkIntent(final Context context, final String linkMessage) {
        final Uri kakaoLinkUri = Uri.parse(linkMessage);
        final Intent intent = new Intent(Intent.ACTION_SEND, kakaoLinkUri);
        // kakaolink를 지원하는 kakaotalk이 install 되어 있는지.
        return checkSupportedService(context, intent, TalkProtocol.TALK_MIN_VERSION_SUPPORT_CAPRI);
    }
}
