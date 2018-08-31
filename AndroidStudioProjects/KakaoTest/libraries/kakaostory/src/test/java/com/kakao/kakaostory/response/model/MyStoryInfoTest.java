package com.kakao.kakaostory.response.model;

import android.os.Parcel;

import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author kevin.kang. Created on 2017. 8. 23..
 */

public class MyStoryInfoTest extends KakaoTestCase {
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void testParcelable() {
        List<MyStoryImageInfo> imageInfoList =
                Collections.singletonList(new MyStoryImageInfo("xLarge", "large", "medium",
                        "small", "original"));
        List<StoryComment> commentList =
                Collections.singletonList(new StoryComment("comment",
                        new StoryActor("kevin", "profile_url")));
        List<StoryLike> likeList =
                Collections.singletonList(new StoryLike(StoryLike.Emotion.HAPPY,
                        new StoryActor("kevin", "profile_url")));
        testParcelableWithStoryInfo(new MyStoryInfo("id", "url", "type", "date", 1, 1, "content", "permission",
                imageInfoList, commentList, likeList));
        testParcelableWithStoryInfo(new MyStoryInfo("id", "url", "type", "date", 1, 1, "content", "permission",
                null, null, likeList));

    }

    private void testParcelableWithStoryInfo(final MyStoryInfo storyInfo) {
        Parcel parcel = Parcel.obtain();
        storyInfo.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MyStoryInfo retrieved = MyStoryInfo.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(storyInfo, retrieved);
    }
}
