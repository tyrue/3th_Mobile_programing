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
package com.kakao.sdk.sample.common.widget;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.kakao.sdk.sample.R;
import com.kakao.sdk.sample.common.GlobalApplication;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

//TODO image upload, back button, cancel button
/**
 * 기본 UserProfile(사용자 ID, 닉네임, 프로필 이미지)을 그려주는 Layout.
 * </br>
 * 1. 프로필을 노출할 layout에 {@link com.kakao.sdk.sample.common.widget.ProfileLayout}을 선언한다.
 * </br>
 * 2. {@link com.kakao.sdk.sample.common.widget.ProfileLayout#setMeResponseCallback(MeResponseCallback)}를 이용하여 사용자정보 요청 결과에 따른 callback을 설정한다.
 * </br>
 * @author MJ
 */
public class ProfileLayout extends FrameLayout {
    private MeResponseCallback meResponseCallback;

    private String email;
    private String nickname;
    private String userId;
    private String birthDay;
    private String countryIso;
    private NetworkImageView profile;
    private NetworkImageView background;
    private TextView profileDescription;

    public ProfileLayout(Context context) {
        super(context);
        initView();
    }

    public ProfileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ProfileLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 사용자정보 요청 결과에 따른 callback을 설정한다.
     * @param callback 사용자정보 요청 결과에 따른 callback
     */
    public void setMeResponseCallback(final MeResponseCallback callback){
        this.meResponseCallback = callback;
    }
    /**
     * param으로 온 UserProfile에 대해 view를 update한다.
     * @param userProfile 화면에 반영할 사용자 정보
     */
    public void setUserProfile(final UserProfile userProfile) {
        setEmail(userProfile.getEmail());
        setProfileURL(userProfile.getProfileImagePath());
        setNickname(userProfile.getNickname());
        setUserId(String.valueOf(userProfile.getId()));
    }

    public void setEmail(final String email) {
        this.email = email;
        updateLayout();
    }

    /**
     * 프로필 이미지에 대해 view를 update한다.
     * @param profileImageURL 화면에 반영할 프로필 이미지
     */
    public void setProfileURL(final String profileImageURL) {
        if (profile != null && profileImageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            profile.setImageUrl(profileImageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

    public void setBgImageURL(String bgImageURL) {
        if (bgImageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            background.setImageUrl(bgImageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

    public void setDefaultBgImage(int imageResId) {
        if (background != null) {
            background.setBackgroundResource(imageResId);
        }
    }

    public void setDefaultProfileImage(int imageResId) {
        if (profile != null) {
            profile.setBackgroundResource(imageResId);
        }
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
        updateLayout();
    }

    /**
     * 별명 view를 update한다.
     * @param nickname 화면에 반영할 별명
     */
    public void setNickname(final String nickname) {
        this.nickname = nickname;
        updateLayout();
    }

    public void setBirthDay(final String birthDay) {
        this.birthDay = birthDay;
        updateLayout();
    }

    /**
     * 사용자 아이디 view를 update한다.
     * @param userId 화면에 반영할 사용자 아이디
     */
    public void setUserId(final String userId) {
        this.userId = userId;
        updateLayout();
    }

    private void updateLayout() {
        StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(email)) {
            builder.append(getResources().getString(R.string.com_kakao_profile_email)).append('\n').append(email).append('\n');
        }
        if (nickname != null && nickname.length() > 0) {
            builder.append(getResources().getString(R.string.com_kakao_profile_nickname)).append("\n").append(nickname).append("\n");
        }

        if (userId != null && userId.length() > 0) {
            builder.append(getResources().getString(R.string.com_kakao_profile_userId)).append("\n").append(userId).append("\n");
        }

        if (birthDay != null && birthDay.length() > 0) {
            builder.append(getResources().getString(R.string.com_kakao_profile_userId)).append("\n").append(birthDay);
        }

        if (countryIso != null) {
            builder.append(getResources().getString(R.string.kakaotalk_country_label)).append("\n").append(countryIso);
        }

        if (profileDescription != null) {
            profileDescription.setText(builder.toString());
        }
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.layout_common_kakao_profile, this);

        profile = (NetworkImageView) view.findViewById(R.id.com_kakao_profile_image);
        background = (NetworkImageView) view.findViewById(R.id.background);
        profileDescription = (TextView) view.findViewById(R.id.profile_description);
    }

    /**
     * 사용자 정보를 요청한다.
     */
    public void requestMe() {
        UserManagement.requestMe(meResponseCallback);
    }
}
