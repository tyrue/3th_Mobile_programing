package com.kakao.kakaolink.v2.network;

import com.kakao.kakaolink.v2.KakaoLInkTestHelper;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.RequestConfiguration;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 3. 14..
 */
public class TemplateDefaultRequestTest extends KakaoTestCase {
    private RequestConfiguration configuration;

    @Before
    public void setup() {
        super.setup();
        configuration = KakaoLInkTestHelper.createMockRequestConfiguration();
    }
    @Test
    public void testMethodIsGet() {
        TemplateDefaultRequest request = new TemplateDefaultRequest(configuration,
                FeedTemplate.newBuilder(ContentObject.newBuilder("title", "imageUrl",
                        LinkObject.newBuilder().build()).build()).build());
        Assert.assertEquals("GET", request.getMethod());
    }
}
