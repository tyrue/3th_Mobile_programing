package com.kakao.kakaolink.v2.network;

import android.net.Uri;

import com.kakao.kakaolink.v2.KakaoLInkTestHelper;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;
import com.kakao.test.common.KakaoTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin.kang. Created on 2016. 11. 28..
 */
public class TemplateValidateRequestTest extends KakaoTestCase {
    private RequestConfiguration configuration;
    private String templateId = "12345";
    private Map<String, String> templateArgs;

    @Before
    public void setup() {
        super.setup();
        templateArgs = new HashMap<String, String>();
        configuration = KakaoLInkTestHelper.createMockRequestConfiguration();
    }

    @Test
    public void testMethodIsGet() {
        TemplateValidateRequest request = new TemplateValidateRequest(configuration, templateId, templateArgs);
        Assert.assertEquals("GET", request.getMethod());
    }

    @Test
    public void testGetHeaders() {
        TemplateValidateRequest request = new TemplateValidateRequest(configuration, templateId, templateArgs);
        Assert.assertNotNull(request.getHeaders());
        Assert.assertEquals(5, request.getHeaders().size());
    }

    @Test
    public void testGetUrlWithNullTemplateArgs() {
        testGetParamsSize(2);
    }

    @Test
    public void testGetUrlWithEmptyTemplateArgs() {
        testGetParamsSize(2);
    }

    @Test
    public void testGetUrlWithOneTemplateArgs() {
        templateArgs.put("name", "Kevin Kang");
        testGetParamsSize(3);
    }

    @Test
    public void testGetUrlWithTwoTemplateArgs() {
        templateArgs.put("name", "Kevin Kang");
        templateArgs.put("age", "26");
        testGetParamsSize(3);
    }

    private void testGetParamsSize(final int paramsSize) {
        TemplateValidateRequest request = new TemplateValidateRequest(configuration, templateId, templateArgs);

        String url = request.getUrl();
        Assert.assertNotNull(url);
        Uri uri = Uri.parse(url);
        Assert.assertNotNull(uri);

        Assert.assertEquals("https", uri.getScheme());
        Assert.assertEquals(ServerProtocol.API_AUTHORITY, uri.getAuthority());
        Assert.assertEquals("/" + ServerProtocol.LINK_TEMPLATE_VALIDATE_PATH, uri.getPath());

        Assert.assertEquals(paramsSize, uri.getQueryParameterNames().size());
    }
}
