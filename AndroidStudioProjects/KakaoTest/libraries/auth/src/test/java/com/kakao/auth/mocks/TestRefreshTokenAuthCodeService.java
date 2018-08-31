package com.kakao.auth.mocks;

import android.content.Intent;

import com.kakao.auth.authorization.authcode.AuthCodeListener;
import com.kakao.auth.authorization.authcode.AuthCodeRequest;
import com.kakao.auth.authorization.authcode.AuthCodeService;
import com.kakao.auth.helper.StartActivityWrapper;

import java.util.List;

/**
 * @author kevin.kang. Created on 2017. 6. 1..
 */

public class TestRefreshTokenAuthCodeService implements AuthCodeService {
    @Override
    public boolean requestAuthCode(AuthCodeRequest request, StartActivityWrapper wrapper, AuthCodeListener listener) {
        return false;
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data, AuthCodeListener listener) {
        return false;
    }

    @Override
    public boolean isLoginAvailable() {
        return true;
    }
}
