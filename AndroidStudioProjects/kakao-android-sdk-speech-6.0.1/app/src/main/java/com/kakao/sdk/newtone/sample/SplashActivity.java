package com.kakao.sdk.newtone.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static java.lang.Thread.sleep;

/**
 * Created by jack on 26/07/2017.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            sleep(800);
        }
        catch(Exception e)
        {

        }

        Intent intent = new Intent(this, SpeechSampleActivity.class);
        startActivity(intent);
        finish();
    }
}
