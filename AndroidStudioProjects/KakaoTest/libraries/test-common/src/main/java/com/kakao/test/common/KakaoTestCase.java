package com.kakao.test.common;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;


import static org.junit.Assert.*;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2016. 8. 25..
 */

@Config(shadows = {ShadowLog.class}, sdk = 25, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public abstract class KakaoTestCase {
    @Before
    public void setup() {
        ShadowLog.stream = System.out;
        MockitoAnnotations.initMocks(this);
    }
}
