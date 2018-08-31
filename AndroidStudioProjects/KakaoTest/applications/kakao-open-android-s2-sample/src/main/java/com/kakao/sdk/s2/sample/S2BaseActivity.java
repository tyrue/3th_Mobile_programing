package com.kakao.sdk.s2.sample;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.s2.Event;
import com.kakao.s2.S2Service;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.Utility;

import java.util.Date;

public class S2BaseActivity extends AppCompatActivity {
    private ResponseCallback<Integer> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s2_base);

        callback = new ResponseCallback<Integer>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e(this.getClass().getName(), errorResult.toString());
                Toast.makeText(getApplicationContext(), "There was an error adding an event to batch.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Integer result) {
                Toast.makeText(getApplicationContext(), "Successfully added an event to batch. Current batch size is " + result + ".", Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected boolean isEventsOn(String prefKeyForActivity) {
        SharedPreferences preferences = getSharedPreferences(Utility.getAppPackageName(getApplicationContext()), MODE_PRIVATE);
        return preferences.getBoolean(prefKeyForActivity, false);
    }

    protected void addEventToBatch(final String targetActivity, final String action) {
        try {
            Event event = new Event.Builder().setTimestamp(new Date().getTime())
                    .setTo(targetActivity).setAction(action).build();
            S2Service.getInstance().addAdidEvent(event, callback);
        } catch (KakaoException e) {
            Log.e(this.getClass().getName(), e.toString());
        }
    }
}
