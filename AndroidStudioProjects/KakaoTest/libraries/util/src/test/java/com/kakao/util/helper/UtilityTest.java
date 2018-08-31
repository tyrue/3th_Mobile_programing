package com.kakao.util.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.anyOf;

/**
 * Created by kevin.kang on 16. 8. 11..
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class UtilityTest {

    private HashMap<String, String> paramMap;

    @Before
    public void setup() {
        paramMap = new HashMap<>();
    }

    @Test
    public void testBuildQueryStringWithEmptyMap() {
        assertEquals(Utility.buildQueryString(paramMap), null);
    }

    @Test
    public void testBuildQueryStringWithOneElement() {
        paramMap.put("place", "1111");
        assertEquals(Utility.buildQueryString(paramMap), "place=1111");
    }

    @Test
    public void testBuildQueryStringWithTwoElement() {
        paramMap.put("place", "1111");
        paramMap.put("nickname", "kevin");

        assertThat(Utility.buildQueryString(paramMap), anyOf(equalTo("place=1111&nickname=kevin"), equalTo("nickname=kevin&place=1111")));
    }

    @Test
    public void testBuildQueryStringWithNullElement() {
        paramMap.put("place", null);
        assertEquals(Utility.buildQueryString(paramMap), "place=null");
    }
}