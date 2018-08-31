package com.kakao.kakaostory.response.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kakao.kakaostory.response.ProfileResponse;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author kevin.kang. Created on 2017. 8. 23..
 */

public class StoryProfile implements Parcelable {
    private final String nickName;
    private final String profileImageURL;

    public String getNickName() {
        return nickName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getBgImageURL() {
        return bgImageURL;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getBirthday() {
        return birthday;
    }

    public ProfileResponse.BirthdayType getBirthdayType() {
        return birthdayType;
    }

    public Calendar getBirthdayCalendar() {
        return birthdayCalendar;
    }

    private final String thumbnailURL;
    private final String bgImageURL;
    private final String permalink;
    private final String birthday;
    private final ProfileResponse.BirthdayType birthdayType;
    private Calendar birthdayCalendar;

    protected StoryProfile(Parcel in) {
        nickName = in.readString();
        profileImageURL = in.readString();
        thumbnailURL = in.readString();
        bgImageURL = in.readString();
        permalink = in.readString();
        birthday = in.readString();
        birthdayType = (ProfileResponse.BirthdayType) in.readSerializable();
        birthdayCalendar = (Calendar) in.readSerializable();
    }

    public static final Creator<StoryProfile> CREATOR = new Creator<StoryProfile>() {
        @Override
        public StoryProfile createFromParcel(Parcel in) {
            return new StoryProfile(in);
        }

        @Override
        public StoryProfile[] newArray(int size) {
            return new StoryProfile[size];
        }
    };

    public StoryProfile(String nickName, String profileImageURL, String thumbnailURL, String bgImageURL, String permalink, String birthday, ProfileResponse.BirthdayType birthdayType) {
        this.nickName = nickName;
        this.profileImageURL = profileImageURL;
        this.thumbnailURL = thumbnailURL;
        this.bgImageURL = bgImageURL;
        this.permalink = permalink;
        this.birthday = birthday;
        this.birthdayType = birthdayType;
        this.birthdayCalendar = createCalendar(birthday);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nickName);
        parcel.writeString(profileImageURL);
        parcel.writeString(thumbnailURL);
        parcel.writeString(bgImageURL);
        parcel.writeString(permalink);
        parcel.writeString(birthday);
        parcel.writeSerializable(birthdayType);
        parcel.writeSerializable(birthdayCalendar);
    }

    /**
     * jackson에서 객체를 만들 때 사용한다.
     * @param birthday MMdd 형태의 생일 string 값
     * @return Calendar instance corresponding to birthday
     */
    private Calendar createCalendar(final String birthday) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryProfile)) return false;
        StoryProfile that = (StoryProfile) o;
        if (!TextUtils.equals(profileImageURL, that.profileImageURL)) return false;
        if (!TextUtils.equals(thumbnailURL, that.thumbnailURL)) return false;
        if (!TextUtils.equals(bgImageURL, that.bgImageURL)) return false;
        if (!TextUtils.equals(permalink, that.permalink)) return false;
        if (!TextUtils.equals(birthday, that.birthday)) return false;
        if (birthdayType != that.birthdayType) return false;
        return birthdayCalendar != null ? birthdayCalendar.equals(that.birthdayCalendar) : that.birthdayCalendar == null;
    }
}
