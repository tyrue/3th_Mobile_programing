package com.kakao.sdk.s2.sample;

import android.os.Bundle;

public class ThirdActivity extends S2BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        if (isEventsOn(getResources().getString(R.string.text_activity3))) {
            addEventToBatch(getResources().getString(R.string.text_activity3), "enter");
        }
    }
}
