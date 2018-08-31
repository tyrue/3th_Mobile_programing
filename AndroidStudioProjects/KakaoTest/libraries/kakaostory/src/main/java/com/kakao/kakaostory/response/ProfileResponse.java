/**
 * Copyright 2014-2015 Kakao Corp.
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
package com.kakao.kakaostory.response;

import com.kakao.auth.network.response.JSONObjectResponse;
import com.kakao.kakaostory.StringSet;
import com.kakao.kakaostory.response.model.StoryProfile;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseData;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author leoshin, created at 15. 8. 4..
 * * 카카오스토리 프로필 요청의 결과 객체로
 * 카카오스토리의 닉네임 정보 (nickName), 카카오스토리의 프로필 이미지 URL (profileImageURL), 카카오스토리의 프로필 이미지의 썸네일 URL (thumbnailURL), 카카오스토리의 배경 이미지 URL (bgImageURL),
 * 내스토리 퍼마링크(permalink), 카카오스토리의 생일 (birthday), 카카오스토리의 생일 타입 (birthdayType)로 구성되어 있다.
 */
public class ProfileResponse extends JSONObjectResponse {
    /**
     * 카카오스토리의 생일 타입
     */
    public enum BirthdayType {
        /**
         * 양력 생일
         */
        SOLAR("+"),
        /**
         * 음력 생일
         */
        LUNAR("-");
        private final String displaySymbol;

        BirthdayType(String s) {
            this.displaySymbol = s;
        }

        /**
         * 양력 생일은 '+', 음력 생일은 '-'로 반환한다.
         * @return '+','-'로 생일 타입을 반환한다.
         */
        public String getDisplaySymbol() {
            return displaySymbol;
        }
    }

    private final StoryProfile profile;
    private final String nickName;
    private final String profileImageURL;
    private final String thumbnailURL;
    private final String bgImageURL;
    private final String permalink;
    private final String birthday;
    private final BirthdayType birthdayType;
    private Calendar birthdayCalendar;

    public ProfileResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);

        this.nickName = body.optString(StringSet.nickName, null);
        this.profileImageURL = body.optString(StringSet.profileImageURL, null);
        this.thumbnailURL = body.optString(StringSet.thumbnailURL, null);
        this.bgImageURL = body.optString(StringSet.bgImageURL, null);
        this.permalink = body.optString(StringSet.permalink, null);
        this.birthday = body.optString(StringSet.birthday, null);

        String type = body.optString(StringSet.birthdayType, null);
        if (type == null || type.equalsIgnoreCase(BirthdayType.SOLAR.name())) {
            birthdayType = BirthdayType.SOLAR;
        } else {
            birthdayType = BirthdayType.LUNAR;
        }

        birthdayCalendar = createCalendar(birthday);
        profile = new StoryProfile(nickName, profileImageURL, thumbnailURL, bgImageURL, permalink, birthday, birthdayType);
    }

    public StoryProfile getProfile() {
        return profile;
    }

    /**
     * 카카오스토리 별명
     * @return 카카오스토리 별명
     */
    @Deprecated
    public String getNickName() {
        return nickName;
    }

    /**
     * 480px * 480px ~ 1024px * 1024px 크기의 카카오스토리 프로필 이미지 URL
     * @return 카카오스토리 프로필 이미지 URL
     */
    @Deprecated
    public String getProfileImageURL() {
        return profileImageURL;
    }

    /**
     * 160px * 160px 크기의 카카오스토리 썸네일 프로필 이미지 URL
     * @return 카카오스토리 썸네일 프로필 이미지 URL
     */
    @Deprecated
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    /**
     * 480px * 480px ~ 1024px * 1024px 크기의 카카오스토리 배경 이미지 URL
     * @return 카카오스토리 배경 이미지 URL
     */
    @Deprecated
    public String getBgImageURL() {
        return bgImageURL;
    }

    /**
     * 내 스토리를 방문할 수 있는 웹 page의 URL
     * @return 내스토리 permanent link
     */
    @Deprecated
    public String getPermalink() {
        return permalink;
    }

    /**
     * 카카오스토리 생일 MMdd 형태
     * @return 카카오스토리 생일 MMdd 형태
     */
    @Deprecated
    public String getBirthday() {
        return birthday;
    }

    /**
     * 카카오스토리 생일을 Calendar 타입
     * @return 카카오스토리 생일을 Calendar 형식으로 반환 nullable vaule
     */
    @Deprecated
    public Calendar getBirthdayCalendar() {
        return birthdayCalendar;
    }

    /**
     * 카카오스토리 생일 타입. 양력 또는 음력
     * @return 카카오스토리 생일 타입. 양력 또는 음력
     */
    @Deprecated
    public BirthdayType getBirthdayType() {
        return birthdayType;
    }

    /**
     * jackson에서 객체를 만들 때 사용한다.
     * @param birthday MMdd 형태의 생일 string 값
     * @return Calendar instance corresponding to birthday
     */
    private static Calendar createCalendar(final String birthday) {
        if(birthday == null) {
            return null;
        }

        final SimpleDateFormat form = new SimpleDateFormat("MMdd", Locale.getDefault());
        Calendar birthdayCalendar  = Calendar.getInstance();
        try {
            birthdayCalendar.setTime(form.parse(birthday));
        } catch (java.text.ParseException e) {
            Logger.w(e);
        }
        return birthdayCalendar;
    }

    /**
     * 각 정보를 string으로 표현하여 반환한다.
     * @return 각 정보를 포함한 string
     */
    @Override
    public String toString() {
        return "KakaoStoryProfile{" + "nickName='" + nickName + '\'' +
                ", profileImageURL='" + profileImageURL + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                ", bgImageURL='" + bgImageURL + '\'' +
                ", permalink='" + permalink + '\'' +
                ", birthday='" + birthday + '\'' +
                ", birthdayType=" + birthdayType +
                '}';
    }
}
