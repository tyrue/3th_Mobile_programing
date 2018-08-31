package com.kakao.s2;

import android.content.Context;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.response.ResponseData;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.util.helper.CommonProtocol;
import com.kakao.util.helper.SystemInfo;
import com.kakao.util.helper.Utility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.util.Transcript;
import org.robolectric.util.concurrent.RoboExecutorService;

import java.util.Date;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2016. 8. 22..
 */

@PrepareForTest({ AdvertisingIdClient.class, KakaoTaskQueue.class, Executors.class, S2Api.class, Utility.class, SystemInfo.class})
public class S2ServiceTest extends KakaoPowerMockTestCase {
    private final String to = "http://www.kakaocorp.com";
    private final String action = "click";
    private final String adid = "adid from AdvertisingIdClient";

    private int successCount = -1;

    public enum EVENTS_PUBLISH_RESULTS {
        SINGLE_FAILURE,
        SINGLE_SUCCESS,
    }

    private final Transcript transcript = new Transcript();

    @Before
    public void setup() {
        super.setUp();
        RoboExecutorService service = new RoboExecutorService();

        PowerMockito.spy(Utility.class);
        stub(method(Executors.class, "newCachedThreadPool")).toReturn(service);
        stub(method(SystemInfo.class, "initialize")).toReturn(null);
        try {
            PowerMockito.doReturn("mock_app_key").when(Utility.class, "getMetadata", Matchers.any(Context.class), Matchers.eq(CommonProtocol.APP_KEY_PROPERTY));
            stub(method(S2Api.class, "requestPublishingEvents")).toReturn(new EventsLogResponse(new ResponseData(200, "{count:1}".getBytes())));
        } catch (Exception e) {
            ShadowLog.e("Exception in setup: mocking", e.toString());
        }

        Robolectric.getBackgroundThreadScheduler().pause();
        KakaoTaskQueue.getInstance().setExecutor(service);

        try {
            S2Service.init(RuntimeEnvironment.application, 10);
        } catch (S2Service.AlreadyInitializedException e) {
            ShadowLog.e(S2ServiceTest.class.getName(), e.toString());
        }
    }

    @After
    public void cleanup() {
        transcript.clear();
    }

    @Test
    public void testAddAdidEvent() {
        stub(method(AdvertisingIdClient.class, "getAdvertisingIdInfo")).toThrow(new GooglePlayServicesNotAvailableException(300));

        Event event = new Event.Builder().setTimestamp(new Date().getTime()).setTo(to).setAction(action).build();

        S2Service.getInstance().addAdidEvent(event, new ResponseCallback<Integer>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                transcript.add(EVENTS_PUBLISH_RESULTS.SINGLE_FAILURE.toString());
            }

            @Override
            public void onSuccess(Integer result) {
                transcript.add(EVENTS_PUBLISH_RESULTS.SINGLE_SUCCESS.toString());
            }
        });

        ShadowApplication.runBackgroundTasks();
        assertEquals(null, event.getFrom());
        transcript.assertEventsSoFar(EVENTS_PUBLISH_RESULTS.SINGLE_SUCCESS.toString());
    }
}
