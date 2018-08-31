package com.kakao.sdk.s2.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class S2MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_main);

        findViewById(R.id.activity1).setOnClickListener(this);
        findViewById(R.id.activity2).setOnClickListener(this);
        findViewById(R.id.activity3).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity1:
                startActivity(new Intent(S2MainActivity.this, FirstActivity.class));
                break;
            case R.id.activity2:
                startActivity(new Intent(S2MainActivity.this, SecondActivity.class));
                break;
            case R.id.activity3:
                startActivity(new Intent(S2MainActivity.this, ThirdActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(S2MainActivity.this, SettingsActivity.class));
                break;
        }
    }
}
