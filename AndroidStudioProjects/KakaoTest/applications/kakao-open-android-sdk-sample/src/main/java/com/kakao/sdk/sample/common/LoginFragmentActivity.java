package com.kakao.sdk.sample.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.kakao.sdk.sample.R;

/**
 * Container activity for LoginFragment.
 */
public class LoginFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_fragment);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
