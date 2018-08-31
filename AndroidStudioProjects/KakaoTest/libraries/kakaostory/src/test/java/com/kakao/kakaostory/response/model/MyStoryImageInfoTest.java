package com.kakao.kakaostory.response.model;

import android.os.Parcel;

import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 8. 23..
 */

public class MyStoryImageInfoTest extends KakaoTestCase {
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void parcelable() {
        testParcelableWithImageInfo(new MyStoryImageInfo("xLarge", "large", "medium", "small", "original"));
        testParcelableWithImageInfo(new MyStoryImageInfo("xLarge", "large", "medium", "small", null));
        testParcelableWithImageInfo(new MyStoryImageInfo(null, null, null, null, null));
    }

    private void testParcelableWithImageInfo(final MyStoryImageInfo imageInfo) {
        Parcel parcel = Parcel.obtain();
        imageInfo.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MyStoryImageInfo retrieved = MyStoryImageInfo.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(imageInfo, retrieved);
    }
}
