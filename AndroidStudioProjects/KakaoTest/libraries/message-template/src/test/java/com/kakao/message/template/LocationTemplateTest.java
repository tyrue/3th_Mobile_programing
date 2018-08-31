package com.kakao.message.template;

import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.MessageTemplateProtocol;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 3. 14..
 */

public class LocationTemplateTest extends KakaoTestCase {
    @Test
    public void testObjectType() {
        LocationTemplate params =
                LocationTemplate.newBuilder("address",
                        ContentObject.newBuilder("title", "imageUrl", LinkObject.newBuilder().build()).build()).build();
        Assert.assertEquals(MessageTemplateProtocol.TYPE_LOCATION, params.getObjectType());
    }

    @Test
    public void testUnsupportedBuilderMethod() {
        try {
            FeedTemplate params =
                    LocationTemplate.newBuilder(ContentObject.newBuilder("title", "iamgeUrl",
                            LinkObject.newBuilder().build()).build()).build();
            Assert.fail("Unsupported newBuilder method did not throw an exception.");
        } catch (UnsupportedOperationException ignored) {
        }

    }
}
