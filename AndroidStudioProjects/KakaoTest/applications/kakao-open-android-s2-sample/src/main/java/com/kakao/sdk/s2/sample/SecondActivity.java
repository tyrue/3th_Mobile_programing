package com.kakao.sdk.s2.sample;

import android.os.Bundle;

public class SecondActivity extends S2BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        if (isEventsOn(getResources().getString(R.string.text_activity2))) {
            addEventToBatch(getResources().getString(R.string.text_activity2), "enter");
        }
    }
}
