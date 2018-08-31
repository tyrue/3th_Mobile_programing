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

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author kevin.kang
 * Created by kevin.kang on 2017. 1. 19..
 */

public class TemplateScrapResponse extends KakaoLinkTemplateResponse {
    private String templateId;
    private JSONObject templateArgs;

    public TemplateScrapResponse(ResponseBody responseBody) throws ResponseBody.ResponseBodyException {
        super(responseBody);
        JSONObject resJson = responseBody.getJson();
        try {
            templateId = resJson.getString(KakaoTalkLinkProtocol.TEMPLATE_ID);
            templateArgs = resJson.getJSONObject(KakaoTalkLinkProtocol.TEMPLATE_ARGS);
        } catch (JSONException e) {
            throw new KakaoException(KakaoException.ErrorType.JSON_PARSING_ERROR, "There was an error parsing template scrap response");
        }
    }

    public String getTemplateId() {
        return templateId;
    }
    public JSONObject getTemplateArgs() {
        return templateArgs;
    }
}
