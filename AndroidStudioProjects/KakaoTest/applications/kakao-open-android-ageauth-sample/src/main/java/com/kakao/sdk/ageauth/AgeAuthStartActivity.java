package com.kakao.sdk.ageauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.kakao.sdk.ageauth.sample.R;

/**
 * @author leoshin
 */
public class AgeAuthStartActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);

        findViewById(R.id.start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                showLoginActivity();
                break;
        }
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, AgeAuthLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
