package com.kakao.kakaolink.v2.network;

import com.kakao.kakaolink.v2.KakaoLInkTestHelper;
import com.kakao.network.RequestConfiguration;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 3. 14..
 */
public class TemplateScrapRequestTest extends KakaoTestCase {
    private RequestConfiguration configuration;

    @Before
    public void setup() {
        configuration = KakaoLInkTestHelper.createMockRequestConfiguration();
    }

    @Test
    public void testMethodIsGet() {
        TemplateScrapRequest request = new TemplateScrapRequest(configuration, "url", null, null);
        Assert.assertEquals("GET", request.getMethod());
    }
}
