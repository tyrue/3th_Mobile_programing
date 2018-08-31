package com.kakao.s2;

import com.kakao.network.NetworkTask;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseData;

import java.io.IOException;
import java.util.List;

/**
 * S2 이벤트 API 요청을 담당한다.
 * @author kevin.kang
 * Created by kevin.kang on 2016. 8. 22..
 */

class S2Api {
    /**
     * 이벤트를 publish하는 API를 호출한다.
     * @param configuration 현재 앱의 정보들을 담고 있는 RequestConfiguration 객체
     * @param rootEvent Top-Level attribute들을 담고 있는 이벤트 object
     * @param leafEvents Batching되어 보내질 이벤트 리스트. 싱글 이벤트를 보낼 때에는 빈 리스트가 넘겨진다.
     * @return EventsLogResponse 리퀘스트가 성공했을 경우 response를 담은 object
     * @throws IOException
     * @throws ResponseBody.ResponseBodyException
     * @throws EventsLogResponse.EventsLogErrorResponseException
     */
    static EventsLogResponse requestPublishingEvents(final RequestConfiguration configuration, Event rootEvent, List<Event> leafEvents) throws IOException, ResponseBody.ResponseBodyException, EventsLogResponse.EventsLogErrorResponseException {
        NetworkTask networkTask = new NetworkTask();
        ResponseData result = networkTask.request(new EventsLogRequest(configuration, rootEvent, leafEvents));
        return new EventsLogResponse(result);
    }

    static EventsLogResponse requestPublishingAdidEvents(final RequestConfiguration configuration, Event rootEvent, List<Event> leafEvents) throws IOException, ResponseBody.ResponseBodyException, EventsLogResponse.EventsLogErrorResponseException {
        NetworkTask networkTask = new NetworkTask();
        ResponseData result = networkTask.request(new PublishAdidEventsRequest(configuration, rootEvent, leafEvents));
        return new EventsLogResponse(result);
    }
}
