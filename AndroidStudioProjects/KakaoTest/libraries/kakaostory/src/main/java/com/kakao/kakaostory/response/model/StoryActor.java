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
package com.kakao.kakaostory.response.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kakao.kakaostory.StringSet;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseBody.BodyConverter;
import com.kakao.network.response.ResponseBody.ResponseBodyException;

/**
 * @author leoshin, created at 15. 8. 3..
 */
public class StoryActor implements Parcelable {
    private final String displayName;
    private final String profileThumbnailUrl;

    public StoryActor(ResponseBody body) {
        this.displayName = body.optString(StringSet.display_name, null);
        this.profileThumbnailUrl = body.optString(StringSet.profile_thumbnail_url, null);
    }

    public static final ResponseBody.BodyConverter<StoryActor> CONVERTER = new BodyConverter<StoryActor>() {
        @Override
        public StoryActor convert(ResponseBody body) throws ResponseBodyException {
            return new StoryActor(body);
        }
    };

    protected StoryActor(Parcel in) {
        displayName = in.readString();
        profileThumbnailUrl = in.readString();
    }

    public static final Creator<StoryActor> CREATOR = new Creator<StoryActor>() {
        @Override
        public StoryActor createFromParcel(Parcel in) {
            return new StoryActor(in);
        }

        @Override
        public StoryActor[] newArray(int size) {
            return new StoryActor[size];
        }
    };

    StoryActor(String displayName, String profileThumbnailUrl) {
        this.displayName = displayName;
        this.profileThumbnailUrl = profileThumbnailUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileThumbnailUrl() {
        return profileThumbnailUrl;
    }

    @Override
    public String toString() {
        return "StoryActor{" + "displayName='" + displayName + '\'' +
                ", profileThumbnailUrl='" + profileThumbnailUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(displayName);
        parcel.writeString(profileThumbnailUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryActor)) return false;
        StoryActor that = (StoryActor) o;
        if (!TextUtils.equals(getDisplayName(), that.getDisplayName())) return false;
        return TextUtils.equals(getProfileThumbnailUrl(), that.getProfileThumbnailUrl());
    }
}
