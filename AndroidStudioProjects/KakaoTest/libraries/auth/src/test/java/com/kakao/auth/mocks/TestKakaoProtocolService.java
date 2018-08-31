package com.kakao.auth.mocks;

import android.content.Context;
import android.content.Intent;

import com.kakao.util.protocol.KakaoProtocolService;

/**
 * @author kevin.kang. Created on 2017. 5. 30..
 */

public class TestKakaoProtocolService implements KakaoProtocolService {
    @Override
    public Intent resolveIntent(Context context, Intent intent, int minVersion) {
        return intent;
    }
}
