/*
  Copyright 2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.kakaolink.v2.network;

import com.kakao.kakaolink.internal.KakaoTalkLinkProtocol;
import com.kakao.network.response.ResponseBody;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Template validate response class.
 * Created by kevin.kang on 2016. 11. 25..
 */

public class KakaoLinkTemplateResponse {
    private JSONObject templateMsg;
    private JSONObject warningMsg;
    private JSONObject argumentMsg;

    public KakaoLinkTemplateResponse(ResponseBody responseBody) {
        if (responseBody.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new KakaoException(KakaoException.ErrorType.ILLEGAL_ARGUMENT, responseBody.toString());
        }

        JSONObject resJson = responseBody.getJson();
        try {
            templateMsg = resJson.getJSONObject(KakaoTalkLinkProtocol.TEMPLATE_MSG);
            warningMsg = resJson.getJSONObject(KakaoTalkLinkProtocol.WARNING_MSG);
            argumentMsg = resJson.getJSONObject(KakaoTalkLinkProtocol.ARGUMENT_MSG);
        } catch (JSONException e) {
            throw new KakaoException(KakaoException.ErrorType.JSON_PARSING_ERROR, "There was an error parsing response");
        }
    }

    public JSONObject getTemplateMsg() {
        return templateMsg;
    }

    public JSONObject getWarningMsg() {
        return warningMsg;
    }

    public JSONObject getArgumentMsg() {
        return  argumentMsg;
    }
}
