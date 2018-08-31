package com.kakao.sdk.sample.kakaotalk;

import android.app.Application;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.kakao.auth.common.MessageSendable;
import com.kakao.friends.response.model.FriendInfo;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.ListTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.sample.R;
import com.kakao.sdk.sample.common.GlobalApplication;
import com.kakao.sdk.sample.common.log.Logger;
import com.kakao.sdk.sample.common.widget.KakaoToast;
import com.kakao.sdk.sample.friends.FriendsMainActivity;
import com.kakao.usermgmt.response.model.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author leo.shin
 */
public class KakaoTalkFriendListActivity extends FriendsMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final UserProfile userProfile = UserProfile.loadFromCache();
        if (userProfile != null) {
            View headerView = getLayoutInflater().inflate(R.layout.view_friend_item, list, false);

            NetworkImageView profileView = headerView.findViewById(R.id.profile_image);
            profileView.setDefaultImageResId(R.drawable.thumb_story);
            profileView.setErrorImageResId(R.drawable.thumb_story);
            TextView nickNameView = headerView.findViewById(R.id.nickname);

            String profileUrl = userProfile.getThumbnailImagePath();
            Application app  = GlobalApplication.getGlobalApplicationContext();
            if (profileUrl != null && profileUrl.length() > 0) {
                profileView.setImageUrl(profileUrl, ((GlobalApplication) app).getImageLoader());
            } else {
                profileView.setImageResource(R.drawable.thumb_story);
            }

            String nickName = getString(R.string.text_send_to_me) + " " + userProfile.getNickname();
            nickNameView.setText(nickName);

            list.addHeaderView(headerView);

            headerView.setOnClickListener(v -> {
                String message = "Test for send Memo";
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("''yy년 MM월 dd일 E요일", Locale.getDefault());
                KakaoTalkMessageBuilder builder = new KakaoTalkMessageBuilder();
                builder.addParam("username", userProfile.getNickname());
                builder.addParam("labelMsg", "Hi " + userProfile.getNickname() + ". this is test message");
                requestSendMemo(builder);
            });
        }
    }

    @Override
    public void onItemSelected(final int position, final FriendInfo friendInfo) {
        if (!friendInfo.isAllowedMsg()) {
            return;
        }

        TalkMessageHelper.showSendMessageDialog(this, (dialog, which) -> {
            MSG_TYPE type = MSG_TYPE.valueOf(msgType.getSelectedItemPosition());
            requestSendMessage(type, friendInfo, makeMessageBuilder(type, friendInfo.getProfileNickname()));
        });
    }

    private KakaoTalkMessageBuilder makeMessageBuilder(MSG_TYPE type, String nickName) {
        KakaoTalkMessageBuilder builder = new KakaoTalkMessageBuilder();

        if (type == MSG_TYPE.FEED) {
            builder.addParam("username", nickName);
            builder.addParam("labelMsg", "Hi " + nickName + ". this is test message");
        }

        return builder;
    }

    private void requestSendMessage(MSG_TYPE type, MessageSendable friendInfo, KakaoTalkMessageBuilder builder) {
        if (type == MSG_TYPE.DEFAULT) {
            requestDefaultMessage(friendInfo);
            return;
        }
        KakaoTalkService.getInstance().requestSendMessage(new TalkResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Logger.d("++ send message result : " + result);
                KakaoToast.makeToast(getApplicationContext(), "Send message success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNotKakaoTalkUser() {
                KakaoToast.makeToast(getApplicationContext(), "not a KakaoTalk user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                KakaoToast.makeToast(getApplicationContext(), "failure : " + errorResult, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                KakaoToast.makeToast(getApplicationContext(), "onNotSignedUp : " + "User Not Registed App", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDidStart() {
                showWaitingDialog();
            }

            @Override
            public void onDidEnd() {
                cancelWaitingDialog();
            }
        }, friendInfo, TalkMessageHelper.getSampleTemplateId(type), builder.build());
    }

    private void requestSendMemo(KakaoTalkMessageBuilder builder) {
        MSG_TYPE type = MSG_TYPE.valueOf(msgType.getSelectedItemPosition());
        if (type == MSG_TYPE.DEFAULT) {
            requestDefaultMemo();
            return;
        }
        KakaoTalkService.getInstance().requestSendMemo(new TalkResponseCallback<Boolean>() {
            @Override
            public void onNotKakaoTalkUser() {
                KakaoToast.makeToast(getApplicationContext(), "not a KakaoTalk user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                KakaoToast.makeToast(getApplicationContext(), "failure : " + errorResult, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                KakaoToast.makeToast(getApplicationContext(), "onNotSignedUp : " + "User Not Registed App", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(Boolean result) {
                KakaoToast.makeToast(getApplicationContext(), "Send message success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDidStart() {
                showWaitingDialog();
            }

            @Override
            public void onDidEnd() {
                cancelWaitingDialog();
            }
        }, TalkMessageHelper.getSampleTemplateId(type), builder.build());
    }

}
