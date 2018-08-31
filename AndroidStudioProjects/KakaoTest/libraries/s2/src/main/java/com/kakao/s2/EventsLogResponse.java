package com.kakao.s2;

import com.kakao.network.exception.ResponseStatusError;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseData;

import java.net.HttpURLConnection;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2016. 8. 22..
 */
public class EventsLogResponse {
    private int successCount = 0;
    EventsLogResponse(ResponseData responseData) throws ResponseBody.ResponseBodyException, EventsLogErrorResponseException {
        ResponseBody body = new ResponseBody(responseData.getHttpStatusCode(), responseData.getData());
        if (responseData.getHttpStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new EventsLogErrorResponseException(body.getInt("code"), body.optString("msg", ""), responseData.getHttpStatusCode());
        }
        String COUNT = "count";
        successCount = body.getInt(COUNT);
    }

    public int getSuccessCount() {
        return successCount;
    }

    static class EventsLogErrorResponseException extends ResponseStatusError {
        private final int errorCode;
        private final String errorMsg;
        private final int httpStatusCode;

        EventsLogErrorResponseException(int errorCode, String errorMsg, int httpStatusCode) {
            super(errorMsg);
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
            this.httpStatusCode = httpStatusCode;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }
    }
}