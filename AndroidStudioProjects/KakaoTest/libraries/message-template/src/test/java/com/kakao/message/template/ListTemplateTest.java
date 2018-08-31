package com.kakao.message.template;

import com.kakao.message.template.LinkObject;
import com.kakao.message.template.ListTemplate;
import com.kakao.message.template.MessageTemplateProtocol;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 3. 14..
 */

public class ListTemplateTest extends KakaoTestCase {

    @Test
    public void testObjectType() {
        ListTemplate params =
                ListTemplate.newBuilder("title", LinkObject.newBuilder().build()).build();
        Assert.assertEquals(MessageTemplateProtocol.TYPE_LIST, params.getObjectType());
    }
}
