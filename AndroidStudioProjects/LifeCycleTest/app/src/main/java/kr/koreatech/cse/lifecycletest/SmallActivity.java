package kr.koreatech.cse.lifecycletest;

import android.app.Activity;
import android.os.Bundle;

public class SmallActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small);
    }
}