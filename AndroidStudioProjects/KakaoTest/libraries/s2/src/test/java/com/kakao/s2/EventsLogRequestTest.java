package com.kakao.s2;

import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.AppConfig;
import com.kakao.util.helper.CommonProtocol;

import org.apache.maven.settings.Server;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowLog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2016. 8. 24..
 */

public class EventsLogRequestTest extends KakaoTestCase {
    private Event rootEvent;
    private EventsLogRequest request;
    private AppConfig appConfig = new AppConfig("sample_app_key", "sample_key_hash", "sample_ka_header","sample_app_ver", "sample_package_name");
    private RequestConfiguration configuration =  new RequestConfiguration(appConfig, "sample_ka_header", "sample_extras");

    @Before
    public void setup() {
        ShadowLog.stream = System.out;
        rootEvent = EventsTestHelper.buildNormalEvent(null);
//        rootEvent.setFrom("adid");
//        rootEvent.setAdidEnabled(true);

        List<Event> leafEvents = EventsTestHelper.buildLeafEvents();
        request = new EventsLogRequest(configuration, rootEvent, leafEvents);
    }

    @Test
    public void testGetParams() {
        Map<String, String> params = request.getParams();
        assertEquals(5, params.size());
        assertEquals("adid", params.get(Event.FROM));
        assertEquals(null, params.get(Event.PROPS));
    }

    @Test
    public void testGetParamsProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("prop1", "value1");
        props.put("prop2", "value2");
        rootEvent = EventsTestHelper.buildNormalEvent(props);

        request = new EventsLogRequest(configuration, rootEvent, null);
        Map<String, String> params = request.getParams();
        assertEquals("{\"prop2\":\"value2\",\"prop1\":\"value1\"}", params.get(Event.PROPS));
    }

    @Test
    public void testGetParamsWithAdid() {
        Map<String, Object> props = new HashMap<>();
        props.put("prop1", "value1");
        props.put("prop2", "value2");
        rootEvent = new Event.Builder().setTo("url").setFrom("adid").setAdidEnabled(true)
                .setAction("action").setTimestamp(new Date().getTime()).setProps(props).build();

        request = new EventsLogRequest(configuration, rootEvent, null);
        Map<String, String> params = request.getParams();
        assertEquals("{\"prop2\":\"value2\",\"prop1\":\"value1\",\"adid_enabled\":1}", params.get(Event.PROPS));

    }

    @Test
    public void testGetHeaders() {
        Map<String, String> headers = request.getHeaders();
        assertTrue(headers.containsKey(ServerProtocol.AUTHORIZATION_HEADER_KEY));
        assertEquals(headers.get(ServerProtocol.AUTHORIZATION_HEADER_KEY), ServerProtocol.KAKAO_AK_HEADER_KEY + " " + configuration.getAppKey());
    }
}
