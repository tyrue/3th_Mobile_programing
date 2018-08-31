package com.kakao.message.template;

import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.MessageTemplateProtocol;
import com.kakao.message.template.SocialObject;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 3. 14..
 */

public class FeedTemplateTest extends KakaoTestCase {

    @Test
    public void testObjectType() {
        FeedTemplate params =
                FeedTemplate.newBuilder(ContentObject.newBuilder("title", "imageUrl",
                        LinkObject.newBuilder().build()).build()).build();
        Assert.assertEquals(MessageTemplateProtocol.TYPE_FEED, params.getObjectType());
    }

    @Test
    public void testNewBuilder() {
        FeedTemplate params =
                FeedTemplate.newBuilder(null).build();
        Assert.assertNull(params.getContentObject());
        Assert.assertEquals(0, params.getButtons().size());
        Assert.assertNull(params.getSocial());

        params = FeedTemplate.newBuilder(ContentObject.newBuilder("title", "imageUrl",
                LinkObject.newBuilder().build()).build()).build();

        Assert.assertNotNull(params.getContentObject());
    }

    @Test
    public void testSetSocial() {
        FeedTemplate params =
                FeedTemplate.newBuilder(ContentObject.newBuilder("title", "imageUrl",
                        LinkObject.newBuilder().build()).build())
                        .setSocial(SocialObject.newBuilder().build()).build();
        Assert.assertNotNull(params.getSocial());
    }

    @Test
    public void testAddButton() {
        FeedTemplate.Builder paramsBuilder =
                FeedTemplate.newBuilder(ContentObject.newBuilder("title", "imageUrl",
                        LinkObject.newBuilder().build()).build())
                        .addButton(new ButtonObject("title", LinkObject.newBuilder().build()));

        FeedTemplate params = paramsBuilder.build();
        Assert.assertEquals(1, params.getButtons().size());

        paramsBuilder.addButton(new ButtonObject("title", LinkObject.newBuilder().build()));
        params = paramsBuilder.build();
        Assert.assertEquals(2, params.getButtons().size());

        paramsBuilder.addButton(new ButtonObject("title", LinkObject.newBuilder().build())).build();
        params = paramsBuilder.build();
        Assert.assertEquals(3, params.getButtons().size());
    }

    @Test
    public void testToJSONObject() {

    }
}
