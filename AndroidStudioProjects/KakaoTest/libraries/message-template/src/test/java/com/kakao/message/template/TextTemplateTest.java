package com.kakao.message.template;

import com.kakao.test.common.KakaoTestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 9. 13..
 */

public class TextTemplateTest extends KakaoTestCase {
    @Before
    public void setup() {
        super.setup();
    }

    @Test(expected = IllegalArgumentException.class)
    public void withNullText() {
        TextTemplate.newBuilder(null, LinkObject.newBuilder().build()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void withNullLink() {
        TextTemplate.newBuilder("text", null).build();
    }

    @Test
    public void objectType() {
        TextTemplate params = TextTemplate.newBuilder("text", LinkObject.newBuilder().build()).build();
        Assert.assertEquals(MessageTemplateProtocol.TYPE_TEXT, params.getObjectType());
    }

    @Test
    public void nullButtonTitle() {
        TextTemplate params = TextTemplate.newBuilder("text", LinkObject.newBuilder().build())
                .setButtonTitle(null).build();
        JSONObject jsonObject = params.toJSONObject();
        Assert.assertNotNull(jsonObject);
    }

    @Test
    public void nullButton() {
        TextTemplate params = TextTemplate.newBuilder("text", LinkObject.newBuilder().build())
                .addButton(null).build();
        JSONObject jsonObject = params.toJSONObject();
        Assert.assertNotNull(jsonObject);
    }
    @Test
    public void fullTextTemplate() {
        TextTemplate params = TextTemplate.newBuilder("text", LinkObject.newBuilder().build())
                .setButtonTitle("buttonTitle").addButton(new ButtonObject("button title", LinkObject.newBuilder().build())).build();

        Assert.assertEquals(MessageTemplateProtocol.TYPE_TEXT, params.getObjectType());
        Assert.assertEquals("text", params.getText());
        Assert.assertEquals(1, params.getButtons().size());
        Assert.assertEquals("buttonTitle", params.getButtonTitle());
        Assert.assertNotNull(params.getLinkObject());

        JSONObject jsonObject = params.toJSONObject();
        Assert.assertNotNull(jsonObject);

        try {
            Assert.assertEquals(MessageTemplateProtocol.TYPE_TEXT, jsonObject.get(MessageTemplateProtocol.OBJ_TYPE));
            Assert.assertEquals("text", jsonObject.get(MessageTemplateProtocol.TEXT));
            Assert.assertEquals(1, jsonObject.getJSONArray(MessageTemplateProtocol.BUTTONS).length());
            Assert.assertEquals("buttonTitle", jsonObject.get(MessageTemplateProtocol.BUTTON_TITLE));
            Assert.assertNotNull(jsonObject.get(MessageTemplateProtocol.LINK));
        } catch (JSONException e) {
            Assert.fail(e.toString());
        }
    }

}
