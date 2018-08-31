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
import com.kakao.network.response.ResponseBody.ResponseBodyException;

/**
 * 내스토리의 이미지를 크기별로 얻을 수 있는 객체.
 * @author leoshin, created at 15. 8. 3..
 */
public class MyStoryImageInfo implements Parcelable {
    private final String xlarge;
    private final String large;
    private final String medium;
    private final String small;
    private final String original;
    public MyStoryImageInfo(ResponseBody body) {
        this.xlarge = body.optString(StringSet.xlarge, null);
        this.large = body.optString(StringSet.large, null);
        this.medium = body.optString(StringSet.medium, null);
        this.small = body.optString(StringSet.small, null);
        this.original = body.optString(StringSet.original, null);
    }

    protected MyStoryImageInfo(Parcel in) {
        xlarge = in.readString();
        large = in.readString();
        medium = in.readString();
        small = in.readString();
        original = in.readString();
    }

    public static final Creator<MyStoryImageInfo> CREATOR = new Creator<MyStoryImageInfo>() {
        @Override
        public MyStoryImageInfo createFromParcel(Parcel in) {
            return new MyStoryImageInfo(in);
        }

        @Override
        public MyStoryImageInfo[] newArray(int size) {
            return new MyStoryImageInfo[size];
        }
    };

    public MyStoryImageInfo(String xlarge, String large, String medium, String small, String original) {
        this.xlarge = xlarge;
        this.large = large;
        this.medium = medium;
        this.small = small;
        this.original = original;
    }

    public String getXlarge() {
        return xlarge;
    }

    public String getLarge() {
        return large;
    }

    public String getMedium() {
        return medium;
    }

    public String getSmall() {
        return small;
    }

    public String getOriginal() {
        return original;
    }

    @Override
    public String toString() {
        return "KakaoStoryActivityImage{" + "original='" + original + '\'' +
                ", xlarge='" + xlarge + '\'' +
                ", large='" + large + '\'' +
                ", medium='" + medium + '\'' +
                ", small='" + small + '\'' +
                '}';
    }

    public static final ResponseBody.BodyConverter<MyStoryImageInfo> CONVERTER = new ResponseBody.BodyConverter<MyStoryImageInfo>() {
        @Override
        public MyStoryImageInfo convert(ResponseBody body) throws ResponseBodyException {
            return new MyStoryImageInfo(body);
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(xlarge);
        parcel.writeString(large);
        parcel.writeString(medium);
        parcel.writeString(small);
        parcel.writeString(original);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyStoryImageInfo)) return false;
        MyStoryImageInfo that = (MyStoryImageInfo) o;
        if (!TextUtils.equals(getXlarge(), that.getXlarge())) return false;
        if (!TextUtils.equals(getLarge(), that.getLarge())) return false;
        if (!TextUtils.equals(getMedium(), that.getMedium())) return false;
        if (!TextUtils.equals(getSmall(), that.getSmall())) return false;
        if (!TextUtils.equals(getOriginal(), that.getOriginal())) return false;
        return true;
    }
}
