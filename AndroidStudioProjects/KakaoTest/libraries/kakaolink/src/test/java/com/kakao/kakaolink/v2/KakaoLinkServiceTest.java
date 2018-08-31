package com.kakao.kakaolink.v2;

import android.app.Activity;

import com.kakao.kakaolink.BuildConfig;
import com.kakao.kakaolink.R;
import com.kakao.network.ErrorResult;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.test.common.KakaoTestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.util.concurrent.RoboExecutorService;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.Logger;
import org.robolectric.util.Transcript;
//import org.robolectric.util.concurrent.RoboExecutorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin.kang. Created on 2016. 11. 28..
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class KakaoLinkServiceTest extends KakaoTestCase {
    private Activity activity;
    private final List<String> transcript = new ArrayList<>();
    private KakaoLinkService linkService;
    private ResponseCallback<KakaoLinkResponse> callback;

    @Before
    public void setup() {
        super.setup();
        activity = Mockito.mock(Activity.class);
        RoboExecutorService service = new RoboExecutorService();
        linkService = Mockito.spy(new KakaoLinkService(new TestKakaoProtocolService()));
        callback = new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                transcript.add("failure");
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                transcript.add("success");
            }
        };

        Mockito.doReturn(new KakaoTaskQueue(service)).when(linkService).getTaskQueue();
        RequestConfiguration configuration = KakaoLInkTestHelper.createMockRequestConfiguration();
        Mockito.doReturn(configuration).when(linkService).getRequestConfiguration(activity);
        Mockito.doReturn(KakaoLInkTestHelper.createMockNetworkTask()).when(linkService).getNetworkTask();
        Robolectric.getBackgroundThreadScheduler().pause();
    }

    @After
    public void after() {
        if (transcript != null) {
            transcript.clear();
        }
    }

    @Test
    public void testSend() {
        Mockito.doReturn(true).when(linkService).isKakaoLinkV2Available(activity);
        Map<String, String> templateArgs = new HashMap<String, String>();
        templateArgs.put("${iphoneAppParam", "key1=value1");

        linkService.sendCustom(activity, "91", templateArgs, callback);
        Assert.assertTrue(transcript.isEmpty());
        ShadowApplication.runBackgroundTasks();
        Assert.assertTrue(transcript.contains("success"));
    }

    @Test
    public void testSendWithLowerTalkVersion() {
        Mockito.doReturn(false).when(linkService).isKakaoLinkV2Available(activity);
        Mockito.doReturn("com_kakao_alert_install_kakaotalk").when(activity).getString(R.string.com_kakao_alert_install_kakaotalk);

        Map<String, String> templateArgs = new HashMap<String, String>();
        templateArgs.put("${iphoneAppParam", "key1=value1");

        linkService.sendCustom(activity, "91", templateArgs, callback);
        Assert.assertTrue(transcript.contains("failure"));
    }
}
