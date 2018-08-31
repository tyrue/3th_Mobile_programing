package com.kakao.sdk.sample.common;

import android.app.Activity;
import android.content.Intent;

import com.kakao.sdk.sample.common.widget.WaitingDialog;

/**
 * @author leoshin, created at 15. 7. 20..
 */
public abstract class BaseActivity extends Activity {
    protected void showWaitingDialog() {
        WaitingDialog.showWaitingDialog(this);
    }

    protected void cancelWaitingDialog() {
        WaitingDialog.cancelWaitingDialog();
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, RootLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, SampleSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
