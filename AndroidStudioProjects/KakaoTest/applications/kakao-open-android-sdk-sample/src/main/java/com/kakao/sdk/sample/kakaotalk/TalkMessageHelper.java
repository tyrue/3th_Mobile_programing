package com.kakao.sdk.sample.kakaotalk;

import android.content.Context;
import android.content.DialogInterface;

import com.kakao.sdk.sample.R;
import com.kakao.sdk.sample.common.widget.DialogBuilder;
import com.kakao.sdk.sample.friends.FriendsMainActivity.MSG_TYPE;
import com.kakao.util.helper.log.Logger.DeployPhase;

/**
 * @author leoshin on 15. 9. 4.
 */
class TalkMessageHelper {
    static String getMemoTemplateId() {
        switch (DeployPhase.current()) {
            case Local:
            case Alpha:
                return "20253";
            case Sandbox:
                return "224";
            case Beta:
            case Release:
                return "3356";
            default:
                return null;
        }
    }

    static String getSampleTemplateId(MSG_TYPE msgType) {
        switch (DeployPhase.current()) {
            case Local:
            case Alpha:
                return getAlphaTemplateId(msgType);
            case Sandbox:
                return getSandboxTemplateId(msgType);
            case Beta:
            case Release:
                return getReleaseTemplateId(msgType);
            default:
                return null;
        }
    }

    static String getAlphaTemplateId(MSG_TYPE msgType) {
        switch (msgType) {
            case FEED:
                return "20253";
            case LIST:
                return "20254";
            default:
                return "20253";
        }
    }

    static String getSandboxTemplateId(MSG_TYPE msgType) {
        switch (msgType) {
            case FEED:
                return "224";
            case LIST:
                return "225";
            default:
                return "224";
        }
    }

    static String getReleaseTemplateId(MSG_TYPE msgType) {
        switch (msgType) {
            case FEED:
                return "3356";
            case LIST:
                return "3357";
            default:
                return "3356";
        }
    }

    static void showSendMessageDialog(Context context, final DialogInterface.OnClickListener listener) {
        final String message = context.getString(R.string.send_message);
        new DialogBuilder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onClick(dialog, which);
                        }
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
