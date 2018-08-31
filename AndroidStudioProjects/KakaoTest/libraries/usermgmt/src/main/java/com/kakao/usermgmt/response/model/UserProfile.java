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
package com.kakao.usermgmt.response.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.kakao.auth.Session;
import com.kakao.util.helper.SharedPreferencesCache;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.usermgmt.StringSet;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leoshin, created at 15. 8. 6..
 */
public class UserProfile implements User, Parcelable {
    private static final String CACHE_USER_ID = "com.kakao.user.userId";
    private static final String CACHE_USER_EMAIL = "com.kakao.user.email";
    private static final String CACHE_EMAIL_VERIFIED = "com.kakao.user.email_verified";
    private static final String CACHE_USER_PREFIX = "com.kakao.user.properties.";
    private static final String CACHE_NICKNAME = "com.kakao.user.nickname";
    private static final String CACHE_THUMBNAIL_PATH = "com.kakao.user.thumbbnailpath";
    private static final String CACHE_PROFILE_PATH = "com.kakao.user.profilepath";

    private static final String CACHE_FRIEND_UUID = "com.kakao.user.uuid";
    private static final String CACHE_SERVICE_USER_ID = "com.kakao.user.serviceuserid";
    private static final String CACHE_REMAINING_INVITE_COUNT = "com.kakao.user.remaininginvitecount";
    private static final String CACHE_REMAINING_GRUOP_MSG_COUNT = "com.kakao.user.remaininggroupmsgcount";

    // predefined property를 제외한 사용자가 정의한 property
    private Map<String, String> properties = new HashMap<String, String>();
    private final long id;
    private String email;
    private boolean emailVerified;
    private String nickname;
    private String thumbnailImagePath;
    private String profileImagePath;

    private final String uuid;
    private final long serviceUserId;
    private final int remainingInviteCount;
    private final int remainingGroupMsgCount;

    /**
     * UserProfile 객체를 만든다.
     * @param body 사용자정보 요청결과 json으로 부터 얻은 ResponseBody
     * @throws ResponseBodyException if the response is malformed
     */
    public UserProfile(ResponseBody body) throws ResponseBodyException {
        this.id = body.getLong(StringSet.id);
        if(id <= 0) {
            throw new ResponseBodyException("User is called but the result user is null.");
        }

        this.email = body.optString(StringSet.email, null);
        this.emailVerified = body.optBoolean(StringSet.email_verified, false);
        this.uuid = body.optString(StringSet.uuid, null);
        this.serviceUserId = body.optLong(StringSet.service_user_id, 0);
        this.remainingInviteCount = body.optInt(StringSet.remaining_invite_count, 0);
        this.remainingGroupMsgCount = body.optInt(StringSet.remaining_group_msg_count, 0);

        if (body.has(StringSet.properties)) {
            this.properties = ResponseBody.toMap(body.getBody(StringSet.properties));
            this.nickname = properties.remove(StringSet.nickname);
            this.thumbnailImagePath = properties.remove(StringSet.thumbnail_image);
            this.profileImagePath = properties.remove(StringSet.profile_image);
        } else {
            this.nickname = null;
            this.thumbnailImagePath = null;
            this.profileImagePath = null;
        }
    }

    public UserProfile(SharedPreferencesCache cache) {
        this.id = cache.getLong(CACHE_USER_ID);
        this.email = cache.getString(CACHE_USER_EMAIL);
        this.emailVerified = cache.getBoolean(CACHE_EMAIL_VERIFIED);
        this.nickname = cache.getString(CACHE_NICKNAME);
        this.thumbnailImagePath = cache.getString(CACHE_THUMBNAIL_PATH);
        this.profileImagePath = cache.getString(CACHE_PROFILE_PATH);
        this.properties = cache.getStringMap(CACHE_USER_PREFIX);

        this.uuid = cache.getString(CACHE_FRIEND_UUID);
        this.serviceUserId = cache.getLong(CACHE_SERVICE_USER_ID);
        this.remainingInviteCount = cache.getInt(CACHE_REMAINING_INVITE_COUNT);
        this.remainingGroupMsgCount = cache.getInt(CACHE_REMAINING_GRUOP_MSG_COUNT);
    }

    public UserProfile(Parcel in) {
        this.id = in.readLong();
        this.email = in.readString();
        this.emailVerified = in.readInt() != 0;
        this.nickname = in.readString();
        this.thumbnailImagePath = in.readString();
        this.profileImagePath = in.readString();
        this.uuid = in.readString();
        this.serviceUserId = in.readLong();
        this.remainingInviteCount = in.readInt();
        this.remainingGroupMsgCount = in.readInt();
        in.readMap(properties, getClass().getClassLoader());
    }

    /**
     * 남은 일별 초대 메시지 전송 횟수
     * TALK_MESSAGE_SEND permission이 있는 경우에만 내려 줌.
     * @return 남은 일별 초대 메시지 전송 횟수
     */
    public int getRemainingInviteCount() {
        return remainingInviteCount;
    }

    /**
     * 남은 일별 그룹 메시지 전송 횟수
     * TALK_MESSAGE_SEND permission이 있는 경우에만 내려 줌.
     * @return 남은 일별 초대 메시지 전송 횟수
     */
    public int getRemainingGroupMsgCount() {
        return remainingGroupMsgCount;
    }

    /**
     * 현재까지 저장되어 있는 사용자의 모든 정보를 key, value로 구성된 json type으로 반환
     * @return 앱에 저장된 사용자의 모든 정보
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * 앱 등록 당시 정의한 사용자의 정보 중 key에 해당하는 정보
     * @param propertyKey 알고 싶은 사용자 정보의 key
     * @return 해당 key의 정보
     */
    public String getProperty(final String propertyKey) {
        if(properties != null)
            return properties.get(propertyKey);
        else
            return null;
    }

    /**
     * User's email
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    /**
     * 사용자 별명
     * @return 사용자 별명
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 110px * 110px(톡에서 가지고 온 경우) 또는 160px * 160px(스토리에서 가지고 온 경우) 크기의 사용자의 썸네일 프로필 이미지 경로
     * @return 사용자의 썸네일 프로필 이미지 경로
     */
    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    /**
     * 480px * 480px ~ 1024px * 1024px 크기의 사용자의 프로필 이미지 경로
     * @return 사용자의 프로필 이미지 경로
     */
    public String getProfileImagePath() {
        return profileImagePath;
    }

    /**
     * 사용자 ID
     * @return 사용자 ID
     */
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * friend_id는 FRIENDS permission이 있는 경우에만 내려 줌.
     * 해당 앱에서 유일한 친구의 code
     * 가변적인 데이터.
     */
    @Override
    public String getUUID() {
        return uuid;
    }

    /**
     * 친구의 카카오 회원번호. 앱의 특정 카테고리나 특정 권한에 한해 내려줌
     * @return 친구의 카카오 회원번호.
     */
    public long getServiceUserId() {
        return serviceUserId;
    }

    /**
     * 캐시로부터 사용자정보를 읽어온다.
     * @return 캐시에서 읽은 사용자정보
     */
    public static UserProfile loadFromCache() {
        SharedPreferencesCache cache = Session.getCurrentSession().getAppCache();
        if(cache == null) {
            return null;
        }
        return new UserProfile(cache);
    }

    /**
     * 사용자의 프로필 정보를 캐시에 저장한다.
     */
    public void saveUserToCache() {
        SharedPreferencesCache cache = Session.getCurrentSession().getAppCache();
        if(cache == null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putLong(CACHE_USER_ID, id);
        bundle.putString(CACHE_USER_EMAIL, email);
        bundle.putBoolean(CACHE_EMAIL_VERIFIED, emailVerified);
        bundle.putString(CACHE_NICKNAME, nickname);
        bundle.putString(CACHE_THUMBNAIL_PATH, thumbnailImagePath);
        bundle.putString(CACHE_PROFILE_PATH, profileImagePath);
        bundle.putString(CACHE_FRIEND_UUID, uuid);
        bundle.putLong(CACHE_SERVICE_USER_ID, serviceUserId);
        bundle.putInt(CACHE_REMAINING_INVITE_COUNT, remainingInviteCount);
        bundle.putInt(CACHE_REMAINING_GRUOP_MSG_COUNT, remainingGroupMsgCount);

        if(!properties.isEmpty()){
            for(String key : properties.keySet()){
                bundle.putString(CACHE_USER_PREFIX + key, properties.get(key));
            }
        }
        cache.save(bundle);
    }

    /**
     * 원본 사용자정보와 update할 사용자 정보를 받아 원본에 update할 정보만 update한 결과를 준다.
     * @param properties update할 사용자 정보
     * @return input을 merge한 결과
     */
    public UserProfile updateUserProfile(final Map<String, String> properties) {
        if (properties != null) {
            final String nickname = properties.remove(StringSet.nickname);
            if(nickname != null) {
                this.nickname = nickname;
            }

            final String thumbnailPath = properties.remove(StringSet.thumbnail_image);
            if(thumbnailPath != null) {
                this.thumbnailImagePath = thumbnailPath;
            }

            final String profilePath = properties.remove(StringSet.profile_image);
            if(profilePath != null) {
                this.profileImagePath = profilePath;
            }

            if(!properties.isEmpty()) {
                this.properties.putAll(properties);
            }
        }
        return this;
    }

    /**
     * 사용자의 프로필 정보를 String으로 변환한다
     * @return 사용자의 프로필 정보를 String으로 변환힌 값
     */
    @Override
    public String toString() {
        return "UserProfile{" + "nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", email_verified='" + emailVerified + '\'' +
                ", thumbnailImagePath='" + thumbnailImagePath + '\'' +
                ", profileImagePath='" + profileImagePath + '\'' +
                ", code='" + uuid + '\'' +
                ", serviceUserId='" + serviceUserId + '\'' +
                ", remainingInviteCount='" + remainingInviteCount + '\'' +
                ", remainingGroupMsgCount='" + remainingGroupMsgCount + '\'' +
                ", properties=" + properties +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(email);
        dest.writeInt(emailVerified ? 1: 0);
        dest.writeString(nickname);
        dest.writeString(thumbnailImagePath);
        dest.writeString(profileImagePath);
        dest.writeString(uuid);
        dest.writeLong(serviceUserId);
        dest.writeInt(remainingInviteCount);
        dest.writeInt(remainingGroupMsgCount);
        dest.writeMap(properties);
    }

    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

}
