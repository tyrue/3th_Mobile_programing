package com.kakao.message.template;

import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.MessageTemplateProtocol;
import com.kakao.test.common.KakaoTestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 3. 14..
 */

public class ContentObjectTest extends KakaoTestCase {
    private final String title = "title";
    private final String imageUrl = "imageUrl";
    private final String description = "description";
    private final int imageHeight = 200;
    private final int imageWidth = 200;

    @Test
    public void testMembersAreNull() {
        ContentObject contentObject = ContentObject.newBuilder(title, imageUrl,
                LinkObject.newBuilder().build()).build();
        Assert.assertNull(contentObject.getDescription());
        Assert.assertNull(contentObject.getImageHeight());
        Assert.assertNull(contentObject.getImageWidth());
    }

    @Test
    public void testMembersAreCorrectlySet() {
        ContentObject contentObject = ContentObject.newBuilder(title, imageUrl,
                LinkObject.newBuilder().build())
                .setDescrption(description)
                .setImageHeight(imageHeight)
                .setImageWidth(imageWidth).build();

        Assert.assertEquals(title, contentObject.getTitle());
        Assert.assertEquals(imageUrl, contentObject.getImageUrl());
        Assert.assertEquals(description, contentObject.getDescription());
        Assert.assertEquals(imageHeight, contentObject.getImageHeight().intValue());
        Assert.assertEquals(imageWidth, contentObject.getImageWidth().intValue());
    }

    @Test
    public void testToJSONObject() throws JSONException {
        ContentObject.Builder builder = ContentObject.newBuilder("title", "imageUrl",
                LinkObject.newBuilder().build());
        ContentObject contentObject = builder.build();
        JSONObject contentJson = contentObject.toJSONObject();
        Assert.assertNotNull(contentJson);
        Assert.assertEquals(3, contentJson.length());
        Assert.assertEquals(contentObject.getTitle(), contentJson.getString(MessageTemplateProtocol.TITLE));
        Assert.assertEquals(contentObject.getImageUrl(), contentJson.getString(MessageTemplateProtocol.IMAGE_URL));
        Assert.assertNotNull(contentJson.get(MessageTemplateProtocol.LINK));
    }
}
