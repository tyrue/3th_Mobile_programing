package com.kakao.kakaostory.response.model;

import android.os.Parcel;

import com.kakao.kakaostory.response.ProfileResponse;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 8. 23..
 */

public class StoryProfileTest extends KakaoTestCase {
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void parcelable() {
        testParcelableWithProfile(new StoryProfile("kevin", "profile_url",
                "thumbnail_url", null, "permalink", null,
                ProfileResponse.BirthdayType.SOLAR));
        testParcelableWithProfile(new StoryProfile("kevin", null,
                null, null, null, "0115", ProfileResponse.BirthdayType.LUNAR));

        testParcelableWithProfile(new StoryProfile("kevin", null,
                null, null, null, "0115", null));

    }

    private void testParcelableWithProfile(final StoryProfile profile) {
        Parcel parcel = Parcel.obtain();
        profile.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        StoryProfile retrieved = StoryProfile.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(profile, retrieved);
    }
}