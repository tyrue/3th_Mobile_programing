package com.kakao.kakaostory.response.model;

import android.os.Parcel;

import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 8. 23..
 */

public class StoryLikeTest extends KakaoTestCase {
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void parcelable() {
        testParcelableWithLike(new StoryLike(StoryLike.Emotion.HAPPY, new StoryActor("kevin", "profile_url")));
        testParcelableWithLike(new StoryLike(StoryLike.Emotion.CHEER_UP, null));
        testParcelableWithLike(new StoryLike(null, null));
    }

    private void testParcelableWithLike(final StoryLike like) {
        Parcel parcel = Parcel.obtain();
        like.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        StoryLike retrieved = StoryLike.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(like, retrieved);
    }
}
