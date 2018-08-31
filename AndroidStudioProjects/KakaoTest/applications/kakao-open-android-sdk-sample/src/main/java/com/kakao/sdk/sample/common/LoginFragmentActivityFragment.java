package com.kakao.sdk.sample.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.sdk.sample.R;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

/**
 * Sample login fragmeent. Note that LoginButton's setFragment() should be called in order for
 * login button to work properly.
 */
public class LoginFragmentActivityFragment extends android.support.v4.app.Fragment {

    private SessionCallback callback;

    public LoginFragmentActivityFragment() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_fragment, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button_fragment);
        loginButton.setSuportFragment(this); // set fragment for LoginButton

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            final Intent intent = new Intent(getActivity(), SampleSignupActivity.class);
            startActivity(intent);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
}
