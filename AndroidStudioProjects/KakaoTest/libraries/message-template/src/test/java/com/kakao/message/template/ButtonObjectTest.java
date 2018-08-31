package com.kakao.message.template;

import com.kakao.message.template.ButtonObject;
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

public class ButtonObjectTest extends KakaoTestCase {
    @Test
    public void testToJSONObject() throws JSONException {
        ButtonObject buttonObject = new ButtonObject("title", LinkObject.newBuilder().build());
        JSONObject buttonJson = buttonObject.toJSONObject();
        Assert.assertNotNull(buttonJson);
        Assert.assertEquals(buttonObject.getTitle(), buttonJson.getString(MessageTemplateProtocol.TITLE));
        Assert.assertNotNull(buttonJson.get(MessageTemplateProtocol.LINK));
    }
}
