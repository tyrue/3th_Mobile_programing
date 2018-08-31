package com.kakao.message.template;

import com.kakao.message.template.SocialObject;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author kevin.kang. Created on 2017. 3. 14..
 */

public class SocialObjectTest extends KakaoTestCase {
    @Test
    public void testMembersAreNull() {
        SocialObject socialObject = SocialObject.newBuilder().build();
        Assert.assertNull(socialObject.getCommentCount());
        Assert.assertNull(socialObject.getLikeCount());
        Assert.assertNull(socialObject.getSharedCount());
        Assert.assertNull(socialObject.getSubscriberCount());
        Assert.assertNull(socialObject.getViewCount());
    }

    @Test
    public void testMembersAreCorrectlySet() {
        SocialObject socialObject = SocialObject.newBuilder()
                .setLikeCount(100)
                .setSharedCount(50)
                .setCommentCount(10)
                .setViewCount(500)
                .setSubscriberCount(30)
                .build();
        Assert.assertEquals(100, socialObject.getLikeCount().longValue());
        Assert.assertEquals(50, socialObject.getSharedCount().longValue());
        Assert.assertEquals(10, socialObject.getCommentCount().longValue());
        Assert.assertEquals(500, socialObject.getViewCount().longValue());
        Assert.assertEquals(30, socialObject.getSubscriberCount().longValue());

    }
}
