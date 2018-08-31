package com.kakao.kakaolink.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.kakao.kakaolink.R;
import com.kakao.kakaolink.internal.KakaoTalkLinkProtocol;
import com.kakao.kakaolink.v2.network.TemplateValidateRequest;
import com.kakao.kakaolink.v2.network.KakaoLinkTemplateResponse;
import com.kakao.network.NetworkTask;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseData;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.exception.KakaoException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin.kang. Created on 2016. 11. 28..
 */

public class KakaoLinkSenderTest extends KakaoTestCase {
    private KakaoLinkSender sender;
    private TemplateValidateRequest request;
    private KakaoLinkTemplateResponse response;
    private String templateId = "9999";
    private Map<String, String> templateArgs;
    @Before
    public void setup() {
        super.setup();
        templateArgs = new HashMap<String, String>();
        templateArgs.put("iphoneAppParam", "key1=value1");

        RequestConfiguration configuration = KakaoLInkTestHelper.createMockRequestConfiguration();
        sender = new KakaoLinkSender(KakaoTaskQueue.getInstance(), new KakaoLinkApi(new NetworkTask()), new TestKakaoProtocolService());
        try {
            request = Mockito.spy(new TemplateValidateRequest(configuration, templateId, templateArgs));
            ResponseData data = KakaoLInkTestHelper.createMockResponseData();
            response = Mockito.spy(new KakaoLinkTemplateResponse(new ResponseBody(data.getHttpStatusCode(), data.getData())));
        } catch (ResponseBody.ResponseBodyException e) {
            Assert.fail("There was an error parsing response");
        }
    }

    @Test
    public void testCreateIntent() {
        Intent intent = sender.createKakaoLinkIntent(RuntimeEnvironment.application.getApplicationContext(), request, response);
        Uri uri = intent.getData();
        Assert.assertNotNull(uri);
        Assert.assertEquals(KakaoTalkLinkProtocol.LINK_SCHEME, uri.getScheme());
        Assert.assertEquals(KakaoTalkLinkProtocol.LINK_AUTHORITY, uri.getAuthority());
        Assert.assertEquals(KakaoTalkLinkProtocol.LINK_VERSION_40, uri.getQueryParameter(KakaoTalkLinkProtocol.LINKVER));
        Assert.assertEquals(request.getAppKey(), uri.getQueryParameter(KakaoTalkLinkProtocol.APP_KEY));
        Assert.assertEquals(uri.getQueryParameter(KakaoTalkLinkProtocol.APP_VER), request.getAppVer());

        Assert.assertEquals(templateId, uri.getQueryParameter(KakaoTalkLinkProtocol.TEMPLATE_ID));
        Assert.assertNotNull(uri.getQueryParameter(KakaoTalkLinkProtocol.TEMPLATE_ARGS));
        Assert.assertNotNull(uri.getQueryParameter(KakaoTalkLinkProtocol.TEMPLATE_JSON));
    }

    @Test
    public void testCreateIntentWithLargeUri() {
        String largeString = "";
        for (int i = 0; i < 10000; i++) {
            largeString += "aaaaaaaaaa";
        }
        Mockito.doReturn(largeString).when(request).getAppVer();

        Activity activity = Mockito.mock(Activity.class);
        Mockito.doReturn("com_kakao_alert_uri_too_long").when(activity).getString(R.string.com_kakao_alert_uri_too_long);
        try {
            Intent intent = sender.createKakaoLinkIntent(activity, request, response);
            Assert.fail("Should fail with KakaoException " + KakaoException.ErrorType.URI_LENGTH_EXCEEDED);
        } catch (KakaoException e) {
            Assert.assertEquals(e.getErrorType(), KakaoException.ErrorType.URI_LENGTH_EXCEEDED);
        }
    }
}
