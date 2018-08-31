package com.kakao.auth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kakao.util.apicompatibility.APICompatibility;
import com.kakao.util.helper.log.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leoshin on 15. 11. 19.
 */
public class SmsReceiver extends BroadcastReceiver {
    public interface ISmsReceiver {
        void onCompleteSms(String code);
    }
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final ISmsReceiver listener;

    public SmsReceiver(ISmsReceiver listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("SmsReceiver:onReceive()");
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Logger.w("BroadcastReceiver failed, no intent data to process.");
            return;
        }
        if (intent.getAction().equals(ACTION)) {
            Logger.d("SMS_RECEIVED");

            String smsDisplayMessage = APICompatibility.getInstance().getSmsMessage(intent);
            String passCode = parsePassCode(smsDisplayMessage);
            listener.onCompleteSms(passCode);
        }
    }

    private final static String regex = "(^\\[드림시큐리티\\]).*\\[([0-9]{6})";
    private static String parsePassCode(String message) {
        Logger.d(message);
        if (TextUtils.isEmpty(message)) {
            return message;
        }

        String passCode = null;
        String reg = regex;

        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE | Pattern.UNIX_LINES | Pattern.DOTALL);
        Matcher match = pattern.matcher(message);

        if (match.find()) {
            passCode = match.group(2);
            Logger.d(passCode);
        }
        return passCode;
    }
}
