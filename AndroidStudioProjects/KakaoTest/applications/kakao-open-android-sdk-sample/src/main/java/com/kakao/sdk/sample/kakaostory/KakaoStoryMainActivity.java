/**
 * Copyright 2014-2016 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.sdk.sample.kakaostory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.friends.request.FriendsRequest.FriendType;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.KakaoStoryService.StoryType;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest.StoryPermission;
import com.kakao.kakaostory.response.LinkInfoResponse;
import com.kakao.kakaostory.response.ProfileResponse;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.sample.R;
import com.kakao.sdk.sample.common.BaseActivity;
import com.kakao.sdk.sample.common.log.Logger;
import com.kakao.sdk.sample.common.widget.DialogBuilder;
import com.kakao.sdk.sample.common.widget.KakaoToast;
import com.kakao.sdk.sample.common.widget.ProfileLayout;
import com.kakao.sdk.sample.friends.FriendsMainActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.KakaoParameterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 카카오스토리 API인 프로필, 포스팅(이미지 업로드)를 테스트 한다.
 */
public class KakaoStoryMainActivity extends BaseActivity implements OnClickListener {
    private final String noteContent;
    private final String photoContent;
    private final String linkContent;

    private final String execParam = "place=1111";
    private final String marketParam = "referrer=kakaostory";
    private final String scrapUrl = "http://developers.kakao.com";
    private ProfileLayout profileLayout;
    private UserProfile userProfile;
    private Button getPostButton;
    private Button deletePostButton;
    private Button getPostsButton;
    private String lastMyStoryId;

    {
        noteContent = "A Rainbow - William Wordsworth\n" +
                "\n" +
                "My heart leaps up when I behold\n" +
                "A rainbow in the sky:\n" +
                "So was it when my life began;\n" +
                "So is it now I am a man;\n" +
                "So be it when I shall grow old,\n" +
                "Or let me die!\n" +
                "The Child is father of the Man;\n" +
                "I could wish my days to be\n" +
                "Bound each to each by natural piety.";
        photoContent = "This cafe is really awesome!";
        linkContent = "better than expected!";
    }

    /**
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkExecParams();
    }

    private void onClickIsStoryUser() {
        KakaoStoryService.requestIsStoryUser(new KakaoStoryResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                KakaoToast.makeToast(getApplicationContext(), "check story user : " + result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickProfile() {
        KakaoStoryService.requestProfile(new KakaoStoryResponseCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse result) {
                KakaoToast.makeToast(getApplicationContext(), "succeeded to get story profile", Toast.LENGTH_LONG).show();
                applyStoryProfileToView(result);
            }
        });
    }

    private void requestPostPhoto() {
        try {
            List<File> fileList = new ArrayList<File>();
            final File uploadFile = new File(writeStoryImage(R.drawable.kakaostory_animated_gif));
            fileList.add(uploadFile);

            KakaoStoryService.requestPostPhoto(new KakaoStoryResponseCallback<MyStoryInfo>() {
                @Override
                public void onSuccess(MyStoryInfo result) {
                    Logger.d(result.toString());
                    handleStoryPostResult(StoryType.PHOTO, result);
                }

                private void deleteUploadFiles() {
                    if (uploadFile != null && !uploadFile.delete()) {
                        Logger.w("failed to delete file: " + uploadFile.getPath());
                    }
                }

                @Override
                public void onDidEnd() {
                    deleteUploadFiles();
                }
            }, fileList, photoContent, StoryPermission.PUBLIC, true, execParam, execParam, marketParam, marketParam);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    private void requestPostNote() {
        // 앱이 설치되어 있는 경우 kakao<app_key>://kakaostory?place=1111 로 이동.
        // 앱이 설치되어 있지 않은 경우 market://details?id=com.kakao.sample.kakaostory&referrer=kakaostory로 이동

        // Map interface의 execParam으로 테스트.
        HashMap<String, String> execParamMap = new HashMap<String, String>();
        HashMap<String, String> marketParamMap = new HashMap<String, String>();
        execParamMap.put("place", "1111");
        execParamMap.put("nickname", "kevin");
        marketParamMap.put("referrer", "kakaostory");

        try {
            KakaoStoryService.requestPostNote(new KakaoStoryResponseCallback<MyStoryInfo>() {
                @Override
                public void onSuccess(MyStoryInfo result) {
                    Logger.d(result.toString());
                    handleStoryPostResult(StoryType.NOTE, result);
                }
            }, noteContent, StoryPermission.PUBLIC, true, execParamMap, execParamMap, marketParamMap, execParamMap);
        } catch (KakaoParameterException e) {
            Logger.e(e);
        }
    }

    private void requestPostLink() {
        try {
            KakaoStoryService.requestPostLink(new KakaoStoryResponseCallback<MyStoryInfo>() {

                @Override
                public void onSuccess(MyStoryInfo result) {
                    handleStoryPostResult(StoryType.NOTE, result);
                }
            }, scrapUrl, linkContent, StoryPermission.PUBLIC, true, execParam, execParam, marketParam, marketParam);
        } catch (KakaoParameterException e) {
            Logger.e(e);
        }
    }

    private void handleStoryPostResult(StoryType type, MyStoryInfo info) {
        if (info.getId() != null) {
            lastMyStoryId = info.getId();
            getPostButton.setEnabled(true);
            getPostsButton.setEnabled(true);
            deletePostButton.setEnabled(true);
            Logger.d("requestPost : %s", "succeeded to post " + type + " on KakaoStory.\nmyStoryId=" + lastMyStoryId);
            KakaoToast.makeToast(getApplicationContext(), "succeeded to post " + type + " on KakaoStory.\nmyStoryId=" + lastMyStoryId, Toast.LENGTH_LONG).show();
        } else {
            Logger.d("requestPost : %s", "failed to post " + type + " on KakaoStory.\nmyStoryId=null");
            KakaoToast.makeToast(getApplicationContext(), "failed to post " + type + " on KakaoStory.\nmyStoryId=null", Toast.LENGTH_LONG).show();
        }
    }

    private void getLinkInfo() {
        KakaoStoryService.requestGetLinkInfo(new KakaoStoryResponseCallback<LinkInfoResponse>() {
            @Override
            public void onSuccess(LinkInfoResponse result) {
                if (result != null && result.isValidResult()) {
                    showAlertDialog("succeeded to get link info.\n" + result);
                } else {
                    KakaoToast.makeToast(getApplicationContext(), "failed to get link info.\nkakaoStoryLinkInfo=null", Toast.LENGTH_LONG).show();
                }
            }
        }, scrapUrl);
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    private void requestGetMyStory() {
        try {
            KakaoStoryService.requestGetMyStory(new KakaoStoryResponseCallback<MyStoryInfo>() {
                @Override
                public void onSuccess(MyStoryInfo result) {
                    String message;
                    if (lastMyStoryId.equals(result.getId())) {
                        Logger.d("succeeded to get my post from KakaoStory.\nmyStoryInfo=" + result);
                        message = "succeeded to get my post from KakaoStory.\nmyStoryInfo=" + result;
                    } else {
                        Logger.d("failed to get my post from KakaoStory.\nexpectedId=" + lastMyStoryId + ",id=" + result.getId());
                        message = "failed to get my post from KakaoStory.\nexpectedId=" + lastMyStoryId + ",id=" + result.getId();
                    }

                    showAlertDialog(message);
                }
            }, lastMyStoryId);
        } catch (KakaoParameterException e) {
            Logger.e(e);
        }
    }

    private void showAlertDialog(String message) {
        new DialogBuilder(KakaoStoryMainActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void requestGetMyStories() {
        KakaoStoryService.requestGetMyStories(new KakaoStoryResponseCallback<List<MyStoryInfo>>() {
            @Override
            public void onSuccess(List<MyStoryInfo> result) {
                String message = "succeeded to get my posts from KakaoStory." + "\ncount=" + result.size();
                showAlertDialog(message);
            }
        }, lastMyStoryId);
    }

    private void requestDeleteMyStory() {
        try {
            KakaoStoryService.requestDeleteMyStory(new KakaoStoryResponseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    getPostButton.setEnabled(false);
                    getPostsButton.setEnabled(false);
                    deletePostButton.setEnabled(false);
                    KakaoToast.makeToast(getApplicationContext(), "succeeded to delete my post from KakaoStory.\nid=" + lastMyStoryId, Toast.LENGTH_LONG).show();
                    lastMyStoryId = null;
                }
            }, lastMyStoryId);
        } catch (KakaoParameterException e) {
            Logger.e(e);
        }
    }

    private void initializeView() {
        setContentView(R.layout.layout_kakaostory_main);
        profileLayout = (ProfileLayout) findViewById(R.id.com_kakao_user_profile);
        profileLayout.setDefaultBgImage(R.drawable.bg_image_01);
        profileLayout.setDefaultProfileImage(R.drawable.thumb_story);
        ((TextView)findViewById(R.id.text_title)).setText(getString(R.string.text_kakaostory));

        findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 로그인 하면서 caching되어 있는 profile를 그린다.
        userProfile = UserProfile.loadFromCache();
        if (userProfile != null) {
            profileLayout.setUserProfile(userProfile);
        }

        initializeButtons();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_check_button:
                onClickIsStoryUser();
                break;
            case R.id.profile_button:
                onClickProfile();
                break;
            case R.id.text_post_button:
                requestPostNote();
                break;
            case R.id.image_post_button:
                requestPostPhoto();
                break;
            case R.id.link_post_button:
                requestPostLink();
                break;
            case R.id.get_last_story:
                if(lastMyStoryId == null){
                    KakaoToast.makeToast(getApplicationContext(), "post first then get info", Toast.LENGTH_LONG).show();
                } else {
                    requestGetMyStory();
                }
                break;
            case R.id.delete_post_button:
                if(lastMyStoryId == null){
                    KakaoToast.makeToast(getApplicationContext(), "post first then delete it", Toast.LENGTH_LONG).show();
                } else {
                    requestDeleteMyStory();
                }
                break;
            case R.id.get_posts_button:
                if (lastMyStoryId == null) {
                    KakaoToast.makeToast(getApplicationContext(), "post first then get info", Toast.LENGTH_LONG).show();
                } else {
                    requestGetMyStories();
                }
                break;
            case R.id.logout_button:
                onClickLogout();
                break;
            case R.id.story_friend_list:
                showStroyFriendListActivity();
                break;
        }
    }

    private void showStroyFriendListActivity() {
        Intent intent = new Intent(this, KakaoStoryFriendListActivity.class);

        String[] friendType = {FriendType.KAKAO_STORY.name()};
        intent.putExtra(FriendsMainActivity.EXTRA_KEY_SERVICE_TYPE, friendType);
        startActivity(intent);
    }

    private void initializeButtons() {
        findViewById(R.id.user_check_button).setOnClickListener(this);
        findViewById(R.id.profile_button).setOnClickListener(this);
        findViewById(R.id.text_post_button).setOnClickListener(this);
        findViewById(R.id.image_post_button).setOnClickListener(this);
        findViewById(R.id.link_post_button).setOnClickListener(this);
        findViewById(R.id.story_friend_list).setOnClickListener(this);
        findViewById(R.id.logout_button).setOnClickListener(this);

        // 마지막 포스팅 정보.
        getPostButton = (Button) findViewById(R.id.get_last_story);
        getPostButton.setOnClickListener(this);

        // 마지막 포스팅 삭제
        deletePostButton = (Button) findViewById(R.id.delete_post_button);
        deletePostButton.setOnClickListener(this);

        // 최근 포스팅 정보.
        getPostsButton = (Button) findViewById(R.id.get_posts_button);
        getPostsButton.setOnClickListener(this);
    }

    // profile view에서 story profile을 update 한다.
    private void applyStoryProfileToView(final ProfileResponse storyProfile) {
        if (profileLayout != null) {
            if (userProfile != null) {
                profileLayout.setUserProfile(userProfile);
            }

            final String nickName = storyProfile.getProfile().getNickName();
            if (nickName != null) {
                profileLayout.setNickname(nickName);
            }

            final String profileImageURL = storyProfile.getProfile().getProfileImageURL();
            if (profileImageURL != null) {
                profileLayout.setProfileURL(profileImageURL);
            }

            final String birthDay = storyProfile.getProfile().getBirthday();
            if (birthDay != null) {
                profileLayout.setBirthDay(birthDay);
            }

            final String backgroundURL = storyProfile.getProfile().getBgImageURL();
            if (backgroundURL != null) {
                profileLayout.setBgImageURL(backgroundURL);
            }
        }
    }

    private String writeStoryImage(final int imageResourceId) throws IOException {
        final int bufferSize = 1024;
        InputStream ins = null;
        FileOutputStream fos = null;
        String outputFileName = null;
        try {
            ins = getResources().openRawResource(imageResourceId);

            final TypedValue value = new TypedValue();
            getResources().getValue(imageResourceId, value, true);
            final String imageFileName =  value.string == null ? null : value.string.toString();
            String extension = null;
            if(imageFileName != null)
                extension = getExtension(imageFileName);
            if(extension == null)
                extension = ".jpg";

            final File diskCacheDir = new File(getCacheDir(), "story");

            if (!diskCacheDir.exists() && !diskCacheDir.mkdirs()) {
                Logger.e("failed to mkdir: " + diskCacheDir.getPath());
            }

            outputFileName = diskCacheDir.getAbsolutePath() + File.separator + "temp_" + System.currentTimeMillis() + extension;

            fos = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[bufferSize];
            int read;
            while((read = ins.read(buffer)) != -1){
                fos.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ins != null)
                    ins.close();
                if (fos != null)
                    fos.close();
            } catch (Throwable t) {
            }
        }

        return outputFileName;
    }

    public static String getExtension(String fileName) {
        String ext = null;
        int i = fileName.lastIndexOf('.');
        int endIndex = fileName.lastIndexOf("?");

        if (i > 0 && i < fileName.length() - 1) {
            Locale curLocale = Locale.getDefault();
            if (endIndex < 0) {
                ext = fileName.substring(i).toLowerCase(curLocale);
            } else {
                ext = fileName.substring(i, endIndex).toLowerCase(curLocale);
            }
        }
        return ext;
    }

    private void checkExecParams() {
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            return;
        }
        KakaoToast.makeToast(getApplicationContext(), "URL is " + intent.getData().toString(), Toast.LENGTH_LONG).show();
    }

    private abstract class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {

        @Override
        public void onNotKakaoStoryUser() {
            KakaoToast.makeToast(getApplicationContext(), "not KakaoStory user", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            final String message = "MyKakaoStoryHttpResponseHandler : failure : " + errorResult;
            Logger.w(message);
            KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            redirectSignupActivity();
        }
    }
}