package com.kakao.message.template;

import com.kakao.test.common.KakaoTestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 6. 14..
 */

public class CommerceTemplateTest extends KakaoTestCase {
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void testCommerceDetail() {
        CommerceDetailObject.Builder builder = CommerceDetailObject.newBuilder(12345);
        JSONObject object = builder.build().toJSONObject();
        Assert.assertEquals(1, object.length());
        builder.setDiscountPrice(null);
        Assert.assertEquals(1, builder.build().toJSONObject().length());
        builder.setDiscountPrice(10000);
        Assert.assertEquals(2, builder.build().toJSONObject().length());
        builder.setDiscountRate(20).setFixedDiscountPrice(2000);
        Assert.assertEquals(4, builder.build().toJSONObject().length());
    }

    @Test
    public void testNewBuilder() {
        CommerceTemplate params = CommerceTemplate.newBuilder(
                ContentObject.newBuilder("신메뉴 출시❤️ 체리블라썸라떼",
                        "http://mud-kage.kakao.co.kr/dn/bSbH9w/btqgegaEDfW/vD9KKV0hEintg6bZT4v4WK/kakaolink40_original.png",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption("이번 주는 체리블라썸라떼 1+1").build(),
                CommerceDetailObject.newBuilder(12345).setDiscountPrice(10000).setDiscountRate(20).setFixedDiscountPrice(2000).build())
                .build();

        Assert.assertEquals(MessageTemplateProtocol.TYPE_COMMERCE, params.getObjectType());
        JSONObject jsonObject = params.toJSONObject();

        try {
            JSONObject contentJson = (JSONObject) jsonObject.get(MessageTemplateProtocol.CONTENT);
            Assert.assertEquals(4, contentJson.length());

            JSONObject commerceDetail = (JSONObject) jsonObject.get(MessageTemplateProtocol.COMMERCE);
            Assert.assertEquals(4, commerceDetail.length());
            Assert.assertEquals(2000, commerceDetail.getInt(MessageTemplateProtocol.FIXED_DISCOUNT_PRICE));
        } catch (JSONException e) {
            Assert.fail();
        }
    }
}
