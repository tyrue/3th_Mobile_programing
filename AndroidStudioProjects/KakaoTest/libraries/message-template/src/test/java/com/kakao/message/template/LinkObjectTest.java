package com.kakao.message.template;

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

public class LinkObjectTest extends KakaoTestCase {
    private final String androidExecutionParams = "androidParams";
    private final String iosExecutionParams = "iosParams";
    private final String mobileWebUrl = "mobileWebUrl";
    private final String webUrl = "webUrl";
    @Test
    public void testMembersAreNull() {
        LinkObject linkObject = LinkObject.newBuilder().build();
        Assert.assertNull(linkObject.getAndroidExecutionParams());
        Assert.assertNull(linkObject.getIosExecutionParams());
        Assert.assertNull(linkObject.getMobileWebUrl());
        Assert.assertNull(linkObject.getWebUrl());
    }

    @Test
    public void testMembersAreCorrectlySet() {
        LinkObject linkObject = LinkObject.newBuilder()
                .setAndroidExecutionParams(androidExecutionParams)
                .setIosExecutionParams(iosExecutionParams)
                .setMobileWebUrl(mobileWebUrl)
                .setWebUrl(webUrl).build();

        Assert.assertEquals(androidExecutionParams, linkObject.getAndroidExecutionParams());
        Assert.assertEquals(iosExecutionParams, linkObject.getIosExecutionParams());
        Assert.assertEquals(mobileWebUrl, linkObject.getMobileWebUrl());
        Assert.assertEquals(webUrl, linkObject.getWebUrl());
    }

    @Test
    public void testToJSONObject() throws JSONException {
        LinkObject linkObject = LinkObject.newBuilder()
                .setAndroidExecutionParams(androidExecutionParams)
                .setIosExecutionParams(iosExecutionParams)
                .setMobileWebUrl(mobileWebUrl)
                .setWebUrl(webUrl).build();

        JSONObject linkJson = linkObject.toJSONObject();
        Assert.assertNotNull(linkJson);
        Assert.assertEquals(4, linkJson.length());
        Assert.assertEquals(androidExecutionParams, linkJson.getString(MessageTemplateProtocol.ANDROID_PARAMS));
        Assert.assertEquals(iosExecutionParams, linkJson.getString(MessageTemplateProtocol.IOS_PARAMS));
        Assert.assertEquals(mobileWebUrl, linkJson.getString(MessageTemplateProtocol.MOBILE_WEB_URL));
        Assert.assertEquals(webUrl, linkJson.getString(MessageTemplateProtocol.WEB_URL));
    }
}
