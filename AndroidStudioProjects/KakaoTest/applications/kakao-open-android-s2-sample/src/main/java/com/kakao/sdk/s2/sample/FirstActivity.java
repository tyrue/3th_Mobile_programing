package com.kakao.sdk.s2.sample;

import android.os.Bundle;

public class FirstActivity extends S2BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        if (isEventsOn(getResources().getString(R.string.text_activity1))) {
            addEventToBatch(getResources().getString(R.string.text_activity1), "enter");
        }
    }
}
