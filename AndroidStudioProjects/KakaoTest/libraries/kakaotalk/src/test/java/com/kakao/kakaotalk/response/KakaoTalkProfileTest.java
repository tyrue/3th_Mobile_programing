package com.kakao.kakaotalk.response;

import android.os.Parcel;

import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 8. 23..
 */

public class KakaoTalkProfileTest extends KakaoTestCase {
    private KakaoTalkProfile profile;
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void testParcelable() {
        profile = new KakaoTalkProfile("kevin", "profile_url", "thumbnail_url", "ko-KR");
        Parcel parcel = Parcel.obtain();
        profile.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        KakaoTalkProfile retrieved = KakaoTalkProfile.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(profile, retrieved);
    }
}
