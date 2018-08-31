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
package com.kakao.friends.response.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kakao.auth.common.MessageSendable;
import com.kakao.friends.StringSet;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.usermgmt.response.model.User;

/**
 * Friend 에 대한 정보.
 * {@link com.kakao.friends.request.FriendsRequest}를 이용하여 얻을 수 있음.
 * @author leo.shin
 */
public class FriendInfo implements MessageSendable, User {
    /**
     * 친구와 나의 톡, 스토리 내에서와 관계
     */
    enum Relation {
        NONE("N/A"),
        FRIEND("FRIEND"),
        NOT_FRIEND("NO_FRIEND");

        final private String name;
        Relation(String name) {
            this.name = name;
        }

        public static Relation convert(String i) {
            for (Relation current : values()) {
                if (current.name.equalsIgnoreCase(i)) {
                    return current;
                }
            }
            return NONE;
        }
    }

    public static class FriendRelation implements Parcelable {
        private final Relation talk;
        private final Relation story;

        public FriendRelation(ResponseBody body) {
            this.talk = Relation.convert(body.optString(StringSet.talk, null));
            this.story = Relation.convert(body.optString(StringSet.story, null));
        }

        FriendRelation(Relation talk, Relation story) {
            this.talk = talk;
            this.story = story;
        }

        protected FriendRelation(Parcel in) {
            talk = (Relation) in.readSerializable();
            story = (Relation) in.readSerializable();
        }

        public static final Creator<FriendRelation> CREATOR = new Creator<FriendRelation>() {
            @Override
            public FriendRelation createFromParcel(Parcel in) {
                return new FriendRelation(in);
            }

            @Override
            public FriendRelation[] newArray(int size) {
                return new FriendRelation[size];
            }
        };

        /**
         * 친구와 내가 스토리 친구인지 여부.
         * @return 친구와 내가 스토리 친구인지 여부.
         */
        public boolean isStoryFriend() throws NotAvailableOperationException {
            if (story != null && story == Relation.NONE) {
                throw new NotAvailableOperationException("This method is available for talk friend type.");
            }
            return story != null && story == Relation.FRIEND;
        }

        /**
         * 친구와 내가 톡친구인지 여부
         * @return 친구와 내가 톡친구인지 여부
         */
        public boolean isTalkFriend() throws NotAvailableOperationException {
            if (talk != null && talk == Relation.NONE) {
                throw new NotAvailableOperationException("This method is available for story friend type.");
            }
            return talk != null && talk == Relation.FRIEND;
        }

        @Override
        public String toString() {
            return "[talk : " + talk +
                    ", story : " + story + "]";
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof FriendRelation)) return false;
            FriendRelation relation = (FriendRelation) object;
            return talk == relation.talk && story == relation.story;
        }

        public static final ResponseBody.BodyConverter<FriendRelation> CONVERTER = new ResponseBody.BodyConverter<FriendRelation>() {
            @Override
            public FriendRelation convert(ResponseBody body) throws ResponseBodyException {
                return new FriendRelation(body);
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeSerializable(talk);
            parcel.writeSerializable(story);
        }
    }


    public static final Creator<FriendInfo> CREATOR = new Creator<FriendInfo>() {
        @Override
        public FriendInfo createFromParcel(Parcel in) {
            return new FriendInfo(in);
        }

        @Override
        public FriendInfo[] newArray(int size) {
            return new FriendInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeLong(userId);
        parcel.writeLong(serviceUserId);
        parcel.writeInt(isAppRegistered ? 1: 0);
        parcel.writeString(profileNickname);
        parcel.writeString(profileThumbnailImage);
        parcel.writeString(talkOs);
        parcel.writeInt(isAllowedMsg ? 1: 0);
        parcel.writeParcelable(relation, 0);
    }


    final private String uuid;
    final private long userId;
    final private long serviceUserId;
    final private boolean isAppRegistered;
    final private String profileNickname;
    final private String profileThumbnailImage;
    final private String talkOs;
    final private boolean isAllowedMsg;

    public FriendRelation getRelation() {
        return relation;
    }

    final private FriendRelation relation;

    /**
     * @param body {@link com.kakao.friends.request.FriendsRequest}를 통해서 응답받은 결과.
     * @throws ResponseBodyException 프로토콜과 맞지 않는 응답이 왔을때 던지는 에러.
     */
    public FriendInfo(ResponseBody body) throws ResponseBodyException {
        this.userId = body.optLong(StringSet.id, 0);
        this.uuid = body.optString(StringSet.uuid, null);
        this.serviceUserId = body.optLong(StringSet.service_user_id, 0);
        this.isAppRegistered = body.optBoolean(StringSet.app_registered, false);
        this.profileNickname = body.optString(StringSet.profile_nickname, null);
        this.profileThumbnailImage = body.optString(StringSet.profile_thumbnail_image, null);
        this.talkOs = body.optString(StringSet.talk_os, null);
        this.isAllowedMsg = body.optBoolean(StringSet.allowed_msg, false);
        this.relation = body.optConverted(StringSet.relation, FriendRelation.CONVERTER, null);
    }

    FriendInfo(final String uuid, final long userId, final long serviceUserId, final boolean isAppRegistered, final String profileNickname, final String profileThumbnailImage, final String talkOs, final boolean isAllowedMsg, final FriendRelation relation) {
        this.uuid = uuid;
        this.userId = userId;
        this.serviceUserId = serviceUserId;
        this.isAppRegistered = isAppRegistered;
        this.profileNickname = profileNickname;
        this.profileThumbnailImage = profileThumbnailImage;
        this.talkOs = talkOs;
        this.isAllowedMsg = isAllowedMsg;
        this.relation = relation;
    }

    FriendInfo(final Parcel parcel) {
        uuid = parcel.readString();
        userId = parcel.readLong();
        serviceUserId = parcel.readLong();
        isAppRegistered = parcel.readInt() == 1;
        profileNickname = parcel.readString();
        profileThumbnailImage = parcel.readString();
        talkOs = parcel.readString();
        isAllowedMsg = parcel.readInt() == 1;
        relation = parcel.readParcelable(FriendRelation.class.getClassLoader());
    }

    /**
     * 메세지를 전송할 대상에 대한 ID.
     * @return 메세지를 전송할 대상에 대한 ID
     */
    @Override
    public String getTargetId() {
        return this.uuid;
    }

    /**
     * 사용자 ID
     * @return 사용자 ID
     */
    @Override
    public long getId() {
        return this.userId;
    }

    /**
     * 해당 앱에서 유일한 친구의 code
     * 가변적인 데이터.
     */
    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public String getType() {
        return StringSet.uuid;
    }

    /**
     * 친구의 카카오 회원번호. 앱의 특정 카테고리나 특정 권한에 한해 내려줌
     * @return 친구의 카카오 회원번호.
     */
    @Override
    public long getServiceUserId() {
        return serviceUserId;
    }

    /**
     * 친구의 앱 가입 여부
     * @return true is registerd, false is otherwise.
     */
    public boolean isAppRegistered() {
        return isAppRegistered;
    }

    /**
     * 친구의 대표 프로필 닉네임. 앱 가입친구의 경우 앱에서 설정한 닉네임. 미가입친구의 경우 톡 또는 스토리의 닉네임
     * 요청한 friend_type에 따라 달라짐
     * @return 대표 프로필 닉네임
     */
    public String getProfileNickname() {
        return profileNickname;
    }

    /**
     * 친구의 썸네일 이미지
     * @return 친구의 썸네일 이미지 url
     */
    public String getProfileThumbnailImage() {
        return profileThumbnailImage;
    }

    /**
     * 톡에 가입된 기기의 os 정보 (android/ios)
     * @return android or ios
     */
    public String getTalkOs() {
        return talkOs;
    }

    /**
     * 메세지 수신이 허용되었는지 여부.
     * 앱가입 친구의 경우는 feed msg에 해당. 앱미가입친구는 invite msg에 해당
     * @return 메시지 수신 허용 여부
     */
    public boolean isAllowedMsg() {
        return isAllowedMsg;
    }

    /**
     * 친구와 내가 카카오톡 친구인지 여부.
     * @return 친구와 내가 카카오톡 친구인지 여부.
     * @throws NotAvailableOperationException KakaoTalk 친구를 요청하지 않았는데 호출된 경우.
     */
    public boolean isTalkFriend() throws NotAvailableOperationException {
        return relation != null && relation.isTalkFriend();
    }

    /**
     * 친구와 내가 스토리 친구인지 여부.
     * @return 친구와 내가 스토리 친구인지 여부.
     * @throws NotAvailableOperationException KakaoStory 친구를 요청하지 않았는데 호출된 경우.
     */
    public boolean isStoryFriend() throws NotAvailableOperationException {
        return relation != null && relation.isStoryFriend();
    }

    /**
     * @param obj Object to be compared
     * @return true if they are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FriendInfo)) return false;
        FriendInfo compare = (FriendInfo) obj;
        if (!TextUtils.equals(getUUID(), compare.getUUID())) return false;
        if (getId() != compare.getId()) return false;
        if (getServiceUserId() != compare.getServiceUserId()) return false;
        if (isAppRegistered() != compare.isAppRegistered()) return false;
        if (!TextUtils.equals(getProfileNickname(), compare.getProfileNickname())) return false;
        if (!TextUtils.equals(getProfileThumbnailImage(), compare.getProfileThumbnailImage())) return false;
        if (!TextUtils.equals(getTalkOs(), compare.getTalkOs())) return false;
        if (isAllowedMsg() != compare.isAllowedMsg()) return false;
        if (relation == null ? compare.getRelation() != null : !relation.equals(compare.getRelation())) return false;
        return true;
    }

    @Override
    public String toString() {
        return "++ uuid : " + uuid +
                ", userId : " + userId +
                ", serviceUserId : " + serviceUserId +
                ", isAppRegistered : " + isAppRegistered +
                ", profileNickname : " + profileNickname +
                ", profileThumbnailImage : " + profileThumbnailImage +
                ", talkOs : " + talkOs +
                ", isAllowedMsg : " + isAllowedMsg +
                ", relation : " + (relation == null ? "" : relation.toString());

    }

    public static final ResponseBody.BodyConverter<FriendInfo> CONVERTER = new ResponseBody.BodyConverter<FriendInfo>() {
        @Override
        public FriendInfo convert(ResponseBody body) throws ResponseBodyException {
            return new FriendInfo(body);
        }
    };

    public static class NotAvailableOperationException extends Exception {
        public NotAvailableOperationException(String detailMsg) {
            super(detailMsg);
        }
    }
}
